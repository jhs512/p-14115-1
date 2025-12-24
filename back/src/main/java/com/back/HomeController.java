package com.back;

import com.back.domain.member.member.service.AuthTokenService;
import com.back.global.rq.Rq;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private static final String VISIT_COUNT_KEY = "visitCount";
    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;
    private final Rq rq;

    @Value("${custom.site.frontUrl}")
    private String frontUrl;

    @Value("${custom.site.backUrl}")
    private String backUrl;

    @Value("${custom.site.name}")
    private String siteName;

    @Value("${custom.jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${custom.accessToken.expirationSeconds}")
    private int accessTokenExpirationSeconds;

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String main(HttpSession session) throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();

        int visitCount = Optional.ofNullable(session.getAttribute(VISIT_COUNT_KEY))
                .filter(Integer.class::isInstance)
                .map(Integer.class::cast)
                .orElse(0) + 1;

        session.setAttribute(VISIT_COUNT_KEY, visitCount);

        return """
                <h1>API 서버</h1>
                <p>Host Name: %s</p>
                <p>Host Address: %s</p>
                <hr />
                <p>Site Name: %s</p>
                <p>Front URL: %s</p>
                <p>Back URL: %s</p>
                <hr />
                <p>JWT Secret Key: %s</p>
                <p>Access Token Expiration (seconds): %d</p>
                <hr />
                <p>Session ID: %s</p>
                <p>visitCount: %d</p>
                
                <div>
                    <a href="/session">세션 확인(JSON)</a>
                </div>
                <div>
                    <a href="/session/clear">세션 클리어</a>
                </div>
                """
                .formatted(
                        localHost.getHostName(),
                        localHost.getHostAddress(),
                        siteName,
                        frontUrl,
                        backUrl,
                        jwtSecretKey,
                        accessTokenExpirationSeconds,
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
        session.invalidate();

        return Map.of(
                "ok", true,
                "message", "세션이 무효화되었습니다. 다음 요청부터 새 세션이 생성될 수 있습니다."
        );
    }

    @GetMapping("/members")
    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    @GetMapping("/members/{id}")
    public Member getMember(@PathVariable int id) {
        return memberRepository.findById(id).get();
    }

    @GetMapping("/members/me")
    public Member getMe() {
        String accessToken = rq.getCookieValue("accessToken", "");

        if (accessToken == null) throw new NoSuchElementException("Access token not found in cookies.");

        Map<String, Object> authPayload = authTokenService.payload(accessToken);

        int id = (int) authPayload.get("id");

        return memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found: " + id));
    }

    @GetMapping("/members/{id}/auth/generate")
    public String genMemberAccessToken(@PathVariable int id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found: " + id));

        String accessToken = authTokenService.genAccessToken(member);
        rq.setCookie("accessToken", accessToken);

        return "OK";
    }
}