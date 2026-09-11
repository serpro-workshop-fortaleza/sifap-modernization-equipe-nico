# Relatório de Descoberta — Estágio 1: Arqueologia Digital

> **Trilha:** [Kit do Time](../README.md) › [Estágio 1](README.md) › **Relatório de Descoberta**

**Artefato preenchido pelo time ao fim do Estágio 1.** Consolida os achados da arqueologia e é a entrada principal do Estágio 2.

| Campo | Valor |
|---|---|
| **Público-alvo** | Todas as duplas — consolidação ao fim do Estágio 1 |
| **Pré-requisitos** | Catálogo de regras, mapa de dependências e glossário preenchidos |
| **Estágio** | Estágio 1 — Arqueologia |
| **Resultado esperado** | Documento de até 3 páginas com resumo, hipóteses de fatiamento e artefatos de origem |

> [!IMPORTANT]
> Este documento consolida todos os achados do Estágio 1. Preencha cada seção com as conclusões do time. Sem ele, a especificação do Estágio 2 não tem base de evidência.

> [!NOTE]
> Guia passo a passo: [`GUIDE.md`](GUIDE.md).

**Time**: Equipe Nico
**Data**: 2026-09-10
**Edição**: Imersão de modernização de legado
**Escopo lido**: 24 membros Natural, 4 DDMs, 2 JCLs e 3 documentos históricos da biblioteca `legacy-sifap`

---

## 1. Resumo executivo

O SIFAP, Sistema de Fiscalização e Administração de Pagamentos, é um sistema Natural/Adabas de 1997 que cadastra beneficiários, calcula e gera a folha mensal de benefícios sociais, transmite a remessa ao Banco do Brasil em layout CNAB 240 e concilia o arquivo de retorno. A biblioteca tem 24 membros e 4 arquivos Adabas no DBID 057, sendo `PAYMENT` (arquivo 152) o agregado central, com 611.902.774 registros e 257 GB no último ADAREP e sem política de expurgo. O sistema funciona, mas acumula caminhos concorrentes que nunca foram desativados: dois regimes de desconto contraditórios, uma cadeia de cálculo corporativa cujo resultado é descartado, um fator de ajuste aplicado duas vezes por fórmulas diferentes e um grupo inteiro de campos de conciliação no DDM que nenhum programa grava. Nove dos tickets citados nos próprios comentários do código continuam abertos, o mais antigo de 2004. O estágio produziu 4 mistérios canônicos, **todos fechados por validação humana em 2026-09-10**, e 27 achados adicionais que seguem abertos — todos com evidência `arquivo:linha`.

---

## 2. O que sabemos (confirmado)

### 2.1 Regras de negócio

Consulte [business-rules-catalog.md](business-rules-catalog.md). Estado atual: **45 regras extraídas**, todas com evidência `arquivo:linha`.

- **3 regras confirmadas por comentário literal do código**: desconto de tipo judicial (`VALUE 'J'`) isento do teto de 30% (`legacy-sifap/natural-programs/CALCDSCT.NSP:128`), gravação de auditoria na inclusão de programa social (`CADPROG.NSP:140`–`:147`) e obrigatoriedade da trilha (`CCAUDIT.NSC:60`–`:98`).
- **26 regras inferidas** a partir de estrutura de código sem comentário que as sustente. Ver seção 3.2.
- **17 regras convertidas em questões em aberto** por conflito entre código e documentação ou por lacuna sem fonte.
- **26 candidatas a requisito EARS** identificadas e prontas para o Estágio 2.
- **Cobertura**: a extração formal cobre `CALCDSCT.NSP`, `CADPROG.NSP` e `CCAUDIT.NSC` — os três membros que sustentam as duas primeiras fatias do Strangler Fig. O comportamento de `BATCHPGT`, `BATCHREL` e `BATCHCON` foi lido integralmente, mas os achados foram registrados em [mysteries-found.md](mysteries-found.md) e [dependency-map.md](dependency-map.md), não como regras — porque em quase todos os casos o comportamento observado conflita com outra fonte, o que o desqualifica como regra confirmada. **Cadastro de Beneficiários e Conciliação Bancária seguem sem extração formal.**

### 2.2 Dependências

Consulte [dependency-map.md](dependency-map.md) e o diagrama autônomo [dependency-map.mmd](dependency-map.mmd).

- **18 arestas programa → programa**: 9 `CALLNAT` e 9 `INCLUDE`, todas com `arquivo:linha`.
- **3 arestas por área de dados compartilhada**: `LDASIFAP` acopla 13 programas; `PDAVALID` acopla 7; `PDACALC` acopla 3.
- **36 arestas programa → DDM** e **3 arestas para arquivos sequenciais** (`CMWKF01` de remessa e de retorno, `CMWKF02` de rejeitados).
- **Hubs**: `CCAUDIT` com 7 inclusões de entrada; `SUBVALCP` com 4 chamadas de entrada; `PAYMENT` acessado por 8 programas e `BENEFIC` por 9.
- **Cinco escritores no mesmo agregado**: `BATCHPGT`, `CALCBENF`, `CALCCORR`, `CALCDSCT` e `BATCHCON` gravam em `PAYMENT` sem coordenação transacional entre si.
- **Ordem do batch**: `SIFAPJ01` STEP010 executa `BATCHPGT` (Control-M `SIFAP-MENSAL`, primeiro dia útil às 22:00, janela de 4 h, recurso `ADABAS-057`), STEP020 copia a remessa se `RC ≤ 4`, STEP030 emite aviso se `RC ≥ 5`; `SIFAPJ02` executa `BATCHREL` condicionado a `RC=0`. `BATCHCON` **não tem JCL** e é executado manualmente (`legacy-sifap/natural-programs/BATCHCON.NSP:14`).
- **Código sem chamador**: `CALCDSCT` não é invocado por nenhum programa da biblioteca, apesar de o cabeçalho de `BATCHPGT.NSP:17` afirmar o contrário. `VALBENEF.NSN:30` declara uma `VIEW` que nunca usa.

### 2.3 Estruturas de dados

Quatro DDMs, todos no DBID 057:

| DDM | Arquivo | Papel | Observação estrutural |
|---|---|---|---|
| `BENEFIC` | 150 | Cadastro de beneficiários | Chave de negócio `CPF`; escrito apenas por `CADBENEF` e `CADDEPEN`. |
| `SOCPROG` | 151 | Catálogo de programas sociais | Escrito apenas por `CADPROG`; leitura para todo o resto. |
| `PAYMENT` | 152 | Pagamentos | 57 campos elementares, 1 grupo periódico de descontos e 4 descritores derivados. 611.902.774 registros, 257 GB, crescimento de 3,8 milhões/mês, sem expurgo. |
| `AUDIT` | 153 | Trilha de auditoria | Justificada pela IN-TCU 63/2010 (`legacy-sifap/natural-programs/CADBENEF.NSP:38`). Escrita centralizada em `CCAUDIT`. |

Pontos estruturais que condicionam o modelo relacional do Estágio 3:

- O grupo periódico `PE` de descontos em `PAYMENT` é **ordenado**, e o cálculo depende dessa ordem física (ver seção 3.1). Uma tabela filha sem coluna de ordem não reproduz o legado.
- Os superdescritores `S1 SUPER-CPF-PERIOD`, `S2 SUPER-PROG-PERIOD-STAT` e `S3 SUPER-CYCLE-STAT` equivalem a índices compostos e devem ser recriados explicitamente.
- `S3` é construído sobre `AF NUM-CYCLE`, campo que **nenhum programa lido grava**.
- O DDM registra que o `ISN` não deve ser usado como chave externa: não há identidade estável além das chaves de negócio.
- Valores monetários são decimais empacotados (`P 9,2` e `P 3,2`) e exigem `BigDecimal` em Java. `PCT-DISC` é `P 3,2`, com máximo de 9,99, embora o teto aplicado pelo programa seja 30% e o IR seja 27,5%.

---

## 3. O que é arriscado

### 3.1 Questões em aberto aguardando validação humana

Registro completo, com impacto e responsável, em [mysteries-found.md](mysteries-found.md): **4 mistérios canônicos e 27 achados adicionais**. Os quatro canônicos foram fechados por Pedro de Lara em 2026-09-10; os 27 bônus continuam abertos.

| Questão em aberto | Evidência (`path:line`) | Impacto | Decisão validada em 2026-09-10 | Validado por | Status |
|---|---|---|---|---|---|
| `SIFAP-M-05` — Por que as posições 26 e 27 da tabela de fator regional são carregadas se a guarda de índice aceita apenas 1 a 25? | `BATCHPGT.NSP:218` · `:390` · `LDASIFAP.NSL:35` | Implementações corretas e incorretas produzem o mesmo resultado hoje e divergem quando a tabela mudar. | A guarda de índice está errada. A tabela tem 27 regiões válidas; 26 e 27 não são reservadas. | Pedro de Lara | fechada |
| `SIFAP-M-06` — Qual cálculo é a fonte de verdade da folha: o retorno de `CALCBENF` ou o cálculo inline que alimenta o `STORE`? | `BATCHPGT.NSP:351` · `:381` · `:479` | Escolher uma das duas muda os valores pagos ou perpetua um caminho não aprovado desde 2011. | O retorno de `CALCBENF` é a fonte de verdade. O cálculo inline é caminho legado a descartar. | Pedro de Lara | fechada |
| `SIFAP-M-07` — `P` em `STAT-PAYMENT` significa *pendente*, como diz o DDM, ou *pago*, como assume o código? | `PAYMENT.ddm:60` · `BATCHCON.NSP:207` · `BATCHREL.NSP:98` · `:188` | Define o enum de status; e faz o relatório com retenção legal de 10 anos contar cancelados como gerados. | `P` significa *pago*. A legenda de 1997 do DDM está desatualizada. O tratamento de `X` e `R` **não** foi respondido e virou achado bônus. | Pedro de Lara | fechada com resíduo |
| `SIFAP-M-08` — Por que nenhum campo do grupo `BANK RECONCILIATION` do DDM é gravado pelo programa de conciliação? | `PAYMENT.ddm:94`–`:99` · `BATCHCON.NSP:23` · `:205` | Não existe estado de conciliação consultável na base; gerar colunas a partir do DDM cria quatro colunas sempre nulas. | Os quatro campos estão mortos. Não viram colunas; o contexto de Conciliação Bancária cria tabela própria. | Pedro de Lara | fechada |

> [!WARNING]
> Fechar `SIFAP-M-06` em favor do `CALCBENF` **agrava** o conflito de alíquotas em vez de resolvê-lo. A sub-rotina `CALC-DISC` do próprio `CALCBENF` aplica 3% fixos acima de R$ 500,00 (`CALCBENF.NSN:358`–`:365`), com um comentário remetendo a `CALCDSCT` como *full version*. O caminho declarado autoritativo é justamente o da alíquota simplificada.

Os três achados bônus prioritários, selecionados para o facilitador, estão em [mysteries-found.md](mysteries-found.md): os dois regimes de contribuição concorrentes (3% fixos × tabela progressiva de 3/5/7/9%), o fator de ajuste aplicado duas vezes com a constante `0,347215` sem origem documentada, e o `MOVE TRUE TO #FOUND` fora do bloco `NO RECORDS FOUND` em `CADPROG`. Também pesam para o Estágio 2 o truncamento `A3` → `A1` de `TYPE-DISC`, que descarta 4 dos 8 valores do DDM, a dependência do resultado em relação à ordem física do grupo periódico e a ausência de trilha de auditoria em `CALCDSCT`.

### 3.2 Regras com evidência fraca

As 26 regras marcadas como **Inferida** em [business-rules-catalog.md](business-rules-catalog.md) descrevem o que o código faz, sem nenhuma fonte que confirme que é o que deveria fazer. As de maior risco:

- **Teto de 30% aplicado dentro do laço.** O código produz o comportamento; nenhum documento diz que é intencional. Um requisito EARS escrito a partir da leitura direta perpetua um efeito colateral.
- **Faixa de contribuição ausente acima de R$ 9.999,99.** Benefícios acima desse valor não recolhem contribuição. Reproduzir perpetua uma renúncia; corrigir altera valores históricos. Ticket 5510 aberto desde 2016.
- **Divergência entre a RN-021 documentada e o código.** O texto de 2012 descreve rejeição total dos descontos; o código trunca e mantém. Impossível saber qual é o requisito sem validação humana.
- **Tolerância de R$ 0,01 na conciliação.** Constante sem norma citada, aplicada sobre centenas de milhões de registros.
- **Idade calculada só pelo ano.** `#AGE = #YEAR - #YEAR-BIRTH` ignora mês e dia, o que desloca em até 11 meses os fatores etários de 60 e 65 anos.

> [!WARNING]
> Nenhuma regra desta seção deve virar requisito EARS no Estágio 2 sem passar antes por validação humana. Escrever `source_legacy:` apontando para uma regra inferida satisfaz a CI, mas não satisfaz a corretude.

---

## 4. Hipóteses de fatiamento recomendadas

> [!NOTE]
> As cinco hipóteses abaixo são **hipóteses**, não decisões de arquitetura. Foram derivadas de quem escreve em cada DDM e de quem chama quem — não de uma visão de domínio validada com a área de negócio. Cabe ao Estágio 2 confirmá-las, fundi-las ou descartá-las.

### Hipótese 1: Catálogo de Programas Sociais

- Programas: `CADPROG`
- DDMs: `SOCPROG` (151)
- Justificativa: o menor e mais independente agrupamento da biblioteca. Escritor único, sem `CALLNAT` de saída, apenas `INCLUDE CCAUDIT`. Todos os demais programas só leem `SOCPROG`. É o candidato natural à primeira fatia de um Strangler Fig, porque falhar aqui custa pouco.

### Hipótese 2: Cadastro de Beneficiários

- Programas: `CADBENEF`, `CADDEPEN`, `VALBENEF`, `VALDOCS`, `SUBVALCP`, `SUBVALNI`, `CCVALCPF`
- DDMs: `BENEFIC` (150)
- Justificativa: é o único agrupamento que escreve em `BENEFIC`, e todas as suas chamadas de saída são validação de documento (CPF, NIS/PIS/PASEP). Não depende de `PAYMENT` em nenhuma aresta. A fronteira é visível no próprio grafo de chamadas.

### Hipótese 3: Cálculo e Folha de Pagamento

- Programas: `BATCHPGT`, `CALCBENF`, `VALELEG`, `CALCDSCT`, `CALCCORR`
- DDMs: `PAYMENT` (152) para escrita; `BENEFIC` (150) e `SOCPROG` (151) para leitura
- Justificativa: concentra os cinco escritores de `PAYMENT` e é onde vivem os dois mistérios de maior impacto financeiro (`SIFAP-M-06` e os regimes de desconto concorrentes). Também é o agrupamento mais acoplado por área de dados: `LDASIFAP` e `PDACALC` atravessam todos os seus membros. **Não deve ser a primeira fatia.**

### Hipótese 4: Conciliação Bancária

- Programas: `BATCHCON`
- DDMs: `PAYMENT` (152) para atualização, `AUDIT` (153) para escrita; arquivos CNAB 240 de remessa e retorno
- Justificativa: é o único agrupamento com fronteira externa real — o Banco do Brasil. Não tem contraparte online, não é agendado e depende de um formato de arquivo estável desde 2008. Separá-lo isola o risco de integração do risco de cálculo.

### Hipótese 5: Relatórios, Consultas e Auditoria

- Programas: `BATCHREL`, `RELPGT`, `RELAUDIT`, `CONSBENF`, `CCAUDIT`
- DDMs: `AUDIT` (153) para escrita; `PAYMENT` e `BENEFIC` somente leitura
- Justificativa: somente leitura sobre os dados de negócio, com escrita restrita à trilha de auditoria. A retenção legal de 10 anos do relatório consolidado (Lei 8.159, art. 14) e a IN-TCU 63/2010 sugerem um ciclo de vida de persistência próprio, distinto do transacional.

---

## 5. Artefatos de origem

| Artefato | Caminho | Status |
|---|---|---|
| Inventário | [inventory.md](inventory.md) | Completo — 40 arquivos catalogados |
| Regras de Negócio | [business-rules-catalog.md](business-rules-catalog.md) | Parcial — 45 regras; cobre `CALCDSCT`, `CADPROG` e `CCAUDIT` |
| Dependências | [dependency-map.md](dependency-map.md) | Completo — 57 arestas com evidência |
| Questões em aberto | [mysteries-found.md](mysteries-found.md) | Completo — 4 canônicos fechados, 27 bônus abertos |
| Glossário | [glossary.md](glossary.md) | Completo — 67 termos, todos confirmados |
| Checklist de mistérios | [mysteries-checklist.md](mysteries-checklist.md) | Placar da dupla preenchido |

---

## 6. Aprovação do time

- Revisado por: **Pedro de Lara**
- Data: **2026-09-10**
- Confiança: **Alta**

> [!IMPORTANT]
> A revisão humana de 2026-09-10 cobre os quatro mistérios canônicos e as 14 hipóteses do glossário. **Não** cobre os 27 achados bônus, que seguem abertos, nem as 26 regras inferidas da seção 3.2. Os responsáveis nominados em [mysteries-found.md](mysteries-found.md) foram extraídos dos cabeçalhos dos próprios artefatos legados e precisam ser confirmados antes de qualquer contato.

---

## Definição de pronto

- [x] Resumo com no máximo 5 frases.
- [x] De 3 a 5 hipóteses de fatiamento documentadas. **5 hipóteses.**
- [x] Todos os artefatos de origem têm status preenchido.
- [x] O documento não passa de 3 páginas.
- [x] Revisão humana e nível de confiança registrados na seção 6.

---

### Continue lendo

| Anterior | Próximo |
|---|---|
| [GUIDE do Estágio 1](GUIDE.md)<br/><sub>Cronograma passo a passo.</sub> | [Estágio 2 — Especificação moderna](../02-modern-spec/README.md)<br/><sub>Handoff H1 e início do EARS.</sub> |

<sub>[Voltar ao índice do kit](../README.md)</sub>
