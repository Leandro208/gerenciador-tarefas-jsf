CREATE TABLE gestao.historico_tarefa (
    id BIGSERIAL PRIMARY KEY,
    id_tarefa BIGINT NOT NULL,
    campo VARCHAR(100) NOT NULL,
    valor_anterior TEXT,
    valor_novo TEXT,
    autor_nome VARCHAR(255) NOT NULL,
    data_alteracao TIMESTAMP NOT NULL,
    CONSTRAINT fk_historico_tarefa_tarefa
        FOREIGN KEY (id_tarefa) REFERENCES gestao.tarefa(id) ON DELETE CASCADE
);

CREATE INDEX idx_historico_tarefa_data
    ON gestao.historico_tarefa(id_tarefa, data_alteracao DESC);
