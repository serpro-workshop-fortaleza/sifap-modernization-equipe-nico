# STATUS diário — Painel de progresso

> **Trilha:** [Kit do Time](../README.md) › [Documentação](README.md) › **STATUS**

**Painel de acompanhamento da imersão em tempo real:** status dos estágios, handoffs e métricas do dia.

![Painel de status diário](https://img.shields.io/badge/Painel-Status%20di%C3%A1rio-171717?style=flat-square) ![Atualização a cada 30 minutos](https://img.shields.io/badge/Atualiza%C3%A7%C3%A3o-A%20cada%2030%20min-737373?style=flat-square) ![Responsável: Technical Lead](https://img.shields.io/badge/Respons%C3%A1vel-Technical%20Lead-A3A3A3?style=flat-square)

| Campo | Valor |
|---|---|
| **Público-alvo** | Technical Lead (atualiza) e facilitador (consulta rapidamente) |
| **Frequência de atualização** | A cada 30 minutos ou em cada transição de estágio |
| **Resultado esperado** | Visão de uma página do que está pronto, em andamento e bloqueado |

---

## Status geral

| Indicador | Status | Observações |
|---|---|---|
| Time inteiro presente | Não comprovado | Participantes não foram registrados |
| Ferramentas locais validadas em 5/5 laptops | Não comprovado | Há validação somente neste ambiente local |
| Branch `develop` protegida | Não verificado | Sem evidência registrada da regra de proteção |
| CI verde em `develop` | Sim | `ci.yml` e `spec-quality.yml` aprovados no commit `a9e8534` |
| Demonstração ensaiada | Não comprovado | Não há registro de ensaio |

---

## Progresso nos quatro estágios

| Estágio | Status | Responsável | Início | DoD concluída? | Observações |
|---|---|---|---|---|---|
| **1 — Arqueologia** | Parcial | Todas as duplas | Não registrado | 3/5 (60%) | Evidência da fatia, catálogo e relatório; leitura das cinco duplas e H1 não comprovados |
| **2 — Especificação** | Parcial | Dupla 2 | Não registrado | 4/5 (80%) | Spec, plano, tarefas, fontes e decisões existem; aprovação do PO e H2 não registrados |
| **3 — Implementação** | Concluído tecnicamente | Duplas 3 e 4 | Não registrado | 7/7 (100%) | Protótipo executado localmente, testes e CI aprovados; H3 não registrado |
| **4 — Evolução** | Parcial | Dupla 5 | Não registrado | 3/5 (60%) | Rascunho e próxima etapa registrados; relatório humano e comunicação à demo pendentes |

**Progresso consolidado:** 17 de 22 critérios atendidos (**77%**). O registro
administrativo do Estágio 4 documenta honestamente a delegação pendente; não
significa que o Copilot Agent tenha executado a Issue.

**Legenda de status:** Não iniciado · Em andamento · Concluído · Atrasado · Bloqueado

---

## Handoffs dos estágios

| Handoff | Origem e destino | Quando | Status |
|---|---|---|---|
| **H1** | Dupla 1 para Dupla 2 | Fim do Estágio 1 | Não registrado |
| **H2** | Dupla 2 para Duplas 3 e 4 | Fim do Estágio 2 | Não registrado |
| **H3** | Duplas 3 e 4 para Dupla 5 | Fim do Estágio 3 | Não registrado |

> [!NOTE]
> Cada handoff é uma conversa síncrona de cinco minutos entre as duplas que entregam e recebem. O cronograma detalhado está em [`00-TEAM-FLOW.md`](../00-TEAM-FLOW.md).

---

## Métricas do dia

| Métrica | Meta | Atual |
|---|---|---|
| Fontes do legado confirmadas para o escopo | Todo REQ-ID | 17/17 REQ-IDs com `source_legacy:`; gate aprovado |
| Especificação formal (`spec.md`, `plan.md`, `tasks.md`) | Uma funcionalidade completa | 1 feature implementada; aprovação formal não registrada |
| Decisões de escopo registradas | Pelo menos uma | 16 decisões na rodada vigente de `scope-decisions.md` |
| Primeiro incremento implementado | Um | Catálogo de Programas Sociais em backend e frontend |
| Cobertura de testes do backend | Pelo menos 70% | 87% no relatório JaCoCo local; CI aprovada |
| Cobertura de testes do frontend | Pelo menos 60% | 86,27% de linhas no relatório local; CI aprovada |
| Issues criadas para o modo Agent | Pelo menos uma | 0 publicadas; 1 rascunho revisado |
| PRs integrados em `develop` | — | 0 PRs; integração registrada por merge direto |

---

## Alertas ativos

> [!WARNING]
> Adicione uma entrada abaixo sempre que surgir um bloqueio ou risco. O Technical Lead a lê em voz alta no próximo stand-up.

- [ ] Confirmar a leitura dos três programas atribuídos a cada uma das cinco duplas; somente `SIFAP-M-05` a `SIFAP-M-08` estão registrados no placar canônico.
- [ ] Registrar, se realmente ocorreram, os handoffs H1, H2 e H3 e a aprovação do PO; o repositório não contém essas evidências.
- [ ] Publicar e delegar a Issue do Agent pela interface do GitHub; não há Issue nem PR remoto.
- [ ] Adicionar teste rastreado para REQ-013; é o único dos 27 REQ-IDs sem referência em testes.
- [ ] Substituir `SIFAPSYS` por identidade autenticada antes de produção.

---

## Marcos alcançados

Marque cada marco quando ele for alcançado:

- [x] **Primeira regra de negócio documentada com `Programa de origem`** — entrada do Estágio 1 concluída.
- [x] **Primeira especificação EARS escrita** com o campo `source_legacy:` preenchido.
- [x] **Primeira decisão de escopo registrada** e vinculada ao plano.
- [ ] **CI verde no primeiro Pull Request** — CI verde em `develop`, mas nenhum PR foi registrado.
- [x] **Primeiro endpoint REST funcionando** e visível pelo Swagger.
- [x] **Cobertura de testes do backend igual ou superior a 70%**.
- [ ] **Primeiro Pull Request do modo Agent revisado e integrado**.
- [ ] **Plano do Terraform concluído sem erros** — não criado; opcional e não aplicável a este recorte.
- [ ] **Demonstração final do SIFAP 2.0 concluída com sucesso**.

---

## Registro do stand-up (uma frase por dupla em cada transição)

### H1 — fim do Estágio 1

| Dupla | Persona | Registro |
|---|---|---|
| Dupla 1 | Visão (PO + RE) | Não registrado |
| Dupla 2 | Arquitetura (EA + SA) | Não registrado |
| Dupla 3 | Implementação (TL + Dev) | Não registrado |
| Dupla 4 | Qualidade (DBA + QA) | Não registrado |
| Dupla 5 | Operações (DevOps + TW) | Não registrado |

### H2 — fim do Estágio 2

| Dupla | Registro |
|---|---|
| Dupla 1 | Não registrado |
| Dupla 2 | Não registrado |
| Dupla 3 | Não registrado |
| Dupla 4 | Não registrado |
| Dupla 5 | Não registrado |

### H3 — fim do Estágio 3

| Dupla | Registro |
|---|---|
| Dupla 1 | Não registrado |
| Dupla 2 | Não registrado |
| Dupla 3 | Não registrado |
| Dupla 4 | Não registrado |
| Dupla 5 | Não registrado |

---

### Continue lendo

| Anterior | Próximo |
|---|---|
| [Roteiro da demonstração](demo-script.md)<br/><sub>Roteiro para a demonstração final de três minutos.</sub> | [Checklist do líder](CHECKLIST-LIDER.md)<br/><sub>Guia hora a hora para o Technical Lead.</sub> |

<sub>[Voltar ao índice do kit](../README.md)</sub>
