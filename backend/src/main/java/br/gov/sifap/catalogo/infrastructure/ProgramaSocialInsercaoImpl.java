package br.gov.sifap.catalogo.infrastructure;

import br.gov.sifap.catalogo.domain.ProgramaSocial;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class ProgramaSocialInsercaoImpl implements ProgramaSocialInsercao {

    @PersistenceContext
    private EntityManager gerenciador;

    @Override
    public ProgramaSocial inserir(ProgramaSocial programa) {
        gerenciador.persist(programa);
        gerenciador.flush();
        return programa;
    }
}
