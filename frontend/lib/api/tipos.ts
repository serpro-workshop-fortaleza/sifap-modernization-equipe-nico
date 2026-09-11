/**
 * Contratos do catalogo de programas sociais expostos por /api/v1/programas-sociais.
 * Os seis campos de ProgramaSocialConsultado espelham REQ-011 e nada alem disso.
 */

export type TipoPrograma = 'A' | 'P' | 'T';

export type SituacaoPrograma = 'A' | 'I' | 'E';

export interface InclusaoDeProgramaSocial {
  codigo: string;
  nome: string;
  tipo: string;
  valorBase: string;
  fatorAjuste: string;
  codigoElegibilidade: string | null;
  dataCriacao: string;
  dataEncerramento: string | null;
  rendaPercapitaMaxima: string | null;
  idadeMinima: number;
  idadeMaxima: number;
}

/** REQ-011: exatamente seis campos. Renda, idades e fator de ajuste ficam de fora. */
export interface ProgramaSocialConsultado {
  codigo: string;
  nome: string;
  tipo: TipoPrograma;
  valorBase: number;
  codigoElegibilidade: string | null;
  situacao: SituacaoPrograma;
}

/** Recusa devolvida pela API em application/problem+json. */
export interface RecusaDaApi {
  status: number;
  titulo: string;
  detalhe: string;
  /** Campo apontado por DadoInvalidoException, quando houver. */
  campo?: string;
}

export type ResultadoDeInclusao =
  | { situacao: 'incluido'; programa: ProgramaSocialConsultado }
  | { situacao: 'recusado'; recusa: RecusaDaApi };

export type ResultadoDeConsulta =
  | { situacao: 'encontrado'; programa: ProgramaSocialConsultado }
  | { situacao: 'nao-encontrado'; codigo: string };
