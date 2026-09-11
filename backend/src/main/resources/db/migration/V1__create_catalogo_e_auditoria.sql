-- V1__create_catalogo_e_auditoria.sql
-- Esquema da fatia 001-social-program-catalog, conforme a secao 3 de specs/001-social-program-catalog/plan.md.
-- Origem legada: 01-archaeology/legacy-sifap/adabas-ddms/SOCPROG.ddm e AUDIT.ddm.

-- ---------------------------------------------------------------------------
-- Catalogo de Programas Sociais (REQ-001 a REQ-009)
-- ---------------------------------------------------------------------------
CREATE TABLE programa_social (
    codigo                  VARCHAR(4)     NOT NULL,
    nome                    VARCHAR(60)    NOT NULL,
    tipo                    CHAR(1)        NOT NULL,
    valor_base              NUMERIC(7, 2)  NOT NULL,
    fator_ajuste            NUMERIC(7, 4)  NOT NULL DEFAULT 0,
    codigo_elegibilidade    VARCHAR(5),
    data_criacao            DATE           NOT NULL,
    data_encerramento       DATE,
    situacao                CHAR(1)        NOT NULL DEFAULT 'A',
    renda_percapita_maxima  NUMERIC(7, 2),
    idade_minima            SMALLINT       NOT NULL DEFAULT 0,
    idade_maxima            SMALLINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_programa_social
        PRIMARY KEY (codigo),

    -- REQ-007: dominio de TYPE-PROGRAM em SOCPROG.ddm
    CONSTRAINT ck_programa_social_tipo
        CHECK (tipo IN ('A', 'P', 'T')),

    -- REQ-003: dominio de STATUS-PROGRAM em SOCPROG.ddm
    CONSTRAINT ck_programa_social_situacao
        CHECK (situacao IN ('A', 'I', 'E')),

    -- REQ-005, REQ-006: teto do campo compactado P 7,2
    CONSTRAINT ck_programa_social_valor_base
        CHECK (valor_base BETWEEN 0 AND 99999.99),

    -- REQ-009: encerramento nunca antecede a criacao
    CONSTRAINT ck_programa_social_vigencia
        CHECK (data_encerramento IS NULL OR data_encerramento >= data_criacao),

    -- REQ-008: zero significa ausencia de limite, entao so compara quando ambos existem
    CONSTRAINT ck_programa_social_faixa_etaria
        CHECK (idade_minima = 0 OR idade_maxima = 0 OR idade_minima <= idade_maxima)
);

-- REQ-004: nulo em data_encerramento representa vigencia indeterminada, no lugar do zero do legado.
COMMENT ON COLUMN programa_social.data_encerramento IS 'NULL = vigencia indeterminada (REQ-004)';
COMMENT ON COLUMN programa_social.fator_ajuste IS 'Guardado sem ser aplicado ao valor base (REQ-005)';

-- ---------------------------------------------------------------------------
-- Trilha de auditoria (REQ-014 a REQ-017)
-- Somente as 13 colunas que o legado efetivamente grava — decisao 3 do ADR-0003.
-- ---------------------------------------------------------------------------
CREATE TABLE evento_auditoria (
    id              BIGINT        GENERATED ALWAYS AS IDENTITY,
    data_evento     DATE          NOT NULL,
    hora_evento     TIME          NOT NULL,
    marca_temporal  TIMESTAMP     NOT NULL,
    codigo_acao     VARCHAR(2)    NOT NULL,
    modulo_origem   VARCHAR(8)    NOT NULL,
    descricao_acao  VARCHAR(80)   NOT NULL,
    tipo_entidade   VARCHAR(4)    NOT NULL,
    id_entidade     VARCHAR(15)   NOT NULL,
    cpf_afetado     VARCHAR(11),
    usuario         VARCHAR(8)    NOT NULL,
    nome_job_batch  VARCHAR(16),
    situacao_batch  CHAR(1),

    CONSTRAINT pk_evento_auditoria
        PRIMARY KEY (id),

    -- REQ-016: consulta nunca vira evento de auditoria
    CONSTRAINT ck_evento_auditoria_sem_consulta
        CHECK (codigo_acao <> 'CO')
);

COMMENT ON COLUMN evento_auditoria.usuario IS 'Provisoriamente sempre SIFAPSYS — ver P4 na secao 8 de plan.md';

-- Sustenta a consulta futura da trilha por entidade sem varredura.
CREATE INDEX ix_evento_auditoria_entidade
    ON evento_auditoria (tipo_entidade, id_entidade, marca_temporal DESC);

-- REQ-014: a imutabilidade vive no banco porque a exigencia legal nao pode
-- depender de disciplina de codigo da aplicacao.
CREATE FUNCTION recusar_alteracao_de_evento_auditoria() RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'evento_auditoria e imutavel: operacao % recusada', TG_OP;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_evento_auditoria_imutavel
    BEFORE UPDATE OR DELETE ON evento_auditoria
    FOR EACH ROW
    EXECUTE FUNCTION recusar_alteracao_de_evento_auditoria();
