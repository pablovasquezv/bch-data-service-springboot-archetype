package cl.banchile.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Clase de configuración Spring Boot para la característica Swagger UI
 */
@Configuration
public class SpringfoxOpenAPIConfiguration {
    
    /**
     * Define un Bean en el contexto de Spring que configura la generación y exposición de artefactos Swagger
     * @return Docket que representa cla configuración de Springfox Openapi
     */
    @Bean
    public Docket api() { 
        // De define por defecto la versión de OpenApi 2, para generación de especificaciones
        return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("cl.banchile.application.adapters.in.rest"))
        .paths(PathSelectors.any())
        .build();
    }
}
