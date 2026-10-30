CREATE TABLE prescricao_cardio (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    professor_id BIGINT NOT NULL,
    modalidade VARCHAR(100) NOT NULL,
    duracao_minutos INTEGER,
    frequencia_semanal INTEGER,
    observacoes TEXT,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cardio_aluno FOREIGN KEY (aluno_id) REFERENCES aluno(id) ON DELETE RESTRICT,
    CONSTRAINT fk_cardio_professor FOREIGN KEY (professor_id) REFERENCES professor(id) ON DELETE RESTRICT,
    CONSTRAINT chk_cardio_duracao CHECK (duracao_minutos IS NULL OR duracao_minutos > 0),
    CONSTRAINT chk_cardio_frequencia CHECK (frequencia_semanal IS NULL OR frequencia_semanal BETWEEN 1 AND 7)
);

CREATE INDEX idx_cardio_aluno ON prescricao_cardio(aluno_id);
CREATE INDEX idx_cardio_professor ON prescricao_cardio(professor_id);