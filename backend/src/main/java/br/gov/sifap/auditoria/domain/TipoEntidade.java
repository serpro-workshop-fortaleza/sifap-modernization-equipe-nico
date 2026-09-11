package br.gov.sifap.auditoria.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

/**
 * Tipos de entidade afetada por um evento de auditoria.
 *
 * <p>Codigos de quatro posicoes conforme {@code TYPE-ENTITY} em {@code AUDIT.ddm}.
 */
public enum TipoEntidade {

    BENEFICIARIO("BENF"),
    PAGAMENTO("PGTO"),
    PROGRAMA("PROG"),
    ADMINISTRACAO("ADMN"),
    SISTEMA("SIST");

    private final String codigo;

    TipoEntidade(String codigo) {
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }

    public static TipoEntidade de(String codigo) {
        return Arrays.stream(values())
                .filter(tipo -> tipo.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("tipo de entidade desconhecido: " + codigo));
    }

    @Converter(autoApply = true)
    public static class ConversorJpa implements AttributeConverter<TipoEntidade, String> {

        @Override
        public String convertToDatabaseColumn(TipoEntidade tipo) {
            return tipo == null ? null : tipo.codigo;
        }

        @Override
        public TipoEntidade convertToEntityAttribute(String codigo) {
            return codigo == null ? null : de(codigo);
        }
    }
}
