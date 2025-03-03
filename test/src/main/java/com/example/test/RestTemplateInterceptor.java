package com.example.test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

   private HeaderRequest headerRequest;

    public RestTemplateInterceptor(HeaderRequest request) {
        this.headerRequest = request;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        System.out.println("-- Rest Interceptor Invoked --");
        if (!StringUtils.isEmpty(headerRequest.getCookies()) && !StringUtils.isEmpty(headerRequest.getXsrfToken())) {
            HttpHeaders headers = request.getHeaders();
            headers.clear();
            if(headerRequest.getMediaType() != null){
                headers.setContentType(headerRequest.getMediaType());
            }
            System.out.println("-- Adding Cookies -- " + headerRequest.getCookies());
            headers.add("X-XSRF-TOKEN", headerRequest.getXsrfToken());
            headers.addAll(HttpHeaders.COOKIE, headerRequest.getCookies());

            System.out.println("-- Request Header Data befor calling API --" + headers);
        }
        return execution.execute(request, body);
    }
}
