/* =========================================================
   TABELA: comentario (gestao)
   ========================================================= */
CREATE TABLE gestao.comentario (
                                   id BIGSERIAL PRIMARY KEY,
                                   id_tarefa BIGINT NOT NULL,
                                   id_responsavel BIGINT,
                                   texto TEXT NOT NULL,
                                   data_cadastro TIMESTAMP,
                                   id_registro_acesso BIGINT,

                                   CONSTRAINT fk_comentario_tarefa
                                       FOREIGN KEY (id_tarefa)
                                           REFERENCES gestao.tarefa(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT fk_comentario_responsavel
                                       FOREIGN KEY (id_responsavel)
                                           REFERENCES pessoa.responsavel(id),

                                   CONSTRAINT fk_comentario_registro_acesso
                                       FOREIGN KEY (id_registro_acesso)
                                           REFERENCES seguranca.registro_acesso(id)
);

CREATE INDEX idx_comentario_tarefa ON gestao.comentario(id_tarefa);
