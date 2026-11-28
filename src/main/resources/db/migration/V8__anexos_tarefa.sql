CREATE TABLE gestao.tarefa_anexo (
    id BIGSERIAL PRIMARY KEY,
    id_tarefa BIGINT NOT NULL,
    id_usuario BIGINT,
    nome_arquivo VARCHAR(255) NOT NULL,
    tipo_mime VARCHAR(100) NOT NULL,
    tamanho BIGINT NOT NULL,
    data_upload TIMESTAMP NOT NULL,
    conteudo BYTEA NOT NULL,
    CONSTRAINT fk_tarefa_anexo_tarefa
        FOREIGN KEY (id_tarefa) REFERENCES gestao.tarefa(id) ON DELETE CASCADE,
    CONSTRAINT fk_tarefa_anexo_usuario
        FOREIGN KEY (id_usuario) REFERENCES pessoa.usuario(id) ON DELETE SET NULL,
    CONSTRAINT ck_tarefa_anexo_tipo_mime
        CHECK (tipo_mime IN (
            'application/pdf',
            'image/jpeg',
            'image/png',
            'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
            'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        )),
    CONSTRAINT ck_tarefa_anexo_tamanho
        CHECK (tamanho > 0 AND tamanho <= 10485760)
);

CREATE INDEX ix_tarefa_anexo_tarefa ON gestao.tarefa_anexo(id_tarefa);
