ALTER TABLE gestao.historico_tarefa
    ADD COLUMN id_usuario BIGINT;

UPDATE gestao.historico_tarefa h
SET id_usuario = ra.id_usuario
FROM seguranca.registro_acesso ra
WHERE h.id_registro_acesso = ra.id;

UPDATE gestao.historico_tarefa h
SET id_usuario = autor.id_usuario
FROM (
    SELECT r.nome, MIN(r.id_usuario) AS id_usuario
    FROM pessoa.responsavel r
    WHERE r.id_usuario IS NOT NULL
    GROUP BY r.nome
    HAVING COUNT(DISTINCT r.id_usuario) = 1
) autor
WHERE h.id_usuario IS NULL
  AND h.autor_nome = autor.nome;

ALTER TABLE gestao.historico_tarefa
    ADD CONSTRAINT fk_historico_tarefa_usuario
        FOREIGN KEY (id_usuario) REFERENCES pessoa.usuario(id) ON DELETE SET NULL;

ALTER TABLE gestao.historico_tarefa
    DROP CONSTRAINT fk_historico_tarefa_registro_acesso;

ALTER TABLE gestao.historico_tarefa
    DROP COLUMN id_registro_acesso,
    DROP COLUMN autor_nome;
