package com.lstrainer.lstrainer_api.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EmailVerificationService {

    private final RestClient restClient;
    private final String apiKey;

    public EmailVerificationService(RestClient.Builder builder,
                                    @Value("${app.email-verification.api-key:}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = builder
                .baseUrl("https://api.bouncezero.io/api/v1")
                .defaultHeader("X-API-Key", apiKey)
                .build();
    }

    public boolean isEmailReal(String email) {
        // Se a chave não estiver configurada (ex: ambiente local), permite o cadastro.
        // Em produção, a chave deve existir.
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("⚠️ Chave de API de verificação não configurada. Pulando verificação.");
            return true; 
        }

        try {
            VerifyResult result = restClient.post()
                    .uri("/verify")
                    .body(java.util.Map.of("email", email))
                    .retrieve()
                    .body(VerifyResult.class);

            if (result != null) {
                // A API retorna "valid" se o e-mail existe.
                // Também bloqueamos e-mails descartáveis (temporários).
                return "valid".equals(result.classification()) && !result.disposable();
            }
        } catch (Exception e) {
            // Se a API falhar (rede, timeout, limite), NÃO bloqueamos o usuário.
            // Isso evita que um problema na API externa derrube o seu cadastro.
            System.err.println("Erro ao verificar e-mail: " + e.getMessage());
            return true; 
        }
        return false;
    }

    // DTO para mapear a resposta da API da BounceZero
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VerifyResult(
            String classification, // "valid" ou "invalid"
            @JsonProperty("is_deliverable") boolean isDeliverable,
            boolean disposable    // true se for e-mail temporário
    ) {}
}