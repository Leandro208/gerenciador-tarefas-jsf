ALTER TABLE gestao.historico_tarefa
    ADD COLUMN id_autor_usuario BIGINT;

ALTER TABLE gestao.historico_tarefa
    ADD CONSTRAINT fk_historico_tarefa_autor_usuario
        FOREIGN KEY (id_autor_usuario) REFERENCES pessoa.usuario(id) ON DELETE SET NULL;
