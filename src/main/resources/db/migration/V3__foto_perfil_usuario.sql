CREATE TABLE pessoa.foto_perfil (
    id_usuario BIGINT PRIMARY KEY,
    conteudo BYTEA NOT NULL,
    tipo_mime VARCHAR(20) NOT NULL,
    CONSTRAINT fk_foto_perfil_usuario
        FOREIGN KEY (id_usuario) REFERENCES pessoa.usuario(id) ON DELETE CASCADE,
    CONSTRAINT ck_foto_perfil_tipo_mime
        CHECK (tipo_mime IN ('image/jpeg', 'image/png'))
);
