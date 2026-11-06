CREATE TABLE treino_agendado (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    professor_id BIGINT NOT NULL,
    data DATE NOT NULL,
    nome_treino VARCHAR(100) NOT NULL, -- Ex: "Treino A"
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_treino_aluno FOREIGN KEY (aluno_id) REFERENCES aluno(id) ON DELETE CASCADE,
    CONSTRAINT fk_treino_professor FOREIGN KEY (professor_id) REFERENCES professor(id) ON DELETE RESTRICT
);

CREATE TABLE item_treino_agendado (
    id BIGSERIAL PRIMARY KEY,
    treino_agendado_id BIGINT NOT NULL,
    exercicio_id BIGINT NOT NULL,
    ordem INTEGER NOT NULL,
    series INTEGER NOT NULL,
    repeticoes VARCHAR(30) NOT NULL,
    carga VARCHAR(50),
    descanso_segundos INTEGER,
    observacoes TEXT,
    CONSTRAINT fk_item_treino FOREIGN KEY (treino_agendado_id) REFERENCES treino_agendado(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_exercicio_agendado FOREIGN KEY (exercicio_id) REFERENCES exercicio(id) ON DELETE RESTRICT
);