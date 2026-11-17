package com.trivia.challenge.infrastructure.triviadatabase.config;

import com.trivia.challenge.infrastructure.triviadatabase.rest.client.api.OpenTriviaDbApi;
import com.trivia.challenge.infrastructure.triviadatabase.rest.client.invoker.ApiClient;
import java.net.http.HttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({OpenTriviaDbProperties.class})
public class OpenTriviaDbConfiguration {

    @Bean
    ApiClient openTriviaApiClient(OpenTriviaDbProperties properties) {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();

        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.readTimeout());

        var restClient = RestClient.builder().requestFactory(requestFactory).build();

        return new ApiClient(restClient).setBasePath(properties.baseUrl().toString());
    }

    @Bean
    OpenTriviaDbApi openTriviaDbApi(ApiClient apiClient) {
        return new OpenTriviaDbApi(apiClient);
    }
}
