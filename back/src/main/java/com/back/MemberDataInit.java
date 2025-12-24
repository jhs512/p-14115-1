package com.back;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class MemberDataInit {
    private final MemberDataInit self;
    private final MemberRepository memberRepository;

    public MemberDataInit(
            @Lazy MemberDataInit self,
            MemberRepository memberRepository
    ) {
        this.self = self;
        this.memberRepository = memberRepository;
    }

    @Bean
    public ApplicationRunner memberDataInitApplicationRunner() {
        return args -> {
            self.makeBaseMembers();
        };
    }

    @Transactional
    public void makeBaseMembers() {
        if (memberRepository.count() > 0) return;

        Member systemMember = new Member("system", "1234", "시스템");
        memberRepository.save(systemMember);

        Member holdingMember = new Member("holding", "1234", "홀딩");
        memberRepository.save(holdingMember);

        Member adminMember = new Member("admin", "1234", "관리자");
        memberRepository.save(adminMember);

        Member user1Member = new Member("user1", "1234", "유저1");
        memberRepository.save(user1Member);

        Member user2Member = new Member("user2", "1234", "유저2");
        memberRepository.save(user2Member);

        Member user3Member = new Member("user3", "1234", "유저3");
        memberRepository.save(user3Member);
    }
}