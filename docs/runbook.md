# Runbook operacional

![Tipo: runbook](https://img.shields.io/badge/Tipo-Manual%20operacional-171717?style=flat-square)
![Responsável: DevOps](https://img.shields.io/badge/Respons%C3%A1vel-DevOps-737373?style=flat-square)

> **Trilha:** [Kit do Time](../README.md) › [Documentação](README.md) › **Runbook**

**Guia operacional para executar, verificar e diagnosticar o ambiente da imersão.**

| Campo | Valor |
|---|---|
| **Público-alvo** | DevOps Engineer e o time inteiro |
| **Pré-requisitos** | Setup local concluído conforme [`00-SETUP.md`](../00-SETUP.md) |
| **Resultado esperado** | Ambiente local funcionando, CI compreensível e escalonamento correto |

---

## Verificações iniciais (primeiro uso)

- [ ] **Verifique os pré-requisitos** — execute cada linha e confirme que nenhum erro ocorre:

```bash
git --version
java -version
node --version
docker --version
specify version
```

> [!NOTE]
> O kit não inclui um protótipo pronto. Quando o time criar `backend/`, `frontend/` e, se necessário, `infra/`, registre aqui os comandos reais de execução.

URLs confirmadas em execução local em 2026-09-11:

| Serviço | URL / Comando |
|---|---|
| API do backend | `http://localhost:8080/api/v1/programas-sociais` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| Frontend local | `http://localhost:3000` |
| Inclusão de programa | `http://localhost:3000/programas-sociais/novo` |
| Consulta de programa | `http://localhost:3000/programas-sociais/<codigo>` |
| Health do backend | Não disponível; o protótipo não inclui Spring Boot Actuator |
| Credenciais da demonstração | Não se aplica; autenticação está fora do recorte |

### Iniciar o protótipo

Defina uma senha somente no ambiente local e inicie o PostgreSQL:

```bash
export POSTGRES_PASSWORD='<senha-local>'
docker compose -f backend/compose.yml up -d --wait
```

Em outro terminal, inicie o backend:

```bash
cd backend
./mvnw spring-boot:run
```

Em outro terminal, instale exatamente o lockfile e inicie o frontend:

```bash
cd frontend
npm ci
npm run dev
```

---

## Rotina diária

- [ ] **Verifique o estado do repositório:**

```bash
git status
```

- [ ] **Execute a validação do backend:**

```bash
cd backend && ./mvnw -B verify
```

- [ ] **Execute a validação do frontend:**

```bash
cd frontend
npm ci
npm run lint
npm run typecheck
npm run test:coverage
npm run build
```

---

## CI — Entenda os fluxos de trabalho

A CI é executada automaticamente em pushes para `main`, `develop`, `spec/**` e `impl/**`.

| Arquivo de fluxo de trabalho | O que verifica | Quando é executado |
|---|---|---|
| `ci.yml` | Backend `mvn verify`, frontend lint + test + typecheck, Terraform fmt + validate | Em cada push e PR |
| `spec-quality.yml` | markdownlint e rastreabilidade dos REQ-IDs | Quando arquivos `.md` ou `specs/` mudam |

- [ ] **Quando a CI falhar** — abra a aba Actions no GitHub, selecione a execução que falhou e leia o log.
- [ ] **Corrija localmente** — reproduza o erro com os comandos do protótipo criado pelo time antes de fazer outro push.

---

## Azure — Estágio 4

**Status neste recorte:** Terraform não criado e não aplicável. O Estágio 4
permite registrar esse resultado quando IaC não é necessária para a entrega.
Não há diretório `infra/` nem plano Terraform para validar.

> [!CAUTION]
> Não execute `terraform apply` durante a imersão. Se IaC entrar em um recorte
> futuro, primeiro crie e revise os módulos e valide `terraform plan`.

---

## Problemas comuns

| Sintoma | Causa provável | Correção | Como confirmar |
|---|---|---|---|
| O ambiente local trava | A porta 5432, 8080 ou 3000 já está em uso | Execute `lsof -i :5432` e encerre o processo | O serviço inicia sem erro de porta |
| `mvn verify` falha no Testcontainers | O Docker não está em execução | Inicie o Docker Desktop | Os testes passam na próxima execução |
| `npm test` falha após alteração intencional | O comportamento esperado do teste mudou | Revise a alteração e execute `npm test` novamente | Os testes passam sem casos desabilitados |
| `terraform apply` é rejeitado | O recurso não tem a tag `team=` | Adicione a tag ao recurso que falhou | `terraform plan` não apresenta erros de validação |
| O GitHub Actions não consegue acessar o Azure | Divergência na declaração do subject OIDC | Execute `az ad sp create-for-rbac` novamente para o time | O fluxo de trabalho passa na próxima execução |

---

## Quando escalar para o facilitador

- [ ] O build falha há mais de 20 minutos sem solução.
- [ ] A assinatura do Azure parece estar suspensa.
- [ ] Uma ação irreversível foi executada por engano, como `terraform destroy`.

Use o formato de escalonamento em três linhas descrito em [`00-TEAM-FLOW.md §4`](../00-TEAM-FLOW.md).

---

### Continue lendo

| Anterior | Próximo |
|---|---|
| [FAQ](FAQ.md)<br/><sub>Perguntas frequentes.</sub> | [Solução de problemas](troubleshooting.md)<br/><sub>Erros comuns e soluções.</sub> |

<sub>[Voltar ao índice do kit](README.md)</sub>
