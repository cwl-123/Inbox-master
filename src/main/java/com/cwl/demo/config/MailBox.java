package com.cwl.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:MailBox.properties")
public class MailBox {

    @Value("${MailBox.protocol}")
    private String protocol;

    @Value("${MailBox.host}")
    private String host;

    @Value("${MailBox.port}")
    private String  port;

    @Value("${MailBox.username}")
    private String username;

    @Value("${MailBox.password}")
    private String password;

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String  getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
