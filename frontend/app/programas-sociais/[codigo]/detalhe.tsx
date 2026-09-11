import type { ProgramaSocialConsultado, SituacaoPrograma, TipoPrograma } from '@/lib/api/tipos';

const TIPOS: Record<TipoPrograma, string> = {
  A: 'Assistência',
  P: 'Previdência',
  T: 'Trabalho',
};

const SITUACOES: Record<SituacaoPrograma, string> = {
  A: 'Ativo',
  I: 'Inativo',
  E: 'Encerrado',
};

const MOEDA = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

/** REQ-011: exatamente estes seis campos. Renda, idades e fator de ajuste nao sao apresentados. */
export function DetalheDoPrograma({ programa }: Readonly<{ programa: ProgramaSocialConsultado }>) {
  return (
    <dl className="divide-y divide-[var(--govbr-gray-20)] border-y border-[var(--govbr-gray-20)]">
      {[
        ['Código', programa.codigo],
        ['Nome', programa.nome],
        ['Tipo', TIPOS[programa.tipo] ?? programa.tipo],
        ['Valor base', MOEDA.format(programa.valorBase)],
        ['Código de elegibilidade', programa.codigoElegibilidade ?? 'Não informado'],
        ['Situação', SITUACOES[programa.situacao] ?? programa.situacao],
      ].map(([rotulo, valor]) => (
        <div className="grid gap-1 py-4 sm:grid-cols-[14rem_1fr] sm:gap-6" key={rotulo}>
          <dt className="font-semibold text-[var(--govbr-gray-70)]">{rotulo}</dt>
          <dd className="break-words text-[var(--govbr-gray-80)]">{valor}</dd>
        </div>
      ))}
    </dl>
  );
}

/** REQ-012: ausencia e informada como ausencia, nunca como erro de processamento. */
export function ProgramaNaoEncontrado({ codigo }: Readonly<{ codigo: string }>) {
  return (
    <div role="alert" className="border-l-4 border-[var(--govbr-warning-50)] bg-[var(--govbr-warning-10)] p-4 text-[var(--govbr-gray-80)]">
      <p className="font-bold">Programa não encontrado</p>
      <p className="mt-1">Não foi localizado um programa social com o código {codigo}.</p>
    </div>
  );
}
