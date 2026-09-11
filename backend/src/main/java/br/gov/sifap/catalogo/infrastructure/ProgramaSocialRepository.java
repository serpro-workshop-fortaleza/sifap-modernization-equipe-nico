package br.gov.sifap.catalogo.infrastructure;

import br.gov.sifap.catalogo.domain.CodigoPrograma;
import br.gov.sifap.catalogo.domain.ProgramaSocial;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistencia do catalogo de programas sociais (REQ-001, REQ-011).
 */
public interface ProgramaSocialRepository
        extends JpaRepository<ProgramaSocial, CodigoPrograma>, ProgramaSocialInsercao {
}
