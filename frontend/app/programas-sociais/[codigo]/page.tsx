import { consultarPrograma } from '@/lib/api/programas-sociais';
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
    <main className="mx-auto flex max-w-2xl flex-col gap-6 p-8">
      <h1 className="text-2xl font-semibold text-slate-900">Programa social</h1>
      {resultado.situacao === 'encontrado' ? (
        <DetalheDoPrograma programa={resultado.programa} />
      ) : (
        <ProgramaNaoEncontrado codigo={resultado.codigo} />
      )}
    </main>
  );
}
