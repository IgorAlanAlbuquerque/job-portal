package com.igoralan.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.igoralan.jobportal.config.JwtProperties;

@SpringBootApplication(exclude = {
		RedisRepositoriesAutoConfiguration.class,
		ReactiveElasticsearchRepositoriesAutoConfiguration.class
})
@EnableConfigurationProperties(JwtProperties.class)
@EnableCaching
@EnableJpaRepositories(basePackages = "com.igoralan.jobportal.repository")
@EnableElasticsearchRepositories(basePackages = "com.igoralan.jobportal.elasticsearch.repository")
public class JobportalApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobportalApplication.class, args);
	}

}
