CREATE TABLE aluno (
    id BIGSERIAL PRIMARY KEY,
    professor_id BIGINT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    data_nascimento DATE,
    contato VARCHAR(100),
    nivel_experiencia VARCHAR(30),
    observacoes TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_aluno_professor FOREIGN KEY (professor_id) REFERENCES professor(id) ON DELETE RESTRICT
);

CREATE INDEX idx_aluno_professor ON aluno(professor_id);
CREATE INDEX idx_aluno_nome ON aluno(nome);
CREATE INDEX idx_aluno_ativo ON aluno(ativo);