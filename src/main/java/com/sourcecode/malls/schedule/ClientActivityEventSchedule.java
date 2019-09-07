package com.sourcecode.malls.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sourcecode.malls.domain.merchant.Merchant;
import com.sourcecode.malls.dto.query.PageInfo;
import com.sourcecode.malls.repository.jpa.impl.merchant.MerchantRepository;
import com.sourcecode.malls.schedule.base.AbstractSchedule;
import com.sourcecode.malls.service.impl.ClientBonusService;

@Component
public class ClientActivityEventSchedule extends AbstractSchedule {

	@Autowired
	private MerchantRepository merchantRepository;

	@Autowired
	private ClientBonusService bonusService;

	@Scheduled(cron = "0 * * * * ?")
	@Override
	public void run() {
		super.run();
	}

	@Override
	protected void execute() throws Exception {
		PageInfo pageInfo = new PageInfo();
		pageInfo.setNum(1);
		pageInfo.setSize(1000);
		pageInfo.setProperty("createTime");
		pageInfo.setOrder(Direction.ASC.name());
		Pageable pageable = pageInfo.pageable();
		Page<Merchant> result = null;
		do {
			result = merchantRepository.findAll(pageable);
			if (result.hasContent()) {
				result.get().forEach(it -> {
					if (it.isEnabled()) {
						bonusService.setIsActivityEventTime(it);
					}
				});
				pageable = pageable.next();
			}
		} while (result.hasNext());
	}

}
