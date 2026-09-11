import type { Metadata } from 'next';
import { incluirProgramaSocial } from './acoes';
import { FormularioDePrograma } from './formulario';

export const metadata: Metadata = {
  title: 'Incluir programa social — SIFAP',
};

export default function PaginaDeInclusao() {
  return (
    <main className="mx-auto flex max-w-2xl flex-col gap-6 p-8">
      <header>
        <h1 className="text-2xl font-semibold text-slate-900">Incluir programa social</h1>
        <p className="text-slate-600">
          O catalogo aceita apenas inclusao e consulta; nao ha alteracao nem exclusao.
        </p>
      </header>
      <FormularioDePrograma acao={incluirProgramaSocial} />
    </main>
  );
}
