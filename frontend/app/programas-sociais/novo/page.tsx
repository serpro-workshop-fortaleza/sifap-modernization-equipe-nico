import type { Metadata } from 'next';
import { Migalhas } from '@/components/migalhas';
import { incluirProgramaSocial } from './acoes';
import { FormularioDePrograma } from './formulario';

export const metadata: Metadata = {
  title: 'Incluir programa social — SIFAP',
};

export default function PaginaDeInclusao() {
  return (
    <main id="conteudo-principal" className="bg-[var(--govbr-gray-5)]" tabIndex={-1}>
      <div className="mx-auto max-w-4xl px-4 py-10 sm:px-6 md:py-14">
        <Migalhas itens={[{ rotulo: 'Cadastrar programa' }]} />
        <header className="mb-8 border-b border-[var(--govbr-gray-20)] pb-6">
          <p className="mb-3 text-sm font-bold uppercase text-[var(--govbr-blue-warm-vivid-70)]">
            Catálogo de programas sociais
          </p>
          <h1 className="text-3xl font-bold leading-tight text-[var(--govbr-blue-warm-vivid-90)] md:text-4xl">
            Cadastrar programa social
          </h1>
          <p className="mt-4 max-w-3xl leading-7 text-[var(--govbr-gray-60)]">
            Preencha os dados abaixo. Os campos marcados como obrigatórios devem ser informados.
          </p>
        </header>
        <div className="border-t-4 border-[var(--govbr-blue-warm-vivid-70)] bg-white p-5 shadow-sm sm:p-8">
          <FormularioDePrograma acao={incluirProgramaSocial} />
        </div>
      </div>
    </main>
  );
}
