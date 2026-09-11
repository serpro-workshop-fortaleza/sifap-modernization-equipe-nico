package br.gov.sifap.catalogo.infrastructure;

import br.gov.sifap.catalogo.domain.ProgramaSocial;

/**
 * REQ-010: o catalogo so insere. Nunca use {@code save}, que faz merge e sobrescreveria
 * silenciosamente um codigo ja existente em vez de violar a chave primaria.
 */
public interface ProgramaSocialInsercao {

    ProgramaSocial inserir(ProgramaSocial programa);
}
