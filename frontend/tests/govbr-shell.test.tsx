import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { CabecalhoGovBr, RodapeGovBr } from '@/components/govbr-shell';

describe('CabecalhoGovBr', () => {
  it('should_offer_institutional_identity_primary_navigation_and_a_skip_link', () => {
    render(<CabecalhoGovBr />);

    expect(screen.getByRole('link', { name: 'Ir para o conteúdo' })).toHaveAttribute(
      'href',
      '#conteudo-principal',
    );
    expect(screen.getByRole('link', { name: /gov.br — página inicial do SIFAP/i })).toBeInTheDocument();
    expect(screen.getByRole('navigation', { name: 'Navegação principal' })).toBeInTheDocument();
  });
});

describe('RodapeGovBr', () => {
  it('should_identify_the_service_and_its_prototype_context', () => {
    render(<RodapeGovBr />);

    expect(screen.getByRole('contentinfo')).toHaveTextContent(
      'Sistema de Fiscalização e Administração de Pagamentos',
    );
    expect(screen.getByText(/Protótipo de modernização/)).toBeInTheDocument();
  });
});