package com.sourcecode.malls.schedule;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcecode.malls.domain.client.WechatToken;
import com.sourcecode.malls.dto.query.PageInfo;
import com.sourcecode.malls.dto.setting.DeveloperSettingDTO;
import com.sourcecode.malls.dto.wechat.WechatAccessInfo;
import com.sourcecode.malls.repository.jpa.impl.client.WechatTokenRepository;
import com.sourcecode.malls.schedule.base.AbstractSchedule;
import com.sourcecode.malls.service.impl.MerchantSettingService;

@Component
public class RefreshWechatTokenSchedule extends AbstractSchedule {
	@Autowired
	private WechatTokenRepository repository;

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
					try {
						Optional<DeveloperSettingDTO> developerSetting = settingService
								.loadWechatGzh(tokens.getMerchantId());
						String response = httpClient.getForObject(String.format(refreshTokenUrl,
								developerSetting.get().getAccount(), tokens.getRefreshToken()), String.class);
						WechatAccessInfo accessInfo = mapper.readValue(response, WechatAccessInfo.class);
						if (!StringUtils.isEmpty(accessInfo.getErrmsg())) {
							throw new Exception(accessInfo.getErrcode() + ": " + accessInfo.getErrmsg());
						}
						tokens.setAccessToken(accessInfo.getAccessToken());
						tokens.setRefreshToken(accessInfo.getRefreshToken());
						tokens.setOpenId(accessInfo.getOpenId());
						repository.save(tokens);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						repository.delete(tokens);
					}
				}
				page = page.next();
			}
		} while (result.hasNext());
	}

}
