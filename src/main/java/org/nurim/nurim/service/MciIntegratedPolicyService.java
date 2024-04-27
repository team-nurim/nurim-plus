package org.nurim.nurim.service;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.nurim.nurim.domain.entity.MciIntegratedPolicy;
import org.nurim.nurim.repository.MciIntegratedPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class MciIntegratedPolicyService {
    private final MciIntegratedPolicyRepository repository; // 통합 정책 리포지토리만 주입받습니다.

    @Autowired
    public MciIntegratedPolicyService(MciIntegratedPolicyRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MciIntegratedPolicy save(MciIntegratedPolicy policy) {
        return repository.save(policy);
    }

    public Optional<MciIntegratedPolicy> findById(Long id) {
        return repository.findById(id);
    }

    // 필터링 로직은 통합 정책만 처리
    public ResponseEntity<?> findByIntegratedFilters(String region, String offerType, String businessEntity) {
        Specification<MciIntegratedPolicy> spec = createSpecification(region, offerType, businessEntity);
        return ResponseEntity.ok(repository.findAll(spec));
    }

    // Specification 생성 로직
    private Specification<MciIntegratedPolicy> createSpecification(String region, String offerType, String businessEntity) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (region != null && !region.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("region"), region));
            }
            if (offerType != null && !offerType.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("offerType"), offerType));
            }
            if (businessEntity != null && !businessEntity.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("businessEntity"), businessEntity));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}