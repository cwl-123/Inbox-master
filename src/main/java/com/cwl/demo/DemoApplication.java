package com.cwl.demo;

import com.cwl.demo.statistics.statistics;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.cwl.demo")
public class DemoApplication {

    public static void main(String[] args) throws Exception {
        System.out.println("hello!");
        new POP3MailReceiverTest();
        SpringApplication.run(DemoApplication.class, args);
//        new statistics();
//        new POP3MailReceiverTest();
    }

}
