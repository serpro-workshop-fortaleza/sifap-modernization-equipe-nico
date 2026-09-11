package br.gov.sifap.catalogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

/**
 * REQ-002 — codigo alfanumerico de exatamente quatro posicoes.
 * A chave real do arquivo 151 e {@code A 4}; a tela legada so alcanca a faixa numerica.
 */
@Embeddable
public record CodigoPrograma(@Column(name = "codigo", length = 4, nullable = false) String valor) {

    private static final int POSICOES = 4;
    private static final Pattern ALFANUMERICO = Pattern.compile("[A-Za-z0-9]{" + POSICOES + "}");

    public CodigoPrograma {
        if (valor == null || !ALFANUMERICO.matcher(valor).matches()) {
            throw new DadoInvalidoException("codigo",
                    "o codigo do programa deve ter exatamente quatro posicoes alfanumericas");
        }
    }

    /**
     * Normaliza entrada numerica com zeros a esquerda, como CADPROG.NSP:L110 faz ao
     * converter a variavel de tela N4 para a chave A4.
     */
    public static CodigoPrograma de(String informado) {
        if (informado == null || informado.isBlank()) {
            throw new DadoInvalidoException("codigo", "o codigo do programa e obrigatorio");
        }
        String limpo = informado.strip();
        if (limpo.length() < POSICOES && limpo.chars().allMatch(Character::isDigit)) {
            limpo = "0".repeat(POSICOES - limpo.length()) + limpo;
        }
        return new CodigoPrograma(limpo);
    }

    @Override
    public String toString() {
        return valor;
    }
}
