package br.gov.sifap.auditoria.application;

/**
 * Fornece o usuario responsavel gravado em cada evento de auditoria (REQ-014).
 *
 * <p>Ponto de extensao unico: quando a autenticacao existir, basta trocar a implementacao
 * registrada no contexto. Ver P4 na secao 8 de {@code specs/001-social-program-catalog/plan.md}.
 */
public interface ProvedorUsuarioResponsavel {

    /** Login de ate 8 posicoes, conforme {@code USR-EVENT} em {@code AUDIT.ddm}. */
    String obter();
}
