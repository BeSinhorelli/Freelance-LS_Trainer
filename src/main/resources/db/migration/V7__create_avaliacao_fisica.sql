CREATE TABLE avaliacao_fisica (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    professor_id BIGINT NOT NULL,
    data DATE NOT NULL DEFAULT CURRENT_DATE,
    peso_kg DECIMAL(5,2),
    altura_cm DECIMAL(5,2),
    observacoes TEXT,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_avaliacao_aluno FOREIGN KEY (aluno_id) REFERENCES aluno(id) ON DELETE RESTRICT,
    CONSTRAINT fk_avaliacao_professor FOREIGN KEY (professor_id) REFERENCES professor(id) ON DELETE RESTRICT,
    CONSTRAINT chk_avaliacao_peso CHECK (peso_kg IS NULL OR peso_kg > 0),
    CONSTRAINT chk_avaliacao_altura CHECK (altura_cm IS NULL OR altura_cm > 0)
);

CREATE INDEX idx_avaliacao_aluno ON avaliacao_fisica(aluno_id);
CREATE INDEX idx_avaliacao_professor ON avaliacao_fisica(professor_id);
CREATE INDEX idx_avaliacao_data ON avaliacao_fisica(data);