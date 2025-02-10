package com.tatSoftGestionUsuarios.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("usuarios")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("API de Usuarios - TatSoft")
                                .description("API para la gestión de usuarios")
                                .version("1.0.0")
                                .contact(new Contact().name("Soporte TatSoft").email("soporte@tatsoft.com"))
                        )
                        .servers(List.of(new Server().url("http://localhost:10101").description("Servidor Local")))
                        .externalDocs(new ExternalDocumentation()
                                .description("Documentación Externa")
                                .url("http://localhost:10101/swagger.yaml")
                        )
                )
                .pathsToMatch("/api/usuarios/**")
                .build();
    }
}
