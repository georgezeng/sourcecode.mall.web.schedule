package com.sourcecode.malls.schedule;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sourcecode.malls.domain.order.Order;
import com.sourcecode.malls.dto.query.PageInfo;
import com.sourcecode.malls.dto.query.QueryInfo;
import com.sourcecode.malls.enums.OrderStatus;
import com.sourcecode.malls.schedule.base.AbstractSchedule;
import com.sourcecode.malls.service.impl.OrderService;

@Component
public class CloseOrderSchedule extends AbstractSchedule {
	@Autowired
	private OrderService orderService;

	@Scheduled(cron = "0 * * * * ?")
	@Override
	public void run() {
		super.run();
	}

	@Override
	protected void execute() throws Exception {
		QueryInfo<OrderStatus> queryInfo = new QueryInfo<>();
		queryInfo.setData(OrderStatus.UnPay);
		PageInfo page = new PageInfo();
		page.setOrder("ASC");
		page.setProperty("createTime");
		page.setNum(1);
		page.setSize(1000);
		queryInfo.setPage(page);
		Page<Order> result = null;
		do {
			result = orderService.getOrders(queryInfo);
			if (result.hasContent()) {
				for (Order order : result.getContent()) {
					Calendar c1 = Calendar.getInstance();
					c1.setTime(order.getCreateTime());
					c1.add(Calendar.DATE, 1);
					Calendar c2 = Calendar.getInstance();
					if (c2.after(c1)) {
						order.setStatus(OrderStatus.Closed);
						orderService.save(order);
					}
				}
				page.setNum(page.getNum() + 1);
			}
		} while (result.hasNext());
	}

}
