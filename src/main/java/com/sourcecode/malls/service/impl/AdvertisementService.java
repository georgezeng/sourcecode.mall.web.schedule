package com.sourcecode.malls.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sourcecode.malls.domain.merchant.AdvertisementSetting;
import com.sourcecode.malls.domain.merchant.Merchant;
import com.sourcecode.malls.repository.jpa.impl.merchant.AdvertisementSettingRepository;
import com.sourcecode.malls.repository.jpa.impl.merchant.MerchantRepository;

@Service
@Transactional
public class AdvertisementService {

	@Autowired
	private AdvertisementSettingRepository repository;

	@Autowired
	private MerchantRepository merchantRepository;

	@Transactional(readOnly = true)
	public void updateStatus(Long merchantId) {
		Date now = new Date();
		Optional<Merchant> merchant = merchantRepository.findById(merchantId);
		if (merchant.isPresent()) {
			List<AdvertisementSetting> list = repository.findAllByMerchant(merchant.get());
			for (AdvertisementSetting setting : list) {
				setting.setEnabled(!setting.getStartTime().after(now) && !now.after(setting.getEndTime()));
			}
			repository.saveAll(list);
		}
	}

}
