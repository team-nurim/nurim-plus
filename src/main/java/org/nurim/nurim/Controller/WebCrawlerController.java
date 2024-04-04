package org.nurim.nurim.Controller;

import org.nurim.nurim.service.WebCrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crawler")
public class WebCrawlerController {

    private final WebCrawlingService webCrawlingService;

    @Autowired
    public WebCrawlerController(WebCrawlingService webCrawlingService) {
        this.webCrawlingService = webCrawlingService;
    }

    @GetMapping("/start")
    public String startCrawling() {
        String url = "https://www.myhome.go.kr/hws/portal/cont/selectYouthPolicyHappyView.do";
        webCrawlingService.crawlAndSaveContentFromUrl(url);
        return "Crawling and saving process initiated successfully.";
    }
}

