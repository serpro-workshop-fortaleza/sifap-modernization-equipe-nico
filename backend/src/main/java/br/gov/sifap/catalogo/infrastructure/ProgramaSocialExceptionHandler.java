package br.gov.sifap.catalogo.infrastructure;

import br.gov.sifap.catalogo.application.ProgramaNaoEncontradoException;
import br.gov.sifap.catalogo.domain.DadoInvalidoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduz as recusas do catalogo para {@code application/problem+json}.
 */
@RestControllerAdvice(assignableTypes = ProgramaSocialController.class)
public class ProgramaSocialExceptionHandler {

    @ExceptionHandler(DadoInvalidoException.class)
    public ProblemDetail dadoInvalido(DadoInvalidoException erro) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, erro.getMessage());
        problema.setTitle("Dado invalido");
        problema.setProperty("campo", erro.campo());
        return problema;
    }

    @ExceptionHandler(ProgramaNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(ProgramaNaoEncontradoException erro) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, erro.getMessage());
        problema.setTitle("Programa nao encontrado");
        problema.setProperty("codigo", erro.codigo());
        return problema;
    }

    /** Traducao da violacao de chave primaria, conforme a decisao 5 da secao 6 de spec.md. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail codigoJaExistente(DataIntegrityViolationException erro) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "ja existe um programa social com o codigo informado");
        problema.setTitle("Codigo ja existente");
        return problema;
    }
}
