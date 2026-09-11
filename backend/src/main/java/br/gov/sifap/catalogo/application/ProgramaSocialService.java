package br.gov.sifap.catalogo.application;

import br.gov.sifap.auditoria.application.RegistradorAuditoria;
import br.gov.sifap.auditoria.application.SolicitacaoDeRegistro;
import br.gov.sifap.catalogo.domain.CodigoPrograma;
import br.gov.sifap.catalogo.domain.FaixaEtaria;
import br.gov.sifap.catalogo.domain.ProgramaSocial;
import br.gov.sifap.catalogo.domain.TipoPrograma;
import br.gov.sifap.catalogo.domain.ValorBase;
import br.gov.sifap.catalogo.domain.Vigencia;
import br.gov.sifap.catalogo.infrastructure.ProgramaSocialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Casos de uso do catalogo: apenas inclusao e consulta (REQ-010).
 */
@Service
public class ProgramaSocialService {

    private static final String MODULO = "CADPROG";

    private final ProgramaSocialRepository repositorio;
    private final RegistradorAuditoria auditoria;

    public ProgramaSocialService(ProgramaSocialRepository repositorio, RegistradorAuditoria auditoria) {
        this.repositorio = repositorio;
        this.auditoria = auditoria;
    }

    /**
     * REQ-001 e REQ-015. O evento de auditoria so e registrado depois da persistencia, de modo que
     * uma inclusao recusada na validacao nao produz evento algum. Qualquer erro posterior desfaz o
     * catalogo (REQ-013) sem alcancar o evento, que vive em transacao propria (REQ-017).
     */
    @Transactional
    public ProgramaSocialConsultado incluir(InclusaoDeProgramaSocial solicitacao) {
        ProgramaSocial programa = ProgramaSocial.incluir(
                CodigoPrograma.de(solicitacao.codigo()),
                solicitacao.nome(),
                TipoPrograma.de(solicitacao.tipo()),
                new ValorBase(solicitacao.valorBase()),
                solicitacao.fatorAjuste(),
                solicitacao.codigoElegibilidade(),
                Vigencia.de(solicitacao.dataCriacao(), solicitacao.dataEncerramento()),
                solicitacao.rendaPercapitaMaxima(),
                new FaixaEtaria(zeroSeAusente(solicitacao.idadeMinima()), zeroSeAusente(solicitacao.idadeMaxima())));

        ProgramaSocial persistido = repositorio.inserir(programa);

        auditoria.registrar(SolicitacaoDeRegistro.inclusaoDePrograma(
                MODULO, persistido.codigo().valor(), "Inclusao de programa social"));

        return ProgramaSocialConsultado.de(persistido);
    }

    /** REQ-011, REQ-012 e REQ-016: consulta nao gera evento de auditoria. */
    @Transactional(readOnly = true)
    public ProgramaSocialConsultado consultar(String codigo) {
        CodigoPrograma chave = CodigoPrograma.de(codigo);
        return repositorio.findById(chave)
                .map(ProgramaSocialConsultado::de)
                .orElseThrow(() -> new ProgramaNaoEncontradoException(chave.valor()));
    }

    private static int zeroSeAusente(Integer informado) {
        return Optional.ofNullable(informado).orElse(0);
    }
}
