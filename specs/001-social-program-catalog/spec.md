---
feature: "001-social-program-catalog"
title: "Catálogo de Programas Sociais"
version: "1.0.0"
status: "implementada; aprovação formal não registrada"
author: "Equipe Nico"
date: "2026-09-10"
bounded_context: "Catálogo de Programas Sociais"
---

# Especificação — Catálogo de Programas Sociais

> **Trilha:** [Kit do Time](../../README.md) › [Especificações](../README.md) › **001 · Catálogo de Programas Sociais**

**Requisitos EARS da primeira fatia do Strangler Fig: inclusão e consulta de programa social, com a trilha de auditoria que a inclusão dispara.**

| Campo | Valor |
|---|---|
| **Contexto delimitado** | Catálogo de Programas Sociais — ver [`bounded-contexts.md`](../../02-modern-spec/bounded-contexts.md) |
| **Estágio** | Estágio 2 — Especificação |
| **Membros legados lidos** | `CADPROG.NSP` (187 linhas) · `CCAUDIT.NSC` (100 linhas) |
| **DDMs cruzados** | `SOCPROG.ddm` (arquivo 151, DBID 057) · `AUDIT.ddm` (arquivo 153, DBID 057) |
| **Requisitos normativos** | 17 |
| **Candidatas adiadas** | 6 — registradas em [`scope-decisions.md`](../../02-modern-spec/scope-decisions.md) |
| **Validação humana** | Rodada de decisões de 2026-09-10 — ver seção 6 |

> [!IMPORTANT]
> Onze requisitos desta especificação nascem de regras que o Estágio 1 classificou como **Inferida**. Elas foram promovidas a Confirmada em validação humana explícita, registrada na seção 6 e em [`scope-decisions.md`](../../02-modern-spec/scope-decisions.md). Nenhuma regra classificada como **Mistério** virou requisito. O aviso de [`discovery-report.md`](../../01-archaeology/discovery-report.md) continua valendo: `source_legacy:` válido satisfaz a CI, não a corretude.

---

## 1. Por que esta fatia

O Catálogo de Programas Sociais é a fatia 1 do Strangler Fig em [`bounded-contexts.md`](../../02-modern-spec/bounded-contexts.md). Três razões sustentam a escolha:

1. **É a raiz de dependência do sistema.** O arquivo 151 é lido por `BATCHPGT`, `CALCBENF` e `CALCDSCT`; o programa social é o parâmetro de que todo cálculo depende. Nenhuma outra fatia pode ser cortada antes dela sem duplicar dados.
2. **Tem uma tela completa e autocontida.** `CADPROG.NSP` cobre inclusão e consulta em 187 linhas, com um único DDM de escrita. É a menor superfície de corte que ainda entrega valor de ponta a ponta.
3. **Arrasta a trilha de auditoria pelo caminho mínimo.** A inclusão é o único gatilho de auditoria com regra **Confirmada** desde o Estágio 1 (Regra 29). O mecanismo (Regra 36) entra como requisito de apoio, porque sem ele a Regra 29 não tem critério de aceitação verificável.

A trilha de auditoria é obrigação legal declarada no próprio corpus: a IN-TCU 63/2010 aparece no cabeçalho do copycode (`CCAUDIT.NSC:L8-L10`) e no do DDM (`AUDIT.ddm:L13-L15`), que também impõe registro imutável e retenção mínima de 10 anos pelo art. 14 da Lei 8.159 (`AUDIT.ddm:L16-L17`).

---

## 2. Escopo

### Dentro do escopo

- Inclusão de programa social, com validação de entrada nos limites do sistema.
- Consulta de programa social pelo código.
- Registro do evento de auditoria disparado pela inclusão, e o mecanismo mínimo que o sustenta.
- Interface web para os dois fluxos: tela de inclusão e tela de consulta.

### Fora do escopo

| Item | Motivo |
|---|---|
| Alteração e exclusão de programa social | Não existem no legado. O manual de 2008 afirma que a tela faz "inclusão **e alteração**" (`TECHNICAL-MANUAL-SIFAP-2008.md:279`), mas não há caminho de alteração em `CADPROG.NSP`. Divergência entre documento e código, não resolvida. |
| Verificação explícita de código duplicado | Regra 21 é **Mistério**: `MOVE TRUE TO #FOUND` está fora do bloco `NO RECORDS FOUND` (`CADPROG.NSP:L113-L115`). Decisão de 2026-09-10: a unicidade é garantida pela chave primária, sem requisito de mensagem de negócio. Ver seção 5. |
| Fator K e a constante `0,347215` | Aplicado na inclusão (`CADPROG.NSP:L124`) e de novo no pagamento por fórmula diferente (`BATCHPGT.NSP:432`, `CALCBENF.NSN:262`). Regras 23 e 24 são **Mistério**. REQ-005 registra a divergência deliberada. |
| Campo persistido `FACTOR-K` do DDM | `SOCPROG.ddm:L47` é marcado `>>> UNDOCUMENTED <<<`, tem aviso da SENARC e **nenhum gravador nem leitor conhecido**. Regra 24, Mistério. |
| Faixas de cálculo, tipos de desconto e parâmetros regionais | Grupos periódicos e campo múltiplo (`SOCPROG.ddm:L69`, `:L79`, `:L86`) que `CADPROG` nunca grava. Regra 35, Mistério. Um programa incluído nasce sem faixas também no legado. |
| Lista de códigos de elegibilidade válidos | Não existe em nenhum membro nem documento. Regra 33, Mistério. O campo é aceito sem validação de domínio. |
| Controle de acesso por perfil | O manual de 2008 afirma acesso restrito ao perfil ADMIN (`TECHNICAL-MANUAL-SIFAP-2008.md:287`); não há verificação no programa. Regra 34, Mistério. |
| Valor anterior e valor novo no evento de auditoria | Exigidos pela RN-010 e pelo projeto de 1997, **nunca implementados** no legado. A fatia só tem inclusão, que não tem estado anterior. Questão em aberto. |
| Contexto de lote no evento de auditoria | O legado grava `'S'` incondicionalmente, inclusive quando o lote falha (`CCAUDIT.NSC:L93-L95`). Fora desta fatia, que não tem processamento batch. |
| Perfil, nome, lotação, IP, sessão, terminal e transação do usuário | 22 dos cerca de 35 campos do DDM `AUDIT` não têm gravador conhecido. `COD-PROFILE` tem o ticket 7742 aberto (`CCAUDIT.NSC:L50-L51`). |
| Demais contextos delimitados | Cadastro de Beneficiários, Cálculo e Folha e Conciliação Bancária. Ver [`scope-decisions.md`](../../02-modern-spec/scope-decisions.md). |

---

## 3. Requisitos

### 3.1 Cadastro

### REQ-001 — Incluir programa social

QUANDO a inclusão de um programa social for solicitada com código, nome, tipo, valor base, código de elegibilidade, data de criação, data de encerramento, renda per capita máxima, idade mínima, idade máxima e fator de ajuste, o sistema DEVE persistir o programa e confirmar a inclusão.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP#L95-L139

- **Evidência de apoio:** `SOCPROG.ddm:L28-L37` (identificação e situação) · `SOCPROG.ddm:L41` (valor base) · `SOCPROG.ddm:L56-L58` (elegibilidade) · `SOCPROG.ddm:L52` (fator de ajuste)
- **Classificação da regra de origem:** Confirmada por validação humana em 2026-09-10 (bloco de inclusão de `CADPROG.NSP`)

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — inclusão aceita**
  - Dado um conjunto de dados de programa social que satisfaz REQ-002 e REQ-006 a REQ-009
  - Quando a inclusão for solicitada
  - Então o programa passa a existir no catálogo com todos os campos informados
  - E a resposta confirma a inclusão e devolve o código do programa

- **Cenário 2 — campo obrigatório ausente**
  - Dado uma solicitação de inclusão sem código, sem nome ou sem tipo
  - Quando a inclusão for solicitada
  - Então o sistema recusa a inclusão e informa qual campo está ausente
  - E nenhum programa é persistido

---

### REQ-002 — Identificar o programa por código alfanumérico de quatro posições

O sistema DEVE identificar cada programa social por um código alfanumérico de exatamente quatro posições, único no catálogo.

source_legacy: 01-archaeology/legacy-sifap/adabas-ddms/SOCPROG.ddm#L28

- **Evidência de apoio:** `CADPROG.NSP:L110` e `:L157` convertem entrada numérica `N4` para `A4` com zeros à esquerda; a chave real é alfanumérica
- **Classificação da regra de origem:** Confirmada por validação humana em 2026-09-10 (Regra 28, Mistério parcialmente resolvido pela decisão de adotar a chave real)

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — código alfanumérico aceito**
  - Dado um código de quatro posições contendo letras
  - Quando a inclusão for solicitada
  - Então o código é aceito e persistido como informado

- **Cenário 2 — entrada numérica normalizada**
  - Dado um código informado como número de até quatro dígitos
  - Quando a inclusão for solicitada
  - Então o código é normalizado com zeros à esquerda até quatro posições

- **Cenário 3 — comprimento inválido**
  - Dado um código com menos de uma ou mais de quatro posições após a normalização
  - Quando a inclusão for solicitada
  - Então o sistema recusa a inclusão e informa a ocorrência

> [!NOTE]
> A tela legada só alcança códigos numéricos (`MOVE EDITED ... (EM=9999)`). Se existirem códigos alfanuméricos no arquivo 151, eles são inalcançáveis pela única tela de manutenção. Adotar `A4` é condição para ler os dados existentes. Regra 28 permanece registrada como Mistério em [`mysteries-found.md`](../../01-archaeology/mysteries-found.md).

---

### REQ-003 — Registrar o programa incluído com situação ativa

QUANDO um programa social for incluído, o sistema DEVE registrá-lo com a situação ativa.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP#L134

- **Evidência de apoio:** `SOCPROG.ddm:L37` (`A=ACTIVE I=INACTIVE E=ENDED`)
- **Classificação da regra de origem:** Confirmada por validação humana em 2026-09-10 (Regra 25)

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — situação atribuída pelo sistema**
  - Dado uma solicitação de inclusão válida
  - Quando o programa for persistido
  - Então sua situação é ativa, independentemente de qualquer situação informada na solicitação

> [!NOTE]
> O legado grava `'A'` fixo e não tem caminho de alteração: a situação **nunca muda** por esta tela, embora o DDM admita inativa e encerrada. A transição de situação fica fora desta fatia.

---

### REQ-004 — Tratar ausência de data de encerramento como vigência indeterminada

ONDE a data de encerramento do programa não estiver definida, o sistema DEVE considerar a vigência do programa como indeterminada.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP#L133

- **Evidência de apoio:** `SOCPROG.ddm:L36` (`YYYYMMDD (0=ACTIVE)`) · `CADPROG.NSP:L101` rotula o campo na tela como `(0=OPEN-ENDED)`
- **Classificação da regra de origem:** Confirmada por validação humana em 2026-09-10 (Regra 26)

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — sem data de encerramento**
  - Dado uma inclusão em que a data de encerramento não é informada
  - Quando o programa for persistido
  - Então a vigência é registrada como indeterminada
  - E a consulta apresenta o programa como vigente por prazo indeterminado

---

### REQ-005 — Persistir o valor base como informado e o fator de ajuste em campo próprio

QUANDO um programa social for incluído, o sistema DEVE persistir o valor base exatamente como informado e persistir o fator de ajuste em campo próprio, sem aplicar fator de correção no momento da inclusão.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP#L124-L125

- **Evidência de apoio:** `SOCPROG.ddm:L41` (valor base) · `SOCPROG.ddm:L52` (fator de ajuste, acrescentado em 2002) · `BATCHPGT.NSP:432` e `CALCBENF.NSN:262` aplicam `× (1 + fator)` na folha
- **Classificação da regra de origem:** **Divergência deliberada** do legado, decidida em 2026-09-10. Regras 23 e 24 permanecem Mistério.

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — valor preservado**
  - Dado uma inclusão com valor base e fator de ajuste informados
  - Quando o programa for persistido
  - Então o valor base armazenado é idêntico ao informado
  - E o fator de ajuste é armazenado separadamente, sem alterar o valor base

> [!WARNING]
> O legado grava `valor × (1,00 + fator × 0,347215)` na inclusão e a folha aplica o ajuste **de novo**, por fórmula diferente. A constante `0,347215` não aparece em nenhum outro membro nem em nenhum documento do corpus. Reproduzir esse encadeamento propagaria para o sistema novo um ajuste aplicado duas vezes por fórmulas incompatíveis. A decisão de não aplicar o fator na inclusão exige ADR e **muda o valor persistido em relação ao legado** — a migração de dados históricos precisa tratar isso explicitamente.

---

### REQ-006 — Recusar valor base acima do limite representável

SE o valor base informado exceder 99.999,99, ENTÃO o sistema DEVE recusar a inclusão e informar a ocorrência.

source_legacy: 01-archaeology/legacy-sifap/adabas-ddms/SOCPROG.ddm#L41

- **Evidência de apoio:** `CADPROG.NSP:L49` e `:L62` declaram as variáveis de tela como `P9.2`, enquanto o campo persistido é `P7.2`; o `MOVE` de `CADPROG.NSP:L130` trunca dígitos de alta ordem em silêncio. O manual de 2008 registra o erro `U4038 — overflow no cálculo` (`TECHNICAL-MANUAL-SIFAP-2008.md:487`).
- **Classificação da regra de origem:** **Divergência deliberada** do legado, decidida em 2026-09-10. Regra 27 permanece Mistério.

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — valor no limite**
  - Dado um valor base igual a 99.999,99
  - Quando a inclusão for solicitada
  - Então a inclusão é aceita

- **Cenário 2 — valor acima do limite**
  - Dado um valor base igual a 100.000,00
  - Quando a inclusão for solicitada
  - Então o sistema recusa a inclusão e informa que o valor excede o limite
  - E nenhum programa é persistido

> [!NOTE]
> O limite reproduz a capacidade do campo legado, para preservar a coexistência com o arquivo 151 durante o Strangler Fig. A divergência é recusar explicitamente em vez de truncar em silêncio.

---

### REQ-007 — Recusar tipo de programa fora do domínio previsto

SE o tipo de programa informado não for assistência, previdência ou trabalho, ENTÃO o sistema DEVE recusar a inclusão e informar a ocorrência.

source_legacy: 01-archaeology/legacy-sifap/adabas-ddms/SOCPROG.ddm#L31

- **Evidência de apoio:** `SOCPROG.ddm:L31-L32` declara `A=ASSISTANCE T=EMPLOYMENT P=SOCIAL SECURITY` com marcador de domínio fixo · `CADPROG.NSP:L18` repete o domínio em comentário · `CADPROG.NSP:L97` rotula a tela com `(A/P/T)`
- **Classificação da regra de origem:** **Greenfield fundamentado no DDM**, decidido em 2026-09-10. O legado declara o domínio e **não o valida**. Regra 33 permanece Mistério.

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — tipo válido**
  - Dado um tipo pertencente ao domínio declarado no DDM
  - Quando a inclusão for solicitada
  - Então a inclusão prossegue

- **Cenário 2 — tipo inválido**
  - Dado um tipo fora do domínio declarado
  - Quando a inclusão for solicitada
  - Então o sistema recusa a inclusão e informa os valores aceitos
  - E nenhum programa é persistido

---

### REQ-008 — Recusar faixa etária incoerente

SE a idade mínima e a idade máxima estiverem ambas definidas e a idade mínima for maior que a idade máxima, ENTÃO o sistema DEVE recusar a inclusão e informar a ocorrência.

source_legacy: 01-archaeology/legacy-sifap/adabas-ddms/SOCPROG.ddm#L57

- **Evidência de apoio:** `SOCPROG.ddm:L57-L58` declara ambas as idades com `(0=NONE)`, isto é, zero significa ausência de limite, não idade zero
- **Classificação da regra de origem:** **Greenfield fundamentado no DDM**, decidido em 2026-09-10. Regra 33 permanece Mistério.

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — sem limite de idade**
  - Dado idade mínima e idade máxima informadas como zero
  - Quando a inclusão for solicitada
  - Então a inclusão prossegue e o programa não tem restrição de idade

- **Cenário 2 — faixa invertida**
  - Dado idade mínima 65 e idade máxima 18
  - Quando a inclusão for solicitada
  - Então o sistema recusa a inclusão e informa a incoerência

- **Cenário 3 — apenas um limite definido**
  - Dado idade mínima 65 e idade máxima zero
  - Quando a inclusão for solicitada
  - Então a inclusão prossegue, porque zero indica ausência de limite superior

---

### REQ-009 — Recusar datas de vigência incoerentes

SE a data de encerramento estiver definida e for anterior à data de criação, ENTÃO o sistema DEVE recusar a inclusão e informar a ocorrência.

source_legacy: 01-archaeology/legacy-sifap/adabas-ddms/SOCPROG.ddm#L36

- **Evidência de apoio:** `SOCPROG.ddm:L35-L36` declara as duas datas no formato `YYYYMMDD`, com `0=ACTIVE` apenas para o encerramento
- **Classificação da regra de origem:** **Greenfield fundamentado no DDM**, decidido em 2026-09-10. Regra 33 permanece Mistério.

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — datas coerentes**
  - Dado data de criação anterior à data de encerramento
  - Quando a inclusão for solicitada
  - Então a inclusão prossegue

- **Cenário 2 — encerramento anterior à criação**
  - Dado data de encerramento anterior à data de criação
  - Quando a inclusão for solicitada
  - Então o sistema recusa a inclusão e informa a incoerência

- **Cenário 3 — vigência indeterminada**
  - Dado data de encerramento não informada
  - Quando a inclusão for solicitada
  - Então a regra não se aplica e a inclusão prossegue, conforme REQ-004

---

### REQ-010 — Recusar operação não suportada

SE a operação solicitada não for a inclusão nem a consulta de programa social, ENTÃO o sistema DEVE recusá-la e informar a ocorrência.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP#L84-L87

- **Evidência de apoio:** `CADPROG.NSP:L82` oferece apenas `(I/C)` na tela, sem legenda; `CADPROG.NSP:L89` confirma que `C` é consulta, não alteração
- **Classificação da regra de origem:** Confirmada por validação humana em 2026-09-10 (Regra 19)

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — operação desconhecida**
  - Dado uma solicitação que não corresponde a inclusão nem a consulta
  - Quando ela for recebida
  - Então o sistema a recusa e informa a ocorrência
  - E nenhum dado é alterado

---

### REQ-011 — Consultar programa social pelo código

QUANDO a consulta de um programa social pelo código for solicitada e o programa existir, o sistema DEVE apresentar o código, o nome, o tipo, o valor base, o código de elegibilidade e a situação do programa.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP#L158-L165

- **Evidência de apoio:** `CADPROG.NSP:L89-L92` encaminha a operação `C` para a sub-rotina de consulta · `SOCPROG.ddm:L28-L41` declara os campos apresentados
- **Classificação da regra de origem:** Confirmada por validação humana em 2026-09-10 (Regra 20)

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — programa existente**
  - Dado um programa social cadastrado
  - Quando a consulta pelo seu código for solicitada
  - Então o sistema apresenta exatamente os seis campos exigidos

- **Cenário 2 — campos não apresentados**
  - Dado um programa social cadastrado
  - Quando a consulta pelo seu código for solicitada
  - Então a resposta não inclui renda per capita máxima, idades nem fator de ajuste, porque a consulta legada não os apresenta

---

### REQ-012 — Informar programa social não localizado

SE o programa social consultado não for localizado, ENTÃO o sistema DEVE informar que o programa não foi encontrado.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP#L168-L170

- **Evidência de apoio:** o próprio código marca o estilo como não padronizado e cita o ticket 3312/2004 em aberto (`CADPROG.NSP:L166-L167`)
- **Classificação da regra de origem:** Confirmada por validação humana em 2026-09-10 (Regra 31)

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — código inexistente**
  - Dado um código que não corresponde a nenhum programa
  - Quando a consulta for solicitada
  - Então o sistema informa que o programa não foi encontrado
  - E a resposta distingue "não encontrado" de erro de processamento

---

### REQ-013 — Desfazer alterações não confirmadas em caso de erro

SE ocorrer erro não tratado durante a inclusão, ENTÃO o sistema DEVE desfazer as alterações não confirmadas do catálogo e sinalizar a falha.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP#L181-L186

- **Evidência de apoio:** `CADPROG.NSP:L184` (`BACKOUT TRANSACTION`) · `CADPROG.NSP:L185` (`TERMINATE 12`)
- **Classificação da regra de origem:** Confirmada por validação humana em 2026-09-10 (Regra 32)

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — erro durante a inclusão**
  - Dado uma inclusão em andamento
  - Quando ocorrer erro não tratado antes da confirmação
  - Então nenhuma alteração do catálogo permanece persistida
  - E o sistema sinaliza a falha de forma distinguível de uma recusa por validação

- **Cenário 2 — evento de auditoria preservado**
  - Dado o mesmo cenário anterior
  - Quando o desfazimento ocorrer
  - Então o evento de auditoria já registrado permanece, conforme REQ-017

---

### 3.2 Trilha de auditoria

### REQ-014 — Registrar evento de auditoria em operação que altera dados

QUANDO uma operação alterar dados persistidos do sistema, o sistema DEVE registrar um evento de auditoria imutável contendo identificador único do evento, data do evento, hora do evento, marca temporal do evento, código da ação, módulo de origem, descrição da ação, tipo da entidade afetada, identificador da entidade afetada e usuário responsável.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CCAUDIT.NSC#L60-L98

- **Evidência de apoio:** `AUDIT.ddm:L13-L18` (imutabilidade, IN-TCU 63/2010, retenção de 10 anos) · `AUDIT.ddm:L31-L34` · `AUDIT.ddm:L39-L51` · `AUDIT.ddm:L55-L57`
- **Classificação da regra de origem:** Confirmada desde o Estágio 1 (Regra 36)

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — evento gravado**
  - Dado que uma operação de negócio altera dados persistidos
  - Quando a operação for concluída com sucesso
  - Então existe exatamente um evento de auditoria correspondente, com os dez atributos exigidos preenchidos
  - E o módulo de origem identifica a operação que provocou o evento, não o componente de auditoria

- **Cenário 2 — imutabilidade**
  - Dado um evento de auditoria já gravado
  - Quando qualquer operação tentar alterá-lo ou removê-lo
  - Então o sistema recusa a operação e o evento permanece inalterado

- **Cenário 3 — identificador único sob concorrência**
  - Dado duas operações de negócio executadas simultaneamente
  - Quando ambas registrarem seus eventos
  - Então os identificadores são distintos e monotônicos

> [!NOTE]
> O Cenário 3 exige unicidade sob concorrência, **não** o algoritmo do legado. `CCAUDIT.NSC:L65-L71` lê o maior identificador existente uma única vez por execução e incrementa em memória, sem trava — duas sessões simultâneas produzem o mesmo número, contra o `U UNIQUE SEQUENCE` declarado em `AUDIT.ddm:L31`. A decisão de 2026-09-10 é delegar a geração ao banco de dados. Ver ADR correspondente.

> [!WARNING]
> **O usuário responsável é provisório nesta fatia.** Como o recorte não inclui autenticação, a decisão de 2026-09-10 grava a constante `SIFAPSYS` em todos os eventos. O requisito continua exigindo o usuário responsável; o que está adiado é a capacidade de identificá-lo. Enquanto isso valer, a trilha registra o que mudou e quando, **nunca quem** — o que não satisfaz a finalidade da IN-TCU 63/2010 citada em `AUDIT.ddm:L13-L18`. Ver P4 na seção 8 de [`plan.md`](plan.md).

---

### REQ-015 — Registrar a inclusão de programa social na trilha de auditoria

QUANDO um programa social for incluído, o sistema DEVE registrar um evento de auditoria com a ação de inclusão, o tipo de entidade correspondente a programa social e o código do programa incluído como identificador da entidade.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP#L140-L147

- **Evidência de apoio:** `AUDIT.ddm:L39-L40` (`IN=INSERTION`) · `AUDIT.ddm:L55` (`TYPE-ENTITY` admite `PROG`) · `AUDIT.ddm:L57` (`NUM-CPF-AFFECTED` aplicável apenas quando existe titular)
- **Classificação da regra de origem:** Confirmada desde o Estágio 1 (Regra 29)

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — evento da inclusão**
  - Dado que uma inclusão de programa social foi aceita
  - Quando o programa for persistido
  - Então existe um evento de auditoria cuja ação indica inclusão, cujo tipo de entidade indica programa social e cujo identificador da entidade é o código do programa incluído

- **Cenário 2 — ausência de titular**
  - Dado o evento de auditoria de uma inclusão de programa social
  - Quando o CPF afetado for consultado
  - Então ele está vazio, porque programa social não tem pessoa titular

- **Cenário 3 — inclusão recusada não gera evento**
  - Dado uma inclusão recusada por qualquer requisito de validação desta especificação
  - Quando a recusa ocorrer
  - Então nenhum evento de auditoria de inclusão é registrado, porque nenhum dado foi alterado

---

### REQ-016 — Recusar registro de auditoria para operações de consulta

SE a ação submetida ao componente de auditoria for uma consulta, ENTÃO o sistema DEVE recusar o registro do evento.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CCAUDIT.NSC#L45-L48

- **Evidência de apoio:** `AUDIT.ddm:L43` declara a ação `CO=QUERY` · `CADPROG.NSP:L156-L171` confirma que a sub-rotina de consulta não chama a gravação de auditoria
- **Classificação da regra de origem:** Confirmada por validação humana em 2026-09-10 (Regras 30 e 46, promovidas a partir do comentário literal que cita a Portaria CGTI 213/2010)

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — consulta não auditada**
  - Dado uma consulta de programa social bem-sucedida
  - Quando ela for concluída
  - Então nenhum evento de auditoria é registrado

- **Cenário 2 — recusa centralizada**
  - Dado um chamador que submeta ao componente de auditoria um evento com ação de consulta
  - Quando o registro for tentado
  - Então o componente recusa o registro e sinaliza a violação da regra

> [!NOTE]
> O legado delega o bloqueio a quem chama: "esta rotina não bloqueia `CO`; o bloqueio é responsabilidade de quem chama. Ticket 7740" (`CCAUDIT.NSC:L47-L48`). A decisão de 2026-09-10 é impor a regra no próprio componente, substituindo sete chamadores confiáveis por uma guarda única. Divergência deliberada, com ADR.

---

### REQ-017 — Preservar o evento de auditoria quando a operação de negócio for desfeita

SE a operação de negócio for desfeita por erro após o registro do evento de auditoria, ENTÃO o sistema DEVE preservar o evento registrado.

source_legacy: 01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP#L181-L186

- **Evidência de apoio:** `CCAUDIT.NSC:L57-L58` declara que a rotina não emite confirmação de transação · `CADPROG.NSP:L184` desfaz a transação inteira · as ações `ER` e `RE` previstas em `AUDIT.ddm:L47-L49` nunca são gravadas
- **Classificação da regra de origem:** **Divergência deliberada** do legado, decidida em 2026-09-10

**Aceitação (Dado/Quando/Então):**

- **Cenário 1 — negócio desfeito, auditoria preservada**
  - Dado uma inclusão que registrou o evento de auditoria e em seguida falhou
  - Quando o desfazimento da operação de negócio ocorrer
  - Então o programa social não é persistido
  - E o evento de auditoria correspondente permanece consultável

- **Cenário 2 — falha da auditoria não impede o negócio**
  - Dado uma inclusão válida
  - Quando o registro do evento de auditoria falhar
  - Então a falha é sinalizada e registrada, e o comportamento definido é o de REQ-013

> [!WARNING]
> No legado, `STORE PROGRAM-V`, a gravação da auditoria e a confirmação da transação são consecutivos (`CADPROG.NSP:L139-L147`): um erro posterior apaga o evento junto com a operação. Este requisito **inverte** esse comportamento. É a divergência de maior impacto arquitetural da fatia e exige ADR próprio.

---

## 4. Matriz de rastreabilidade

| REQ-ID | Padrão EARS | source_legacy | Regra de origem | Classificação no Estágio 1 | Situação |
|---|---|---|---|---|---|
| REQ-001 | Orientado a evento | `CADPROG.NSP#L95-L139` | bloco de inclusão | — | Promovida em 2026-09-10 |
| REQ-002 | Ubíquo | `SOCPROG.ddm#L28` | Regra 28 | Mistério | Promovida em 2026-09-10 |
| REQ-003 | Orientado a evento | `CADPROG.NSP#L134` | Regra 25 | Inferida | Promovida em 2026-09-10 |
| REQ-004 | Opcional | `CADPROG.NSP#L133` | Regra 26 | Inferida | Promovida em 2026-09-10 |
| REQ-005 | Orientado a evento | `CADPROG.NSP#L124-L125` | Regras 23 e 24 | Mistério | Divergência deliberada |
| REQ-006 | Indesejado | `SOCPROG.ddm#L41` | Regra 27 | Mistério | Divergência deliberada |
| REQ-007 | Indesejado | `SOCPROG.ddm#L31` | Regra 33 | Mistério | Greenfield fundamentado no DDM |
| REQ-008 | Indesejado | `SOCPROG.ddm#L57` | Regra 33 | Mistério | Greenfield fundamentado no DDM |
| REQ-009 | Indesejado | `SOCPROG.ddm#L36` | Regra 33 | Mistério | Greenfield fundamentado no DDM |
| REQ-010 | Indesejado | `CADPROG.NSP#L84-L87` | Regra 19 | Inferida | Promovida em 2026-09-10 |
| REQ-011 | Orientado a evento | `CADPROG.NSP#L158-L165` | Regra 20 | Inferida | Promovida em 2026-09-10 |
| REQ-012 | Indesejado | `CADPROG.NSP#L168-L170` | Regra 31 | Inferida | Promovida em 2026-09-10 |
| REQ-013 | Indesejado | `CADPROG.NSP#L181-L186` | Regra 32 | Inferida | Promovida em 2026-09-10 |
| REQ-014 | Orientado a evento | `CCAUDIT.NSC#L60-L98` | Regra 36 | **Confirmada** | Sem alteração |
| REQ-015 | Orientado a evento | `CADPROG.NSP#L140-L147` | Regra 29 | **Confirmada** | Sem alteração |
| REQ-016 | Indesejado | `CCAUDIT.NSC#L45-L48` | Regras 30 e 46 | Inferida | Promovida em 2026-09-10 |
| REQ-017 | Indesejado | `CADPROG.NSP#L181-L186` | Regra 32 | Inferida | Divergência deliberada |

Nenhum requisito desta especificação usa `[GREENFIELD]`: os três requisitos de validação de domínio (REQ-007 a REQ-009) apontam para as declarações de domínio do DDM, que são evidência legada legítima.

---

## 5. Questões em aberto preservadas

Copiadas de [`mysteries-found.md`](../../01-archaeology/mysteries-found.md) **sem alteração de status**. As decisões de 2026-09-10 escolheram um comportamento-alvo para a fatia; **não responderam** às questões.

| Questão em aberto | Evidência (`path:line`) | Impacto nesta fatia | Hipótese (não confirmada) | Pessoa/área responsável | Status |
|---|---|---|---|---|---|
| Se o `MOVE TRUE TO #FOUND` está no corpo do laço `FIND`, depois do `END-NOREC` e sem `ESCAPE`, como os cerca de 45 programas ativos do arquivo 151 foram incluídos por esta tela? | `CADPROG.NSP:111` · `:113` · `:115` · `:118` · `SOCPROG.ddm:13` | A verificação de duplicidade ficou fora do escopo; a unicidade passa a ser garantida pela chave primária, sem mensagem de negócio dedicada. Se a resposta indicar que a inclusão nunca funcionou, REQ-001 perde a âncora de equivalência com o legado. | Não confirmada: o `MOVE FALSE` foi escrito para o bloco `NO RECORDS FOUND` e o `MOVE TRUE` ficou fora dele; ou os programas ativos foram carregados por utilitário fora da biblioteca. | Marcos Antônio Ribeiro (`CADPROG.NSP:4`) | aberta |
| O fator de ajuste é aplicado duas vezes, por duas fórmulas incompatíveis, uma na inclusão e outra no pagamento. É composição deliberada ou duplicação? De onde vem a constante `0,347215`? | `CADPROG.NSP:124` · `BATCHPGT.NSP:432` · `CALCBENF.NSN:262` · `BUSINESS-RULES-2012.md:132` | REQ-005 escolheu não aplicar o fator na inclusão. A resposta determina se a migração de dados históricos precisa reverter o ajuste já embutido no arquivo 151. | Não confirmada: o `FACTOR-K` de 2008 foi acrescentado sem que o cálculo de folha fosse revisado. | SENARC + Adilson Batista (`SOCPROG.ddm:9`) | aberta |
| Campo persistido `FACTOR-K`, protegido por aviso formal da SENARC no DDM, sem nenhum escritor e sem nenhum leitor. Está morto ou é alimentado por fora da biblioteca? | `SOCPROG.ddm:47` · `CADPROG.NSP:124-138` | Mantém o campo fora do modelo do alvo. Se houver gravador externo, o modelo fica incompleto. | Não confirmada: o campo foi criado para um cálculo que nunca chegou a ser implementado. | SENARC + DBA Adabas — Roberto Carlos Ferreira (`SOCPROG.ddm:5`) | aberta |
| Onde vive a lista de códigos de elegibilidade válidos, se o cabeçalho de 2012 registra "novos códigos de elegibilidade" e não há lista em lugar nenhum? | `CADPROG.NSP:8` · `SOCPROG.ddm:65` | O código de elegibilidade é aceito sem validação de domínio. É a única validação de entrada que a fatia não consegue especificar. | Não confirmada: a lista vive em tabela externa, em procedimento manual ou apenas na cabeça da área. | Fernanda Oliveira (`SOCPROG.ddm:10`) | aberta |
| Por onde as faixas de cálculo do grupo periódico são carregadas, já que a única tela de manutenção não as grava? | `SOCPROG.ddm:69-77` · `CADPROG.NSP:15-27` | Um programa incluído nasce sem faixas de cálculo, no legado e no alvo. Torna a RN-018 inaplicável a programas novos. | Não confirmada: carga por utilitário Adabas fora da biblioteca Natural. | SENARC + DBA Adabas | aberta |
| Existe programa social com valor base acima de 99.999,99? O truncamento silencioso já ocorreu em produção? | `CADPROG.NSP:49` · `:62` · `:130` · `SOCPROG.ddm:41` · `TECHNICAL-MANUAL-SIFAP-2008.md:487` | REQ-006 recusa esses valores. Se já houver dados truncados no arquivo 151, a migração precisa identificá-los. | Não confirmada: nenhum programa social real chegou perto do limite. | SUPDE/DESIF + DBA Adabas | aberta |
| O controle de acesso por perfil ADMIN, afirmado no manual de 2008, é externo, feito pelo monitor de transação, ou nunca existiu? | `TECHNICAL-MANUAL-SIFAP-2008.md:287` · `CADPROG.NSP` (programa inteiro) | Nenhum requisito de autorização foi especificado. A fatia entrega uma API sem controle de acesso de negócio e, por consequência, grava a constante `SIFAPSYS` como usuário de todo evento de auditoria (decisão 16). | Não confirmada: o controle era feito pelo monitor CICS ou pela camada de menu, fora do programa. | SUPDE/DESIF | aberta |
| Como sete módulos, três deles telas online, convivem com uma sequência de auditoria obtida por maior valor mais um, lida uma única vez por execução e sem trava? | `CCAUDIT.NSC:65` · `:66` · `:71` · `AUDIT.ddm:31` · `CADPROG.NSP:184` | REQ-014 delega a geração ao banco. A resposta indica se houve colisão histórica a tratar na migração. | Não confirmada: o volume de escrita online seria baixo o bastante para a colisão nunca ter sido observada. | DBA Adabas — Roberto Carlos Ferreira (`AUDIT.ddm:5`) | aberta |
| O registro de valor anterior e valor novo exigido pela RN-010 e pela seção 6.2 do projeto de 1997 nunca foi implementado, ou existe outro gravador da trilha? | `AUDIT.ddm:61` · `:66` · `:68` · `CCAUDIT.NSC:60` · `:98` · `ORIGINAL-ARCHITECTURE-1997.md:373` · `BUSINESS-RULES-2012.md:101` | Fora do escopo: a fatia só tem inclusão, que não tem estado anterior. Volta a ser bloqueante quando houver alteração. | Não confirmada: os grupos `GRP-BEFORE` e `GRP-AFTER` foram criados em 2005 para o `LOGAUDIT` projetado e nunca conectados ao copycode. | SUPDE/DESIF + Adilson Batista (`AUDIT.ddm:8`) | aberta |

---

## 6. Decisões de validação humana

Registradas na rodada de 2026-09-10. Detalhamento e justificativas em [`scope-decisions.md`](../../02-modern-spec/scope-decisions.md).

| # | Decisão | Efeito nesta especificação |
|---|---|---|
| 1 | A primeira fatia é o Catálogo de Programas Sociais, não a Trilha de Auditoria | Substitui a especificação `001-audit-trail`, que foi descartada |
| 2 | Regras 19, 20, 25, 26, 31 e 32 promovidas de Inferida a Confirmada | REQ-003, REQ-004, REQ-010, REQ-011, REQ-012 e REQ-013 |
| 3 | Regras 30 e 46 promovidas de Inferida a Confirmada | REQ-016 |
| 4 | O mecanismo de auditoria entra nesta fatia como requisito de apoio | REQ-014 |
| 5 | A verificação de código duplicado fica fora do escopo; a unicidade é garantida pela chave primária | Nenhum requisito; Regra 22 permanece adiada |
| 6 | O fator K não é aplicado na inclusão | REQ-005 |
| 7 | Valor base acima do limite legado é recusado, não truncado | REQ-006 |
| 8 | As validações que o DDM sustenta são especificadas | REQ-007, REQ-008 e REQ-009 |
| 9 | O código do programa é alfanumérico de quatro posições | REQ-002 |
| 10 | O identificador do evento de auditoria é gerado pelo banco de dados | Cenário 3 de REQ-014 |
| 11 | Valores anterior e novo não são implementados nesta fatia | Fora do escopo |
| 12 | A tabela de auditoria tem apenas as colunas efetivamente gravadas pelo legado | REQ-014 |
| 13 | O evento de auditoria é registrado em transação própria | REQ-017 |
| 14 | A proibição de auditar consultas é imposta no componente de auditoria | REQ-016 |
| 15 | O frontend cobre inclusão e consulta | REQ-001 e REQ-011 |
| 16 | O usuário responsável do evento de auditoria é a constante `SIFAPSYS`, em caráter provisório | REQ-014 — ver o aviso no requisito |

---

## 7. Definição de pronto

- [x] `spec.md` contém somente requisitos desta funcionalidade.
- [x] Cada requisito tem formulação EARS, critério verificável e `source_legacy:` válido.
- [x] Cada `source_legacy:` foi aberto e conferido linha a linha antes da escrita.
- [x] Cada promoção de regra Inferida a Confirmada tem validação humana registrada na seção 6.
- [x] Questões em aberto permanecem fora dos requisitos e sem mudança de status.
- [x] A matriz relaciona cada REQ-ID à regra e à sua classificação de origem.
- [ ] Revisão por par registrada em PR.

---

## Continue lendo

- [`plan.md`](plan.md) — plano de implementação da fatia
- [`tasks.md`](tasks.md) — decomposição em tarefas
- [`scope-decisions.md`](../../02-modern-spec/scope-decisions.md) — escopo, adiamentos e decisões
- [`bounded-contexts.md`](../../02-modern-spec/bounded-contexts.md) — contextos delimitados e ordem do Strangler Fig
- [`business-rules-catalog.md`](../../01-archaeology/business-rules-catalog.md) — catálogo de regras do Estágio 1
