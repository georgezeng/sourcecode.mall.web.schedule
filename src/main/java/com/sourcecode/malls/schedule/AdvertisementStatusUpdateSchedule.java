package com.sourcecode.malls.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sourcecode.malls.repository.jpa.impl.merchant.MerchantRepository;
import com.sourcecode.malls.schedule.base.AbstractSchedule;
import com.sourcecode.malls.service.impl.AdvertisementService;

@Component
public class AdvertisementStatusUpdateSchedule extends AbstractSchedule {

	@Autowired
	private MerchantRepository merchantRepository;

	@Autowired
	private AdvertisementService service;

	@Scheduled(cron = "0 * * * * ?")
	@Override
	public void run() {
		super.run();
	}

	@Override
	protected void execute() throws Exception {
		long count = merchantRepository.count();
		for (long i = 1; i <= count; i++) {
			service.updateStatus(count);
		}
	}

}
