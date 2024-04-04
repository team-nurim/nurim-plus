package org.nurim.nurim.Controller;

import lombok.RequiredArgsConstructor;
import org.nurim.nurim.domain.dto.community.*;
import org.nurim.nurim.service.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping("/communityCreate")
    @ResponseBody
    public ResponseEntity<CreateCommunityResponse> createCommunity(@RequestBody CreateCommunityRequest request){
        CreateCommunityResponse response = communityService.communityCreate(request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/communityRead/{communityId}")
    @ResponseBody
    public ResponseEntity<ReadCommunityResponse> readCommunity(@PathVariable Long communityId){
        ReadCommunityResponse response = communityService.communityRead(communityId);
        return ResponseEntity.ok().body(response);
    }
    @DeleteMapping("/communityDelete/{communityId}")
    @ResponseBody
    public ResponseEntity<DeleteCommunityResponse> deleteCommunity(@PathVariable Long communityId){
        DeleteCommunityResponse response = communityService.communityDelete(communityId);
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("/communityUpdate/{communityId}")
    @ResponseBody
    public ResponseEntity<UpdateCommunityResponse> updateCommunity(@PathVariable Long communityId, @RequestBody UpdateCommunityRequest request){
        UpdateCommunityResponse response = communityService.communityUpdate(communityId, request);
        return ResponseEntity.ok().body(response);
    }
}
