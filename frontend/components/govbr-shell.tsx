import Link from 'next/link';

export function CabecalhoGovBr() {
  return (
    <>
      <a
        className="sr-only z-50 bg-white px-4 py-3 font-semibold text-[var(--govbr-blue-warm-vivid-70)] focus:not-sr-only focus:fixed focus:left-4 focus:top-4"
        href="#conteudo-principal"
      >
        Ir para o conteúdo
      </a>
      <header className="border-b border-[var(--govbr-gray-20)] bg-white shadow-sm">
        <div className="mx-auto flex min-h-16 max-w-6xl items-center justify-between px-4 sm:px-6">
          <Link
            className="text-[1.75rem] font-black leading-none text-[var(--govbr-blue-warm-vivid-70)]"
            href="/"
            aria-label="gov.br — página inicial do SIFAP"
          >
            gov.br
          </Link>
          <span className="text-sm font-semibold text-[var(--govbr-gray-70)]">Governo Federal</span>
        </div>
        <div className="bg-[var(--govbr-blue-warm-vivid-90)] text-white">
          <div className="mx-auto flex max-w-6xl flex-col gap-4 px-4 py-4 sm:px-6 md:flex-row md:items-center md:justify-between">
            <Link className="w-fit" href="/">
              <span className="block text-xs font-bold uppercase tracking-wide text-[var(--govbr-blue-warm-vivid-20)]">
                SIFAP
              </span>
              <span className="block text-lg font-semibold">Catálogo de Programas Sociais</span>
            </Link>
            <nav aria-label="Navegação principal">
              <ul className="flex flex-wrap gap-x-6 gap-y-2 text-sm font-semibold">
                <li>
                  <Link className="underline-offset-4 hover:underline" href="/">
                    Início
                  </Link>
                </li>
                <li>
                  <Link className="underline-offset-4 hover:underline" href="/#consulta">
                    Consultar
                  </Link>
                </li>
                <li>
                  <Link className="underline-offset-4 hover:underline" href="/programas-sociais/novo">
                    Cadastrar
                  </Link>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </header>
    </>
  );
}

export function RodapeGovBr() {
  return (
    <footer className="mt-auto bg-[var(--govbr-blue-warm-vivid-90)] text-white">
      <div className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
        <p className="text-xl font-black">gov.br</p>
        <div className="mt-5 border-t border-white/30 pt-5 text-sm leading-6 text-[var(--govbr-blue-warm-vivid-20)]">
          <p className="font-semibold text-white">Sistema de Fiscalização e Administração de Pagamentos</p>
          <p>Protótipo de modernização do catálogo de programas sociais.</p>
        </div>
      </div>
    </footer>
  );
}