package com.example.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import model.DataTableRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    HttpSession session;

    @GetMapping("/test")
    public Object test() {
        RestTemplate restTemplate = new RestTemplate();
        String lasUrl = "http://lasdevapp.ov.otto.de/las-frontend/login";
        System.out.println("--- Calling token url ---");
        ResponseEntity<String> tokenResponse = restTemplate.getForEntity(lasUrl, String.class);
        if (tokenResponse.getHeaders().containsKey("Set-Cookie")) {
            List<String> cookies = tokenResponse.getHeaders().get("Set-Cookie");
            System.out.println("-- Token Url headers -- " + cookies);

            String xsrfToken = cookies.stream().filter(x -> x.startsWith("XSRF-TOKEN")).findFirst().get().split(";")[0].split("=")[1];
            String sessionId = cookies.stream().filter(x -> x.startsWith("JSESSIONID")).findFirst().get().split(";")[0].split("=")[1];
            System.out.println("-- Extracted SessionId and Xsrf Token -- " + sessionId + " " + xsrfToken);

            MultiValueMap<String, String> payLoad = new LinkedMultiValueMap<>();
            payLoad.add("ssoId", "admin");
            payLoad.add("password", "LasdevAdmin@2020");
            payLoad.add("_csrf", xsrfToken);

            String sessionCookie = "JSESSIONID=" + sessionId;
            String xsrfTokenCookie = "X-XSRF-TOKEN=" + xsrfToken;

            restTemplate.getInterceptors().add(new RestTemplateInterceptor(new HeaderRequest(List.of(sessionCookie, xsrfTokenCookie), xsrfToken, MediaType.APPLICATION_FORM_URLENCODED)));
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(payLoad, null);

            System.out.println("-- Calling Login API -- ");
            ResponseEntity<String> loginResponse = restTemplate
                    .exchange(lasUrl, HttpMethod.POST, entity, String.class);

            System.out.println(loginResponse.getStatusCode());
            System.out.println("-- Login API headers -- " + loginResponse.getHeaders());

            if (loginResponse.getHeaders().containsKey("Set-Cookie")) {
                List<String> loginResponseCookies = loginResponse.getHeaders().get("Set-Cookie");
                System.out.println("-- login API Cookies --" + loginResponseCookies);
                String updatesSessionId = loginResponseCookies.stream().filter(x -> x.startsWith("JSESSIONID")).findFirst().get().split(";")[0].split("=")[1];
                String updateSessionCookies = "JSESSIONID=" + updatesSessionId;
                System.out.println("-- Extracted Updated SessionId -- : " + updatesSessionId);

                List<String> loginHeaderCookies = new ArrayList<>();
                loginHeaderCookies.add(updateSessionCookies);
                loginHeaderCookies.add(xsrfTokenCookie);

                System.out.println("-- Calling las-frontend for generating CSRF token --");

                restTemplate.getInterceptors().add(new RestTemplateInterceptor(new HeaderRequest(loginHeaderCookies, xsrfToken, MediaType.APPLICATION_JSON)));

                String csrfTokenUrl = "http://lasdevapp.ov.otto.de/las-frontend/";
                ResponseEntity<String> getCsrfTokenResponse = restTemplate.getForEntity(csrfTokenUrl, String.class);

                System.out.println("-- las-frontend headers --:" + getCsrfTokenResponse.getHeaders());

                if (getCsrfTokenResponse.getHeaders().containsKey("Set-Cookie")) {
                    List<String> csrfTokenCookiesList = getCsrfTokenResponse.getHeaders().get("Set-Cookie");
                    String updatedCsrfToken = csrfTokenCookiesList.stream().filter(x -> x.startsWith("XSRF-TOKEN")).findFirst().get().split(";")[0].split("=")[1];
                    String csrfTokenCookie = "X-XSRF-TOKEN=" + updatedCsrfToken;
                    System.out.println("--- Extracted Updated CSRF token --- : " + updatedCsrfToken);

                    System.out.println("-- Calling License Total count API --");
                    List<String> licenceCookies = new ArrayList<>();
                    licenceCookies.add(updateSessionCookies);
                    licenceCookies.add(csrfTokenCookie);

                    restTemplate.getInterceptors().add(new RestTemplateInterceptor(new HeaderRequest(licenceCookies, updatedCsrfToken, MediaType.APPLICATION_JSON)));

                    String licenseCountUrl = "http://lasdevapp.ov.otto.de/las-frontend/rest/items/withoutLicenseTotal";
                    ResponseEntity<String> licenseCountResponse = restTemplate.getForEntity(licenseCountUrl, String.class);

                    System.out.println(licenseCountResponse.getStatusCode());
                    System.out.println("-- License Headers -- " + licenseCountResponse.getHeaders());
                    System.out.println("-- License API Response --" + licenseCountResponse.getBody());

                    System.out.println("-- Calling Items API -- ");
                    String itemsDataUrl = "http://lasdevapp.ov.otto.de/las-frontend/rest/items/paging";

                    List<String> itemsCookies = new ArrayList<>();
                    itemsCookies.add(updateSessionCookies);
                    itemsCookies.add(csrfTokenCookie);

                    String itemsPayloadPath = "json/itemsPayload.json";
                    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(itemsPayloadPath);
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        DataTableRequest itemsPayload = objectMapper.readValue(inputStream, DataTableRequest.class);
                        restTemplate.getInterceptors().add(new RestTemplateInterceptor(new HeaderRequest(itemsCookies, updatedCsrfToken, MediaType.APPLICATION_JSON)));

                        HttpHeaders itemsHeader = new HttpHeaders();
                        itemsHeader.setContentType(MediaType.APPLICATION_JSON);
                        HttpEntity<DataTableRequest> itemsEntity = new HttpEntity<>(itemsPayload, null);

                        ResponseEntity<Object> itemDataResponse = restTemplate
                                .postForEntity(itemsDataUrl, itemsEntity, Object.class);


                        System.out.println("-- Item Data Response --");
                        System.out.println(itemDataResponse.getBody());
                        return itemDataResponse.getBody();
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }


                }
            }

        }
        return "welcome";
    }

    public void login() {
        RestTemplate restTemplate = new RestTemplate();
        String lasUrl = "http://lasdevapp.ov.otto.de/las-frontend/login";
        System.out.println("--- Calling token url ---");
        ResponseEntity<String> tokenResponse = restTemplate.getForEntity(lasUrl, String.class);
        if (tokenResponse.getHeaders().containsKey("Set-Cookie")) {
            List<String> cookies = tokenResponse.getHeaders().get("Set-Cookie");
            System.out.println("-- Token Url headers -- " + cookies);

            String xsrfToken = cookies.stream().filter(x -> x.startsWith("XSRF-TOKEN")).findFirst().get().split(";")[0].split("=")[1];
            String sessionId = cookies.stream().filter(x -> x.startsWith("JSESSIONID")).findFirst().get().split(";")[0].split("=")[1];
            System.out.println("-- Extracted SessionId and Xsrf Token -- " + sessionId + " " + xsrfToken);

            MultiValueMap<String, String> payLoad = new LinkedMultiValueMap<>();
            payLoad.add("ssoId", "admin");
            payLoad.add("password", "LasdevAdmin@2020");
            payLoad.add("_csrf", xsrfToken);

            String sessionCookie = "JSESSIONID=" + sessionId;
            String xsrfTokenCookie = "X-XSRF-TOKEN=" + xsrfToken;

            restTemplate.getInterceptors().add(new RestTemplateInterceptor(new HeaderRequest(List.of(sessionCookie, xsrfTokenCookie), xsrfToken, MediaType.APPLICATION_FORM_URLENCODED)));
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(payLoad, null);

            System.out.println("-- Calling Login API -- ");
            ResponseEntity<String> loginResponse = restTemplate
                    .exchange(lasUrl, HttpMethod.POST, entity, String.class);

            System.out.println(loginResponse.getStatusCode());
            System.out.println("-- Login API headers -- " + loginResponse.getHeaders());

            if (loginResponse.getHeaders().containsKey("Set-Cookie")) {
                List<String> loginResponseCookies = loginResponse.getHeaders().get("Set-Cookie");
                System.out.println("-- login API Cookies --" + loginResponseCookies);
                String updatesSessionId = loginResponseCookies.stream().filter(x -> x.startsWith("JSESSIONID")).findFirst().get().split(";")[0].split("=")[1];
                String updateSessionCookies = "JSESSIONID=" + updatesSessionId;
                System.out.println("-- Extracted Updated SessionId -- : " + updatesSessionId);

                List<String> loginHeaderCookies = new ArrayList<>();
                loginHeaderCookies.add(updateSessionCookies);
                loginHeaderCookies.add(xsrfTokenCookie);

                System.out.println("-- Calling las-frontend for generating CSRF token --");

                restTemplate.getInterceptors().add(new RestTemplateInterceptor(new HeaderRequest(loginHeaderCookies, xsrfToken, MediaType.APPLICATION_JSON)));

                String csrfTokenUrl = "http://lasdevapp.ov.otto.de/las-frontend/";
                ResponseEntity<String> getCsrfTokenResponse = restTemplate.getForEntity(csrfTokenUrl, String.class);

                System.out.println("-- las-frontend headers --:" + getCsrfTokenResponse.getHeaders());

                if (getCsrfTokenResponse.getHeaders().containsKey("Set-Cookie")) {
                    List<String> csrfTokenCookiesList = getCsrfTokenResponse.getHeaders().get("Set-Cookie");
                    String updatedCsrfToken = csrfTokenCookiesList.stream().filter(x -> x.startsWith("XSRF-TOKEN")).findFirst().get().split(";")[0].split("=")[1];
                    String csrfTokenCookie = "X-XSRF-TOKEN=" + updatedCsrfToken;
                    System.out.println("--- Extracted Updated CSRF token --- : " + updatedCsrfToken);
                    session.setAttribute("sessionId", updatesSessionId);
                    session.setAttribute("csrfToken", updatedCsrfToken);
                }
            }
        }
    }


    public Object getItems(boolean updateToken) throws IOException {
        try {
            String updateSessionCookies = "JSESSIONID=" + session.getAttribute("sessionId");
            String csrfTokenCookie = "X-XSRF-TOKEN=" + (updateToken ? "aa-22333ddddd" : session.getAttribute("csrfToken"));
            RestTemplate restTemplate = new RestTemplate();
            String itemsDataUrl = "http://lasdevapp.ov.otto.de/las-frontend/rest/items/paging";

            List<String> itemsCookies = new ArrayList<>();
            itemsCookies.add(updateSessionCookies);
            itemsCookies.add(csrfTokenCookie);

            String itemsPayloadPath = "json/itemsPayload.json";
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(itemsPayloadPath);
            ObjectMapper objectMapper = new ObjectMapper();
            DataTableRequest itemsPayload = objectMapper.readValue(inputStream, DataTableRequest.class);
            restTemplate.getInterceptors().add(new RestTemplateInterceptor(new HeaderRequest(itemsCookies, updateToken ? "ddxa23" : (String) session.getAttribute("csrfToken"), MediaType.APPLICATION_JSON)));

            HttpHeaders itemsHeader = new HttpHeaders();
            itemsHeader.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<DataTableRequest> itemsEntity = new HttpEntity<>(itemsPayload, null);

            ResponseEntity<Object> itemDataResponse = restTemplate
                    .postForEntity(itemsDataUrl, itemsEntity, Object.class);


            System.out.println("-- Item Data Response --");
            System.out.println(itemDataResponse.getBody());
        } catch (Exception ex) {
            System.out.println(ex);
            throw ex;
        }
        return null;
    }

    @GetMapping("/las")
    public String lasApi(){
        try {
            RetryTemplate retryTemplate = new RetryTemplate();
            login();
            retryTemplate.execute(retryContext -> {
                System.out.println("-- Retry Count -- :" + retryContext.getRetryCount());
                if(retryContext.getRetryCount() > 0){
                    return getItems(false);
                }
                return getItems(true);
            });
        } catch (Exception ex) {
        }
        return "--- Welcome ---";
    }
}
