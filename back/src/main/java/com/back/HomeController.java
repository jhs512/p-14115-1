package com.back;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class HomeController {

    private static final String VISIT_COUNT_KEY = "visitCount";

    @Value("${custom.site.frontUrl}")
    private String frontUrl;

    @Value("${custom.site.backUrl}")
    private String backUrl;

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String main(HttpSession session) throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();

        // main 진입할 때마다 세션별 visitCount 증가
        int visitCount = Optional.ofNullable(session.getAttribute(VISIT_COUNT_KEY))
                .filter(Integer.class::isInstance)
                .map(Integer.class::cast)
                .orElse(0) + 1;

        session.setAttribute(VISIT_COUNT_KEY, visitCount);

        return """
                <h1>API 서버</h1>
                <p>Host Name: %s</p>
                <p>Host Address: %s</p>
                <hr/>
                <p>Front URL: %s</p>
                <p>Back URL: %s</p>
                <hr/>
                <p>Session ID: %s</p>
                <p>visitCount: %d</p>
                
                <div>
                    <a href="/session">세션 확인(JSON)</a>
                </div>
                <div>
                    <a href="/session/clear">세션 클리어</a>
                </div>
                """.formatted(
                localHost.getHostName(),
                localHost.getHostAddress(),
                frontUrl,
                backUrl,
                session.getId(),
                visitCount
        );
    }

    @GetMapping("/session")
    public Map<String, Object> session(HttpSession session) {
        // Enumeration -> Stream 로 변환해서 모던하게 Map 생성
        return Collections.list(session.getAttributeNames()).stream()
                .collect(Collectors.toMap(
                        name -> name,
                        session::getAttribute,
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
    }

    @GetMapping("/session/clear")
    public Map<String, Object> clearSession(HttpSession session) {
        // 가장 깔끔한 방식: 세션 무효화 (JSESSIONID 쿠키는 남아있어도 서버 세션은 끊김)
        session.invalidate();

        return Map.of(
                "ok", true,
                "message", "세션이 무효화되었습니다. 다음 요청부터 새 세션이 생성될 수 있습니다."
        );
    }
}