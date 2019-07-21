package com.sourcecode.malls.schedule;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sourcecode.malls.domain.aftersale.AfterSaleApplication;
import com.sourcecode.malls.dto.aftersale.AfterSaleApplicationDTO;
import com.sourcecode.malls.dto.query.PageInfo;
import com.sourcecode.malls.dto.query.QueryInfo;
import com.sourcecode.malls.enums.AfterSaleStatus;
import com.sourcecode.malls.enums.AfterSaleType;
import com.sourcecode.malls.schedule.base.AbstractSchedule;
import com.sourcecode.malls.service.impl.AfterSaleService;

@Component
public class ConfirmPickupAfterSaleApplicationSchedule extends AbstractSchedule {
	@Autowired
	private AfterSaleService service;

	@Scheduled(cron = "0 0 0 * * ?")
	@Override
	public void run() {
		super.run();
	}

	@Override
	protected void execute() throws Exception {
		QueryInfo<AfterSaleApplicationDTO> queryInfo = new QueryInfo<>();
		AfterSaleApplicationDTO dto = new AfterSaleApplicationDTO();
		dto.setType(AfterSaleType.Change);
		dto.setStatus(AfterSaleStatus.WaitForPickup);
		queryInfo.setData(dto);
		PageInfo page = new PageInfo();
		page.setOrder(Direction.ASC.name());
		page.setProperty("sendTime");
		page.setNum(1);
		page.setSize(1000);
		queryInfo.setPage(page);
		Page<AfterSaleApplication> result = null;
		do {
			result = service.getList(queryInfo);
			if (result.hasContent()) {
				for (AfterSaleApplication data : result.getContent()) {
					Calendar c1 = Calendar.getInstance();
					c1.setTime(data.getSendTime());
					c1.add(Calendar.DATE, 7);
					Calendar c2 = Calendar.getInstance();
					if (c2.after(c1)) {
						data.setStatus(AfterSaleStatus.Finished);
						service.save(data);
					}
				}
				page.setNum(page.getNum() + 1);
			}
		} while (result.hasNext());
	}

}
