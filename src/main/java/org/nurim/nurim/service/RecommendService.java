package org.nurim.nurim.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nurim.nurim.domain.dto.community.ReadAllCommunityResponse;
import org.nurim.nurim.domain.dto.community.ReadSearchResponse;
import org.nurim.nurim.repository.CommunityRepository;
import org.nurim.nurim.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class RecommendService {

    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;



}
