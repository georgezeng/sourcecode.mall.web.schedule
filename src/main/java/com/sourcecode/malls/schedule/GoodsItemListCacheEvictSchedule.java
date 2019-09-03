package com.sourcecode.malls.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sourcecode.malls.schedule.base.AbstractSchedule;
import com.sourcecode.malls.service.impl.CacheEvictService;

@Component
public class GoodsItemListCacheEvictSchedule extends AbstractSchedule {

	@Autowired
	private CacheEvictService cacheEvictService;

	@Scheduled(cron = "0 0 * * * ?")
	@Override
	public void run() {
		super.run();
	}

	@Override
	protected void execute() throws Exception {
		cacheEvictService.clearAllGoodsItemList();
	}

}
