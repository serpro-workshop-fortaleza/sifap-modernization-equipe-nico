import type { ProgramaSocialConsultado, SituacaoPrograma, TipoPrograma } from '@/lib/api/tipos';

const TIPOS: Record<TipoPrograma, string> = {
  A: 'Assistencia',
  P: 'Previdencia',
  T: 'Trabalho',
};

const SITUACOES: Record<SituacaoPrograma, string> = {
  A: 'Ativo',
  I: 'Inativo',
  E: 'Encerrado',
};

const MOEDA = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

/** REQ-011: exatamente estes seis campos. Renda, idades e fator de ajuste nao sao apresentados. */
export function DetalheDoPrograma({ programa }: { programa: ProgramaSocialConsultado }) {
  return (
    <dl className="grid grid-cols-[12rem_1fr] gap-y-2">
      <dt className="font-medium text-slate-700">Codigo</dt>
      <dd className="text-slate-900">{programa.codigo}</dd>

      <dt className="font-medium text-slate-700">Nome</dt>
      <dd className="text-slate-900">{programa.nome}</dd>

      <dt className="font-medium text-slate-700">Tipo</dt>
      <dd className="text-slate-900">{TIPOS[programa.tipo] ?? programa.tipo}</dd>

      <dt className="font-medium text-slate-700">Valor base</dt>
      <dd className="text-slate-900">{MOEDA.format(programa.valorBase)}</dd>

      <dt className="font-medium text-slate-700">Codigo de elegibilidade</dt>
      <dd className="text-slate-900">{programa.codigoElegibilidade ?? 'Nao informado'}</dd>

      <dt className="font-medium text-slate-700">Situacao</dt>
      <dd className="text-slate-900">{SITUACOES[programa.situacao] ?? programa.situacao}</dd>
    </dl>
  );
}

/** REQ-012: ausencia e informada como ausencia, nunca como erro de processamento. */
export function ProgramaNaoEncontrado({ codigo }: { codigo: string }) {
  return (
    <p role="alert" className="rounded border border-amber-300 bg-amber-50 px-3 py-2 text-amber-900">
      Programa social {codigo} nao encontrado.
    </p>
  );
}
