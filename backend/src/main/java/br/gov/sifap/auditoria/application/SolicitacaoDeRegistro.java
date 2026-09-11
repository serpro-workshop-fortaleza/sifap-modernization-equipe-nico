package br.gov.sifap.auditoria.application;

import java.util.Objects;

/**
 * Contrato publicado do modulo de auditoria (REQ-014).
 *
 * <p>Carrega apenas tipos da linguagem: nenhuma classe de {@code auditoria.domain} atravessa
 * o limite do modulo, conforme a secao 2 de {@code plan.md}.
 *
 * @param codigoAcao   codigo de duas posicoes conforme {@code COD-ACTION} em {@code AUDIT.ddm}
 * @param moduloOrigem modulo que originou o evento, ate 8 posicoes
 * @param descricaoAcao descricao livre, ate 80 posicoes
 * @param tipoEntidade codigo de quatro posicoes conforme {@code TYPE-ENTITY}
 * @param idEntidade   chave da entidade afetada, ate 15 posicoes
 * @param cpfAfetado   CPF envolvido, quando aplicavel; pode ser nulo
 */
public record SolicitacaoDeRegistro(
        String codigoAcao,
        String moduloOrigem,
        String descricaoAcao,
        String tipoEntidade,
        String idEntidade,
        String cpfAfetado) {

    public SolicitacaoDeRegistro {
        Objects.requireNonNull(codigoAcao, "codigoAcao");
        Objects.requireNonNull(moduloOrigem, "moduloOrigem");
        Objects.requireNonNull(descricaoAcao, "descricaoAcao");
        Objects.requireNonNull(tipoEntidade, "tipoEntidade");
        Objects.requireNonNull(idEntidade, "idEntidade");
    }

    /** Inclusao de um programa social: evita que o chamador conheca os codigos legados. */
    public static SolicitacaoDeRegistro inclusaoDePrograma(String moduloOrigem, String idEntidade,
                                                           String descricaoAcao) {
        return new SolicitacaoDeRegistro("IN", moduloOrigem, descricaoAcao, "PROG", idEntidade, null);
    }
}
