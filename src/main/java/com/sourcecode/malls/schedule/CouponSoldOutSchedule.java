package com.sourcecode.malls.schedule;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sourcecode.malls.domain.coupon.CouponSetting;
import com.sourcecode.malls.dto.query.PageInfo;
import com.sourcecode.malls.dto.query.QueryInfo;
import com.sourcecode.malls.enums.CouponSettingStatus;
import com.sourcecode.malls.schedule.base.AbstractSchedule;
import com.sourcecode.malls.service.impl.CouponService;

@Component
public class CouponSoldOutSchedule extends AbstractSchedule {
	@Autowired
	private CouponService couponService;

	@Scheduled(cron = "0 0 0 * * ?")
	@Override
	public void run() {
		super.run();
	}

	@Override
	protected void execute() throws Exception {
		soldOutCashCoupon();
	}

	private void soldOutCashCoupon() {
		QueryInfo<CouponSettingStatus> queryInfo = new QueryInfo<>();
		queryInfo.setData(CouponSettingStatus.PutAway);
		PageInfo page = new PageInfo();
		page.setOrder(Direction.ASC.name());
		page.setProperty("endDate");
		page.setNum(1);
		page.setSize(1000);
		queryInfo.setPage(page);
		Page<CouponSetting> result = null;
		do {
			result = couponService.getCashCoupons(queryInfo);
			if (result.hasContent()) {
				for (CouponSetting data : result.getContent()) {
					Calendar c1 = Calendar.getInstance();
					c1.setTime(data.getEndDate());
					c1.add(Calendar.DATE, 1);
					Calendar c2 = Calendar.getInstance();
					if (c2.after(c1)) {
						couponService.updateSoldOut(data);
					}
				}
				page.setNum(page.getNum() + 1);
			}
		} while (result.hasNext());
	}

}
