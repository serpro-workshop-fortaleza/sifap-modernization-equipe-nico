# ADR-0003: Integridade e persistência da trilha de auditoria

> **Trilha:** [Kit do Time](../../README.md) › [Documentação](../README.md) › [ADRs](README.md) › **ADR-0003**

| Campo | Valor |
|---|---|
| **Status** | accepted |
| **Data** | 2026-09-10 |
| **Autores** | Arquiteto de Software + DBA — Equipe Nico |
| **Substitui** | N/A |

---

## Contexto

A trilha de auditoria do SIFAP é obrigação legal declarada no próprio corpus: IN-TCU 63/2010 no cabeçalho do copycode (`01-archaeology/legacy-sifap/natural-programs/CCAUDIT.NSC:L8-L10`) e no do DDM (`01-archaeology/legacy-sifap/adabas-ddms/AUDIT.ddm:L13-L15`), com registro imutável e retenção mínima de 10 anos pelo art. 14 da Lei 8.159 (`AUDIT.ddm:L16-L17`).

O mecanismo legado é um copycode expandido por `INCLUDE` em sete módulos. A leitura do Estágio 1 expôs quatro defeitos estruturais que a fatia 001 precisa decidir antes de existir uma migração Flyway:

1. **Sequência sem trava.** `CCAUDIT.NSC:L65-L71` lê o maior `NUM-AUDIT` existente **uma única vez por execução** e incrementa em memória. Duas sessões online simultâneas produzem o mesmo número, contra o `U UNIQUE SEQUENCE` declarado em `AUDIT.ddm:L31`.
2. **Perda do evento em rollback.** `CCAUDIT.NSC:L57-L58` declara que a rotina não emite confirmação de transação. Em `CADPROG.NSP:L184` o `BACKOUT TRANSACTION` do tratador de erro apaga o evento junto com a operação de negócio. É por isso que as ações `ER=ERROR` e `RE=REJECTION`, previstas em `AUDIT.ddm:L47-L49`, **nunca são gravadas**.
3. **Campos sem gravador.** O DDM declara cerca de 35 campos; o copycode grava 13. `COD-PROFILE` tem o ticket 7742 aberto (`CCAUDIT.NSC:L50-L51`); os grupos `GRP-BEFORE` e `GRP-AFTER`, criados em 2005, nunca receberam escritor.
4. **Bloqueio de consulta delegado.** `CCAUDIT.NSC:L45-L48` declara que a ação `CO` não pode ser registrada desde 2010 pela Portaria CGTI 213/2010, mas acrescenta: "esta rotina não bloqueia `CO`; o bloqueio é responsabilidade de quem chama. Ticket 7740." A regra existe em comentário e não é imposta em lugar nenhum do código.

---

## Decisão

Adotaremos quatro decisões para o componente de auditoria da fatia 001:

**1. O identificador do evento é gerado pelo banco de dados.** A coluna usará `BIGINT GENERATED ALWAYS AS IDENTITY` no PostgreSQL. Mantém a natureza numérica e monotônica do `NUM-AUDIT N15` legado, permitindo carga dos dados históricos na mesma coluna, e elimina a corrida por construção. Não reproduziremos o algoritmo `max+1` da aplicação.

**2. O evento de auditoria é gravado em transação própria.** O componente usará propagação `REQUIRES_NEW`, de modo que o rollback da operação de negócio não apague o evento já registrado. Isso **inverte** o comportamento legado e é a condição para que as ações de erro e rejeição passem a existir.

**3. A tabela terá apenas as colunas efetivamente gravadas pelo legado.** São 13: identificador, data, hora, marca temporal, código da ação, módulo de origem, descrição, tipo da entidade, identificador da entidade, CPF afetado, usuário, nome do job e situação do lote. Os cerca de 22 campos sem gravador conhecido não entram no modelo. Valores anterior e novo ficam fora desta fatia, que só tem inclusão.

**4. A proibição de auditar consultas é imposta no componente.** O componente recusa qualquer evento cuja ação seja de consulta, substituindo sete chamadores confiáveis por uma guarda única. Fecha o ticket 7740 por construção.

---

## Alternativas consideradas

| Alternativa | Por que foi rejeitada |
|---|---|
| Identificador `UUID` v7 | Elimina a corrida, mas quebra a compatibilidade numérica com o `NUM-AUDIT N15` histórico e impede carregar os dados existentes na mesma coluna. |
| Reproduzir `max+1` na aplicação | Reproduz fielmente um defeito de concorrência conhecido, com risco de violação da unicidade sob carga concorrente que o legado monousuário não expunha. |
| Evento na mesma transação do negócio | Fiel ao legado, mas mantém a perda do evento em rollback e torna impossível registrar erro e rejeição — exatamente as ações que a IN-TCU exige e que o legado nunca gravou. |
| Padrão outbox | Garante entrega e atomicidade, mas exige processador assíncrono, tabela intermediária e monitoração de fila. Custo desproporcional para uma fatia com um único produtor de eventos. |
| Espelhar o DDM inteiro | Nasceria com mais de 20 colunas sem escritor, replicando no alvo a dívida que o Estágio 1 identificou como mistério aberto. |
| Manter o bloqueio de consulta em quem chama | Fiel ao legado, mas preserva um defeito que o próprio código documenta há mais de uma década sem correção. |

---

## Consequências

- **Mais fácil:** a trilha passa a registrar operações que falharam, o que o legado nunca conseguiu; a unicidade do identificador deixa de depender de disciplina da aplicação; a regra da Portaria CGTI 213/2010 passa a ser verificável por teste unitário do componente.
- **Mais difícil:** o evento em transação própria significa que um evento pode existir sem a operação de negócio correspondente. A leitura da trilha precisa deixar isso explícito, o que exige que a descrição da ação registre o desfecho, não só a intenção.
- **Riscos:** a carga dos dados históricos precisa preservar os valores de `NUM-AUDIT` existentes, o que exige ajustar a sequência da coluna de identidade após a migração; se houver colisões históricas no arquivo 153, elas aparecerão como violação de unicidade na carga.
- **Mitigações:** a migração de dados deve auditar duplicidades de `NUM-AUDIT` antes da carga e reposicionar a sequência com `ALTER TABLE ... ALTER COLUMN ... RESTART WITH`; a questão sobre colisões históricas permanece aberta com o DBA Adabas.

---

## Relacionados

- REQ-IDs: `REQ-014`, `REQ-016`, `REQ-017` de [`spec.md`](../../specs/001-social-program-catalog/spec.md)
- ADRs: [ADR-0004](0004-catalog-deliberate-divergences.md)
- Arquivos-fonte do legado: [`CCAUDIT.NSC`](../../01-archaeology/legacy-sifap/natural-programs/CCAUDIT.NSC) · [`CADPROG.NSP`](../../01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP) · [`AUDIT.ddm`](../../01-archaeology/legacy-sifap/adabas-ddms/AUDIT.ddm)

---

## Referências

- IN-TCU 63/2010, citada em `CCAUDIT.NSC:L8-L10` e `AUDIT.ddm:L13-L15`
- Portaria CGTI 213/2010, citada em `CCAUDIT.NSC:L45-L46`
- Lei 8.159, art. 14, citada em `AUDIT.ddm:L16-L17`
- RN-010 em `01-archaeology/legacy-sifap/legacy-docs/BUSINESS-RULES-2012.md:101`
- Decisões de validação humana de 2026-09-10, em [`scope-decisions.md`](../../02-modern-spec/scope-decisions.md)
