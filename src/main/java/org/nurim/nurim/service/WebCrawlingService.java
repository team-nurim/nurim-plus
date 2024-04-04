package org.nurim.nurim.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nurim.nurim.domain.entity.EligibilityForAHappyHome;
import org.nurim.nurim.domain.entity.TextContent;
import org.nurim.nurim.repository.EligibilityForAHappyHomeRepository;
import org.nurim.nurim.repository.HtmlTableRepository;
import org.nurim.nurim.repository.TextContentRepository;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

//@Service
//public class WebCrawlingService {
//
//    public String crawl(String url) {
//        try {
//            Document doc = Jsoup.connect(url).get();
//            // 웹 페이지의 <body> 내용과 타이틀을 추출
//            String bodyText = doc.body().text(); // <body> 태그 내의 전체 텍스트를 반환
//            String title = doc.title(); // 웹 페이지의 타이틀을 반환
//
//            // <body> 내용과 타이틀을 결합하여 반환
//            return "Title: " + title + "\nBody: " + bodyText;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Crawling failed";
//        }
//    }
//
//    public String crawlWithSeleniumAndJsoup(String url) {
//        String pageSource = getPageSourceWithSelenium(url);
//        // Jsoup을 사용하여 페이지 소스로부터 데이터 추출
//        return parseHtmlWithJsoup(pageSource);
//    }
//
//    private String getPageSourceWithSelenium(String url) {
//        System.setProperty("webdriver.chrome.driver", "C:\\Users\\YYK\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
//        ChromeOptions options = new ChromeOptions();
//        //options.addArguments("--headless"); // 브라우저를 띄우지 않는 옵션
//        WebDriver driver = new ChromeDriver(options);
//        try {
//            driver.get(url);
//            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
//                    ExpectedConditions.presenceOfElementLocated(By.id("content"))); // 페이지 로드 대기
//
//            System.out.println("Page title is: " + driver.getTitle());
//            return driver.getPageSource();
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace(); // 오류 메시지 출력
//            return "Crawling failed";
//
//        } finally {
//            driver.quit();
//        }
//    }
//
//    private String parseHtmlWithJsoup(String html) {
//        Document doc = Jsoup.parse(html);
//        return doc.title();
//    }
//}

@Service
public class WebCrawlingService {

    private static final Logger log = LoggerFactory.getLogger(WebCrawlingService.class);

    @Autowired
    private HtmlTableRepository htmlTableRepository;
    @Autowired
    private TextContentRepository textContentRepository;
    @Autowired
    private EligibilityForAHappyHomeRepository eligibilityForAHappyHomeRepository;

    public void crawlAndSaveContentFromUrl(String url) {
        WebDriver driver = initializeWebDriver(); // WebDriver 초기화
        try {
            //WebDriverManager.chromedriver().setup(); // WebDriverManager를 사용하여 ChromeDriver 자동 설정
            //ChromeOptions options = new ChromeOptions();
            //options.addArguments("--disable-web-security"); // 옵션 설정
            //driver = new ChromeDriver(options);
            driver.get(url);

            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("content")));

            String pageSource = driver.getPageSource();
            saveAllHtmlContent(pageSource);
            //saveAllTableContent(pageSource);
            parseAndSaveEligibility(pageSource);
        } catch (Exception e) {
            log.error("Crawling and saving content failed for URL: {}", url, e);
        } finally {
            if (driver != null) {
                driver.quit(); // WebDriver 종료
            }
        }
    }

    private WebDriver initializeWebDriver() {
        // Chrome WebDriver 경로 설정 및 옵션 설정
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\YYK\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security"); // 크로스 도메인 보안 비활성화
        return new ChromeDriver(options);
    }

    private void saveAllHtmlContent(String htmlContent) {
        // HTML 문서를 파싱하여 모든 텍스트 내용을 저장합니다.
        Document doc = Jsoup.parse(htmlContent);
        Elements allElements = doc.getAllElements();
        for (Element element : allElements) {
            String text = element.ownText().trim();
            if (!text.isEmpty()) {
                // 빈 텍스트가 아닌 경우, 저장 처리
                TextContent textContent = new TextContent();
                textContent.setContent(text);
                textContentRepository.save(textContent);
            }
        }
    }

//    private void saveAllTableContent(String htmlContent) {
//        // HTML 문서를 파싱하여 <table> 내용을 저장합니다.
//        Document doc = Jsoup.parse(htmlContent);
//        Elements tables = doc.select("table");
//        for (Element table : tables) {
//            StringBuilder tableText = new StringBuilder();
//            Elements rows = table.select("tr");
//            for (Element row : rows) {
//                Elements cells = row.select("th, td");
//                for (Element cell : cells) {
//                    String text = cell.text();
//                    tableText.append(text).append(" | ");
//                }
//                tableText.append("\n");
//            }
//
//            String contentToSave = tableText.toString();
//            // MySQL LONGTEXT 최대 크기는 약 4GB입니다. 여기서는 예시로 1MB (약 1,000,000 문자)로 제한합니다.
//            final int MAX_LENGTH = 1_000_000; // 적절한 최대 길이로 설정하세요.
//            if (contentToSave.length() > MAX_LENGTH) {
//                // 데이터의 길이가 최대 길이를 초과하는 경우, 초과분을 잘라냅니다.
//                contentToSave = contentToSave.substring(0, MAX_LENGTH);
//            }
//
//            try {
//                HtmlTable htmlTable = new HtmlTable();
//                htmlTable.setContent(contentToSave);
//                htmlTableRepository.save(htmlTable);
//            } catch (Exception e) {
//                log.error("Error saving HTML table content. Content size: {}", contentToSave.length(), e);
//                // Consider handling the error, maybe retrying with a smaller size or logging the specific content for review.
//            }
//        }
//    }

    private void parseAndSaveEligibility(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        Elements rows = doc.select("table tbody tr");

        String currentCategory = ""; // 현재 카테고리(계층)를 추적하기 위한 변수
        String currentIncomeCriteria = ""; // 현재 소득 기준을 추적하기 위한 변수

        for (Element row : rows) {
            Elements cells = row.select("th, td");

            if (!cells.isEmpty()) {
                String category = !cells.select("th").isEmpty() ? cells.select("th").get(0).text() : currentCategory;
                String subCategory = cells.size() > 1 ? cells.get(1).text() : "";
                String qualification = cells.size() > 2 ? cells.get(2).html() : ""; // HTML로 추출하여 내부 구조를 유지
                String incomeCriteria = cells.size() > 3 ? cells.get(3).text() : currentIncomeCriteria;

                if (!category.isEmpty() && !category.equals(currentCategory)) {
                    currentCategory = category;
                }

                if (!incomeCriteria.isEmpty()) {
                    currentIncomeCriteria = incomeCriteria;
                }

                if (!subCategory.isEmpty()) {
                    EligibilityForAHappyHome eligibility = new EligibilityForAHappyHome();
                    eligibility.setClassification(currentCategory);
                    eligibility.setDetailedClassification(subCategory);
                    eligibility.setQualification(qualification);
                    eligibility.setIncomeCriteria(incomeCriteria);

                    eligibilityForAHappyHomeRepository.save(eligibility); // 레포지토리를 통해 엔티티 저장
                }
            }
        }
    }
    }





