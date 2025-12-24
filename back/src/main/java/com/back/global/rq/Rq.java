package com.back.global.rq;

import com.back.global.app.AppConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Arrays;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;

    // Header 관련
    public String getHeader(String name, String defaultValue) {
        String value = req.getHeader(name);
        return value != null ? value : defaultValue;
    }

    public void setHeader(String name, String value) {
        resp.setHeader(name, value);
    }

    // Cookie 조회
    public String getCookieValue(String name, String defaultValue) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return defaultValue;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst()
                .orElse(defaultValue);
    }

    // Cookie 생성/수정
    public void setCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value != null ? value : "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain(AppConfig.getCookieDomain());
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");

        if (value == null || value.isBlank()) {
            cookie.setMaxAge(0);
        } else {
            cookie.setMaxAge(60 * 60 * 24 * 365); // 1년
        }

        resp.addCookie(cookie);
    }

    // Cookie 삭제
    public void deleteCookie(String name) {
        setCookie(name, null);
    }
}
