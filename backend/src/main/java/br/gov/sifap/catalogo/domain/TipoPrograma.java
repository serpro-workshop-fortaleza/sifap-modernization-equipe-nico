package br.gov.sifap.catalogo.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

/**
 * REQ-007 — dominio de TYPE-PROGRAM em SOCPROG.ddm:L31-L32.
 */
public enum TipoPrograma {

    ASSISTENCIA('A'),
    PREVIDENCIA('P'),
    TRABALHO('T');

    private final char codigo;

    TipoPrograma(char codigo) {
        this.codigo = codigo;
    }

    public char codigo() {
        return codigo;
    }

    public static TipoPrograma de(String informado) {
        if (informado == null || informado.length() != 1) {
            throw new DadoInvalidoException("tipo", "o tipo do programa e obrigatorio e tem uma posicao");
        }
        char procurado = Character.toUpperCase(informado.charAt(0));
        return Arrays.stream(values())
                .filter(tipo -> tipo.codigo == procurado)
                .findFirst()
                .orElseThrow(() -> new DadoInvalidoException("tipo",
                        "tipo de programa fora do dominio: use A, P ou T"));
    }

    @Converter(autoApply = true)
    public static class ConversorJpa implements AttributeConverter<TipoPrograma, String> {

        @Override
        public String convertToDatabaseColumn(TipoPrograma tipo) {
            return tipo == null ? null : String.valueOf(tipo.codigo);
        }

        @Override
        public TipoPrograma convertToEntityAttribute(String codigo) {
            return codigo == null ? null : de(codigo);
        }
    }
}
