package br.gov.sifap.catalogo.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

/**
 * REQ-003 — dominio de STAT-PROGRAM em SOCPROG.ddm:L37. A transicao de situacao
 * esta fora desta fatia: o legado grava 'A' fixo e nao tem caminho de alteracao.
 */
public enum SituacaoPrograma {

    ATIVO('A'),
    INATIVO('I'),
    ENCERRADO('E');

    private final char codigo;

    SituacaoPrograma(char codigo) {
        this.codigo = codigo;
    }

    public char codigo() {
        return codigo;
    }

    public static SituacaoPrograma de(String informado) {
        if (informado == null || informado.length() != 1) {
            throw new DadoInvalidoException("situacao", "a situacao do programa tem uma posicao");
        }
        char procurado = Character.toUpperCase(informado.charAt(0));
        return Arrays.stream(values())
                .filter(situacao -> situacao.codigo == procurado)
                .findFirst()
                .orElseThrow(() -> new DadoInvalidoException("situacao",
                        "situacao de programa fora do dominio: use A, I ou E"));
    }

    @Converter(autoApply = true)
    public static class ConversorJpa implements AttributeConverter<SituacaoPrograma, String> {

        @Override
        public String convertToDatabaseColumn(SituacaoPrograma situacao) {
            return situacao == null ? null : String.valueOf(situacao.codigo);
        }

        @Override
        public SituacaoPrograma convertToEntityAttribute(String codigo) {
            return codigo == null ? null : de(codigo);
        }
    }
}
