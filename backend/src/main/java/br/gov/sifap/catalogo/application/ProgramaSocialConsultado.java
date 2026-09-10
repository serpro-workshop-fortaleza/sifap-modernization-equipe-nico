package br.gov.sifap.catalogo.application;

import br.gov.sifap.catalogo.domain.ProgramaSocial;

import java.math.BigDecimal;

/**
 * REQ-011 — a consulta devolve exatamente seis campos; os demais dados persistidos
 * permanecem fora da resposta (cenario 2 de REQ-011).
 */
public record ProgramaSocialConsultado(
        String codigo,
        String nome,
        String tipo,
        BigDecimal valorBase,
        String codigoElegibilidade,
        String situacao) {

    public static ProgramaSocialConsultado de(ProgramaSocial programa) {
        return new ProgramaSocialConsultado(
                programa.codigo().valor(),
                programa.nome(),
                String.valueOf(programa.tipo().codigo()),
                programa.valorBase().valor(),
                programa.codigoElegibilidade().orElse(null),
                String.valueOf(programa.situacao().codigo()));
    }
}
