CREATE TABLE exercicio (
    id BIGSERIAL PRIMARY KEY,
    professor_id BIGINT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    grupo_muscular VARCHAR(50),
    descricao TEXT,
    instrucoes TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exercicio_professor FOREIGN KEY (professor_id) REFERENCES professor(id) ON DELETE RESTRICT
);

CREATE INDEX idx_exercicio_professor ON exercicio(professor_id);
CREATE INDEX idx_exercicio_nome ON exercicio(nome);