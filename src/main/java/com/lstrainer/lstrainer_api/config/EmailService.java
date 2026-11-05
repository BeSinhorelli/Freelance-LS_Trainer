package com.lstrainer.lstrainer_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;
    private final String apiKey;
    private final String fromEmail;
    private final String frontendUrl;

    public EmailService(RestClient.Builder builder,
                        @Value("${app.email.api-key:}") String apiKey,
                        @Value("${app.email.from:onboarding@resend.dev}") String fromEmail,
                        @Value("${app.email.frontend-url:https://ls-trainer.vercel.app}") String frontendUrl) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.frontendUrl = frontendUrl;
        this.restClient = builder
                .baseUrl("https://api.resend.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public void sendVerificationEmail(String to, String token) {
        if (apiKey == null || apiKey.isBlank()) {
            System.out.println("⚠️ Chave de API de e-mail não configurada. Pulando envio.");
            return;
        }

        String verificationLink = frontendUrl + "/verificar-email?token=" + token;
        String htmlContent = "<h2>Bem-vindo ao LS Trainer!</h2>" +
                "<p>Clique no botão abaixo para ativar sua conta:</p>" +
                "<a href=\"" + verificationLink + "\" style=\"background-color: #1e40af; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Ativar minha conta</a>" +
                "<p>Se o botão não funcionar, copie e cole este link no navegador:</p>" +
                "<p>" + verificationLink + "</p>" +
                "<p>Este link expira em 24 horas.</p>";

        try {
            restClient.post()
                    .uri("/emails")
                    .body(Map.of(
                            "from", fromEmail,
                            "to", to,
                            "subject", "Ative sua conta - LS Trainer",
                            "html", htmlContent
                    ))
                    .retrieve()
                    .toBodilessEntity();
            System.out.println("✅ E-mail de verificação enviado para: " + to);
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}