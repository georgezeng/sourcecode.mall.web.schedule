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

import com.sourcecode.malls.domain.coupon.CouponSetting;
import com.sourcecode.malls.dto.query.QueryInfo;
import com.sourcecode.malls.enums.ClientCouponStatus;
import com.sourcecode.malls.enums.CouponSettingStatus;
import com.sourcecode.malls.repository.jpa.impl.coupon.ClientCouponRepository;
import com.sourcecode.malls.repository.jpa.impl.coupon.CouponSettingRepository;

@Service
@Transactional
public class CouponService {

	@Autowired
	protected CouponSettingRepository couponSettingRepository;

	@Autowired
	protected ClientCouponRepository clientCouponRepository;
	
	@Autowired
	private CacheEvictService cacheEvictService;

	@Transactional(readOnly = true)
	public Page<CouponSetting> getCashCoupons(QueryInfo<CouponSettingStatus> queryInfo) {
		Specification<CouponSetting> spec = new Specification<CouponSetting>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<CouponSetting> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicate = new ArrayList<>();
				predicate.add(criteriaBuilder.equal(root.get("status"), queryInfo.getData()));
				predicate.add(criteriaBuilder.isNotNull(root.get("endDate")));
				return query.where(predicate.toArray(new Predicate[] {})).getRestriction();
			}
		};
		return couponSettingRepository.findAll(spec, queryInfo.getPage().pageable());
	}

	public void updateSoldOut(CouponSetting data) {
		data.setStatus(CouponSettingStatus.SoldOut);
		couponSettingRepository.save(data);
		clientCouponRepository.updateStatus(ClientCouponStatus.Out, data.getId(), ClientCouponStatus.UnUse);
		cacheEvictService.clearClientCouponNums(null);
	}

}
