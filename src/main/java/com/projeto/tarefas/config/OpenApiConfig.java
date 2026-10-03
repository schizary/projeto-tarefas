package com.projeto.tarefas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI definirDocumentacao() {
        Info informacoes = new Info()
                .title("API de Tarefas")
                .version("1.0.0")
                .description("Aplicação para o gerenciamento de tarefas do dia a dia");

        return new OpenAPI().info(informacoes);
    }
}
