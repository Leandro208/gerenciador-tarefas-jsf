ALTER TABLE gestao.historico_tarefa
    DROP CONSTRAINT fk_historico_tarefa_autor_usuario;

ALTER TABLE gestao.historico_tarefa
    DROP COLUMN id_autor_usuario;

ALTER TABLE gestao.historico_tarefa
    ADD COLUMN id_registro_acesso BIGINT;

ALTER TABLE gestao.historico_tarefa
    ADD CONSTRAINT fk_historico_tarefa_registro_acesso
        FOREIGN KEY (id_registro_acesso)
        REFERENCES seguranca.registro_acesso(id) ON DELETE SET NULL;
