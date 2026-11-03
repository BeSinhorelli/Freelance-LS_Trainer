-- Senha: admin123
INSERT INTO professor (nome, email, senha_hash, ativo, criado_em, atualizado_em)
VALUES (
           'bernardo',
           'bernardo@gmail.com',
           '$2a$12$Jq75vk5P5WOJoSD1YIuLO.v8rLx1Hr9G8Ic0ZkUm.0TKUlJqI4OFi',
           true,
           NOW(),
           NOW()
       );