'use server';

import { incluirPrograma } from '@/lib/api/programas-sociais';
import type { InclusaoDeProgramaSocial } from '@/lib/api/tipos';
import type { EstadoDoFormulario } from './estado';

function texto(dados: FormData, nome: string): string {
  const valor = dados.get(nome);
  return typeof valor === 'string' ? valor.trim() : '';
}

function opcional(dados: FormData, nome: string): string | null {
  const valor = texto(dados, nome);
  return valor.length === 0 ? null : valor;
}

function inteiro(dados: FormData, nome: string): number {
  const valor = Number.parseInt(texto(dados, nome), 10);
  return Number.isNaN(valor) ? 0 : valor;
}

/** Monta o corpo da inclusao sem validar regra de negocio: quem valida e o backend (REQ-006 a REQ-009). */
export async function montarInclusao(dados: FormData): Promise<InclusaoDeProgramaSocial> {
  return {
    codigo: texto(dados, 'codigo'),
    nome: texto(dados, 'nome'),
    tipo: texto(dados, 'tipo'),
    valorBase: texto(dados, 'valorBase'),
    fatorAjuste: texto(dados, 'fatorAjuste') || '0',
    codigoElegibilidade: opcional(dados, 'codigoElegibilidade'),
    dataCriacao: texto(dados, 'dataCriacao'),
    dataEncerramento: opcional(dados, 'dataEncerramento'),
    rendaPercapitaMaxima: opcional(dados, 'rendaPercapitaMaxima'),
    idadeMinima: inteiro(dados, 'idadeMinima'),
    idadeMaxima: inteiro(dados, 'idadeMaxima'),
  };
}

/** REQ-001: encaminha a inclusao e devolve a mensagem do problem+json quando ha recusa. */
export async function incluirProgramaSocial(
  _anterior: EstadoDoFormulario,
  dados: FormData,
): Promise<EstadoDoFormulario> {
  const resultado = await incluirPrograma(await montarInclusao(dados));

  if (resultado.situacao === 'recusado') {
    const { detalhe, campo } = resultado.recusa;
    return campo === undefined
      ? { estado: 'recusado', mensagem: detalhe }
      : { estado: 'recusado', mensagem: detalhe, campo };
  }

  return { estado: 'incluido', codigo: resultado.programa.codigo };
}
