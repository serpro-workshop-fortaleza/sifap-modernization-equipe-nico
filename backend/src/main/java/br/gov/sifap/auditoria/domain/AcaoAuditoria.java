package br.gov.sifap.auditoria.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

/**
 * Acoes registraveis na trilha de auditoria.
 *
 * <p>Os codigos de duas posicoes reproduzem o dominio de {@code COD-ACTION} em
 * {@code AUDIT.ddm}. {@link #CONSULTA} existe no dominio legado mas nunca pode ser
 * persistida (REQ-016).
 */
public enum AcaoAuditoria {

    INCLUSAO("IN"),
    ALTERACAO("AL"),
    EXCLUSAO("EX"),
    CONSULTA("CO"),
    LOGIN("LG"),
    LOGOUT("LO"),
    LOTE("BT"),
    ERRO("ER"),
    AUTORIZACAO("AU"),
    RECUSA("RE");

    private final String codigo;

    AcaoAuditoria(String codigo) {
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }

    /** REQ-016: consulta e a unica acao do dominio legado que nao gera evento. */
    public boolean auditavel() {
        return this != CONSULTA;
    }

    public static AcaoAuditoria de(String codigo) {
        return Arrays.stream(values())
                .filter(acao -> acao.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("acao de auditoria desconhecida: " + codigo));
    }

    @Converter(autoApply = true)
    public static class ConversorJpa implements AttributeConverter<AcaoAuditoria, String> {

        @Override
        public String convertToDatabaseColumn(AcaoAuditoria acao) {
            return acao == null ? null : acao.codigo;
        }

        @Override
        public AcaoAuditoria convertToEntityAttribute(String codigo) {
            return codigo == null ? null : de(codigo);
        }
    }
}
