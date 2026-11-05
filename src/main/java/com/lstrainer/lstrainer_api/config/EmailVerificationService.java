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
                .defaultHeader("User-Agent", "LSTrainer/1.0") // OBRIGATÓRIO PELA BOUNCEZERO
                .build();
    }

    public boolean isEmailReal(String email) {
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
            // MUDADO PARA FALSE: Se a API falhar, bloqueia o cadastro e loga o erro real
            System.err.println("❌ ERRO NA API DO BOUNCEZERO: " + e.getMessage());
            return false;
        }
        return false;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VerifyResult(
            String classification,
            @JsonProperty("is_deliverable") boolean isDeliverable,
            boolean disposable
    ) {}
}