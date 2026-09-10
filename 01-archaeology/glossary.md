# Glossário do SIFAP Legado

> **Trilha:** [Kit do Time](../README.md) › [Estágio 1](README.md) › **Glossário**

**Artefato preenchido pelo time durante o Estágio 1.** Uma tabela com todos os termos, abreviações e siglas encontrados no código Natural/Adabas — a base da linguagem ubíqua para o Estágio 2.

| Campo | Valor |
|---|---|
| **Público-alvo** | Todas as duplas — cada dupla contribui com os termos dos seus programas |
| **Pré-requisitos** | Abrir os arquivos `.NSN` e `.ddm` atribuídos |
| **Estágio** | Estágio 1 — Arqueologia |
| **Resultado esperado** | 30 termos ou mais, com programa de origem e status CONFIRMADO/HIPÓTESE |

> [!NOTE]
> Guia passo a passo: [`GUIDE.md`](GUIDE.md).

---

## Por que o glossário importa

Sistemas legados têm vocabulário próprio, raramente documentado em um lugar acessível — ele vive em nomes de variável, abreviações de campo e comentários de código. Se o time do Estágio 2 não souber o que significam `DSCT`, `BENF`, `PE` ou `CTC`, vai escrever uma especificação baseada em suposições sobre esses termos.

O glossário transforma abreviações de 3 a 6 caracteres em uma linguagem ubíqua compartilhada pelo time inteiro — e dá a base para os nomes de entidades e atributos do modelo de domínio no Estágio 3.

**Erro comum:** marcar um termo como CONFIRMADO sem evidência literal no código ou na documentação histórica. Se você inferiu o significado pelo contexto, marque como HIPÓTESE e identifique quem é responsável pela validação.

---

## Como preencher

| Coluna | O que registrar |
|---|---|
| **Termo** | A abreviação ou sigla exatamente como aparece no código. |
| **Expansão** | O significado completo do termo. |
| **Programa** | O arquivo `.NSN` ou `.ddm` onde o termo foi encontrado. |
| **Contexto** | Explicação breve de como e onde o termo é usado. |
| **Status** | `CONFIRMADO` — evidência literal no código ou na documentação. `HIPÓTESE` — inferido do contexto e aguardando validação. |

### Dica de extração com o modo Ask do GitHub Copilot

Antes de usar o prompt abaixo, cole no chat o conteúdo de 2 a 3 arquivos `.NSN`:

> "Liste todas as abreviações e siglas usadas neste código Natural. Para cada uma, sugira a expansão e marque como 'CONFIRMADO' ou 'HIPÓTESE'."

Compare a sugestão do Copilot com o que você observou diretamente no código. Se coincidirem, registre como CONFIRMADO; caso contrário, registre como HIPÓTESE.

---

## Termos encontrados

**Time**: Equipe Nico · **Cobertura**: biblioteca `legacy-sifap` completa (24 membros Natural, 4 DDMs, 3 documentos históricos).

Caminhos são relativos a `legacy-sifap/`.

### Sistema, organização e normas

| # | Termo | Expansão | Programa | Contexto | Status |
|---|---|---|---|---|---|
| 1 | `SIFAP` | Sistema de Fiscalização e Administração de Pagamentos | `natural-programs/BATCHPGT.NSP:3` | Cabeçalho de todos os 24 membros e dos 4 DDMs. Em inglês nos cabeçalhos: *Payment Inspection and Administration System*. | CONFIRMADO |
| 2 | `SIAFI` | Sistema Integrado de Administração Financeira do Governo Federal | `README.md:33` · `legacy-docs/ORIGINAL-ARCHITECTURE-1997.md:65` | Sistema externo com o qual o SIFAP deveria conciliar. Nenhum programa lido implementa a integração. | CONFIRMADO |
| 3 | `MDAS` | Ministério do Desenvolvimento e Assistência Social | `README.md:40` | Órgão gestor dos programas de transferência de renda. | CONFIRMADO |
| 4 | `SAS/MPAS` | Secretaria de Assistência Social / Ministério da Previdência e Assistência Social | `legacy-docs/ORIGINAL-ARCHITECTURE-1997.md:45` | Demandante original do sistema em 1997. | CONFIRMADO |
| 5 | `SUPDE/DESIF` | Superintendência de Desenvolvimento / Departamento de Sistemas de Informação | `legacy-docs/ORIGINAL-ARCHITECTURE-1997.md:45` | Equipe que construiu o SIFAP. Ainda citada como responsável por decisões em aberto. | CONFIRMADO |
| 6 | `SIPAG/DOS` | Sistema de Pagamentos em DOS — antecessor em Clipper 5.2 | `legacy-docs/ORIGINAL-ARCHITECTURE-1997.md:57` | Sistema descentralizado substituído pelo SIFAP. | CONFIRMADO |
| 7 | `IN-TCU 63/2010` | Instrução Normativa do Tribunal de Contas da União nº 63/2010 | `natural-programs/CADBENEF.NSP:38` | Norma citada como origem da obrigação de trilha de auditoria no arquivo 153. | CONFIRMADO |
| 8 | `ITSM-SIFAP` | Manuais de procedimento ITSM do SIFAP (vol. 1 a 3) | `legacy-docs/TECHNICAL-MANUAL-SIFAP-2008.md:107` | Vol. 1 incidentes, vol. 2 operação, vol. 3 mudanças — este último em elaboração desde 2008. | CONFIRMADO |
| 9 | `FEBRABAN` | Federação Brasileira de Bancos | `adabas-ddms/PAYMENT.ddm:79` | Origem do código de banco de 3 posições em `COD-BANK`. | CONFIRMADO |
| 10 | `CNAB 240` | Centro Nacional de Automação Bancária — layout de 240 posições | `natural-programs/BATCHCON.NSP:11` · `:53` | Layout do arquivo de remessa e de retorno do Banco do Brasil. | CONFIRMADO |

### Convenções de nomenclatura dos membros

> [!NOTE]
> As doze expansões desta tabela foram inferidas do português abreviado — nenhum comentário do código as escreve por extenso. Foram validadas por **Pedro de Lara** em 2026-09-10 e por isso constam como CONFIRMADO, com a origem da confirmação registrada na coluna Contexto. A distinção importa: a evidência é humana, não literal.

| # | Termo | Expansão | Programa | Contexto | Status |
|---|---|---|---|---|---|
| 11 | `CAD` | Cadastro | `natural-programs/CADBENEF.NSP` · `CADDEPEN.NSP` · `CADPROG.NSP` | Prefixo dos programas online de manutenção de dados. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 12 | `CALC` | Cálculo | `natural-programs/CALCBENF.NSN` · `CALCCORR.NSP` · `CALCDSCT.NSP` | Prefixo dos programas que produzem valores monetários. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 13 | `CONS` | Consulta | `natural-programs/CONSBENF.NSP` | Prefixo dos programas de leitura sem escrita no dado de negócio. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 14 | `VAL` | Validação | `natural-programs/VALDOCS.NSP` · `VALELEG.NSN` · `VALBENEF.NSN` | Prefixo dos programas que decidem aceitar ou rejeitar um dado. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 15 | `REL` | Relatório | `natural-programs/RELPGT.NSP` · `RELAUDIT.NSP` · `BATCHREL.NSP` | Prefixo dos programas que produzem saída impressa. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 16 | `BATCH` | Processamento em lote | `natural-programs/SIFAPJ01.jcl` · `BATCHPGT.NSP` | Prefixo dos programas executados por `NATBATCH` sob Control-M. | CONFIRMADO |
| 17 | `SUB` | Subprograma reutilizável | `natural-programs/SUBVALCP.NSN` · `SUBVALNI.NSN` | Prefixo dos membros `.NSN` invocados por `CALLNAT`. | CONFIRMADO |
| 18 | `CC` | Copycode | `natural-programs/CCAUDIT.NSC` · `CCVALCPF.NSC` | Prefixo dos membros `.NSC` expandidos por `INCLUDE` em tempo de compilação. | CONFIRMADO |
| 19 | `BENF` | Benefício ou beneficiário | `natural-programs/CALCBENF.NSN` · `VALBENEF.NSN` · `CONSBENF.NSP` | Sufixo temático dos programas do domínio de beneficiários. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 20 | `PGT` | Pagamento | `natural-programs/BATCHPGT.NSP` · `RELPGT.NSP` | Sufixo temático do domínio de folha. Também no grupo Control-M `SIFAP-PGT`. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 21 | `DSCT` | Desconto | `natural-programs/CALCDSCT.NSP` | Nomeia o motor de descontos sobre o valor bruto do benefício. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 22 | `CON` | Conciliação | `natural-programs/BATCHCON.NSP:11` | Sufixo do programa de conciliação do retorno bancário. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 23 | `CORR` | Correção monetária | `natural-programs/CALCCORR.NSP` | Sufixo do programa que recalcula pagamentos já gravados. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 24 | `DEPEN` | Dependente | `natural-programs/CADDEPEN.NSP` | Sufixo do cadastro de dependentes do beneficiário. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 25 | `PROG` | Programa social | `adabas-ddms/SOCPROG.ddm` · `natural-programs/CADPROG.NSP` | O DDM `SOCPROG` (*social program*) confirma a leitura. | CONFIRMADO |
| 26 | `ELEG` | Elegibilidade | `natural-programs/VALELEG.NSN` | Sufixo do subprograma que decide se o beneficiário entra na folha. Validado por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 27 | `CP` | CPF | `natural-programs/SUBVALCP.NSN` · `CCVALCPF.NSC` | Abreviação do documento validado pelo subprograma. | CONFIRMADO |
| 28 | `NI` | NIS | `natural-programs/SUBVALNI.NSN:7` | Abreviação do documento validado pelo subprograma. | CONFIRMADO |

### Prefixos de campo nos DDMs

| # | Termo | Expansão | Programa | Contexto | Status |
|---|---|---|---|---|---|
| 29 | `AMT` | *Amount* — valor monetário | `adabas-ddms/PAYMENT.ddm` | `AMT-GROSS`, `AMT-DISC-TOTAL`, `AMT-NET`, `AMT-RECONCILED`. Sempre decimal empacotado. | CONFIRMADO |
| 30 | `QTY` | *Quantity* — quantidade ou contador | `natural-programs/BATCHPGT.NSP` | `#QTY-GENERATED`, `#QTY-IGNORED`, `#QTY-RECONCILED`. Totalizadores de execução. | CONFIRMADO |
| 31 | `STAT` | *Status* — situação | `adabas-ddms/PAYMENT.ddm:60` | `STAT-PAYMENT`, `STAT-RECONCIL`. Códigos de uma letra. | CONFIRMADO |
| 32 | `COD` | *Code* — código | `adabas-ddms/PAYMENT.ddm:79` | `COD-BANK`, `COD-BANK-RETURN`, `COD-REGION`. | CONFIRMADO |
| 33 | `NUM` | *Number* — número identificador | `adabas-ddms/PAYMENT.ddm` | `NUM-PAYMENT`, `NUM-CYCLE`, `NUM-NIS`. | CONFIRMADO |
| 34 | `DT` | *Date* — data | `adabas-ddms/PAYMENT.ddm` | `DT-CREDIT`, `DT-RECONCIL`, `DT-EVENT`. Formato `AAAAMMDD` numérico. | CONFIRMADO |
| 35 | `DESCR` | *Description* — descrição textual | `adabas-ddms/PAYMENT.ddm:99` | `DESCR-BANK-RETURN`, campo alfanumérico livre. | CONFIRMADO |
| 36 | `TYPE` | Tipo — classificador de domínio fechado | `adabas-ddms/PAYMENT.ddm:51` | `TYPE-DISC`, `TYPE-PAYMENT`. | CONFIRMADO |
| 37 | `PCT` | Percentual | `adabas-ddms/PAYMENT.ddm:53` | `PCT-DISC`, decimal empacotado `P 3,2`. | CONFIRMADO |
| 38 | `REF` | Referência — competência do pagamento | `adabas-ddms/PAYMENT.ddm` | `YEAR-REF`, `PERIOD-REF`. | CONFIRMADO |
| 39 | `RECONCIL` | *Reconciliation* — conciliação bancária | `adabas-ddms/PAYMENT.ddm:94` | Grupo `BANK RECONCILIATION`: `DT-RECONCIL`, `STAT-RECONCIL`, `AMT-RECONCILED`. Nenhum programa da biblioteca grava esses campos. | CONFIRMADO |

### Conceitos de negócio

| # | Termo | Expansão | Programa | Contexto | Status |
|---|---|---|---|---|---|
| 40 | `CPF` | Cadastro de Pessoas Físicas | `natural-programs/SUBVALCP.NSN` · `adabas-ddms/BENEFIC.ddm` | Chave de negócio do beneficiário, 11 posições numéricas com dígito verificador. | CONFIRMADO |
| 41 | `NIS` | Número de Identificação Social | `natural-programs/SUBVALNI.NSN:7` · `:32` | Validado apenas desde 2011 (`SUBVALNI.NSN:11`); antes disso o SIFAP não validava o campo. A sigla aparece na família `NIS/PIS/PASEP`; a expansão em português não está escrita no código e foi validada por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 42 | `PIS/PASEP` | Programa de Integração Social / Programa de Formação do Patrimônio do Servidor Público | `natural-programs/SUBVALNI.NSN:7` | Mesma rotina de dígito verificador do NIS. Expansão validada por Pedro de Lara em 2026-09-10. | CONFIRMADO |
| 43 | `UF` | Unidade da Federação | `natural-programs/LDASIFAP.NSL:69` | `#L-VALID-UF` lista as 27 UFs; `#L-TAB-REGION-UF` mapeia UF para código de região. As duas listas divergem. | CONFIRMADO |
| 44 | Competência | Mês de referência do pagamento, formato `AAAAMM` | `natural-programs/SIFAPJ01.jcl` | Entra por `CMSYNIN`; a execução de referência usa `202601`. | CONFIRMADO |
| 45 | Fator regional | Multiplicador do valor do benefício por código de região | `natural-programs/BATCHPGT.NSP:192` · `LDASIFAP.NSL:35` | Tabela de 27 posições; a guarda de índice do programa aceita só 1 a 25 (ver `SIFAP-M-05`). | CONFIRMADO |
| 46 | Contribuição social | Desconto percentual sobre o valor bruto | `natural-programs/LDASIFAP.NSL:62` · `CALCDSCT.NSP:198` | Tabela progressiva de 3, 5, 7 e 9% por faixa. A folha aplica 3% fixos (`BATCHPGT.NSP:458`). | CONFIRMADO |
| 47 | Desconto judicial | Desconto por ordem judicial, isento do teto de 30% | `natural-programs/CALCDSCT.NSP:128` | Ramo `VALUE 'J'` do `DECIDE`; única regra confirmada por comentário explícito. | CONFIRMADO |
| 48 | 13º / abono | Décimo terceiro e bonificação | `natural-programs/BATCHPGT.NSP:9` | Alterado em 18/12/2009 por José Ferreira: *adjust 13th/bonus*. Reflete-se em `TYPE-PAYMENT` e `#AMT-BONUS`. | CONFIRMADO |
| 49 | Remessa | Arquivo de crédito enviado ao banco | `natural-programs/BATCHPGT.NSP:502` · `SIFAPJ01.jcl` | `CMWKF01`, LRECL 240, copiado para `SIFAP.TRANSM.REMESSA` no STEP020. | CONFIRMADO |
| 50 | Retorno bancário | Arquivo devolvido pelo banco após o crédito | `natural-programs/BATCHCON.NSP:16` · `:135` | `CMWKF01` de entrada de `BATCHCON`, layout CNAB 240 do Banco do Brasil. | CONFIRMADO |

### Vocabulário Natural e Adabas

| # | Termo | Expansão | Programa | Contexto | Status |
|---|---|---|---|---|---|
| 51 | `DDM` | *Data Definition Module* — visão lógica de um arquivo Adabas | `adabas-ddms/README.md:5` | Quatro na biblioteca: `BENEFIC`, `SOCPROG`, `PAYMENT`, `AUDIT`. | CONFIRMADO |
| 52 | `FDT` | *Field Definition Table* — definição física dos campos no Adabas | `adabas-ddms/` (listagem FDT) | Origem dos códigos de dois caracteres (`AA`, `AB`, `CB`, `GA`). | CONFIRMADO |
| 53 | `FNR` | *File Number* — número do arquivo Adabas | `adabas-ddms/PAYMENT.ddm:27` | 150 `BENEFIC`, 151 `SOCPROG`, 152 `PAYMENT`, 153 `AUDIT`. Todos no DBID 057. | CONFIRMADO |
| 54 | `DBID` | *Database Identifier* — identificador do banco Adabas | `adabas-ddms/PAYMENT.ddm:27` | 057 para todos os arquivos do SIFAP. Também é o recurso Control-M `ADABAS-057`. | CONFIRMADO |
| 55 | `ISN` | *Internal Sequence Number* — endereço interno do registro | `adabas-ddms/PAYMENT.ddm` (NOTE2) | Reuso desligado, e o DDM registra que não deve ser usado como chave externa. | CONFIRMADO |
| 56 | `PE` | *Periodic Group* — grupo repetido com ocorrências ordenadas | `adabas-ddms/PAYMENT.ddm:51` | Guarda os descontos do pagamento. A ordem física afeta o resultado do cálculo. | CONFIRMADO |
| 57 | `MU` | *Multiple Value* — campo com múltiplos valores | `adabas-ddms/BENEFIC.ddm` | Usado nos campos repetidos do cadastro. | CONFIRMADO |
| 58 | `SU` / `S1` / `S2` / `S3` | *Superdescriptor* — descritor derivado da concatenação de campos | `adabas-ddms/PAYMENT.ddm` | `S1 SUPER-CPF-PERIOD`, `S2 SUPER-PROG-PERIOD-STAT`, `S3 SUPER-CYCLE-STAT`. Equivalem a índices compostos. | CONFIRMADO |
| 59 | `PDA` | *Parameter Data Area* — contrato de parâmetros entre programas | `natural-programs/PDAVALID.NSA:3` · `PDACALC.NSA:3` | `PDAVALID` para validação, `PDACALC` para cálculo. | CONFIRMADO |
| 60 | `LDA` | *Local Data Area* — área de dados compartilhada por inclusão | `natural-programs/LDASIFAP.NSL:3` | `LDASIFAP` é usada por 13 programas. Qualquer mudança de layout obriga recompilação de todos. | CONFIRMADO |
| 61 | `CALLNAT` | Invocação de subprograma em tempo de execução | `natural-programs/BATCHPGT.NSP:381` | Nove arestas na biblioteca. | CONFIRMADO |
| 62 | `INCLUDE` | Expansão de copycode em tempo de compilação | `natural-programs/BATCHPGT.NSP:594` | Não é chamada: o texto do copycode é colado no programa. | CONFIRMADO |
| 63 | `HISTOGRAM` | Leitura só do índice, sem acessar o registro | `natural-programs/RELAUDIT.NSP:260` | Usado para contar eventos por `DT-EVENT`. | CONFIRMADO |
| 64 | `NATBATCH` | Executor batch do Natural no mainframe | `natural-programs/SIFAPJ01.jcl` | `PGM=NATBATCH PARM='...DBID=57,FNR=150'`. | CONFIRMADO |
| 65 | `CMSYNIN` / `CMPRINT` / `CMWKF01` / `CMWKF02` | DDs padrão do Natural batch: entrada de comandos, saída de impressão e arquivos de trabalho 1 e 2 | `natural-programs/SIFAPJ01.jcl` | `CMSYNIN` traz a competência; `CMWKF01` é a remessa; `CMWKF02` são os rejeitados. | CONFIRMADO |
| 66 | Control-M | Agendador de jobs do mainframe | `natural-programs/SIFAPJ01.jcl` | Tabela `SIFAP-MENSAL`, grupo `SIFAP-PGT`, primeiro dia útil às 22:00, janela de 4 horas. | CONFIRMADO |
| 67 | Janela de século | Regra de desambiguação de ano com 2 dígitos | `natural-programs/LDASIFAP.NSL` | `#L-CENTURY-WINDOW INIT <50>`: resíduo do tratamento de Y2K. | CONFIRMADO |

> [!NOTE]
> Organize por domínio (cadastro, cálculo, batch, validação) se isso ajudar a navegação. Acrescente quantas linhas forem necessárias — a meta é 30 termos ou mais.

---

## Definição de pronto

- [x] 30 termos ou mais registrados. **67 registrados.**
- [x] Todo termo tem um programa de origem.
- [x] Todo termo tem status CONFIRMADO ou HIPÓTESE. **67 confirmados, 0 hipóteses.**
- [x] As hipóteses estão marcadas para validação com um facilitador. **Encerrado em 2026-09-10:** as 14 hipóteses foram validadas por Pedro de Lara — doze convenções de nomenclatura de membros (`CAD`, `CALC`, `CONS`, `VAL`, `REL`, `BENF`, `PGT`, `DSCT`, `CON`, `CORR`, `DEPEN`, `ELEG`) e duas expansões de sigla (`NIS`, `PIS/PASEP`). Nenhuma tem evidência literal no código; a confirmação é humana e está registrada na coluna Contexto de cada linha.

---

### Continue lendo

| Anterior | Próximo |
|---|---|
| [GUIDE do Estágio 1](GUIDE.md)<br/><sub>Cronograma passo a passo.</sub> | [Relatório de Descoberta](discovery-report.md)<br/><sub>Consolidação final do estágio.</sub> |

<sub>[Voltar ao índice do kit](../README.md)</sub>
