---
feature: "001-social-program-catalog"
title: "Tarefas — Catálogo de Programas Sociais"
version: "1.0.0"
status: "entregáveis presentes; rastreabilidade e revisão pendentes"
author: "Equipe Nico"
date: "2026-09-10"
---

# Tarefas — 001-social-program-catalog

> **Trilha:** [Kit do Time](../../README.md) › [Especificações](../README.md) › [001 · Catálogo de Programas Sociais](spec.md) › **Tarefas**

**Decomposição da fatia em tarefas ordenadas por dependência, cada uma rastreada a REQ-IDs de [`spec.md`](spec.md) e ao projeto de [`plan.md`](plan.md).**

| Campo | Valor |
|---|---|
| **Branch** | `impl/001-social-program-catalog`, criada a partir de `develop` |
| **Tarefas** | 19 (`T00` e `T01` a `T18`) |
| **Bloqueadores ativos** | nenhum — a questão P4 foi fechada em 2026-09-10 |
| **Evidência de implementação** | Commit `fd326ae`; CI verde em `a9e8534`; backend e frontend presentes |
| **Ordem TDD** | Planejada, mas a sequência histórica não é comprovável pelo commit consolidado |

> [!WARNING]
> A tarefa T00 implementa uma **decisão provisória**: o usuário responsável do evento de auditoria é a constante `SIFAPSYS`. Enquanto ela valer, a trilha não atribui responsabilidade a pessoa alguma, o que não cumpre a finalidade da IN-TCU 63/2010 citada em `AUDIT.ddm:L13-L18`. A fatia seguinte não pode ir para produção sem substituí-la. Ver P4 na seção 8 de [`plan.md`](plan.md).

## Status de execução

| Tarefas | Status comprovado | Evidência principal |
|---|---|---|
| T01–T05 | Entregáveis presentes | `backend/pom.xml`, `backend/compose.yml`, `ArquiteturaTest` e migração/teste de imutabilidade |
| T06–T11 | Entregáveis presentes | Objetos de domínio e respectivos testes unitários |
| T00, T12–T14 | Entregáveis presentes | Módulo de auditoria e testes de usuário fixo, transação, imutabilidade e concorrência |
| T15–T16 | Entregáveis presentes | Serviço, controller e testes de aplicação/API |
| T17–T18 | Entregáveis presentes | Rotas Next.js, Server Action e testes de frontend |

Os entregáveis foram integrados pelo commit `fd326ae`, que referencia
REQ-001 a REQ-017. A CI do commit `a9e8534` aprovou backend, frontend e
rastreabilidade. Não há PR registrado e o commit consolidado não permite
comprovar a ordem histórica de TDD.

---

## Fase 1 — Fundação

| ID | Tarefa | REQ-ID | Depende de | Responsável |
|---|---|---|---|---|
| T01 | Criar o projeto Spring Boot 3.3 com Java 21, Maven, `springdoc-openapi`, ArchUnit e Flyway. Sem lógica de negócio. | — | — | Dev |
| T02 | Criar `compose.yml` com PostgreSQL 16 para desenvolvimento e para os Testcontainers. | — | T01 | DevOps |
| T03 | Escrever o teste ArchUnit que falha se `domain` importar `infrastructure` ou se `catalogo` importar algo de `auditoria` além de `auditoria.application`. Deve falhar antes dos pacotes existirem. | — | T01 | Dev |
| T04 | Criar `V1__create_catalogo_e_auditoria.sql` com as duas tabelas, as restrições de verificação, o índice de auditoria e a trigger de imutabilidade, conforme a seção 3 de [`plan.md`](plan.md). | REQ-002, REQ-003, REQ-004, REQ-006, REQ-007, REQ-008, REQ-009, REQ-014 | T02 | DBA |
| T05 | Escrever teste de integração que prova que a trigger recusa `UPDATE` e `DELETE` em `evento_auditoria`. | REQ-014 | T04 | QA |

---

## Fase 2 — Domínio do Catálogo

Cada tarefa começa pelo teste unitário que falha.

| ID | Tarefa | REQ-ID | Depende de | Responsável |
|---|---|---|---|---|
| T06 | Objeto de valor `CodigoPrograma`: aceita quatro posições alfanuméricas, normaliza entrada numérica com zeros à esquerda, recusa comprimento inválido. | REQ-002 | T01 | Dev |
| T07 | Enumerações `TipoPrograma` e `SituacaoPrograma`, com recusa de valor fora do domínio do DDM. | REQ-007 | T01 | Dev |
| T08 | Objeto de valor `Vigencia`: data de criação obrigatória, encerramento opcional, recusa encerramento anterior à criação, expõe vigência indeterminada. | REQ-004, REQ-009 | T01 | Dev |
| T09 | Objeto de valor `FaixaEtaria`: zero significa ausência de limite, recusa faixa invertida quando ambos os limites estão definidos. | REQ-008 | T01 | Dev |
| T10 | Objeto de valor `ValorBase`: recusa valor negativo e valor acima de 99.999,99, com mensagem explícita. **Não** aplica fator de correção. | REQ-005, REQ-006 | T01 | Dev |
| T11 | Entidade `ProgramaSocial`: compõe os objetos de valor, nasce com situação ativa e guarda o fator de ajuste sem aplicá-lo. | REQ-001, REQ-003, REQ-005 | T06 a T10 | Dev |

---

## Fase 3 — Módulo de auditoria

| ID | Tarefa | REQ-ID | Depende de | Responsável |
|---|---|---|---|---|
| T00 | Interface `ProvedorUsuarioResponsavel` em `auditoria.application` e implementação `UsuarioSistemaFixo`, que devolve `SIFAPSYS`. Teste unitário verifica o valor e as 8 posições. A implementação carrega um comentário de uma linha marcando a natureza provisória e apontando para P4 em [`plan.md`](plan.md). | REQ-014 | T01 | Dev |
| T12 | Entidade `EventoAuditoria` e enumerações `AcaoAuditoria` e `TipoEntidade`. Sem operação de alteração: a entidade não expõe métodos que mudem estado. | REQ-014 | T04 | Dev |
| T13 | `RegistradorAuditoria` com propagação `REQUIRES_NEW`, usando `ProvedorUsuarioResponsavel` para preencher o usuário; recusa ação de consulta lançando exceção de violação. Teste de integração prova a recusa e prova que o evento sobrevive ao rollback do negócio. | REQ-014, REQ-016, REQ-017 | T00, T12 | Dev + QA |
| T14 | Teste de integração de concorrência: duas inclusões simultâneas produzem identificadores distintos e monotônicos. | REQ-014 | T13 | QA |

---

## Fase 4 — Aplicação e API

| ID | Tarefa | REQ-ID | Depende de | Responsável |
|---|---|---|---|---|
| T15 | `ProgramaSocialService.incluir` e `.consultar`, com transação `REQUIRED` na inclusão e chamada ao `RegistradorAuditoria` após a persistência. Teste prova que a inclusão recusada não gera evento. | REQ-001, REQ-011, REQ-013, REQ-015 | T11, T13 | Dev |
| T16 | `ProgramaSocialController`: `POST` e `GET` conforme a seção 4 de [`plan.md`](plan.md), com `problem+json` nos erros, `404` para não encontrado, `409` para violação de chave e `405` para os demais métodos. | REQ-010, REQ-011, REQ-012 | T15 | Dev |

---

## Fase 5 — Frontend

| ID | Tarefa | REQ-ID | Depende de | Responsável |
|---|---|---|---|---|
| T17 | Rota `/programas-sociais/novo`: formulário com Server Action, mensagens de erro vindas do `problem+json` da API. | REQ-001 a REQ-009 | T16 | Dev frontend |
| T18 | Rota `/programas-sociais/[codigo]`: Server Component apresentando exatamente os seis campos de REQ-011 e a mensagem de programa não encontrado. | REQ-011, REQ-012 | T16 | Dev frontend |

---

## Matriz de cobertura

Todo requisito de [`spec.md`](spec.md) tem ao menos uma tarefa.

| REQ-ID | Tarefas |
|---|---|
| REQ-001 | T11, T15, T17 |
| REQ-002 | T04, T06 |
| REQ-003 | T04, T11 |
| REQ-004 | T04, T08 |
| REQ-005 | T10, T11 |
| REQ-006 | T04, T10 |
| REQ-007 | T04, T07 |
| REQ-008 | T04, T09 |
| REQ-009 | T04, T08 |
| REQ-010 | T16 |
| REQ-011 | T15, T16, T18 |
| REQ-012 | T16, T18 |
| REQ-013 | T15 |
| REQ-014 | T00, T04, T05, T12, T13, T14 |
| REQ-015 | T15 |
| REQ-016 | T13 |
| REQ-017 | T13 |

---

## Definição de pronto da fatia

- [x] Os entregáveis das 19 tarefas estão presentes no repositório.
- [x] Backend verde com `./mvnw -B verify`; cobertura ≥ 70%.
- [x] Frontend verde; cobertura ≥ 60%.
- [x] Migração Flyway validada pelos testes de integração em PostgreSQL.
- [x] OpenAPI gerado pelo `springdoc-openapi` e controllers anotados conforme a seção 4 de [`plan.md`](plan.md).
- [ ] Todos os 17 requisitos possuem referências em testes; REQ-013 está pendente.
- [ ] Revisão por par registrada em PR antes da integração em `develop`.

> [!NOTE]
> O repositório comprova os entregáveis e os testes, mas não comprova a ordem
> histórica vermelho-verde-refatorar. A API do GitHub não registra PR para a
> integração da branch `karlos` em `develop`.

---

## Continue lendo

- [`spec.md`](spec.md) — os 17 requisitos
- [`plan.md`](plan.md) — módulos, dados, contrato e questões de projeto
- [`00-GIT-WORKFLOW.md`](../../00-GIT-WORKFLOW.md) — estratégia de branches
