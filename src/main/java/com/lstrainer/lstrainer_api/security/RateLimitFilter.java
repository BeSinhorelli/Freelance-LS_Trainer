package com.lstrainer.lstrainer_api.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        // Limite: 30 requisições por minuto por IP.
        // Ajuste esse número conforme a necessidade (ex: 60 para ser mais brando).
        Bandwidth limit = Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Pega o IP real do cliente (importante por causa do proxy do Render)
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        } else {
            // O header pode vir com uma lista "IP1, IP2". Pegamos o primeiro.
            ip = ip.split(",")[0].trim();
        }

        // Pega ou cria o balde para este IP
        Bucket bucket = cache.computeIfAbsent(ip, k -> createNewBucket());

        // Tenta consumir 1 ficha
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            // Se não tem ficha, bloqueia com 429
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Muitas requisições. Tente novamente em instantes.\"}");
        }
    }
}