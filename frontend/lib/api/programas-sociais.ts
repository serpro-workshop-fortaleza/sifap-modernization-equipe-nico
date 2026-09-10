import type {
  InclusaoDeProgramaSocial,
  ProgramaSocialConsultado,
  RecusaDaApi,
  ResultadoDeConsulta,
  ResultadoDeInclusao,
} from './tipos';

const PADRAO = 'http://localhost:8080';

/** A base fica em variavel de ambiente: nunca ha credencial nem host fixo no codigo. */
export function baseDaApi(): string {
  return process.env.SIFAP_API_URL ?? PADRAO;
}

function caminhoDoPrograma(codigo: string): string {
  return `${baseDaApi()}/api/v1/programas-sociais/${encodeURIComponent(codigo)}`;
}

/** Traduz o corpo problem+json da API para a recusa que a interface sabe apresentar. */
export async function lerRecusa(resposta: Response): Promise<RecusaDaApi> {
  let corpo: Record<string, unknown> = {};
  try {
    corpo = (await resposta.json()) as Record<string, unknown>;
  } catch {
    corpo = {};
  }

  const campo = typeof corpo.campo === 'string' ? corpo.campo : undefined;

  return {
    status: resposta.status,
    titulo: typeof corpo.title === 'string' ? corpo.title : 'Falha ao processar a solicitacao',
    detalhe:
      typeof corpo.detail === 'string' && corpo.detail.length > 0
        ? corpo.detail
        : 'A solicitacao nao pode ser concluida.',
    ...(campo === undefined ? {} : { campo }),
  };
}

/** REQ-001 a REQ-009: a validacao continua no backend; aqui so encaminhamos e traduzimos a recusa. */
export async function incluirPrograma(
  dados: InclusaoDeProgramaSocial,
): Promise<ResultadoDeInclusao> {
  const resposta = await fetch(`${baseDaApi()}/api/v1/programas-sociais`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados),
    cache: 'no-store',
  });

  if (!resposta.ok) {
    return { situacao: 'recusado', recusa: await lerRecusa(resposta) };
  }

  return { situacao: 'incluido', programa: (await resposta.json()) as ProgramaSocialConsultado };
}

/** REQ-011 e REQ-012: 404 e ausencia, nao erro de processamento. */
export async function consultarPrograma(codigo: string): Promise<ResultadoDeConsulta> {
  const resposta = await fetch(caminhoDoPrograma(codigo), { cache: 'no-store' });

  if (resposta.status === 404) {
    return { situacao: 'nao-encontrado', codigo };
  }

  if (!resposta.ok) {
    const recusa = await lerRecusa(resposta);
    throw new Error(recusa.detalhe);
  }

  return { situacao: 'encontrado', programa: (await resposta.json()) as ProgramaSocialConsultado };
}
