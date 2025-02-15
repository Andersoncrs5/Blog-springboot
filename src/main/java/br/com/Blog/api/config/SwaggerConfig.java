package br.com.Blog.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Documentation")
                        .version("1.0.0")
                        .description("API documentation created with Springdoc OpenAPI and Swagger UI")
                        .contact(new Contact()
                                .name("Anderson")
                                .url("https://github.com/Andersoncrs5")
                                .email("anderson.c.rms2005@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0 License")
                                .url("http://springdoc.org")));
    }

}
