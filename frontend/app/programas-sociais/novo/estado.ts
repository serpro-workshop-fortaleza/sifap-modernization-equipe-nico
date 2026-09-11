/** Estado do formulario de inclusao. Fica fora de acoes.ts porque um arquivo 'use server' so exporta funcoes async. */
export type EstadoDoFormulario =
  | { estado: 'inicial' }
  | { estado: 'incluido'; codigo: string }
  | { estado: 'recusado'; mensagem: string; campo?: string };

export const ESTADO_INICIAL: EstadoDoFormulario = { estado: 'inicial' };
