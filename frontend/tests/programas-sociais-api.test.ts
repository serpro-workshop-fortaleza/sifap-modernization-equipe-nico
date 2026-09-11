import { afterEach, describe, expect, it, vi } from 'vitest';
import { consultarPrograma, incluirPrograma } from '@/lib/api/programas-sociais';
import type { InclusaoDeProgramaSocial } from '@/lib/api/tipos';

const INCLUSAO: InclusaoDeProgramaSocial = {
  codigo: 'C001',
  nome: 'Bolsa Familia',
  tipo: 'A',
  valorBase: '600.00',
  fatorAjuste: '0.0500',
  codigoElegibilidade: 'E0001',
  dataCriacao: '2026-01-15',
  dataEncerramento: null,
  rendaPercapitaMaxima: '218.00',
  idadeMinima: 0,
  idadeMaxima: 17,
};

const PROGRAMA = {
  codigo: 'C001',
  nome: 'Bolsa Familia',
  tipo: 'A',
  valorBase: 600.0,
  codigoElegibilidade: 'E0001',
  situacao: 'A',
};

function respostaDeProblema(status: number, corpo: Record<string, unknown>): Response {
  return new Response(JSON.stringify(corpo), {
    status,
    headers: { 'Content-Type': 'application/problem+json' },
  });
}

afterEach(() => {
  vi.unstubAllGlobals();
});

describe('cliente do catalogo de programas sociais', () => {
  it('should_return_the_created_program_when_the_api_accepts_the_insertion', async () => {
    // REQ-001
    const fetchFalso = vi.fn().mockResolvedValue(
      new Response(JSON.stringify(PROGRAMA), {
        status: 201,
        headers: { 'Content-Type': 'application/json' },
      }),
    );
    vi.stubGlobal('fetch', fetchFalso);

    const resultado = await incluirPrograma(INCLUSAO);

    expect(resultado).toEqual({ situacao: 'incluido', programa: PROGRAMA });
    const [url, opcoes] = fetchFalso.mock.calls[0];
    expect(url).toMatch(/\/api\/v1\/programas-sociais$/);
    expect(opcoes.method).toBe('POST');
  });

  it('should_expose_the_offending_field_when_the_api_refuses_the_amount', async () => {
    // REQ-006
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        respostaDeProblema(400, {
          title: 'Dado invalido',
          detail: 'valor base acima do limite de 99.999,99',
          campo: 'valorBase',
        }),
      ),
    );

    const resultado = await incluirPrograma({ ...INCLUSAO, valorBase: '100000.00' });

    expect(resultado).toEqual({
      situacao: 'recusado',
      recusa: {
        status: 400,
        titulo: 'Dado invalido',
        detalhe: 'valor base acima do limite de 99.999,99',
        campo: 'valorBase',
      },
    });
  });

  it('should_refuse_without_a_field_when_the_code_already_exists', async () => {
    // REQ-002
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        respostaDeProblema(409, {
          title: 'Codigo ja existente',
          detail: 'ja existe um programa social com o codigo informado',
        }),
      ),
    );

    const resultado = await incluirPrograma(INCLUSAO);

    expect(resultado.situacao).toBe('recusado');
    if (resultado.situacao === 'recusado') {
      expect(resultado.recusa.status).toBe(409);
      expect(resultado.recusa.campo).toBeUndefined();
    }
  });

  it('should_return_the_six_published_fields_when_the_program_exists', async () => {
    // REQ-011
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        new Response(JSON.stringify(PROGRAMA), {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
        }),
      ),
    );

    const resultado = await consultarPrograma('C001');

    expect(resultado.situacao).toBe('encontrado');
    if (resultado.situacao === 'encontrado') {
      expect(Object.keys(resultado.programa)).toHaveLength(6);
      expect(resultado.programa.codigo).toBe('C001');
    }
  });

  it('should_report_absence_instead_of_error_when_the_api_answers_404', async () => {
    // REQ-012
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        respostaDeProblema(404, {
          title: 'Programa nao encontrado',
          detail: 'programa social 9999 nao encontrado',
          codigo: '9999',
        }),
      ),
    );

    await expect(consultarPrograma('9999')).resolves.toEqual({
      situacao: 'nao-encontrado',
      codigo: '9999',
    });
  });

  it('should_raise_an_error_when_the_api_fails_for_any_other_reason', async () => {
    // REQ-012
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        respostaDeProblema(500, { title: 'Erro interno', detail: 'falha inesperada' }),
      ),
    );

    await expect(consultarPrograma('C001')).rejects.toThrow('falha inesperada');
  });
});
