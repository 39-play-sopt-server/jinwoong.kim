package org.sopt.common.config;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * CLASS: 소셜 플랫폼 API 호출용 HTTP 클라이언트 설정
 */
@Configuration
public class OAuthClientConfig {

    /**
     * METHOD: GitHub API 호출용 RestClient Bean 등록
     */
    @Bean
    // RestClient: 서버가 다른 서버에 HTTP 요청을 보낼 때 사용하는 도구
    public RestClient githubRestClient(
            @Value("${app.oauth.github-api-base-url:https://api.github.com}") String baseUrl
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        JdkClientHttpRequestFactory factory =
                new JdkClientHttpRequestFactory(httpClient);

        factory.setReadTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("User-Agent", "sopt-assignment")
                .requestFactory(factory)
                .build();
    }
}
