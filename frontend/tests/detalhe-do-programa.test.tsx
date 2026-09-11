import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { DetalheDoPrograma, ProgramaNaoEncontrado } from '@/app/programas-sociais/[codigo]/detalhe';
import type { ProgramaSocialConsultado } from '@/lib/api/tipos';

const PROGRAMA: ProgramaSocialConsultado = {
  codigo: 'C001',
  nome: 'Bolsa Familia',
  tipo: 'A',
  valorBase: 600,
  codigoElegibilidade: 'E0001',
  situacao: 'A',
};

describe('DetalheDoPrograma', () => {
  it('should_present_exactly_the_six_fields_of_the_legacy_query', () => {
    // REQ-011
    const { container } = render(<DetalheDoPrograma programa={PROGRAMA} />);

    const rotulos = Array.from(container.querySelectorAll('dt')).map((dt) => dt.textContent);

    expect(rotulos).toEqual([
      'Código',
      'Nome',
      'Tipo',
      'Valor base',
      'Código de elegibilidade',
      'Situação',
    ]);
  });

  it('should_not_present_income_ages_or_adjustment_factor', () => {
    // REQ-011
    const { container } = render(<DetalheDoPrograma programa={PROGRAMA} />);

    // "elegibilidade" contem "idade": o rotulo precisa ser buscado por inteiro.
    expect(container.textContent).not.toMatch(
      /Renda per capita|Idade minima|Idade maxima|Fator de ajuste/i,
    );
  });

  it('should_translate_the_single_letter_domains_into_readable_labels', () => {
    // REQ-003
    render(<DetalheDoPrograma programa={PROGRAMA} />);

    expect(screen.getByText('Assistência')).toBeInTheDocument();
    expect(screen.getByText('Ativo')).toBeInTheDocument();
  });

  it('should_state_that_the_eligibility_code_is_absent_instead_of_showing_nothing', () => {
    // REQ-011
    render(<DetalheDoPrograma programa={{ ...PROGRAMA, codigoElegibilidade: null }} />);

    expect(screen.getByText('Não informado')).toBeInTheDocument();
  });
});

describe('ProgramaNaoEncontrado', () => {
  it('should_report_absence_naming_the_requested_code', () => {
    // REQ-012
    render(<ProgramaNaoEncontrado codigo="9999" />);

    expect(screen.getByRole('alert')).toHaveTextContent(
      'Não foi localizado um programa social com o código 9999.',
    );
  });
});
