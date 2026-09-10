package br.gov.sifap.auditoria.infrastructure;

import br.gov.sifap.auditoria.domain.EventoAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistencia da trilha de auditoria. Somente insercao e leitura: alteracao e exclusao sao
 * recusadas pelo gatilho {@code tg_evento_auditoria_imutavel} (REQ-014).
 */
public interface EventoAuditoriaRepository extends JpaRepository<EventoAuditoria, Long> {
}
