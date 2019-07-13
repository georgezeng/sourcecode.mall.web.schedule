package com.sourcecode.malls.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sourcecode.malls.domain.order.Order;
import com.sourcecode.malls.dto.query.QueryInfo;
import com.sourcecode.malls.enums.OrderStatus;
import com.sourcecode.malls.repository.jpa.impl.order.OrderRepository;
import com.sourcecode.malls.repository.jpa.impl.order.SubOrderRepository;
import com.sourcecode.malls.service.base.JpaService;

@Service
@Transactional
public class OrderService implements JpaService<Order, Long> {

	@Autowired
	protected OrderRepository orderRepository;

	@Autowired
	protected SubOrderRepository subOrderRepository;

	@Transactional(readOnly = true)
	public Page<Order> getOrders(QueryInfo<OrderStatus> queryInfo) {
		Specification<Order> spec = new Specification<Order>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicate = new ArrayList<>();
				predicate.add(criteriaBuilder.equal(root.get("status"), queryInfo.getData()));
				return query.where(predicate.toArray(new Predicate[] {})).getRestriction();
			}
		};
		return orderRepository.findAll(spec, queryInfo.getPage().pageable(Direction.ASC, "createTime"));
	}

	@Override
	public JpaRepository<Order, Long> getRepository() {
		return orderRepository;
	}

}
