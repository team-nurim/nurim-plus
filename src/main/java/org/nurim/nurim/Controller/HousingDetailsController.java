package org.nurim.nurim.Controller;

import org.nurim.nurim.domain.entity.HousingPolicy;
import org.nurim.nurim.service.HousingDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // 모든 오리진 허용
@Controller
@RequestMapping("/api/v1/housingdetails")
public class HousingDetailsController {

    private final HousingDetailsService housingDetailsService;

    @Autowired
    public HousingDetailsController(HousingDetailsService housingDetailsService) {
        this.housingDetailsService = housingDetailsService;
    }

    @ResponseBody
    @GetMapping("/integratedPublicRental/{id}")
    public ResponseEntity<HousingPolicy> getIntegratedPublicRental(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/purchasedRental/{id}")
    public ResponseEntity<HousingPolicy> getPurchasedRental(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/lumpsumleaseRental/{id}")
    public ResponseEntity<HousingPolicy> getLumpsumleaseRental(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/ahappyhouse/{id}")
    public ResponseEntity<HousingPolicy> getAhappyhouse(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/nationalRental/{id}")
    public ResponseEntity<HousingPolicy> getNationalRental(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/permanentPublicRental/{id}")
    public ResponseEntity<HousingPolicy> getPermanentPublicRental(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/alongtermlumpsumlease/{id}")
    public ResponseEntity<HousingPolicy> getAlongtermlumpsumlease(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/publiclysupportedprivatelease/{id}")
    public ResponseEntity<HousingPolicy> getPubliclysupportedprivatelease(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/publicsale/{id}")
    public ResponseEntity<HousingPolicy> getPublicsale(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/newlywedHopeTown/{id}")
    public ResponseEntity<HousingPolicy> getNewlywedHopeTown(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/aspecialsupplyfornewlyweds/{id}")
    public ResponseEntity<HousingPolicy> getAspecialsupplyfornewlyweds(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/thefirstspecialsupplyinoneslife/{id}")
    public ResponseEntity<HousingPolicy> getThefirstspecialsupplyinoneslife(@PathVariable Long id) {
        return housingDetailsService.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
