import { consultarPrograma } from '@/lib/api/programas-sociais';
import { Migalhas } from '@/components/migalhas';
import { DetalheDoPrograma, ProgramaNaoEncontrado } from './detalhe';

export const dynamic = 'force-dynamic';

export default async function PaginaDeConsulta({
  params,
}: {
  params: Promise<{ codigo: string }>;
}) {
  const { codigo } = await params;
  const resultado = await consultarPrograma(codigo);

  return (
    <main id="conteudo-principal" className="bg-[var(--govbr-gray-5)]" tabIndex={-1}>
      <div className="mx-auto max-w-4xl px-4 py-10 sm:px-6 md:py-14">
        <Migalhas itens={[{ rotulo: 'Consultar programa' }]} />
        <header className="mb-8 border-b border-[var(--govbr-gray-20)] pb-6">
          <p className="mb-3 text-sm font-bold uppercase text-[var(--govbr-blue-warm-vivid-70)]">
            Catálogo de programas sociais
          </p>
          <h1 className="text-3xl font-bold leading-tight text-[var(--govbr-blue-warm-vivid-90)] md:text-4xl">
            Programa social
          </h1>
        </header>
        <div className="border-t-4 border-[var(--govbr-blue-warm-vivid-70)] bg-white p-5 shadow-sm sm:p-8">
          {resultado.situacao === 'encontrado' ? (
            <DetalheDoPrograma programa={resultado.programa} />
          ) : (
            <ProgramaNaoEncontrado codigo={resultado.codigo} />
          )}
        </div>
      </div>
    </main>
  );
}
