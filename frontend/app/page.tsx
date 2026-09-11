import Link from 'next/link';
import { Migalhas } from '@/components/migalhas';
import { ConsultaDePrograma } from './consulta-de-programa';

export default function Inicio() {
  return (
    <main id="conteudo-principal" tabIndex={-1}>
      <section className="border-b border-[var(--govbr-gray-20)] bg-[var(--govbr-gray-5)]">
        <div className="mx-auto max-w-6xl px-4 py-10 sm:px-6 md:py-14">
          <Migalhas />
          <p className="mb-3 text-sm font-bold uppercase text-[var(--govbr-blue-warm-vivid-70)]">
            Serviço digital
          </p>
          <h1 className="max-w-3xl text-3xl font-bold leading-tight text-[var(--govbr-blue-warm-vivid-90)] md:text-4xl">
            Catálogo de Programas Sociais
          </h1>
          <p className="mt-4 max-w-3xl text-lg leading-8 text-[var(--govbr-gray-60)]">
            Consulte os dados de um programa social ou cadastre um novo programa no SIFAP.
          </p>
        </div>
      </section>

      <section className="mx-auto grid w-full max-w-6xl gap-10 px-4 py-10 sm:px-6 lg:grid-cols-[2fr_1fr] lg:py-14">
        <div id="consulta" aria-labelledby="titulo-consulta">
          <h2 id="titulo-consulta" className="text-2xl font-bold text-[var(--govbr-blue-warm-vivid-90)]">
            Consultar programa social
          </h2>
          <p className="mb-6 mt-2 max-w-2xl leading-7 text-[var(--govbr-gray-60)]">
            Localize um programa pelo código cadastrado no catálogo.
          </p>
          <ConsultaDePrograma />
        </div>

        <aside className="border-t border-[var(--govbr-gray-20)] pt-8 lg:border-l lg:border-t-0 lg:pl-10 lg:pt-0" aria-labelledby="titulo-cadastro">
          <h2 id="titulo-cadastro" className="text-xl font-bold text-[var(--govbr-blue-warm-vivid-90)]">
            Cadastrar programa
          </h2>
          <p className="mb-5 mt-2 leading-7 text-[var(--govbr-gray-60)]">
            Inclua um programa social com seus dados de vigência e elegibilidade.
          </p>
          <Link
            className="inline-flex min-h-12 items-center justify-center rounded-full border-2 border-[var(--govbr-blue-warm-vivid-70)] px-6 font-semibold text-[var(--govbr-blue-warm-vivid-70)] transition hover:bg-[var(--govbr-blue-warm-vivid-70)] hover:text-white"
            href="/programas-sociais/novo"
          >
            Iniciar cadastro
          </Link>
        </aside>
      </section>
    </main>
  );
}
