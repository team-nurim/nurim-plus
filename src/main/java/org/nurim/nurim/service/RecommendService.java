package org.nurim.nurim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.policy.ReadChildCareSearchResponse;
import org.nurim.nurim.domain.dto.policy.ReadMciHousingSearchResponse;
import org.nurim.nurim.domain.dto.policy.ReadMciIntegratedSearchResponse;
import org.nurim.nurim.domain.entity.MciHousingPolicy;
import org.nurim.nurim.domain.entity.MciIntegratedPolicy;
import org.nurim.nurim.domain.entity.api.ChildCare;
import org.nurim.nurim.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class RecommendService {

    private final ChildCareRepository childCareRepository;
    private final MciHousingPolicyRepository mciHousingPolicyRepository;
    private final MciIntegratedPolicyRepository mciIntegratedPolicyRepository;

    public Page<ReadChildCareSearchResponse> searchChildCareByRegionAndKeyword(String region, String keyword, Pageable pageable) {

        Page<ChildCare> searchPage = childCareRepository.findBySigunNmContainingAndPaymentContaining(region, keyword, pageable);

        return searchPage.map(childCare -> {
            Long id = childCare.getId();

            return new ReadChildCareSearchResponse(
                    childCare.getId(),
                    childCare.getSigunNm(),
                    childCare.getBizNm(),
                    childCare.getPayment());
        });
    }

    public Page<ReadChildCareSearchResponse> searchChildCareByRegion(String region, Pageable pageable) {

        Page<ChildCare> searchPage = childCareRepository.findBySigunNmContaining(region, pageable);

        return searchPage.map(childCare -> {
            Long id = childCare.getId();

            return new ReadChildCareSearchResponse(
                    childCare.getId(),
                    childCare.getSigunNm(),
                    childCare.getBizNm(),
                    childCare.getPayment());
        });
    }



    public Page<ReadMciHousingSearchResponse> searchMciHousingByRegionAndKeyword(String region, String keyword, Pageable pageable) {

        Page<MciHousingPolicy> searchPage = mciHousingPolicyRepository.findByRegionAndSupportDetailsContaining(region, keyword, pageable);

        return searchPage.map(mciHousingPolicy -> {
            Long id = mciHousingPolicy.getId();

            return new ReadMciHousingSearchResponse(
                    mciHousingPolicy.getId(),
                    mciHousingPolicy.getRegion(),
                    mciHousingPolicy.getBusinessOverview(),
                    mciHousingPolicy.getSupportDetails(),
                    mciHousingPolicy.getBusinessClassification(),
                    mciHousingPolicy.getIncomeCriteria(),
                    mciHousingPolicy.getAssetCriteria(),
                    mciHousingPolicy.getMarriageCriteria(),
                    mciHousingPolicy.getBusinessEntity(),
                    mciHousingPolicy.getWebsite());
        });
    }

    public Page<ReadMciHousingSearchResponse> searchMciHousingByRegion(String region, Pageable pageable) {

        Page<MciHousingPolicy> searchPage = mciHousingPolicyRepository.findByRegion(region, pageable);

        return searchPage.map(mciHousingPolicy -> {
            Long id = mciHousingPolicy.getId();

            return new ReadMciHousingSearchResponse(
                    mciHousingPolicy.getId(),
                    mciHousingPolicy.getRegion(),
                    mciHousingPolicy.getBusinessOverview(),
                    mciHousingPolicy.getSupportDetails(),
                    mciHousingPolicy.getBusinessClassification(),
                    mciHousingPolicy.getIncomeCriteria(),
                    mciHousingPolicy.getAssetCriteria(),
                    mciHousingPolicy.getMarriageCriteria(),
                    mciHousingPolicy.getBusinessEntity(),
                    mciHousingPolicy.getWebsite());
        });
    }



    public Page<ReadMciIntegratedSearchResponse> searchMciIntegratedByRegionAndKeyword(String region, String keyword, Pageable pageable) {

        Page<MciIntegratedPolicy> searchPage = mciIntegratedPolicyRepository.findByRegionAndSupportDetailsContaining(region, keyword, pageable);

        return searchPage.map(mciIntegratedPolicy -> {
            Long id = mciIntegratedPolicy.getId();

            return new ReadMciIntegratedSearchResponse(
                    mciIntegratedPolicy.getId(),
                    mciIntegratedPolicy.getOfferType(),
                    mciIntegratedPolicy.getPaymentAmount(),
                    mciIntegratedPolicy.getSupportTarget(),
                    mciIntegratedPolicy.getRegion(),
                    mciIntegratedPolicy.getBusinessOverview(),
                    mciIntegratedPolicy.getSupportDetails(),
                    mciIntegratedPolicy.getBusinessEntity(),
                    mciIntegratedPolicy.getWebsite());
        });
    }

    public Page<ReadMciIntegratedSearchResponse> searchMciIntegratedByRegion(String region, Pageable pageable) {

        Page<MciIntegratedPolicy> searchPage = mciIntegratedPolicyRepository.findByRegion(region, pageable);

        return searchPage.map(mciIntegratedPolicy -> {
            Long id = mciIntegratedPolicy.getId();

            return new ReadMciIntegratedSearchResponse(
                    mciIntegratedPolicy.getId(),
                    mciIntegratedPolicy.getOfferType(),
                    mciIntegratedPolicy.getPaymentAmount(),
                    mciIntegratedPolicy.getSupportTarget(),
                    mciIntegratedPolicy.getRegion(),
                    mciIntegratedPolicy.getBusinessOverview(),
                    mciIntegratedPolicy.getSupportDetails(),
                    mciIntegratedPolicy.getBusinessEntity(),
                    mciIntegratedPolicy.getWebsite());
        });
    }

}
