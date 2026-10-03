-- =====================================================================
-- projeto-tarefas | script de criacao do banco de dados
-- PostgreSQL
-- =====================================================================

-- executar conectado ao banco "postgres" para criar a base:
--   CREATE DATABASE projeto_tarefas;
-- em seguida reconectar em "projeto_tarefas" e rodar o restante.

CREATE SCHEMA IF NOT EXISTS public;

-- ---------------------------------------------------------------------
-- tabela: tarefa
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS tarefa;

CREATE TABLE tarefa (
    id                 BIGSERIAL      NOT NULL,
    nome               VARCHAR(120)   NOT NULL,
    descricao          VARCHAR(500),
    status             VARCHAR(20)    NOT NULL DEFAULT 'PENDENTE',
    observacoes        VARCHAR(500),
    data_criacao       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_tarefa PRIMARY KEY (id),
    CONSTRAINT ck_tarefa_status CHECK (
        status IN ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA')
    ),
    CONSTRAINT ck_tarefa_nome_nao_vazio CHECK (LENGTH(TRIM(nome)) > 0)
);

COMMENT ON TABLE  tarefa                  IS 'tarefas do dia a dia';
COMMENT ON COLUMN tarefa.id               IS 'identificador da tarefa';
COMMENT ON COLUMN tarefa.nome             IS 'titulo curto da tarefa';
COMMENT ON COLUMN tarefa.descricao        IS 'detalhamento da tarefa';
COMMENT ON COLUMN tarefa.status           IS 'PENDENTE, EM_ANDAMENTO, CONCLUIDA ou CANCELADA';
COMMENT ON COLUMN tarefa.observacoes      IS 'anotacoes livres';
COMMENT ON COLUMN tarefa.data_criacao     IS 'momento em que a tarefa foi criada';
COMMENT ON COLUMN tarefa.data_atualizacao IS 'momento da ultima alteracao';

-- ---------------------------------------------------------------------
-- indices
-- ---------------------------------------------------------------------
CREATE INDEX ix_tarefa_status       ON tarefa (status);
CREATE INDEX ix_tarefa_data_criacao ON tarefa (data_criacao DESC);

-- ---------------------------------------------------------------------
-- carga inicial (opcional)
-- ---------------------------------------------------------------------
INSERT INTO tarefa (nome, descricao, status, observacoes) VALUES
    ('Comprar pao',        'Padaria da esquina, pela manha',      'PENDENTE',     NULL),
    ('Estudar Spring',     'Revisar camada de servico e testes',  'EM_ANDAMENTO', 'Focar em JPA'),
    ('Pagar a conta de luz','Vence dia 10',                       'CONCLUIDA',    'Pago via PIX');
