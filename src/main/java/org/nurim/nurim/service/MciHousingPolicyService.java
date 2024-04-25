package org.nurim.nurim.service;

import jakarta.persistence.criteria.Predicate;
import org.nurim.nurim.domain.entity.MciHousingPolicy;
import org.nurim.nurim.repository.MciHousingPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MciHousingPolicyService {

    private static final Logger log = LoggerFactory.getLogger(MciHousingPolicyService.class);
    private final MciHousingPolicyRepository repository;

    @Autowired
    public MciHousingPolicyService(MciHousingPolicyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MciHousingPolicy saveHousingPolicy(MciHousingPolicy policy) {
        return repository.save(policy);
    }

    public Optional<MciHousingPolicy> findById(Long id) {
        return repository.findById(id);
    }

    // 필터링 로직은 주거 정책만 처리
    public ResponseEntity<?> findByHousingFilters(String region, String businessClassification, String businessEntity) {
        Specification<MciHousingPolicy> spec = createSpecification(region, businessClassification, businessEntity);
        return ResponseEntity.ok(repository.findAll(spec));
    }

    // Specification 생성 로직
    private Specification<MciHousingPolicy> createSpecification(String region, String businessClassification, String businessEntity) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (region != null && !region.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("region"), region));
            }
            if (businessClassification != null && !businessClassification.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("businessClassification"), businessClassification));
            }
            if (businessEntity != null && !businessEntity.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("businessEntity"), businessEntity));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}



