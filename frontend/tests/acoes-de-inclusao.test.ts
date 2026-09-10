import { beforeEach, describe, expect, it, vi } from 'vitest';
import { incluirProgramaSocial, montarInclusao } from '@/app/programas-sociais/novo/acoes';
import { ESTADO_INICIAL } from '@/app/programas-sociais/novo/estado';
import { incluirPrograma } from '@/lib/api/programas-sociais';

vi.mock('@/lib/api/programas-sociais', () => ({ incluirPrograma: vi.fn() }));

const incluirProgramaFalso = vi.mocked(incluirPrograma);

function formulario(sobrescritas: Record<string, string> = {}): FormData {
  const dados = new FormData();
  const base: Record<string, string> = {
    codigo: 'C001',
    nome: 'Bolsa Familia',
    tipo: 'A',
    valorBase: '600.00',
    fatorAjuste: '0.0500',
    codigoElegibilidade: 'E0001',
    dataCriacao: '2026-01-15',
    dataEncerramento: '',
    rendaPercapitaMaxima: '218.00',
    idadeMinima: '0',
    idadeMaxima: '17',
  };
  for (const [chave, valor] of Object.entries({ ...base, ...sobrescritas })) {
    dados.set(chave, valor);
  }
  return dados;
}

beforeEach(() => {
  incluirProgramaFalso.mockReset();
});

describe('incluirProgramaSocial', () => {
  it('should_send_an_empty_end_date_as_null_so_validity_stays_open_ended', async () => {
    // REQ-004
    const inclusao = await montarInclusao(formulario());

    expect(inclusao.dataEncerramento).toBeNull();
    expect(inclusao.dataCriacao).toBe('2026-01-15');
  });

  it('should_never_send_the_situation_because_the_backend_always_activates_the_program', async () => {
    // REQ-003
    const inclusao = await montarInclusao(formulario());

    expect(Object.keys(inclusao)).not.toContain('situacao');
  });

  it('should_send_the_adjustment_factor_in_its_own_field_without_applying_it', async () => {
    // REQ-005
    const inclusao = await montarInclusao(formulario());

    expect(inclusao.fatorAjuste).toBe('0.0500');
    expect(inclusao.valorBase).toBe('600.00');
  });

  it('should_return_the_inserted_code_when_the_api_accepts', async () => {
    // REQ-001
    incluirProgramaFalso.mockResolvedValue({
      situacao: 'incluido',
      programa: {
        codigo: 'C001',
        nome: 'Bolsa Familia',
        tipo: 'A',
        valorBase: 600,
        codigoElegibilidade: 'E0001',
        situacao: 'A',
      },
    });

    await expect(incluirProgramaSocial(ESTADO_INICIAL, formulario())).resolves.toEqual({
      estado: 'incluido',
      codigo: 'C001',
    });
  });

  it('should_carry_the_field_from_the_problem_json_into_the_form_state', async () => {
    // REQ-008
    incluirProgramaFalso.mockResolvedValue({
      situacao: 'recusado',
      recusa: {
        status: 400,
        titulo: 'Dado invalido',
        detalhe: 'idade minima maior que a idade maxima',
        campo: 'idadeMinima',
      },
    });

    await expect(
      incluirProgramaSocial(ESTADO_INICIAL, formulario({ idadeMinima: '30', idadeMaxima: '10' })),
    ).resolves.toEqual({
      estado: 'recusado',
      mensagem: 'idade minima maior que a idade maxima',
      campo: 'idadeMinima',
    });
  });

  it('should_keep_the_message_without_a_field_when_the_refusal_names_none', async () => {
    // REQ-002
    incluirProgramaFalso.mockResolvedValue({
      situacao: 'recusado',
      recusa: {
        status: 409,
        titulo: 'Codigo ja existente',
        detalhe: 'ja existe um programa social com o codigo informado',
      },
    });

    await expect(incluirProgramaSocial(ESTADO_INICIAL, formulario())).resolves.toEqual({
      estado: 'recusado',
      mensagem: 'ja existe um programa social com o codigo informado',
    });
  });
});
