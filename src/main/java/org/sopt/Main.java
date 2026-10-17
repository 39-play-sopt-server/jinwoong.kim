package org.sopt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CLASS: 스프링 부트 애플리케이션의 실행 진입점입니다.
 */
@SpringBootApplication
public class Main {
    /**
     * METHOD: 스프링 부트 애플리케이션을 실행하고 내장 웹 서버를 시작합니다.
     */
    public static void main(String[] args) {
     SpringApplication.run(Main.class, args);
    }
}
