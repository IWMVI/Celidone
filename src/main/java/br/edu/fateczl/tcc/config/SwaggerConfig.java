package br.edu.fateczl.tcc.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String schemeName = "Bearer";
        String bearerFormat = "JWT";
        String scheme = "bearer";

        return new OpenAPI()
                .info(new Info()
                        .title("API de Locação de Trajes a Rigor")
                        .version("1.0.0")
                        .description("Documentação da API para Locação de Trajes a Rigor - TCC.")
                        .contact(new Contact()
                                .name("TCC Fatec")
                                .email("emailgenerico@gmail.com")
                                .url("https://github.com/IWMVI/TCC"))
                )
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(
                                schemeName,
                                new SecurityScheme()
                                        .name(schemeName)
                                        .description("Cole apenas o token JWT. Não inclua 'Bearer '")
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat(bearerFormat)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme(scheme)
                        )
                );
    }
}
