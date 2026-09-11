'use client';

import { useActionState } from 'react';
import { ESTADO_INICIAL, type EstadoDoFormulario } from './estado';

type Envio = (anterior: EstadoDoFormulario, dados: FormData) => Promise<EstadoDoFormulario>;

const ROTULO = 'block text-sm font-semibold text-[var(--govbr-gray-80)]';
const CAMPO =
  'mt-2 h-12 w-full rounded-[4px] border border-[var(--govbr-gray-40)] bg-white px-4 text-[var(--govbr-gray-80)] outline-none transition focus:border-[var(--govbr-blue-warm-vivid-70)] focus:ring-2 focus:ring-[var(--govbr-blue-warm-vivid-20)] aria-invalid:border-[var(--govbr-danger-60)]';

export function FormularioDePrograma({ acao }: Readonly<{ acao: Envio }>) {
  const [estado, enviar, enviando] = useActionState(acao, ESTADO_INICIAL);
  const campoRecusado = estado.estado === 'recusado' ? estado.campo : undefined;

  return (
    <form action={enviar} className="flex flex-col gap-8" noValidate aria-busy={enviando}>
      {estado.estado === 'recusado' && (
        <div role="alert" className="border-l-4 border-[var(--govbr-danger-60)] bg-[var(--govbr-danger-10)] p-4 text-[var(--govbr-gray-80)]">
          <p className="font-bold">Não foi possível concluir o cadastro</p>
          <p className="mt-1">{estado.mensagem}</p>
        </div>
      )}

      {estado.estado === 'incluido' && (
        <output className="block border-l-4 border-[var(--govbr-success-60)] bg-[var(--govbr-success-10)] p-4 text-[var(--govbr-gray-80)]">
          <p className="font-bold">Cadastro concluído</p>
          <p className="mt-1">Programa {estado.codigo} incluído com sucesso.</p>
        </output>
      )}

      <fieldset>
        <legend className="mb-5 text-xl font-bold text-[var(--govbr-blue-warm-vivid-90)]">Identificação</legend>
        <div className="grid gap-5 md:grid-cols-2">
          <div>
            <label className={ROTULO} htmlFor="codigo">Código <span aria-hidden="true">*</span></label>
            <input className={CAMPO} id="codigo" name="codigo" maxLength={4} required aria-invalid={campoRecusado === 'codigo'} />
          </div>
          <div>
            <label className={ROTULO} htmlFor="tipo">Tipo <span aria-hidden="true">*</span></label>
            <select className={CAMPO} id="tipo" name="tipo" required aria-invalid={campoRecusado === 'tipo'}>
              <option value="A">A — Assistência</option>
              <option value="P">P — Previdência</option>
              <option value="T">T — Trabalho</option>
            </select>
          </div>
          <div className="md:col-span-2">
            <label className={ROTULO} htmlFor="nome">Nome <span aria-hidden="true">*</span></label>
            <input className={CAMPO} id="nome" name="nome" maxLength={60} required aria-invalid={campoRecusado === 'nome'} />
          </div>
          <div>
            <label className={ROTULO} htmlFor="codigoElegibilidade">Código de elegibilidade</label>
            <input className={CAMPO} id="codigoElegibilidade" name="codigoElegibilidade" maxLength={5} />
          </div>
        </div>
      </fieldset>

      <fieldset className="border-t border-[var(--govbr-gray-20)] pt-7">
        <legend className="mb-5 text-xl font-bold text-[var(--govbr-blue-warm-vivid-90)]">Valores</legend>
        <div className="grid gap-5 md:grid-cols-2">
          <div>
            <label className={ROTULO} htmlFor="valorBase">Valor base <span aria-hidden="true">*</span></label>
            <input className={CAMPO} id="valorBase" name="valorBase" type="number" step="0.01" min="0" required aria-invalid={campoRecusado === 'valorBase'} />
          </div>
          <div>
            <label className={ROTULO} htmlFor="fatorAjuste">Fator de ajuste</label>
            <input className={CAMPO} id="fatorAjuste" name="fatorAjuste" type="number" step="0.0001" min="0" defaultValue="0" aria-invalid={campoRecusado === 'fatorAjuste'} />
          </div>
          <div>
            <label className={ROTULO} htmlFor="rendaPercapitaMaxima">Renda per capita máxima</label>
            <input className={CAMPO} id="rendaPercapitaMaxima" name="rendaPercapitaMaxima" type="number" step="0.01" min="0" />
          </div>
        </div>
      </fieldset>

      <fieldset className="border-t border-[var(--govbr-gray-20)] pt-7">
        <legend className="mb-5 text-xl font-bold text-[var(--govbr-blue-warm-vivid-90)]">Vigência e faixa etária</legend>
        <div className="grid gap-5 md:grid-cols-2">
          <div>
            <label className={ROTULO} htmlFor="dataCriacao">Data de criação <span aria-hidden="true">*</span></label>
            <input className={CAMPO} id="dataCriacao" name="dataCriacao" type="date" required aria-invalid={campoRecusado === 'dataCriacao'} />
          </div>
          <div>
            <label className={ROTULO} htmlFor="dataEncerramento">Data de encerramento</label>
            <input className={CAMPO} id="dataEncerramento" name="dataEncerramento" type="date" aria-describedby="ajuda-encerramento" aria-invalid={campoRecusado === 'dataEncerramento'} />
            <p id="ajuda-encerramento" className="mt-2 text-sm text-[var(--govbr-gray-60)]">Deixe em branco para vigência indeterminada.</p>
          </div>
          <div>
            <label className={ROTULO} htmlFor="idadeMinima">Idade mínima</label>
            <input className={CAMPO} id="idadeMinima" name="idadeMinima" type="number" min="0" defaultValue="0" aria-invalid={campoRecusado === 'idadeMinima'} />
          </div>
          <div>
            <label className={ROTULO} htmlFor="idadeMaxima">Idade máxima</label>
            <input className={CAMPO} id="idadeMaxima" name="idadeMaxima" type="number" min="0" defaultValue="0" aria-describedby="ajuda-idade" aria-invalid={campoRecusado === 'idadeMaxima'} />
            <p id="ajuda-idade" className="mt-2 text-sm text-[var(--govbr-gray-60)]">Zero significa sem limite.</p>
          </div>
        </div>
      </fieldset>

      <div className="flex justify-end border-t border-[var(--govbr-gray-20)] pt-7">
        <button
          className="min-h-12 w-full rounded-full bg-[var(--govbr-blue-warm-vivid-70)] px-7 font-semibold text-white transition hover:bg-[var(--govbr-blue-warm-vivid-80)] disabled:cursor-not-allowed disabled:opacity-60 sm:w-auto"
          type="submit"
          disabled={enviando}
        >
          {enviando ? 'Incluindo...' : 'Incluir programa'}
        </button>
      </div>
    </form>
  );
}
