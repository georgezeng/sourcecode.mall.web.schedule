package com.sourcecode.malls.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sourcecode.malls.domain.coupon.cash.CashCouponSetting;
import com.sourcecode.malls.dto.query.QueryInfo;
import com.sourcecode.malls.enums.CouponSettingStatus;
import com.sourcecode.malls.repository.jpa.impl.coupon.CashCouponSettingRepository;

@Service
@Transactional
public class CouponService {

	@Autowired
	protected CashCouponSettingRepository cashSettingRepository;

	@Transactional(readOnly = true)
	public Page<CashCouponSetting> getCashCoupons(QueryInfo<CouponSettingStatus> queryInfo) {
		Specification<CashCouponSetting> spec = new Specification<CashCouponSetting>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<CashCouponSetting> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicate = new ArrayList<>();
				predicate.add(criteriaBuilder.equal(root.get("status"), queryInfo.getData()));
				return query.where(predicate.toArray(new Predicate[] {})).getRestriction();
			}
		};
		return cashSettingRepository.findAll(spec, queryInfo.getPage().pageable());
	}

	public void save(CashCouponSetting data) {
		cashSettingRepository.save(data);
	}

}
