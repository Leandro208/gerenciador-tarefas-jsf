CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1;

/* =========================================================
   CRIAÇÃO DOS SCHEMAS
   ========================================================= */
CREATE SCHEMA IF NOT EXISTS pessoa;
CREATE SCHEMA IF NOT EXISTS gestao;
CREATE SCHEMA IF NOT EXISTS seguranca;
CREATE SCHEMA IF NOT EXISTS auditoria;

/* =========================================================
   TABELA: usuario (pessoa)
   ========================================================= */
CREATE TABLE pessoa.usuario (
                                id BIGSERIAL PRIMARY KEY,
                                email VARCHAR(255) NOT NULL UNIQUE,
                                senha VARCHAR(255) NOT NULL,
                                funcao VARCHAR(50)
);

/* =========================================================
   TABELA: registro_acesso (seguranca)
   ========================================================= */
CREATE TABLE seguranca.registro_acesso (
                                           id BIGSERIAL PRIMARY KEY,
                                           id_usuario BIGINT,
                                           data TIMESTAMP,
                                           data_saida TIMESTAMP,
                                           ip VARCHAR(45),

                                           CONSTRAINT fk_registro_usuario
                                               FOREIGN KEY (id_usuario)
                                                   REFERENCES pessoa.usuario(id)
);

/* =========================================================
   TABELA: equipe (gestao)
   ========================================================= */
CREATE TABLE gestao.equipe (
                               id BIGSERIAL PRIMARY KEY,
                               nome VARCHAR(255) NOT NULL,
                               tarefas_concluidas INTEGER,
                               data_cadastro TIMESTAMP,
                               id_registro_acesso BIGINT,

                               CONSTRAINT fk_equipe_registro_acesso
                                   FOREIGN KEY (id_registro_acesso)
                                       REFERENCES seguranca.registro_acesso(id)
);

/* =========================================================
   TABELA: responsavel (pessoa)
   ========================================================= */
CREATE TABLE pessoa.responsavel (
                                    id BIGSERIAL PRIMARY KEY,
                                    nome VARCHAR(50) NOT NULL,
                                    data_nascimento DATE NOT NULL,
                                    id_equipe BIGINT,
                                    id_usuario BIGINT,
                                    data_cadastro TIMESTAMP,

                                    CONSTRAINT fk_responsavel_equipe
                                        FOREIGN KEY (id_equipe)
                                            REFERENCES gestao.equipe(id),

                                    CONSTRAINT fk_responsavel_usuario
                                        FOREIGN KEY (id_usuario)
                                            REFERENCES pessoa.usuario(id)
);

/* =========================================================
   TABELA: tarefa (gestao)
   ========================================================= */
CREATE TABLE gestao.tarefa (
                               id BIGSERIAL PRIMARY KEY,
                               titulo VARCHAR(255) NOT NULL,
                               descricao TEXT NOT NULL,
                               id_responsavel BIGINT,
                               prioridade VARCHAR(50),
                               status VARCHAR(50),
                               deadline DATE NOT NULL,
                               data_finalizacao TIMESTAMP,
                               id_equipe BIGINT,
                               data_cadastro TIMESTAMP,
                               id_registro_acesso BIGINT,

                               CONSTRAINT fk_tarefa_responsavel
                                   FOREIGN KEY (id_responsavel)
                                       REFERENCES pessoa.responsavel(id),

                               CONSTRAINT fk_tarefa_equipe
                                   FOREIGN KEY (id_equipe)
                                       REFERENCES gestao.equipe(id),

                               CONSTRAINT fk_tarefa_registro_acesso
                                   FOREIGN KEY (id_registro_acesso)
                                       REFERENCES seguranca.registro_acesso(id)
);

/* =========================================================
   TABELA: log_db (auditoria)
   ========================================================= */
CREATE TABLE auditoria.log_db (
                                  id BIGSERIAL PRIMARY KEY,
                                  operacao CHAR(1) NOT NULL,
                                  id_elemento BIGINT,
                                  alteracao TEXT,
                                  id_registro_acesso BIGINT,
                                  data TIMESTAMP,
                                  tabela VARCHAR(100),
                                  cod_comando INTEGER,

                                  CONSTRAINT fk_log_registro_acesso
                                      FOREIGN KEY (id_registro_acesso)
                                          REFERENCES seguranca.registro_acesso(id)
);
