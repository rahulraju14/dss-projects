package com.example.test;

import org.springframework.http.MediaType;

import java.util.List;

public class HeaderRequest {

    private List<String> cookies;

    private String xsrfToken;

    private MediaType mediaType;

    public HeaderRequest(List<String> cookies, String xsrfToken, MediaType type) {
        this.cookies = cookies;
        this.xsrfToken = xsrfToken;
        this.mediaType = type;
    }

    public List<String> getCookies() {
        return cookies;
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }

    public String getXsrfToken() {
        return xsrfToken;
    }

    public void setXsrfToken(String xsrfToken) {
        this.xsrfToken = xsrfToken;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
}
