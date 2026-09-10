package br.gov.sifap.auditoria.infrastructure;

import br.gov.sifap.auditoria.application.ProvedorUsuarioResponsavel;
import org.springframework.stereotype.Component;

/**
 * Devolve sempre o usuario tecnico {@code SIFAPSYS}.
 *
 * <p>Enquanto nao houver autenticacao, a trilha nao atribui responsabilidade a pessoa alguma.
 */
@Component
public class UsuarioSistemaFixo implements ProvedorUsuarioResponsavel {

    public static final String USUARIO = "SIFAPSYS";

    // PROVISORIO: substituir pelo usuario autenticado antes de producao — ver P4 na secao 8 de plan.md.
    @Override
    public String obter() {
        return USUARIO;
    }
}
