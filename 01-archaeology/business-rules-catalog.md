# Catálogo de Regras de Negócio — SIFAP Legado

> **Trilha:** [Kit do Time](../README.md) › [Estágio 1](README.md) › **Catálogo de Regras de Negócio**

**Artefato preenchido pelo time durante o Estágio 1.** Cada dupla extrai as regras dos programas `.NSP` e `.NSN` que recebeu e as registra aqui, com rastreabilidade obrigatória até o programa de origem.

| Campo | Valor |
|---|---|
| **Público-alvo** | Todas as duplas — cada dupla preenche a seção dos seus programas |
| **Pré-requisitos** | Ler os programas `.NSP` e `.NSN` atribuídos |
| **Estágio** | Estágio 1 — Arqueologia |
| **Resultado esperado** | Catálogo com `Programa de origem` preenchido para cada regra candidata |

> [!NOTE]
> Cada regra cita o programa de origem com um intervalo de linhas (`arquivo.NSP:Linicio-Lfim` ou `arquivo.NSN:Linicio-Lfim`) e é classificada como **Confirmada** (corroborada pela documentação histórica em `legacy-sifap/legacy-docs/`), **Inferida** (só a partir do código) ou **Mistério** (questão em aberto — registre-a também em [`mysteries-found.md`](mysteries-found.md) com evidência `path:line`, hipótese não confirmada, responsável e status).

> [!IMPORTANT]
> Guia passo a passo: [`GUIDE.md`](GUIDE.md).

> [!NOTE]
> **Promoções feitas no Estágio 2, em 2026-09-10.** As classificações abaixo registram o resultado da leitura do Estágio 1 e **não foram reescritas**. Em validação humana explícita durante o Estágio 2, oito regras foram promovidas de Inferida a Confirmada e convertidas em requisito: **R19, R20, R25, R26, R30, R31, R32 e R46**. Os mistérios associados a R21, R23, R24, R27, R28 e R33 **continuam abertos** — a especificação escolheu um comportamento-alvo, o que não é o mesmo que descobrir o comportamento legado. Registro completo em [`scope-decisions.md`](../02-modern-spec/scope-decisions.md).

**Time**: Equipe Nico

---

## Regras de `CALCDSCT.NSP`

**Lido em:** 2026-09-10 · **Caminho:** `01-archaeology/legacy-sifap/natural-programs/CALCDSCT.NSP` (215 linhas)
**Documentação cruzada:** `legacy-docs/BUSINESS-RULES-2012.md` seção 3 (RN-021, RN-022, RN-023)

> [!WARNING]
> **A documentação de 2012 diverge do código em cinco pontos.** O `README.md` de `legacy-docs/` (linha 54) já avisa: os módulos de cálculo **não têm documentação formal** e as regras existem exclusivamente no código-fonte. Onde houver divergência, o código é a fonte de verdade e a divergência vira mistério — não regra.

| # | Enunciado da regra | Candidato EARS | Origem | Classificação | Notas |
|---|---|---|---|---|---|
| 1 | Quando o pagamento informado não existir, ou existir com CPF diferente do informado, o sistema deverá exibir `PAYMENT NOT FOUND` e encerrar sem calcular. | Indesejada | `CALCDSCT.NSP:L79-L90` | Inferida | O `FIND` busca só por `NUM-PAYMENT`; o CPF é conferido depois, dentro do laço. Pagamento existente com CPF divergente produz a mesma mensagem de inexistente. <!-- mystery: a mensagem única esconde dois casos distintos, inexistência e divergência de titularidade. Foi intencional? --> |
| 2 | Quando o beneficiário do CPF informado não existir, o sistema deverá exibir `BENEFICIARY NOT FOUND` e encerrar sem calcular. | Indesejada | `CALCDSCT.NSP:L93-L99` | Inferida | A view lê `STAT-BENEFICIARY` e `UF`, mas **nenhum dos dois é usado** em qualquer decisão. <!-- mystery: por que carregar o status do beneficiário e nunca testá-lo? Benefício suspenso ou cessado sofre desconto igualmente? --> |
| 3 | O sistema deverá calcular a contribuição social obrigatória por faixa progressiva sobre o valor bruto: até 500,00 = 3%; até 1.000,00 = 5%; até 2.000,00 = 7%; até 9.999,99 = 9%. | Ubíqua | `CALCDSCT.NSP:L62-L69`, `L104`, `L197-L205` | Inferida | Tabela **fixa no código**. O `README.md` de `natural-programs/` descreve `LDASIFAP.NSL` como detentor de "faixas de renda, alíquotas". <!-- mystery: a tabela está duplicada entre LDASIFAP e CALCDSCT? Qual das duas vale em produção? --> |
| 4 | Quando o valor bruto exceder 9.999,99, o sistema não deverá aplicar contribuição social alguma. | Indesejada | `CALCDSCT.NSP:L198-L204` | Mistério | O `FOR` percorre 4 faixas e faz `ESCAPE BOTTOM` ao achar a primeira que comporta o valor. Bruto acima do teto da última faixa **não casa com nenhuma** e a contribuição fica zero, silenciosamente. <!-- mystery: comportamento intencional ou faixa superior faltando? Existe benefício acima de 9.999,99? --> |
| 5 | O sistema deverá limitar o total de descontos a 30% do valor bruto. | Orientada a estado | `CALCDSCT.NSP:L107-L110`, `L170-L174` | Mistério | **Contradiz a RN-021.** A doc diz que descontos acima do limite são *rejeitados*, o benefício é processado *sem descontos* e é *gerada auditoria*. O código **trunca** o total para o teto (`MOVE #AMT-MAX-DISC TO #AMT-TOTAL-DISC`), mantém os descontos e **não grava auditoria**. <!-- mystery: o comportamento documentado, rejeitar e auditar, nunca existiu ou foi removido? --> |
| 6 | Onde o desconto tiver data de fim preenchida e anterior à data corrente, ou data de início posterior à data corrente, o sistema deverá ignorá-lo. | Orientada a estado | `CALCDSCT.NSP:L117-L123` | Inferida | `DT-END-DISC = 0` significa vigência aberta. Comparação direta de `N8` com `*DATN`. |
| 7 | Quando o desconto for do tipo `J` (judicial) e tiver valor fixo maior que zero, o sistema deverá usar esse valor; caso contrário, deverá aplicar o percentual sobre o bruto. | Orientada a evento | `CALCDSCT.NSP:L128-L137` | Inferida | O mesmo padrão valor-fixo-ou-percentual se repete em `P` e `A`. |
| 8 | O sistema não deverá aplicar o teto de 30% a descontos do tipo `J` (judicial). | Orientada a estado | `CALCDSCT.NSP:L170-L174` | **Confirmada** | Confirma a nota interna da RN-021, que registrava a exceção judicial como **não confirmada** por falta de acesso ao código (`BUSINESS-RULES-2012.md:L158-L162`). O código resolve a pendência aberta em 2012. |
| 9 | Quando o desconto for do tipo `P` (pensão alimentícia), o sistema deverá usar o valor fixo se maior que zero; caso contrário, o percentual sobre o bruto. | Orientada a evento | `CALCDSCT.NSP:L138-L146` | Inferida | Sujeito ao teto de 30%, ao contrário de `J`. |
| 10 | Quando o desconto for do tipo `I` (imposto retido na fonte), o sistema deverá aplicar exclusivamente o percentual sobre o bruto. | Orientada a evento | `CALCDSCT.NSP:L147-L151` | Inferida | Único tipo que **ignora** `AMT-DISC` mesmo se preenchido. |
| 11 | Quando o desconto for do tipo `S` (sindical), o sistema deverá aplicar 1% fixo sobre o bruto. | Orientada a evento | `CALCDSCT.NSP:L152-L155` | Inferida | Alíquota literal no código; o `PCT-DISC` do registro é **ignorado**. <!-- mystery: por que o percentual cadastrado é descartado só neste tipo? --> |
| 12 | Quando o desconto for do tipo `A` (administrativo), o sistema deverá usar o valor fixo se maior que zero; caso contrário, o percentual sobre o bruto. | Orientada a evento | `CALCDSCT.NSP:L156-L164` | Inferida | — |
| 13 | Quando o tipo de desconto não for reconhecido, o sistema deverá ignorá-lo silenciosamente. | Indesejada | `CALCDSCT.NSP:L165-L166` | Mistério | O `DEFINE DATA` documenta `C=CONTRIB` (`L20-L21`), mas **não existe `VALUE 'C'`** no `DECIDE`. Um desconto cadastrado como `C` cai em `NONE / IGNORE` e desaparece sem registro. <!-- mystery: o tipo C foi substituído pela sub-rotina de contribuição e o comentário do DDM ficou obsoleto, ou é perda silenciosa de desconto? --> |
| 14 | — (comportamento emergente, não declarável como regra) | — | `CALCDSCT.NSP:L114-L176` | Mistério | O teto é aplicado **dentro do laço**, sobre o acumulado, após cada item. Como `J` escapa da verificação mas **soma no mesmo acumulador**, um item não judicial processado depois de um judicial pode truncar o total e **corroer o desconto judicial** que deveria ser isento. O resultado depende da **ordem física dos elementos no grupo PE**. <!-- mystery: o resultado do cálculo depende da ordem de gravação no grupo periódico. A área de negócio conhece isso? --> |
| 15 | O sistema deverá truncar, não arredondar, os valores monetários para duas casas decimais. | Ubíqua | `CALCDSCT.NSP:L109-L110`, `L180-L181` | Inferida | Idioma `valor * 100` para inteiro `N11` e depois `/ 100`. Trunca para baixo. <!-- mystery: truncar em vez de arredondar favorece sistematicamente o pagador. Há norma que defina a direção do arredondamento? --> |
| 16 | Quando o cálculo concluir, o sistema deverá gravar o total apurado em `AMT-DISC-TOTAL` e confirmar a transação. | Orientada a evento | `CALCDSCT.NSP:L184-L188` | Inferida | Terceiro `FIND` sobre o mesmo pagamento; nenhum dos três reaproveita o anterior. |
| 17 | Se ocorrer erro Natural, então o sistema deverá desfazer a transação e encerrar com código 12. | Indesejada | `CALCDSCT.NSP:L209-L215` | Inferida | O `BACKOUT` só alcança trabalho posterior ao `END TRANSACTION` da linha 187. Erro após o commit não reverte a gravação. |
| 18 | — (ausência de comportamento) | — | `CALCDSCT.NSP` (programa inteiro) | Mistério | O programa **nunca** faz `INCLUDE CCAUDIT` nem grava trilha de auditoria, embora o copycode exista na biblioteca e a RN-021 exija auditoria no estouro do teto. <!-- mystery: o cálculo de desconto é a única operação financeira sem trilha de auditoria? --> |

### Candidatas EARS redigidas — `CALCDSCT.NSP`

Apenas para regras **Confirmadas** e **Inferidas**. Nenhuma regra classificada como Mistério recebe candidata EARS — mistério não vira requisito antes de validação humana.

- **R8 (Confirmada)** — `ONDE o desconto for de natureza judicial, o sistema DEVE aplicá-lo integralmente, sem sujeitá-lo ao limite de 30% do valor bruto.`
- **R3** — `O sistema DEVE calcular a contribuição social obrigatória aplicando a alíquota da primeira faixa de valor bruto que comportar o valor bruto do pagamento.`
- **R5** — `ENQUANTO o total de descontos não judiciais exceder 30% do valor bruto, o sistema DEVE limitá-lo a 30% do valor bruto.`
- **R6** — `ONDE a data corrente estiver fora da vigência do desconto, o sistema DEVE desconsiderá-lo no cálculo.`
- **R10** — `QUANDO o desconto for de imposto retido na fonte, o sistema DEVE calculá-lo como percentual do valor bruto.`
- **R15** — `O sistema DEVE truncar os valores monetários em duas casas decimais.`
- **R16** — `QUANDO o cálculo de descontos concluir, o sistema DEVE persistir o total apurado no pagamento.`
- **R1** — `SE o pagamento informado não for localizado, ENTÃO o sistema DEVE recusar o cálculo e informar a ocorrência.`
- **R2** — `SE o beneficiário informado não for localizado, ENTÃO o sistema DEVE recusar o cálculo e informar a ocorrência.`
- **R17** — `SE ocorrer erro não tratado durante o cálculo, ENTÃO o sistema DEVE desfazer as alterações não confirmadas e encerrar sinalizando falha.`

### Divergências código × documentação — `CALCDSCT.NSP`

| Doc | O que a documentação afirma | O que o código faz | Evidência |
|---|---|---|---|
| RN-021 | Descontos acima de 30% são **rejeitados**; benefício processado **sem descontos**; **gera auditoria**. | **Trunca** o total no teto, mantém os descontos e **não gera auditoria**. | `BUSINESS-RULES-2012.md:L156` × `CALCDSCT.NSP:L170-L174` |
| RN-021 (nota) | Exceção judicial ao teto **não confirmada no código**. | A exceção **existe e está implementada**. | `BUSINESS-RULES-2012.md:L158-L162` × `CALCDSCT.NSP:L170` |
| RN-022 | Tipos de desconto são **numéricos** (`01`…`05`), lista incompleta. | Tipos são **alfabéticos** (`C`, `I`, `J`, `S`, `P`, `A`); nenhum código numérico aparece. | `BUSINESS-RULES-2012.md:L164-L172` × `CALCDSCT.NSP:L20-L21` |
| RN-023 | Ordem de aplicação segue **prioridade numérica**; ao atingir o teto, os de menor prioridade são descartados. | Ordem é a **posição física no grupo PE**; nenhum campo de prioridade existe; nada é descartado, o total é truncado. | `BUSINESS-RULES-2012.md:L174` × `CALCDSCT.NSP:L114-L176` |
| Seção 3 | Módulo **implementado em 2015**. | Cabeçalho do fonte: `DATE: 25/08/1999`, com alterações em 2007, 2015 e 2016. | `BUSINESS-RULES-2012.md:L154` × `CALCDSCT.NSP:L5-L7` |

> [!IMPORTANT]
> As cinco linhas classificadas como **Mistério** e as cinco divergências acima **não estão registradas** em [`mysteries-found.md`](mysteries-found.md). Quem leu o código deve atribuir o ID canônico (`SIFAP-M-NN`) conforme [`mysteries-checklist.md`](mysteries-checklist.md) e registrar com `/catalog-mysteries`. O agente não infere nem atribui ID.

> [!NOTE]
> Duplique a seção acima para cada programa `.NSP` ou `.NSN` lido pela sua dupla.

---

## Regras de `CADPROG.NSP`

**Lido em:** 2026-09-10 · **Caminho:** `01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP` (187 linhas)
**Documentação cruzada:** `legacy-docs/TECHNICAL-MANUAL-SIFAP-2008.md` seção 3.2.3 · `legacy-docs/BUSINESS-RULES-2012.md` RN-003, RN-013, RN-017, RN-018 · `adabas-ddms/SOCPROG.ddm`
**Contexto alvo:** Catálogo de Programas Sociais

> [!WARNING]
> O cabeçalho do programa declara manter a tabela no **arquivo 155** (`CADPROG.NSP:10`), mas a `VIEW OF SOCPROG` resolve para o **arquivo 151** (`SOCPROG.ddm:22`). Mesmo padrão do achado bônus sobre `BATCHPGT.NSP:17`. O número citado no comentário não é evidência de nada.

| # | Enunciado da regra | Candidato EARS | Origem | Classificação | Notas |
|---|---|---|---|---|---|
| 19 | Se a operação informada não for `I` nem `C`, então o sistema deverá exibir `INVALID OPERATION` e encerrar. | Indesejada | `CADPROG.NSP:L84-L87` | Inferida | Só duas operações existem. O manual de 2008 afirma que a tela faz "inclusão **e alteração**" (`TECHNICAL-MANUAL-SIFAP-2008.md:279`); não há caminho de alteração no código. |
| 20 | Quando a operação for `C`, o sistema deverá exibir código, nome, tipo, valor base, elegibilidade e situação do programa, e encerrar. | Orientada a evento | `CADPROG.NSP:L89-L92`, `L156-L171` | Inferida | `C` é consulta, não *change*. A tela rotula apenas `(I/C)`, sem legenda. |
| 21 | Antes de incluir, o sistema deverá verificar se já existe programa com o mesmo código. | Orientada a evento | `CADPROG.NSP:L110-L116` | **Mistério** | `MOVE FALSE TO #FOUND` está dentro do bloco `NO RECORDS FOUND`; `MOVE TRUE TO #FOUND` está no corpo do laço `FIND`, **depois** do `END-NOREC` e sem `ESCAPE`. Em Natural, o corpo do laço é percorrido uma vez também quando nada é encontrado. <!-- mystery: se essa semântica valer na versão 4.2.6 em produção, #FOUND é sempre verdadeiro e nenhum programa social pode ser incluído por esta tela. Como os cerca de 45 programas ativos citados em SOCPROG.ddm:13 foram cadastrados? --> |
| 22 | Se já existir programa com o código informado, então o sistema deverá exibir `PROGRAM ALREADY REGISTERED` e encerrar sem gravar. | Indesejada | `CADPROG.NSP:L118-L121` | Inferida | Depende de R21. |
| 23 | O sistema deverá gravar o valor base ajustado por um fator `K`, calculado como `1,00 + (fator de ajuste × 0,347215)`. | Ubíqua | `CADPROG.NSP:L124-L125`, `L130` | **Mistério** | A constante `0,347215` não aparece em nenhum outro membro nem em nenhum documento. O que se grava em `AMT-BASE-INDIVIDUAL` **já vem ajustado**; depois `BATCHPGT.NSP:432` e `CALCBENF.NSN:262` aplicam `× (1 + FACTOR-ADJUST)` **de novo**, com fórmula diferente. <!-- mystery: o fator de ajuste é aplicado duas vezes, por duas fórmulas incompatíveis, uma na inclusão e outra no pagamento. Isso é composição deliberada ou duplicação? A nota de BUSINESS-RULES-2012.md:132 atribui o FACTOR-K ao CALCBENF, onde ele não existe. --> |
| 24 | — (ausência de comportamento) | — | `CADPROG.NSP:L124-L138` × `SOCPROG.ddm:L47-L51` | **Mistério** | O DDM tem o campo persistido `BG FACTOR-K (P5,4)`, marcado `>>> UNDOCUMENTED <<<`, com aviso de não alterar sem autorização da SENARC. **`CADPROG` nunca grava esse campo** — usa uma variável local homônima e a descarta. Nenhum programa da biblioteca lê `FACTOR-K`. <!-- mystery: campo protegido por aviso formal no DDM, sem nenhum escritor e sem nenhum leitor. Está morto ou é alimentado por fora da biblioteca? --> |
| 25 | Ao incluir, o sistema deverá gravar a situação do programa como ativa. | Ubíqua | `CADPROG.NSP:L134` | Inferida | `'A'` fixo. O DDM admite `I=INACTIVE` e `E=ENDED` (`SOCPROG.ddm:38`), e não há caminho de alteração — **a situação nunca muda por esta tela**. |
| 26 | O sistema deverá tratar data de encerramento igual a zero como vigência indeterminada. | Ubíqua | `CADPROG.NSP:L101`, `L133` | Inferida | Confirmado pelo DDM: `AH DT-CLOSURE ... (0=ACTIVE)` (`SOCPROG.ddm:37`). |
| 27 | — (perda de precisão) | — | `CADPROG.NSP:L49`, `L62`, `L130` × `SOCPROG.ddm:L42` | **Mistério** | `#AMT-BASE` e `#AMT-CALC` são `P9.2` (até 9.999.999,99); `AMT-BASE-INDIVIDUAL` é `P7.2` (até 99.999,99). O `MOVE` trunca os dígitos de alta ordem **em silêncio**. O manual de 2008 registra o erro `U4038 — Erro de overflow no cálculo` (`TECHNICAL-MANUAL-SIFAP-2008.md:487`). <!-- mystery: existe programa social com valor base acima de 99.999,99? O truncamento já ocorreu em produção? --> |
| 28 | O sistema deverá converter o código do programa de numérico para alfanumérico de 4 posições com zeros à esquerda. | Ubíqua | `CADPROG.NSP:L110`, `L157` | **Mistério** | `#COD-PROG` é `N4`, mas `COD-PROGRAM` é `A4` e é a chave primária (`SOCPROG.ddm:29`). Um código com letra é gravável no Adabas e **inalcançável por esta tela**. <!-- mystery: existem códigos de programa não numéricos no arquivo 151? Se existem, quem os criou? --> |
| 29 | Quando um programa social for incluído, o sistema deverá registrar na trilha de auditoria a ação `IN`, a entidade `PROG` e o código do programa. | Orientada a evento | `CADPROG.NSP:L140-L147` | **Confirmada** | Atende a RN-010 (`BUSINESS-RULES-2012.md:101`) e a IN-TCU 63/2010 (`ORIGINAL-ARCHITECTURE-1997.md:373`). Valores válidos conforme `AUDIT.ddm:39` (`IN`) e `AUDIT.ddm:55` (`PROG`). O CPF afetado é zerado — programa social não tem titular. |
| 30 | A consulta de programa não deverá gerar registro de auditoria. | Ubíqua | `CADPROG.NSP:L156-L171` | Inferida | `QUERY-PROG` não chama `WRITE-AUDIT`. Coerente com a Portaria CGTI 213/2010 citada em `CCAUDIT.NSC:44-48`, que veda registrar a ação `CO` por volume. |
| 31 | Se a consulta não localizar o programa, então o sistema deverá exibir `PROGRAM NOT FOUND`. | Indesejada | `CADPROG.NSP:L168-L170` | Inferida | Usa `*NUMBER(PROGRAM-V) = 0` **após** o `END-FIND`, em vez de `NO RECORDS FOUND`. O próprio código marca o estilo como não padronizado e cita o ticket 3312/2004 em aberto (`CADPROG.NSP:166-167`). |
| 32 | Se ocorrer erro Natural, então o sistema deverá desfazer a transação e encerrar com código 12. | Indesejada | `CADPROG.NSP:L181-L186` | Inferida | Como `CCAUDIT` não emite `END TRANSACTION` (`CCAUDIT.NSC:57-58`), o `BACKOUT` **também apaga o registro de auditoria** da operação que falhou. As ações `ER` e `RE` previstas no DDM (`AUDIT.ddm:46`, `:48`) nunca são gravadas. |
| 33 | — (ausência de validação) | — | `CADPROG.NSP:L95-L108` | **Mistério** | Nenhum bloco condicional valida `#TYPE` contra `A/P/T`, `#COD-ELIG` contra a lista de códigos, `#AGE-MIN ≤ #AGE-MAX`, nem a consistência das datas. O cabeçalho registra `18/11/2012 - FERNANDA COSTA - NEW ELIGIBILITY CODES` (`CADPROG.NSP:8`) e o DDM remete ao ticket 4471/2012 (`SOCPROG.ddm:69`), mas **não existe lista de códigos no código**. <!-- mystery: onde vive a lista de códigos de elegibilidade válidos? --> |
| 34 | — (ausência de controle de acesso) | — | `CADPROG.NSP` (programa inteiro) | **Mistério** | O manual de 2008 afirma "Acesso restrito ao perfil ADMIN (verificação pela GDA da sessão)" (`TECHNICAL-MANUAL-SIFAP-2008.md:287`). Não há nenhuma verificação de perfil no programa. <!-- mystery: o controle é externo, feito pelo monitor de transação, ou nunca existiu? --> |
| 35 | — (ausência de parametrização) | — | `CADPROG.NSP:L15-L27` × `SOCPROG.ddm:L73-L85` | **Mistério** | A `VIEW` cobre 12 dos cerca de 30 campos do DDM. O grupo periódico `GRP-CALC-BAND (1:5)` e o campo múltiplo `TYPE-DISC-APPLIC (1:8)` **nunca são gravados**. Um programa incluído por esta tela nasce **sem faixas de cálculo**, o que torna a RN-018 inaplicável a ele. <!-- mystery: por onde as faixas de cálculo são carregadas, já que a única tela de manutenção não as grava? --> |

### Candidatas EARS redigidas — `CADPROG.NSP`

- **R29 (Confirmada)** — `QUANDO um programa social for incluído, o sistema DEVE registrar um evento de auditoria contendo a ação, a entidade, o identificador do programa, o usuário e o instante do evento.`
- **R19** — `SE a operação solicitada não for uma das operações suportadas, ENTÃO o sistema DEVE recusá-la e informar a ocorrência.`
- **R20** — `QUANDO a consulta de um programa social for solicitada, o sistema DEVE apresentar código, nome, tipo, valor base, código de elegibilidade e situação.`
- **R22** — `SE já existir programa social com o código informado, ENTÃO o sistema DEVE recusar a inclusão e informar a ocorrência.`
- **R25** — `QUANDO um programa social for incluído, o sistema DEVE registrá-lo com situação ativa.`
- **R26** — `ONDE a data de encerramento não estiver definida, o sistema DEVE considerar a vigência do programa como indeterminada.`
- **R30** — `O sistema NÃO DEVE registrar evento de auditoria para operações de consulta.`
- **R31** — `SE o programa social consultado não for localizado, ENTÃO o sistema DEVE informar a ocorrência.`
- **R32** — `SE ocorrer erro não tratado durante a inclusão, ENTÃO o sistema DEVE desfazer as alterações não confirmadas e encerrar sinalizando falha.`

---

## Regras de `CCAUDIT.NSC`

**Lido em:** 2026-09-10 · **Caminho:** `01-archaeology/legacy-sifap/natural-programs/CCAUDIT.NSC` (100 linhas)
**Documentação cruzada:** `legacy-docs/ORIGINAL-ARCHITECTURE-1997.md` seção 6.2 · `legacy-docs/BUSINESS-RULES-2012.md` RN-010 · `adabas-ddms/AUDIT.ddm`
**Contexto alvo:** Trilha de Auditoria

> [!NOTE]
> `CCAUDIT` é um **copycode**: `INCLUDE` expande o texto em tempo de compilação, não é chamada em tempo de execução. Sete módulos o expandem (`dependency-map.md`). Cada um carrega **sua própria cópia** da sub-rotina `WRITE-AUDIT` e sua própria semente de sequência. Isso é central para R37.

| # | Enunciado da regra | Candidato EARS | Origem | Classificação | Notas |
|---|---|---|---|---|---|
| 36 | Toda operação que altera dados deverá gravar um registro na trilha de auditoria, contendo número sequencial, data, hora, marca temporal, ação, módulo, descrição, tipo e identificador da entidade, CPF afetado e usuário. | Ubíqua | `CCAUDIT.NSC:L60-L98` | **Confirmada** | Exigência legal IN-TCU 63/2010, declarada em `CCAUDIT.NSC:10` e corroborada por `ORIGINAL-ARCHITECTURE-1997.md:373` e pela RN-010 (`BUSINESS-RULES-2012.md:101`). O DDM declara o registro **imutável**, proibido de reorganização, com retenção mínima de 10 anos (`AUDIT.ddm:14-17`). |
| 37 | O sistema deverá obter o número de auditoria lendo o maior valor existente uma única vez por execução e incrementando-o em memória a cada gravação. | Orientada a estado | `CCAUDIT.NSC:L64-L71` | **Mistério** | `NUM-AUDIT` é descritor **único** (`AUDIT.ddm:30`). A semente é lida sem bloqueio. Duas sessões online concorrentes obtêm o mesmo valor, a segunda gravação viola a unicidade e cai no `ON ERROR` do módulo chamador, que faz `BACKOUT` e `TERMINATE 12`. Mesmo padrão `max+1` já registrado como achado bônus em `BATCHPGT.NSP:240`. <!-- mystery: como sete módulos, incluindo três telas online, convivem com uma sequência sem bloqueio no mesmo arquivo? --> |
| 38 | O sistema deverá registrar a hora do evento com precisão de segundos, descartando os décimos. | Ubíqua | `CCAUDIT.NSC:L75-L77` | Inferida | `*TIMN` devolve `N7` no formato `HHMMSST`; `DIVIDE 10` trunca o décimo para caber no `N6` do DDM. O próprio código documenta a perda (`CCAUDIT.NSC:73-74`). Dois eventos no mesmo segundo ficam indistinguíveis pela hora. |
| 39 | O sistema deverá compor a marca temporal do evento como data multiplicada por um milhão somada à hora. | Ubíqua | `CCAUDIT.NSC:L78-L80` | Inferida | Produz `AAAAMMDDHHMMSS` em `N14`. O DDM anota o campo como "(PRECISION)" (`AUDIT.ddm:34`), mas ele deriva de uma hora já truncada — não acrescenta precisão alguma. |
| 40 | O sistema deverá registrar como módulo do evento o nome do programa em execução. | Ubíqua | `CCAUDIT.NSC:L85` | Inferida | `*PROGRAM` devolve o módulo **que expandiu o copycode**, não `CCAUDIT`. É o que permite rastrear a origem do evento. |
| 41 | Onde a ação for `BT`, o sistema deverá registrar o nome do job batch e a situação do lote. | Orientada a estado | `CCAUDIT.NSC:L92-L96` | Inferida | `*INIT-USER` como nome do job. Bloco acrescentado em 10/07/2015 (`CCAUDIT.NSC:7`). |
| 42 | O sistema deverá registrar a situação do lote sempre como sucesso. | Ubíqua | `CCAUDIT.NSC:L95` | **Mistério** | `'S'` fixo. O DDM admite `E=ERROR` e `W=WARNING` (`AUDIT.ddm:87`), e os campos `NUM-CYCLE-BATCH`, `NUM-SEQ-BATCH` e `DESCR-ERR-BATCH` (`AUDIT.ddm:84`, `:85`, `:88`) nunca são gravados. <!-- mystery: um lote que falhou grava auditoria dizendo que teve sucesso. A área de fiscalização sabe disso? --> |
| 43 | — (ausência de conteúdo) | — | `CCAUDIT.NSC:L60-L98` × `AUDIT.ddm:L61-L69` | **Mistério** | O DDM tem os grupos `GRP-BEFORE` e `GRP-AFTER`, com campos múltiplos `(1:20)` para nome do campo e valores anterior e novo, mais os escalares `AMT-PREV` e `AMT-NEW` de 2014. **Nenhum é gravado.** A trilha registra quem, quando e qual ação, **nunca o que mudou**. <!-- mystery: a RN-010 e a seção 6.2 do projeto de 1997 exigem valor anterior e valor novo. O requisito nunca foi implementado ou existe outro gravador? --> |
| 44 | — (ausência de conteúdo) | — | `CCAUDIT.NSC:L51-L52` × `AUDIT.ddm:L73` | **Mistério** | `COD-PROFILE` (`ADM/OPR/CON/AUD/SUP`) não é preenchido. O próprio copycode declara a lacuna e cita o ticket 7742 em aberto. Também ficam vazios `NAME-USER`, `COD-ASSIGNMENT`, `IP-ORIGIN`, `ID-SESSION`, `COD-TERMINAL`, `COD-LU` e `NUM-TRANSACTION`. <!-- mystery: 13 de cerca de 35 campos do DDM são preenchidos. Quem preenche o resto? --> |
| 45 | A rotina não deverá confirmar a transação; o controle transacional pertence ao módulo chamador. | Ubíqua | `CCAUDIT.NSC:L57-L58`, `L98` | Inferida | Consequência: um `BACKOUT` no `ON ERROR` do chamador apaga o registro de auditoria junto com a operação que falhou. Ver R32. |
| 46 | A rotina não deverá bloquear a ação de consulta; o bloqueio é responsabilidade do chamador. | Ubíqua | `CCAUDIT.NSC:L44-L48` | Inferida | Portaria CGTI 213/2010 veda registrar `CO` por volume desde 2010. A regra é declarada no comentário e **não é imposta em lugar nenhum** — depende de cada um dos sete módulos lembrar dela. |

### Candidatas EARS redigidas — `CCAUDIT.NSC`

- **R36 (Confirmada)** — `QUANDO uma operação alterar dados do sistema, o sistema DEVE registrar um evento de auditoria imutável contendo identificador sequencial, data e hora do evento, ação, módulo de origem, descrição, tipo e identificador da entidade afetada e usuário responsável.`
- **R38** — `O sistema DEVE registrar a hora do evento de auditoria com precisão de segundos.`
- **R39** — `O sistema DEVE registrar uma marca temporal do evento no formato ano, mês, dia, hora, minuto e segundo.`
- **R40** — `O sistema DEVE registrar, em cada evento de auditoria, o módulo que originou a operação.`
- **R41** — `ONDE a operação for executada em processamento batch, o sistema DEVE registrar o identificador do job e a situação do lote.`
- **R45** — `O sistema DEVE gravar o evento de auditoria na mesma transação da operação que o originou.`
- **R46** — `O sistema NÃO DEVE registrar eventos de auditoria para operações de consulta.`

### Divergências código × documentação

| Doc | O que a documentação afirma | O que o código faz | Evidência |
|---|---|---|---|
| Manual 2008 §3.2.3 | `CADPROG` faz "inclusão **e alteração**" de programas sociais. | Só inclusão e consulta. Não existe caminho de alteração. | `TECHNICAL-MANUAL-SIFAP-2008.md:279` × `CADPROG.NSP:L84-L92` |
| Manual 2008 §3.2.3 | `CADPROG` faz "parametrização de faixas de valores (campos **MU** no DDM SOCPROG)". | Não grava faixa alguma. E as faixas são **PE**, não MU; o MU do DDM guarda tipos de desconto. | `TECHNICAL-MANUAL-SIFAP-2008.md:281` × `SOCPROG.ddm:L73-L85` |
| Manual 2008 §3.2.3 | Acesso restrito ao perfil ADMIN, verificado pela GDA da sessão. | Nenhuma verificação de perfil no programa. | `TECHNICAL-MANUAL-SIFAP-2008.md:287` × `CADPROG.NSP` |
| RN-017 | Até **10** faixas, indexadas por **exercício fiscal**. | O PE admite **5** faixas e é indexado por **faixa de renda**; não há campo de exercício. | `BUSINESS-RULES-2012.md:140` × `SOCPROG.ddm:L73-L79` |
| RN-003 | Programa ativo é indicado pelo campo `PS-IN-ATIVO = 'S'`. | O campo não existe. A situação está em `STAT-PROGRAM`, com domínio `A/I/E`. | `BUSINESS-RULES-2012.md:76` × `SOCPROG.ddm:38` |
| RN-013 (nota) | O multiplicador `FACTOR-K` está no código do `CALCBENF` e ninguém soube explicá-lo. | `FACTOR-K` não existe em `CALCBENF`. A fórmula está em `CADPROG`, aplicada na **inclusão**. | `BUSINESS-RULES-2012.md:132` × `CADPROG.NSP:L124` |
| DDM SOCPROG | `FACTOR-K` é campo persistido, protegido por aviso formal da SENARC. | Nenhum programa da biblioteca grava ou lê esse campo. | `SOCPROG.ddm:L47-L51` × biblioteca inteira |
| Projeto 1997 §6.2 · RN-010 | A auditoria registra "campo alterado, valor anterior e valor novo". | Os grupos `GRP-BEFORE` e `GRP-AFTER` nunca são gravados. | `ORIGINAL-ARCHITECTURE-1997.md:373` · `BUSINESS-RULES-2012.md:101` × `CCAUDIT.NSC:L60-L98` |
| RN-010 | A auditoria é gerada pelo **subprograma `LOGAUDIT`**. | `LOGAUDIT` não existe na biblioteca. O mecanismo é o copycode `CCAUDIT`, expandido em 7 módulos. | `BUSINESS-RULES-2012.md:101` × `natural-programs/` |
| DDM AUDIT | `STAT-BATCH` admite `S=SUCCESS`, `E=ERROR`, `W=WARNING`. | Só `'S'` é gravado, incondicionalmente. | `AUDIT.ddm:87` × `CCAUDIT.NSC:L95` |

> [!IMPORTANT]
> As 12 linhas classificadas como **Mistério** e as 10 divergências desta seção estão registradas em [`mysteries-found.md`](mysteries-found.md) como achados `BONUS`. O placar canônico da dupla continua fechado em 4 de 4; nenhum ID `SIFAP-M-NN` foi atribuído a elas.

---

## Resumo geral

| Métrica | Valor |
|---|---:|
| Membros Natural lidos | 3 — `CALCDSCT.NSP`, `CADPROG.NSP`, `CCAUDIT.NSC` |
| DDMs cruzados | 2 de 4 — `SOCPROG` (151) e `AUDIT` (153) |
| Regras catalogadas | 45 |
| Regras confirmadas | 3 |
| Regras inferidas | 26 |
| Mistérios | 17 |
| Divergências código × documentação | 15 |

### Cobertura por bounded context

Ver [`02-modern-spec/bounded-contexts.md`](../02-modern-spec/bounded-contexts.md).

| Contexto | Membros lidos | Pronto para EARS? |
|---|---|---|
| Catálogo de Programas Sociais | `CADPROG.NSP` | Parcial — 9 candidatas redigidas; faixas de cálculo sem escritor conhecido |
| Trilha de Auditoria | `CCAUDIT.NSC` | Parcial — 7 candidatas redigidas; conteúdo do evento incompleto no legado |
| Cadastro de Beneficiários | nenhum | Não |
| Cálculo e Folha de Benefícios | `CALCDSCT.NSP` | **Não** — `SIFAP-M-06` foi fechado em 2026-09-10 em favor do `CALCBENF`, mas o conflito de alíquotas de contribuição segue aberto e continua bloqueando |
| Conciliação Bancária | nenhum | Não |

---

## Definição de pronto

- [x] Todo bloco condicional dos programas lidos foi examinado. **3 membros de 24; a cobertura da biblioteca continua parcial e deliberada.**
- [x] Toda regra cita `arquivo:linha`.
- [x] Toda questão em aberto está registrada em `mysteries-found.md` sem conclusão. **Os 12 mistérios de `CADPROG` e `CCAUDIT` entraram como `BONUS`.**

---

### Continue lendo

| Anterior | Próximo |
|---|---|
| [Inventário](inventory.md)<br/><sub>Passo 1 — varredura de arquivos.</sub> | [Mapa de Dependências](dependency-map.md)<br/><sub>Passo 3 — grafo de chamadas e acessos.</sub> |

<sub>[Voltar ao índice do kit](../README.md)</sub>
