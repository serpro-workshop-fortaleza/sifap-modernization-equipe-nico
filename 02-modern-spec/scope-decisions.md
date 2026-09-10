# Decisões de escopo — Estágio 2

> **Trilha:** [Kit do Time](../README.md) › [Estágio 2](README.md) › **Decisões de escopo**

**Registre as decisões de escopo tomadas durante o Estágio 2: o que foi selecionado, o que foi adiado e quais questões permanecem em aberto.**

| Campo | Valor |
|---|---|
| **Público-alvo** | Dupla 2 durante o Estágio 2; Duplas 3 e 4 durante o handoff H2 |
| **Finalidade** | Apoiar a conversa do estágio; não substitui os artefatos formais do Spec-Kit |
| **Feature relacionada** | [`specs/001-social-program-catalog/`](../specs/001-social-program-catalog/spec.md) |

> [!NOTE]
> Os entregáveis formais permanecem em `specs/<NNN>-<feature>/spec.md`, `plan.md` e `tasks.md`. Não registre requisitos EARS completos aqui. Este arquivo registra somente decisões de escopo e questões em aberto.

---

## Decisões de escopo

### Rodada 2 — validação humana de 2026-09-10

Tomadas para desbloquear o Estágio 3. **Substituem a rodada 1** onde houver conflito.

| # | Decisão | Evidência ou justificativa | Impacto nos artefatos formais |
|---|---|---|---|
| 1 | **A primeira fatia é o Catálogo de Programas Sociais**, não a Trilha de Auditoria | Preserva a ordem do Strangler Fig de [`bounded-contexts.md`](bounded-contexts.md): o arquivo 151 é a raiz de dependência lida por `BATCHPGT`, `CALCBENF` e `CALCDSCT`. | `specs/001-audit-trail/` foi descartada. Criada [`specs/001-social-program-catalog/`](../specs/001-social-program-catalog/spec.md) com 17 requisitos, `plan.md` e `tasks.md`. |
| 2 | **Promover R19, R20, R25, R26, R31 e R32 de Inferida a Confirmada** | Todas têm evidência literal em `CADPROG.NSP` e nenhum mistério associado ao enunciado. Validação humana explícita. | REQ-003, REQ-004, REQ-010, REQ-011, REQ-012 e REQ-013. |
| 3 | **Promover R30 e R46 de Inferida a Confirmada** | Comentário literal com norma e data: "ACTION 'CO' (QUERY) MUST NOT BE RECORDED SINCE 2010 DUE TO VOLUME - PORT. CGTI 213/2010" (`CCAUDIT.NSC:L45-L46`). | REQ-016. Resolve a questão aberta da rodada 1. |
| 4 | **O mecanismo de auditoria entra na fatia do Catálogo** | R29 (`CADPROG.NSP:L140-L147`) não tem critério de aceitação verificável sem R36 (`CCAUDIT.NSC:L60-L98`). | REQ-014 e REQ-015 na mesma spec. Uma spec `002-audit-trail` futura generaliza o mecanismo para os outros seis produtores. |
| 5 | **Não especificar a verificação de código duplicado** | R21 é Mistério: `MOVE TRUE TO #FOUND` está fora do bloco `NO RECORDS FOUND` (`CADPROG.NSP:L113-L115`). A unicidade fica com a chave primária. | Nenhum requisito. R22 permanece adiada. A API traduz a violação de chave em `409`. |
| 6 | **Não aplicar o fator K na inclusão** | `CADPROG.NSP:L124` aplica `1,00 + fator × 0,347215`; `BATCHPGT.NSP:432` e `CALCBENF.NSN:262` aplicam o ajuste de novo por outra fórmula. A constante não aparece em nenhum outro lugar do corpus. | REQ-005. Divergência deliberada em [ADR-0004](../docs/adr/0004-catalog-deliberate-divergences.md). |
| 7 | **Recusar valor base acima de 99.999,99 em vez de truncar** | `SOCPROG.ddm:L41` limita o campo persistido; `CADPROG.NSP:L130` trunca em silêncio. | REQ-006. Divergência deliberada em [ADR-0004](../docs/adr/0004-catalog-deliberate-divergences.md). |
| 8 | **Especificar as validações que o DDM sustenta** | Domínio de tipo em `SOCPROG.ddm:L31-L32`; idades com `0=NONE` em `:L57-L58`; datas em `:L35-L36`. O legado declara e não valida. | REQ-007, REQ-008 e REQ-009. O código de elegibilidade fica sem validação de domínio: a lista não existe no corpus. |
| 9 | **Adotar código de programa alfanumérico de quatro posições** | `SOCPROG.ddm:L28` declara a chave como `A 4`; a tela lê `N4` e é incapaz de alcançar códigos com letra. | REQ-002. Divergência deliberada em [ADR-0004](../docs/adr/0004-catalog-deliberate-divergences.md). |
| 10 | **Gerar o identificador do evento de auditoria pelo banco** | `CCAUDIT.NSC:L65-L71` usa `max+1` sem trava, contra o `U UNIQUE SEQUENCE` de `AUDIT.ddm:L31`. | Cenário 3 de REQ-014. `BIGINT GENERATED ALWAYS AS IDENTITY`, em [ADR-0003](../docs/adr/0003-audit-trail-integrity-and-persistence.md). Resolve a questão aberta da rodada 1. |
| 11 | **Não implementar valores anterior e novo nesta fatia** | `GRP-BEFORE` e `GRP-AFTER` (`AUDIT.ddm`) nunca receberam escritor. A fatia só tem inclusão, que não tem estado anterior. | Fora do escopo. A questão volta a ser bloqueante quando houver alteração. |
| 12 | **Criar apenas as colunas efetivamente gravadas pelo legado** | O copycode grava 13 dos cerca de 35 campos do DDM. `COD-PROFILE` tem o ticket 7742 aberto. | Seção 3 de [`plan.md`](../specs/001-social-program-catalog/plan.md). |
| 13 | **Registrar o evento de auditoria em transação própria** | `CADPROG.NSP:L184` desfaz o evento junto com a operação; por isso as ações `ER` e `RE` nunca são gravadas. | REQ-017, com `REQUIRES_NEW`. Divergência deliberada em [ADR-0003](../docs/adr/0003-audit-trail-integrity-and-persistence.md). |
| 14 | **Impor a proibição de auditar consultas no próprio componente** | `CCAUDIT.NSC:L47-L48` delega o bloqueio a quem chama e registra o ticket 7740 em aberto. | REQ-016. Substitui sete chamadores confiáveis por uma guarda única. |
| 15 | **O frontend cobre inclusão e consulta** | R20 e R31 foram promovidas e descrevem a consulta; sem tela, o handoff H3 não atinge a cobertura de frontend. | Rotas `/programas-sociais/novo` e `/programas-sociais/[codigo]` na seção 6 de [`plan.md`](../specs/001-social-program-catalog/plan.md). |
| 16 | **O usuário responsável do evento de auditoria é a constante `SIFAPSYS`, em caráter provisório** | A fatia não especifica autenticação e REQ-014 exige o usuário. O valor tem as mesmas 8 posições de `COD-USER` (`AUDIT.ddm:L69`). | Tarefa T00 cria `ProvedorUsuarioResponsavel` com uma única implementação fixa. **Pré-requisito da fatia seguinte:** substituir por identidade real antes de qualquer produção. |

> [!IMPORTANT]
> As decisões 6, 7, 9, 13 e 14 **divergem do comportamento legado**. Todas estão registradas em ADR e sinalizadas na especificação. Nenhuma delas responde ao mistério correspondente: escolher um comportamento-alvo não é o mesmo que descobrir por que o legado faz o que faz.

### Rodada 1 — execução de `/write-ears-spec` em 2026-09-10

Mantidas por rastreabilidade. As duas primeiras foram substituídas pela rodada 2.

| Decisão | Situação |
|---|---|
| Especificar primeiro a Trilha de Auditoria, e não o Catálogo | **Substituída** pela decisão 1 da rodada 2 |
| Converter em requisito apenas regras já classificadas como Confirmada | **Substituída** pela decisão 2 da rodada 2, que promoveu oito regras por validação humana |
| Adiar as candidatas EARS classificadas como Inferida | Parcialmente mantida — ver *Candidatas adiadas* |
| Manter a Regra 8 fora desta especificação: a isenção do desconto judicial ao teto de 30% (`CALCDSCT.NSP:L170-L174`) pertence ao contexto Cálculo e Folha | Mantida |
| Não especificar Cadastro de Beneficiários nem Conciliação Bancária: nenhum membro Natural desses contextos passou por extração formal | Mantida |
| Não especificar Cálculo e Folha: o conflito de alíquotas de contribuição segue aberto | Mantida — permanece a **última** fatia |
| Corrigir citações de linha do DDM `AUDIT`: `NUM-AUDIT` está em `:31`, o domínio de `COD-ACTION` começa em `:39` e `IN=INSERTION` está em `:40` | Mantida |

### Candidatas adiadas

Após a rodada 2, restam **6** candidatas EARS retidas em [`business-rules-catalog.md`](../01-archaeology/business-rules-catalog.md).

| Contexto | Regras adiadas | Observação |
|---|---|---|
| Catálogo de Programas Sociais | R22 | Depende de R21, que permanece Mistério. A unicidade passou a ser garantida pela chave primária, sem mensagem de negócio dedicada. |
| Trilha de Auditoria | R38, R39, R40, R41, R45 | R41 depende do contexto batch, cuja situação do lote é Mistério. As demais entram na spec `002-audit-trail`, quando o mecanismo for generalizado para os sete produtores. |

Promovidas na rodada 2 e já convertidas em requisito: R19, R20, R25, R26, R30, R31, R32 e R46.

---

## Questões em aberto

| Questão | Fonte consultada | Próxima pessoa responsável | Situação |
|---|---|---|---|
| Qual é a precisão real de `FACTOR-ADJUST` no Adabas, dada a notação `P 3,4` de `SOCPROG.ddm:L52`? | [`plan.md`](../specs/001-social-program-catalog/plan.md) questão P1 | DBA Adabas | aberta |
| O evento de auditoria do alvo deve registrar valor anterior e valor novo, como a RN-010 sempre exigiu e o legado nunca implementou? | [`mysteries-found.md`](../01-archaeology/mysteries-found.md) | Facilitador — SUPDE/DESIF | aberta, fora desta fatia |
| A inclusão de programa social funciona no legado, dado o `MOVE TRUE TO #FOUND` fora do bloco `NO RECORDS FOUND`? | [`mysteries-found.md`](../01-archaeology/mysteries-found.md) | Facilitador — prioridade 3 | aberta |
| O fator de ajuste aplicado duas vezes é composição deliberada ou duplicação? De onde vem a constante `0,347215`? | [`mysteries-found.md`](../01-archaeology/mysteries-found.md) | SENARC + Adilson Batista | aberta, condiciona a migração de dados |
| Onde vive a lista de códigos de elegibilidade válidos? | [`business-rules-catalog.md`](../01-archaeology/business-rules-catalog.md) Regra 33 | Fernanda Oliveira | aberta, única validação que a fatia não conseguiu especificar |
| Existem programas com valor base já truncado no arquivo 151? | [`business-rules-catalog.md`](../01-archaeology/business-rules-catalog.md) Regra 27 | SUPDE/DESIF + DBA | aberta, condiciona a migração de dados |
| O controle de acesso por perfil ADMIN, afirmado no manual de 2008, existe em algum lugar? | [`business-rules-catalog.md`](../01-archaeology/business-rules-catalog.md) Regra 34 | SUPDE/DESIF | aberta |

### Questões fechadas nesta rodada

| Questão | Resposta |
|---|---|
| A equipe aceita especificar a Trilha de Auditoria antes do Catálogo? | Não. A fatia 1 é o Catálogo, conforme a ordem original do Strangler Fig. |
| R30 e R46 devem ser reclassificadas como Confirmada? | Sim, ambas. Viraram REQ-016. |
| Como gerar o identificador do evento? | Pelo banco, com `BIGINT GENERATED ALWAYS AS IDENTITY`. Ver [ADR-0003](../docs/adr/0003-audit-trail-integrity-and-persistence.md). |
| De onde vem o usuário responsável do evento de auditoria? | Constante `SIFAPSYS`, atrás de `ProvedorUsuarioResponsavel`. Decisão **provisória** — vira pré-requisito da fatia 2, porque uma trilha sem autor identificável não cumpre a IN-TCU 63/2010. |

---

### Continue lendo

| Anterior | Próximo |
|---|---|
| [Guia do Estágio 2](GUIDE.md)<br/><sub>Especificação moderna passo a passo.</sub> | [Template de ADR](ADR-TEMPLATE.md)<br/><sub>Registre a decisão de escopo como uma ADR.</sub> |

<sub>[Voltar ao índice do kit](../README.md)</sub>
