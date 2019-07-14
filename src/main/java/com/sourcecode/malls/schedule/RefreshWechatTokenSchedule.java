package com.sourcecode.malls.schedule;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcecode.malls.domain.client.Client;
import com.sourcecode.malls.domain.client.WechatToken;
import com.sourcecode.malls.dto.query.PageInfo;
import com.sourcecode.malls.dto.setting.DeveloperSettingDTO;
import com.sourcecode.malls.dto.wechat.WechatAccessInfo;
import com.sourcecode.malls.repository.jpa.impl.client.ClientRepository;
import com.sourcecode.malls.repository.jpa.impl.client.WechatTokenRepository;
import com.sourcecode.malls.schedule.base.AbstractSchedule;
import com.sourcecode.malls.service.impl.MerchantSettingService;

@Component
public class RefreshWechatTokenSchedule extends AbstractSchedule {
	@Autowired
	private WechatTokenRepository repository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private MerchantSettingService settingService;

	@Autowired
	private ObjectMapper mapper;

	@Value("wechat.user.url.refresh_token")
	private String refreshTokenUrl;

	@Autowired
	private RestTemplate httpClient;

	@Scheduled(cron = "0 0 * * * ?")
	@Override
	public void run() {
		super.run();
	}

	@Override
	protected void execute() throws Exception {
		PageInfo pageInfo = new PageInfo();
		pageInfo.setNum(1);
		pageInfo.setSize(1000);
		Page<WechatToken> result = null;
		Pageable page = pageInfo.pageable();
		do {
			result = repository.findAll(page);
			if (result.hasContent()) {
				for (WechatToken tokens : result.getContent()) {
					Optional<Client> client = clientRepository.findById(tokens.getUserId());
					if (client.isPresent()) {
						try {
							Optional<DeveloperSettingDTO> developerSetting = settingService
									.loadWechatGzh(client.get().getMerchant().getId());
							String response = httpClient.getForObject(String.format(refreshTokenUrl,
									developerSetting.get().getAccount(), tokens.getRefreshToken()), String.class);
							WechatAccessInfo accessInfo = mapper.readValue(response, WechatAccessInfo.class);
							BeanUtils.copyProperties(accessInfo, tokens);
							repository.save(tokens);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
				page = page.next();
			}
		} while (result.hasNext());
	}

}
