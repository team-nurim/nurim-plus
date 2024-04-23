package org.nurim.nurim.service;

import jakarta.persistence.criteria.Predicate;
import org.nurim.nurim.domain.entity.MciHousingPolicy;
import org.nurim.nurim.domain.entity.MciIntegratedPolicy;
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
    private final MciHousingPolicyRepository mciHousingPolicyRepository;
    private final org.nurim.nurim.repository.MciIntegratedPolicyRepository mciIntegratedPolicyRepository;

    @Autowired
    public MciHousingPolicyService(MciHousingPolicyRepository mciHousingPolicyRepository,
                                   org.nurim.nurim.repository.MciIntegratedPolicyRepository mciIntegratedPolicyRepository) {
        this.mciHousingPolicyRepository = mciHousingPolicyRepository;
        this.mciIntegratedPolicyRepository = mciIntegratedPolicyRepository;
    }

    @Transactional
    public MciHousingPolicy saveHousingPolicy(MciHousingPolicy policy) {
        return mciHousingPolicyRepository.save(policy);
    }

    public Optional<MciHousingPolicy> findById(Long id) {
        return mciHousingPolicyRepository.findById(id);
    }

    public ResponseEntity<?> findByFilters(String category, String region, String businessClassification, String businessEntity) {
        if (!isValidCategory(category)) {
            log.error("Invalid category specified: " + category);
            return ResponseEntity.badRequest().body("Invalid category specified: " + category);
        }

        try {
            Specification<?> spec = createSpecification(category, region, businessClassification, businessEntity);
            if ("housing".equals(category)) {
                return ResponseEntity.ok(mciHousingPolicyRepository.findAll((Specification<MciHousingPolicy>) spec));
            } else if ("integrated".equals(category)) {
                return ResponseEntity.ok(mciIntegratedPolicyRepository.findAll((Specification<MciIntegratedPolicy>) spec));
            } else {
                log.error("Category not handled: " + category);
                return ResponseEntity.badRequest().body("Category not handled: " + category);
            }
        } catch (Exception e) {
            log.error("Error finding policies: " + e.getMessage(), e);
            throw new RuntimeException("Error processing your request", e);
        }
    }

    private boolean isValidCategory(String category) {
        return "housing".equals(category) || "integrated".equals(category);
    }

    private Specification<?> createSpecification(String category, String region, String businessClassification, String businessEntity) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (region != null && !region.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("region"), region));
                log.info("Filtering by region: " + region); // 로깅 추가
            }
            if (businessClassification != null && !businessClassification.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("businessClassification"), businessClassification));
                log.info("Filtering by businessClassification: " + businessClassification); // 로깅 추가
            }
            if (businessEntity != null && !businessEntity.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("businessEntity"), businessEntity));
                log.info("Filtering by businessEntity: " + businessEntity); // 로깅 추가
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public <MciIntegratedPolicy> List<MciIntegratedPolicy> findByCategoryFilters(String integrated, String region, String businessClassification, String businessEntity) {
        return null;
    }

    // 'integrated' 카테고리에 대한 데이터 조회 메서드 추가
    public List<MciIntegratedPolicy> findByIntegratedFilters(String region, String businessClassification, String businessEntity) {
        Specification<MciIntegratedPolicy> spec = (Specification<MciIntegratedPolicy>) createSpecification("integrated", region, businessClassification, businessEntity);
        return mciIntegratedPolicyRepository.findAll(spec);
    }
}



