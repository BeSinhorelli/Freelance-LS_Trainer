CREATE TABLE item_ficha_treino (
    id BIGSERIAL PRIMARY KEY,
    ficha_id BIGINT NOT NULL,
    exercicio_id BIGINT NOT NULL,
    ordem INTEGER NOT NULL,
    series INTEGER NOT NULL,
    repeticoes VARCHAR(30) NOT NULL,
    carga VARCHAR(50),
    descanso_segundos INTEGER,
    observacoes TEXT,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_ficha FOREIGN KEY (ficha_id) REFERENCES ficha_treino(id) ON DELETE RESTRICT,
    CONSTRAINT fk_item_exercicio FOREIGN KEY (exercicio_id) REFERENCES exercicio(id) ON DELETE RESTRICT,
    CONSTRAINT chk_item_ordem CHECK (ordem > 0),
    CONSTRAINT chk_item_series CHECK (series > 0),
    CONSTRAINT chk_item_descanso CHECK (descanso_segundos IS NULL OR descanso_segundos >= 0)
);

CREATE UNIQUE INDEX uq_item_ficha_ordem ON item_ficha_treino(ficha_id, ordem);
CREATE UNIQUE INDEX uq_item_ficha_exercicio ON item_ficha_treino(ficha_id, exercicio_id);