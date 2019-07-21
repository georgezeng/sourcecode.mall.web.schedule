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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sourcecode.malls.domain.aftersale.AfterSaleApplication;
import com.sourcecode.malls.dto.aftersale.AfterSaleApplicationDTO;
import com.sourcecode.malls.dto.query.QueryInfo;
import com.sourcecode.malls.repository.jpa.impl.aftersale.AfterSaleApplicationRepository;
import com.sourcecode.malls.service.base.JpaService;

@Service
@Transactional
public class AfterSaleService implements JpaService<AfterSaleApplication, Long> {

	@Autowired
	protected AfterSaleApplicationRepository repository;

	@Transactional(readOnly = true)
	public Page<AfterSaleApplication> getList(QueryInfo<AfterSaleApplicationDTO> queryInfo) {
		Specification<AfterSaleApplication> spec = new Specification<AfterSaleApplication>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<AfterSaleApplication> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicate = new ArrayList<>();
				predicate.add(criteriaBuilder.equal(root.get("status"), queryInfo.getData().getStatus()));
				predicate.add(criteriaBuilder.equal(root.get("type"), queryInfo.getData().getType()));
				return query.where(predicate.toArray(new Predicate[] {})).getRestriction();
			}
		};
		return repository.findAll(spec, queryInfo.getPage().pageable());
	}

	@Override
	public JpaRepository<AfterSaleApplication, Long> getRepository() {
		return repository;
	}

}
