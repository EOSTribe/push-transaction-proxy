package api.proxy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:global.properties")
@ConfigurationProperties
public class Properties {

    @Value("${apiserver}")
    private String apiserver;

    @Value("${token}")
    private String token;

    @Value("${numretry}")
    private int numretry;


    public String getApiServer() {
        return apiserver;
    }

    public String getToken() { return token; }

    public int getNumRetry() { return numretry; }

}
