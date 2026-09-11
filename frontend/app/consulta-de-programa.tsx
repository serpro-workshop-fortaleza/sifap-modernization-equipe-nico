'use client';

import { useRouter } from 'next/navigation';
import { type SyntheticEvent, useState } from 'react';

export function ConsultaDePrograma() {
  const router = useRouter();
  const [codigo, setCodigo] = useState('');

  function consultar(evento: SyntheticEvent<HTMLFormElement>) {
    evento.preventDefault();
    const codigoNormalizado = codigo.trim().toUpperCase();

    if (!/^[A-Z0-9]{4}$/.test(codigoNormalizado)) {
      return;
    }

    router.push(`/programas-sociais/${codigoNormalizado}`);
  }

  return (
    <form className="flex flex-col gap-3 sm:flex-row sm:items-end" onSubmit={consultar}>
      <div className="w-full max-w-sm">
        <label className="mb-2 block text-sm font-semibold text-[var(--govbr-gray-80)]" htmlFor="codigo-consulta">
          Código do programa
        </label>
        <input
          className="h-12 w-full border border-[var(--govbr-gray-40)] bg-white px-4 text-[var(--govbr-gray-80)] outline-none transition focus:border-[var(--govbr-blue-warm-vivid-70)] focus:ring-2 focus:ring-[var(--govbr-blue-warm-vivid-20)]"
          id="codigo-consulta"
          name="codigo"
          value={codigo}
          onChange={(evento) => setCodigo(evento.target.value)}
          maxLength={4}
          minLength={4}
          pattern="[A-Za-z0-9]{4}"
          required
          aria-describedby="ajuda-codigo-consulta"
        />
        <p id="ajuda-codigo-consulta" className="mt-2 text-sm text-[var(--govbr-gray-60)]">
          Informe as quatro posições alfanuméricas.
        </p>
      </div>
      <button
        className="h-12 w-full rounded-full bg-[var(--govbr-blue-warm-vivid-70)] px-6 font-semibold text-white transition hover:bg-[var(--govbr-blue-warm-vivid-80)] focus:outline-none focus:ring-2 focus:ring-[var(--govbr-blue-warm-vivid-20)] focus:ring-offset-2 sm:w-auto"
        type="submit"
      >
        Consultar
      </button>
    </form>
  );
}