package com.individual.thinking.traitorstown;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.common.eventbus.EventBus;
import com.individual.thinking.traitorstown.ai.learning.model.DiscreteActionSpace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableAsync
public class TraitorstownApplication extends SpringBootServletInitializer {

    public static com.google.common.eventbus.EventBus EventBus = new EventBus();

    public static void main(String[] args) {
		SpringApplication.run(TraitorstownApplication.class, args);
	}

    @Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TraitorstownApplication.class);
	}

	@Bean
    public DiscreteActionSpace actionSpace(TraitorsTownConfiguration configuration){
	    return new DiscreteActionSpace(
	            configuration.getMaximumNumberOfCards(),
                configuration.getMaximumNumberOfPlayers());
    }

    @Bean
    @Profile(value = {"development", "learning", "production"})
    public AmazonS3 amazonS3Client(){
        return AmazonS3ClientBuilder
                .standard()
                .withRegion("eu-west-1")
                .build();
    }

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")
						.allowedMethods("GET", "POST", "PUT", "DELETE");
			}
		};
	}

    @Bean
    @Profile("production")
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter crlf = new CommonsRequestLoggingFilter();
        crlf.setIncludeClientInfo(true);
        crlf.setIncludeQueryString(true);
        crlf.setIncludePayload(true);
        return crlf;
    }
}
