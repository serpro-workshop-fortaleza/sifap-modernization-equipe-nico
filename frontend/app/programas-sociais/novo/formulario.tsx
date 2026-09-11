'use client';

import { useActionState } from 'react';
import { ESTADO_INICIAL, type EstadoDoFormulario } from './estado';

type Envio = (anterior: EstadoDoFormulario, dados: FormData) => Promise<EstadoDoFormulario>;

const ROTULO = 'block text-sm font-medium text-slate-700';
const CAMPO = 'mt-1 w-full rounded border border-slate-300 px-3 py-2 text-slate-900';

export function FormularioDePrograma({ acao }: { acao: Envio }) {
  const [estado, enviar, enviando] = useActionState(acao, ESTADO_INICIAL);
  const campoRecusado = estado.estado === 'recusado' ? estado.campo : undefined;

  return (
    <form action={enviar} className="flex flex-col gap-4" noValidate>
      {estado.estado === 'recusado' && (
        <p role="alert" className="rounded border border-red-300 bg-red-50 px-3 py-2 text-red-800">
          {estado.mensagem}
        </p>
      )}

      {estado.estado === 'incluido' && (
        <p role="status" className="rounded border border-green-300 bg-green-50 px-3 py-2 text-green-900">
          Programa {estado.codigo} incluido com sucesso.
        </p>
      )}

      <div>
        <label className={ROTULO} htmlFor="codigo">
          Codigo
        </label>
        <input
          className={CAMPO}
          id="codigo"
          name="codigo"
          maxLength={4}
          required
          aria-invalid={campoRecusado === 'codigo'}
        />
      </div>

      <div>
        <label className={ROTULO} htmlFor="nome">
          Nome
        </label>
        <input
          className={CAMPO}
          id="nome"
          name="nome"
          maxLength={60}
          required
          aria-invalid={campoRecusado === 'nome'}
        />
      </div>

      <div>
        <label className={ROTULO} htmlFor="tipo">
          Tipo
        </label>
        <select className={CAMPO} id="tipo" name="tipo" required aria-invalid={campoRecusado === 'tipo'}>
          <option value="A">A — Assistencia</option>
          <option value="P">P — Previdencia</option>
          <option value="T">T — Trabalho</option>
        </select>
      </div>

      <div>
        <label className={ROTULO} htmlFor="valorBase">
          Valor base
        </label>
        <input
          className={CAMPO}
          id="valorBase"
          name="valorBase"
          type="number"
          step="0.01"
          min="0"
          required
          aria-invalid={campoRecusado === 'valorBase'}
        />
      </div>

      <div>
        <label className={ROTULO} htmlFor="fatorAjuste">
          Fator de ajuste
        </label>
        <input
          className={CAMPO}
          id="fatorAjuste"
          name="fatorAjuste"
          type="number"
          step="0.0001"
          min="0"
          defaultValue="0"
          aria-invalid={campoRecusado === 'fatorAjuste'}
        />
      </div>

      <div>
        <label className={ROTULO} htmlFor="codigoElegibilidade">
          Codigo de elegibilidade
        </label>
        <input className={CAMPO} id="codigoElegibilidade" name="codigoElegibilidade" maxLength={5} />
      </div>

      <div>
        <label className={ROTULO} htmlFor="dataCriacao">
          Data de criacao
        </label>
        <input
          className={CAMPO}
          id="dataCriacao"
          name="dataCriacao"
          type="date"
          required
          aria-invalid={campoRecusado === 'dataCriacao'}
        />
      </div>

      <div>
        <label className={ROTULO} htmlFor="dataEncerramento">
          Data de encerramento
        </label>
        <input
          className={CAMPO}
          id="dataEncerramento"
          name="dataEncerramento"
          type="date"
          aria-describedby="ajuda-encerramento"
          aria-invalid={campoRecusado === 'dataEncerramento'}
        />
        <p id="ajuda-encerramento" className="mt-1 text-sm text-slate-600">
          Deixe em branco para vigencia indeterminada.
        </p>
      </div>

      <div>
        <label className={ROTULO} htmlFor="rendaPercapitaMaxima">
          Renda per capita maxima
        </label>
        <input
          className={CAMPO}
          id="rendaPercapitaMaxima"
          name="rendaPercapitaMaxima"
          type="number"
          step="0.01"
          min="0"
        />
      </div>

      <div>
        <label className={ROTULO} htmlFor="idadeMinima">
          Idade minima
        </label>
        <input
          className={CAMPO}
          id="idadeMinima"
          name="idadeMinima"
          type="number"
          min="0"
          defaultValue="0"
          aria-invalid={campoRecusado === 'idadeMinima'}
        />
      </div>

      <div>
        <label className={ROTULO} htmlFor="idadeMaxima">
          Idade maxima
        </label>
        <input
          className={CAMPO}
          id="idadeMaxima"
          name="idadeMaxima"
          type="number"
          min="0"
          defaultValue="0"
          aria-describedby="ajuda-idade"
          aria-invalid={campoRecusado === 'idadeMaxima'}
        />
        <p id="ajuda-idade" className="mt-1 text-sm text-slate-600">
          Zero significa sem limite.
        </p>
      </div>

      <button
        className="rounded bg-slate-800 px-4 py-2 text-white disabled:opacity-60"
        type="submit"
        disabled={enviando}
      >
        {enviando ? 'Incluindo...' : 'Incluir programa'}
      </button>
    </form>
  );
}
