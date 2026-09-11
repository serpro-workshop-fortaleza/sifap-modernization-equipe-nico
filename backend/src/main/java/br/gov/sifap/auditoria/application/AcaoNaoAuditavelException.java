package br.gov.sifap.auditoria.application;

/**
 * Lancada quando alguem tenta gravar uma acao que a trilha nao pode registrar (REQ-016).
 */
public class AcaoNaoAuditavelException extends RuntimeException {

    private final String codigoAcao;

    public AcaoNaoAuditavelException(String codigoAcao) {
        super("acao " + codigoAcao + " nao pode ser registrada na trilha de auditoria");
        this.codigoAcao = codigoAcao;
    }

    public String codigoAcao() {
        return codigoAcao;
    }
}
