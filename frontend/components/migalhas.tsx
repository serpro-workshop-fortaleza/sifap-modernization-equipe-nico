import Link from 'next/link';

interface ItemDeMigalha {
  rotulo: string;
  href?: string;
}

export function Migalhas({ itens = [] }: Readonly<{ itens?: ItemDeMigalha[] }>) {
  return (
    <nav className="mb-6 text-sm" aria-label="Navegação estrutural">
      <ol className="flex flex-wrap items-center gap-2 text-[var(--govbr-gray-60)]">
        <li>
          <Link className="font-semibold text-[var(--govbr-blue-warm-vivid-70)] hover:underline" href="/">
            Início
          </Link>
        </li>
        {itens.map((item) => (
          <li className="flex items-center gap-2" key={item.rotulo}>
            <span aria-hidden="true">/</span>
            {item.href ? (
              <Link className="font-semibold text-[var(--govbr-blue-warm-vivid-70)] hover:underline" href={item.href}>
                {item.rotulo}
              </Link>
            ) : (
              <span aria-current="page">{item.rotulo}</span>
            )}
          </li>
        ))}
      </ol>
    </nav>
  );
}