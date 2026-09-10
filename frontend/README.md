# Frontend — Catálogo de Programas Sociais

Next.js 15 (App Router) + TypeScript estrito + Tailwind CSS. Consome a API do backend
em `/api/v1/programas-sociais`.

## Rotas

| Rota | Tarefa | Requisitos |
|---|---|---|
| `/programas-sociais/novo` | T17 | REQ-001 a REQ-009 |
| `/programas-sociais/[codigo]` | T18 | REQ-011, REQ-012 |

## Configuração

| Variável | Padrão | Uso |
|---|---|---|
| `SIFAP_API_URL` | `http://localhost:8080` | Base da API do backend |

## Comandos

```bash
npm install
npm run dev            # servidor de desenvolvimento
npm run build          # build de produção
npm run lint           # ESLint
npm run test           # Vitest
npm run test:coverage  # Vitest com cobertura (mínimo de 60% de linhas)
```

A validação de regra de negócio fica no backend. O frontend apenas encaminha a
solicitação e traduz o `application/problem+json` da recusa para a mensagem exibida.
