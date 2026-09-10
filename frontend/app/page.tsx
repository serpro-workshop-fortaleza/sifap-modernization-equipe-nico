import Link from 'next/link';

export default function Inicio() {
  return (
    <main className="mx-auto flex max-w-2xl flex-col gap-6 p-8">
      <header>
        <h1 className="text-2xl font-semibold text-slate-900">Catalogo de Programas Sociais</h1>
        <p className="text-slate-600">
          O catalogo suporta apenas inclusao e consulta, como a tela legada CADPROG.
        </p>
      </header>
      <nav className="flex flex-col gap-2">
        <Link className="text-slate-800 underline" href="/programas-sociais/novo">
          Incluir programa social
        </Link>
      </nav>
    </main>
  );
}
