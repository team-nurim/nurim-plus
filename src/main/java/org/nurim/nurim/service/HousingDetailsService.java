package org.nurim.nurim.service;

import org.nurim.nurim.domain.entity.HousingPolicy;
import org.nurim.nurim.repository.HousingPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HousingDetailsService {

    private final HousingPolicyRepository housingPolicyRepository;

    @Autowired
    public HousingDetailsService(HousingPolicyRepository housingPolicyRepository) {
        this.housingPolicyRepository = housingPolicyRepository;
    }

    // 상세 페이지에 특화된 메소드 구현
    public Optional<HousingPolicy> findDetailsById(Long id) {
        return housingPolicyRepository.findById(id);

    }
}