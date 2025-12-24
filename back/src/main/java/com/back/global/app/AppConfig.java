package com.back.global.app;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Getter
    private static String cookieDomain;
    @Getter
    private static String frontUrl;
    @Getter
    private static String backUrl;
    @Getter
    private static String siteName;

    @Value("${custom.site.cookieDomain}")
    public void setCookieDomain(String cookieDomain) {
        AppConfig.cookieDomain = cookieDomain;
    }

    @Value("${custom.site.frontUrl}")
    public void setFrontUrl(String frontUrl) {
        AppConfig.frontUrl = frontUrl;
    }

    @Value("${custom.site.backUrl}")
    public void setBackUrl(String backUrl) {
        AppConfig.backUrl = backUrl;
    }

    @Value("${custom.site.name}")
    public void setSiteName(String siteName) {
        AppConfig.siteName = siteName;
    }
}
