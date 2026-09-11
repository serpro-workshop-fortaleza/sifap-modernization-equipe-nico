import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { ConsultaDePrograma } from '@/app/consulta-de-programa';

const { navegar } = vi.hoisted(() => ({ navegar: vi.fn() }));

vi.mock('next/navigation', () => ({
  useRouter: () => ({ push: navegar }),
}));

describe('ConsultaDePrograma', () => {
  it('should_navigate_to_the_program_page_when_a_valid_code_is_submitted', async () => {
    // REQ-011
    const usuario = userEvent.setup();
    render(<ConsultaDePrograma />);

    await usuario.type(screen.getByLabelText('Código do programa'), 'c001');
    await usuario.click(screen.getByRole('button', { name: 'Consultar' }));

    expect(navegar).toHaveBeenCalledWith('/programas-sociais/C001');
  });
});