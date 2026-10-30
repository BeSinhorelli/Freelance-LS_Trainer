CREATE TABLE ficha_treino (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    professor_id BIGINT NOT NULL,
    objetivo VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    data_criacao DATE NOT NULL DEFAULT CURRENT_DATE,
    data_revisao DATE,
    proxima_reavaliacao DATE,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ficha_aluno FOREIGN KEY (aluno_id) REFERENCES aluno(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ficha_professor FOREIGN KEY (professor_id) REFERENCES professor(id) ON DELETE RESTRICT,
    CONSTRAINT chk_ficha_status CHECK (status IN ('ATIVA', 'CONCLUIDA', 'SUBSTITUIDA', 'ARQUIVADA'))
);

CREATE INDEX idx_ficha_aluno ON ficha_treino(aluno_id);
CREATE INDEX idx_ficha_professor ON ficha_treino(professor_id);
CREATE INDEX idx_ficha_status ON ficha_treino(status);