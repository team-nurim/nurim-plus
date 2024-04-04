package org.nurim.nurim.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nurim.nurim.domain.entity.PolicyData;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

@RestController
public class PolicyController {

    private static final Logger LOGGER = Logger.getLogger(PolicyController.class.getName());
    @GetMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE) // JSON 형식의 응답을 요청하도록 설정
    @ResponseBody
    public PolicyData getPolicyData() throws IOException {
        StringBuilder result = new StringBuilder();
        PolicyData policyData = new PolicyData();


        HttpURLConnection urlConnection = null;
        try {
            String urlStr = "https://api.odcloud.kr/api/15101096/v1/uddi:d55c5365-ae81-4b28-b066-d401b94e11f3?";
            String serviceKey = "bcIOgcrjmugupsh8mGOMk51+n5bEixHJbG5Fxe8zi8In0xfTao6KEOlpwolnwPhPOz7yhAuMPcTZNNpv1RrgmQ==";
            //String serviceKey = "bcIOgcrjmugupsh8mGOMk51%2Bn5bEixHJbG5Fxe8zi8In0xfTao6KEOlpwolnwPhPOz7yhAuMPcTZNNpv1RrgmQ%3D%3D";

            int pageNo = 0;
            int numOfRows = 20;

            String apiUrl = urlStr + serviceKey + "&PageNo=" + pageNo + "&numOfRows=" + numOfRows;
            URL url = new URL(apiUrl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            // HTTP 응답 코드 처리 코드 작성
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 정상적인 응답 처리
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String returnLine;
                while ((returnLine = br.readLine()) != null) {
                    result.append(returnLine + "\n\r");
                }
                br.close(); // 사용이 끝난 리소스를 닫아줍니다.
                // 받아온 JSON 데이터를 PolicyData 객체로 매핑
                ObjectMapper objectMapper = new ObjectMapper();
                policyData = objectMapper.readValue(result.toString(), PolicyData.class);
            } else {
                // 오류 응답 처리
                LOGGER.warning("HTTP 오류 응답: " + urlConnection.getResponseCode());
                // 필요한 경우, 오류에 대한 추가적인 처리를 수행할 수 있습니다.
            }
        } catch (IOException e) {
            LOGGER.severe("Error occurred while making API request: " + e.getMessage());
            e.printStackTrace();
            // 예외 처리 코드
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect(); // 사용이 끝난 Connection을 닫아줍니다.
            }
        }

        return policyData;
    }
}