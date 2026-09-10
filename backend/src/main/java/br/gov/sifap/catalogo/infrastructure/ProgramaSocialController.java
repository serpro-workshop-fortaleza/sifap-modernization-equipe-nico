package br.gov.sifap.catalogo.infrastructure;

import br.gov.sifap.catalogo.application.InclusaoDeProgramaSocial;
import br.gov.sifap.catalogo.application.ProgramaSocialConsultado;
import br.gov.sifap.catalogo.application.ProgramaSocialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * REQ-010: o catalogo expoe apenas inclusao e consulta. Qualquer outro metodo sobre estes
 * caminhos e recusado pelo proprio Spring com {@code 405 Method Not Allowed}.
 */
@RestController
@RequestMapping("/api/v1/programas-sociais")
@Tag(name = "Programas sociais", description = "Catalogo de programas sociais (REQ-001 a REQ-012)")
public class ProgramaSocialController {

    private final ProgramaSocialService servico;

    public ProgramaSocialController(ProgramaSocialService servico) {
        this.servico = servico;
    }

    @PostMapping
    @Operation(summary = "Inclui um programa social")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Programa incluido"),
            @ApiResponse(responseCode = "400", description = "Dado invalido", content = @io.swagger.v3.oas.annotations.media.Content),
            @ApiResponse(responseCode = "409", description = "Codigo ja existente", content = @io.swagger.v3.oas.annotations.media.Content)})
    public ResponseEntity<ProgramaSocialConsultado> incluir(@RequestBody InclusaoDeProgramaSocial solicitacao,
                                                            UriComponentsBuilder construtor) {
        ProgramaSocialConsultado incluido = servico.incluir(solicitacao);
        URI local = construtor.path("/api/v1/programas-sociais/{codigo}")
                .buildAndExpand(incluido.codigo())
                .toUri();
        return ResponseEntity.created(local).body(incluido);
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Consulta um programa social pelo codigo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Programa encontrado"),
            @ApiResponse(responseCode = "404", description = "Programa nao encontrado", content = @io.swagger.v3.oas.annotations.media.Content)})
    public ProgramaSocialConsultado consultar(@PathVariable String codigo) {
        return servico.consultar(codigo);
    }
}
