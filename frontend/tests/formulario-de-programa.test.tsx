import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { FormularioDePrograma } from '@/app/programas-sociais/novo/formulario';
import type { EstadoDoFormulario } from '@/app/programas-sociais/novo/estado';

function preencherObrigatorios() {
  return {
    codigo: screen.getByLabelText('Codigo'),
    nome: screen.getByLabelText('Nome'),
    valorBase: screen.getByLabelText('Valor base'),
  };
}

describe('FormularioDePrograma', () => {
  it('should_render_every_field_required_by_the_insertion_contract', () => {
    // REQ-001
    render(<FormularioDePrograma acao={vi.fn()} />);

    for (const rotulo of [
      'Codigo',
      'Nome',
      'Tipo',
      'Valor base',
      'Fator de ajuste',
      'Codigo de elegibilidade',
      'Data de criacao',
      'Data de encerramento',
      'Renda per capita maxima',
      'Idade minima',
      'Idade maxima',
    ]) {
      expect(screen.getByLabelText(rotulo)).toBeInTheDocument();
    }
    expect(screen.getByRole('button', { name: 'Incluir programa' })).toBeInTheDocument();
  });

  it('should_announce_that_an_empty_end_date_means_open_ended_validity', () => {
    // REQ-004
    render(<FormularioDePrograma acao={vi.fn()} />);

    expect(screen.getByLabelText('Data de encerramento')).toHaveAccessibleDescription(
      'Deixe em branco para vigencia indeterminada.',
    );
  });

  it('should_show_the_problem_json_detail_and_mark_the_offending_field_when_the_api_refuses', async () => {
    // REQ-006
    const acao = vi.fn(
      async (): Promise<EstadoDoFormulario> => ({
        estado: 'recusado',
        mensagem: 'valor base acima do limite de 99.999,99',
        campo: 'valorBase',
      }),
    );
    render(<FormularioDePrograma acao={acao} />);

    const campos = preencherObrigatorios();
    await userEvent.type(campos.codigo, 'C001');
    await userEvent.type(campos.nome, 'Bolsa Familia');
    await userEvent.type(campos.valorBase, '100000');
    await userEvent.click(screen.getByRole('button', { name: 'Incluir programa' }));

    expect(await screen.findByRole('alert')).toHaveTextContent(
      'valor base acima do limite de 99.999,99',
    );
    expect(screen.getByLabelText('Valor base')).toHaveAttribute('aria-invalid', 'true');
    expect(screen.getByLabelText('Codigo')).toHaveAttribute('aria-invalid', 'false');
  });

  it('should_confirm_the_insertion_with_the_program_code_when_the_api_accepts', async () => {
    // REQ-001
    const acao = vi.fn(
      async (): Promise<EstadoDoFormulario> => ({ estado: 'incluido', codigo: 'C001' }),
    );
    render(<FormularioDePrograma acao={acao} />);

    await userEvent.click(screen.getByRole('button', { name: 'Incluir programa' }));

    expect(await screen.findByRole('status')).toHaveTextContent(
      'Programa C001 incluido com sucesso.',
    );
    expect(screen.queryByRole('alert')).not.toBeInTheDocument();
  });

  it('should_offer_only_the_three_program_types_declared_by_the_ddm', () => {
    // REQ-007
    render(<FormularioDePrograma acao={vi.fn()} />);

    const opcoes = screen.getAllByRole('option').map((opcao) => (opcao as HTMLOptionElement).value);

    expect(opcoes).toEqual(['A', 'P', 'T']);
  });
});
