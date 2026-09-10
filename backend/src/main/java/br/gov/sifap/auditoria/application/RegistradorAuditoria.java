package br.gov.sifap.auditoria.application;

import br.gov.sifap.auditoria.domain.AcaoAuditoria;
import br.gov.sifap.auditoria.domain.EventoAuditoria;
import br.gov.sifap.auditoria.domain.TipoEntidade;
import br.gov.sifap.auditoria.infrastructure.EventoAuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Ponto unico de gravacao da trilha de auditoria (REQ-014).
 *
 * <p>Propagacao {@code REQUIRES_NEW}: o evento e confirmado em transacao propria e sobrevive ao
 * rollback da transacao de negocio (REQ-017), conforme a decisao 2 do ADR-0003.
 */
@Service
public class RegistradorAuditoria {

    private final EventoAuditoriaRepository repositorio;
    private final ProvedorUsuarioResponsavel provedorUsuario;
    private final Clock relogio;

    public RegistradorAuditoria(EventoAuditoriaRepository repositorio,
                                ProvedorUsuarioResponsavel provedorUsuario,
                                Clock relogio) {
        this.repositorio = repositorio;
        this.provedorUsuario = provedorUsuario;
        this.relogio = relogio;
    }

    /**
     * Grava um evento em transacao independente.
     *
     * @throws AcaoNaoAuditavelException se a acao for consulta (REQ-016)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long registrar(SolicitacaoDeRegistro solicitacao) {
        AcaoAuditoria acao = AcaoAuditoria.de(solicitacao.codigoAcao());
        if (!acao.auditavel()) {
            throw new AcaoNaoAuditavelException(solicitacao.codigoAcao());
        }
        EventoAuditoria evento = EventoAuditoria.registrar(
                LocalDateTime.now(relogio),
                acao,
                solicitacao.moduloOrigem(),
                solicitacao.descricaoAcao(),
                TipoEntidade.de(solicitacao.tipoEntidade()),
                solicitacao.idEntidade(),
                solicitacao.cpfAfetado(),
                provedorUsuario.obter());
        return repositorio.saveAndFlush(evento).id();
    }
}
