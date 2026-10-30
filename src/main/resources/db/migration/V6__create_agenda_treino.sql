CREATE TABLE agenda_treino (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    professor_id BIGINT NOT NULL,
    ficha_id BIGINT,
    data DATE NOT NULL,
    dia_semana VARCHAR(15),
    observacoes TEXT,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_agenda_aluno FOREIGN KEY (aluno_id) REFERENCES aluno(id) ON DELETE RESTRICT,
    CONSTRAINT fk_agenda_professor FOREIGN KEY (professor_id) REFERENCES professor(id) ON DELETE RESTRICT,
    CONSTRAINT fk_agenda_ficha FOREIGN KEY (ficha_id) REFERENCES ficha_treino(id) ON DELETE SET NULL
);

CREATE INDEX idx_agenda_aluno ON agenda_treino(aluno_id);
CREATE INDEX idx_agenda_professor ON agenda_treino(professor_id);
CREATE INDEX idx_agenda_data ON agenda_treino(data);