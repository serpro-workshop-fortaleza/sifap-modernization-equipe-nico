package br.gov.sifap.catalogo.application;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dados aceitos na inclusao de um programa social (REQ-001 a REQ-009).
 *
 * <p>A situacao nao aparece aqui de proposito: e atribuida pelo sistema (REQ-003).
 */
public record InclusaoDeProgramaSocial(
        String codigo,
        String nome,
        String tipo,
        BigDecimal valorBase,
        BigDecimal fatorAjuste,
        String codigoElegibilidade,
        LocalDate dataCriacao,
        LocalDate dataEncerramento,
        BigDecimal rendaPercapitaMaxima,
        Integer idadeMinima,
        Integer idadeMaxima) {
}
