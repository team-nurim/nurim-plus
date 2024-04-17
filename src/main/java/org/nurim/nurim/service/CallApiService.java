package org.nurim.nurim.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.nurim.nurim.domain.entity.api.ChildCare;
import org.nurim.nurim.repository.PolicyRepositroy;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CallApiService {
    private static final Logger logger = LoggerFactory.getLogger(CallApiService.class);
    private final WebClient webClient;
    private final PolicyRepositroy policyRepositroy;

    @Autowired
    public CallApiService(WebClient.Builder webClientBuilder, PolicyRepositroy policyRepositroy) {
        this.webClient=webClientBuilder.baseUrl("https://openapi.gg.go.kr/").build();
        this.policyRepositroy=policyRepositroy; //생성자 주입
    }

    public void callApi(String apiKey, String type, Integer plndex, Integer pSize) {
        String apiUrl= "BrthspprtMnychldprvtrtbz?key={apiKey}&type={type}&plndex={plndex}&pSize={pSize}";
        String responseData = webClient.get()
                .uri(apiUrl, apiKey, type, plndex, pSize)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        parseJson(responseData);
    }

    public void parseJson(String jsonData){

        // New
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject json = (JSONObject) jsonParser.parse(jsonData);
            JSONArray dataArray = (JSONArray) json.get("BrthspprtMnychldprvtrtbz");
            JSONObject dataObject = (JSONObject) dataArray.get(1);
            JSONArray rows = (JSONArray) dataObject.get("row");

            for (Object row : rows) {
                JSONObject rowObj = (JSONObject) row;
                String sigunNm = (String) rowObj.get("SIGUN_NM");
                String bizNm= (String) rowObj.get("BIZ_NM");
                String payment=(String) rowObj.get("PAYMNT_STD_CONT");
                System.out.print("시군명: ");
                System.out.println("사업명: ");
                System.out.println("목적은");
                // db저장
                ChildCare entity = new ChildCare();
                entity.setSigunNm(sigunNm);
                entity.setBizNm(bizNm);
                entity.setPayment(payment);
                policyRepositroy.save(entity);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);

        }
    }
}