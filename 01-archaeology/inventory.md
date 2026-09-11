# Inventário do Legado — Time `<preencher>`

> **Trilha:** [Kit do Time](../README.md) › [Estágio 1](README.md) › **Inventário**

**Primeiro artefato do Estágio 1.** Varra a estrutura e conte os arquivos sem abrir nenhum programa — use apenas os nomes de arquivo e a estrutura de pastas.

| Campo | Valor |
|---|---|
| **Público-alvo** | Dupla responsável pela varredura inicial |
| **Pré-requisitos** | Acesso ao diretório `legacy-sifap/` |
| **Estágio** | Estágio 1 — Arqueologia, Passo 1 |
| **Resultado esperado** | Contagens corretas, padrões de nomenclatura identificados e 3 itens estranhos sinalizados |

> [!NOTE]
> Monte este inventário sem abrir nenhum programa. Trabalhe apenas com nomes de arquivo e estrutura de pastas. Ele será revisado à medida que o time extrai regras, mapeia dependências e registra mistérios.

**Data:** 2026-09-10
**Dupla responsável:** <!-- preencher: nomes da dupla — Equipe Nico -->
**Caminho varrido:** `01-archaeology/legacy-sifap/`

> [!IMPORTANT]
> **Primeira passada, baseada apenas em nomes e estrutura.** Nenhum programa foi aberto para produzir este inventário. Toda coluna de "hipótese" é uma suposição a confirmar nos passos seguintes (`/extract-business-rules`, `/map-dependencies`). Revise este arquivo à medida que as leituras avançarem.

---

## Estrutura de pastas

**4 diretórios** (1 raiz + 3 subdiretórios), **40 arquivos** no total.

```text
01-archaeology/legacy-sifap/
├── HOW-TO-READ-NATURAL.md
├── README.md
├── adabas-ddms/            (6 arquivos)
│   ├── AUDIT.ddm
│   ├── BENEFIC.ddm
│   ├── FDT-150-BENEFICIARY.txt
│   ├── PAYMENT.ddm
│   ├── README.md
│   └── SOCPROG.ddm
├── legacy-docs/            (7 arquivos)
│   ├── BUSINESS-RULES-2012.docx
│   ├── BUSINESS-RULES-2012.md
│   ├── ORIGINAL-ARCHITECTURE-1997.docx
│   ├── ORIGINAL-ARCHITECTURE-1997.md
│   ├── README.md
│   ├── TECHNICAL-MANUAL-SIFAP-2008.docx
│   └── TECHNICAL-MANUAL-SIFAP-2008.md
└── natural-programs/       (25 arquivos, biblioteca plana)
    ├── BATCHCON.NSP    ├── BATCHPGT.NSP    ├── BATCHREL.NSP
    ├── CADBENEF.NSP    ├── CADDEPEN.NSP    ├── CADPROG.NSP
    ├── CALCBENF.NSN    ├── CALCCORR.NSP    ├── CALCDSCT.NSP
    ├── CCAUDIT.NSC     ├── CCVALCPF.NSC    ├── CONSBENF.NSP
    ├── LDASIFAP.NSL    ├── PDACALC.NSA     ├── PDAVALID.NSA
    ├── README.md       ├── RELAUDIT.NSP    ├── RELPGT.NSP
    ├── SIFAPJ01.jcl    ├── SIFAPJ02.jcl    ├── SUBVALCP.NSN
    ├── SUBVALNI.NSN    ├── VALBENEF.NSN    ├── VALDOCS.NSP
    └── VALELEG.NSN
```

> [!NOTE]
> `natural-programs/` é **plana** de propósito: uma biblioteca Natural resolve `CALLNAT`, `INCLUDE` e `USING` **pelo nome do membro**, nunca por caminho. Não espere hierarquia de pacotes aqui.

---

## Contagem de arquivos por tipo

| Extensão | Contagem | Finalidade provável (conhecimento geral de Natural/Adabas) |
|---|---|---|
| `.NSP` | 12 | Programa Natural — unidade executável, ponto de entrada online ou batch |
| `.NSN` | 5 | Subprograma Natural — invocado por `CALLNAT`, com `PARAMETER` próprio |
| `.NSC` | 2 | Copycode — fragmento inserido em tempo de compilação por `INCLUDE` |
| `.NSA` | 2 | PDA (Parameter Data Area) — contrato de parâmetros compartilhado, via `USING` |
| `.NSL` | 1 | LDA (Local Data Area) — estrutura de variáveis locais compartilhada, via `USING` |
| `.jcl` | 2 | JCL z/OS — job de produção que dispara execução batch fora do Natural |
| `.ddm` | 4 | DDM (Data Definition Module) — visão Natural de um arquivo Adabas |
| `.txt` | 1 | Listagem bruta — aqui, aparentemente uma FDT (Field Definition Table) |
| `.md` | 8 | Documentação (2 na raiz, 1 por subpasta + 3 em `legacy-docs/`) |
| `.docx` | 3 | Documentação histórica em formato binário |
| **Total** | **40** | |

**Verificação independente** (outra pessoa da dupla deve reproduzir):

```bash
cd 01-archaeology/legacy-sifap
find . -type d | wc -l                                    # 4
find . -type f | wc -l                                    # 40
find . -type f | sed 's/.*\.//' | sort | uniq -c | sort -rn
```

---

## Padrões da convenção de nomes

Agrupamento pelos primeiros 2–4 caracteres do nome do membro, **sem abrir arquivo algum**. Todos os membros respeitam o limite clássico de 8 caracteres do Natural.

| Prefixo | Contagem | Membros | Hipótese de domínio |
|---|---|---|---|
| `CAD` | 3 | `CADBENEF`, `CADDEPEN`, `CADPROG` | Hipótese: "Cadastro" (pt-BR) — telas/manutenção de dados mestres. **A confirmar.** |
| `BATCH` | 3 | `BATCHCON`, `BATCHPGT`, `BATCHREL` | Ponto de entrada batch — prefixo explícito, sem `INPUT` interativo esperado. **A confirmar.** |
| `CALC` | 3 | `CALCBENF`, `CALCCORR`, `CALCDSCT` | Hipótese: rotinas de cálculo financeiro. **A confirmar.** |
| `VAL` | 3 | `VALBENEF`, `VALDOCS`, `VALELEG` | Hipótese: "Validação" (pt-BR) — regras de aceitação de dados. **A confirmar.** |
| `REL` | 2 | `RELAUDIT`, `RELPGT` | Hipótese: "Relatório" (pt-BR) — saída impressa/listagem. **A confirmar.** |
| `CC` | 2 | `CCAUDIT`, `CCVALCPF` | Convenção Natural: **C**opy**C**ode. Casa 100% com a extensão `.NSC`. Alta confiança. |
| `PDA` | 2 | `PDACALC`, `PDAVALID` | Convenção Natural: **P**arameter **D**ata **A**rea. Casa com `.NSA`. Alta confiança. |
| `SUB` | 2 | `SUBVALCP`, `SUBVALNI` | Convenção Natural: **Sub**programa, alvo de `CALLNAT`. Casa com `.NSN`. Alta confiança. |
| `SIFAPJ` | 2 | `SIFAPJ01`, `SIFAPJ02` | Nome do sistema + `J` de Job + sequência numérica. Casa com `.jcl`. Alta confiança. |
| `LDA` | 1 | `LDASIFAP` | Convenção Natural: **L**ocal **D**ata **A**rea. Casa com `.NSL`. Alta confiança. |
| `CONS` | 1 | `CONSBENF` | Hipótese: "Consulta" (pt-BR) — leitura sem alteração. Prefixo único, **a confirmar**. |

**Observação estrutural:** os prefixos se dividem em dois grupos distintos.
`CC`, `PDA`, `LDA`, `SUB`, `SIFAPJ` são **prefixos técnicos** — descrevem o *tipo* do membro e batem com a extensão.
`CAD`, `BATCH`, `CALC`, `VAL`, `REL`, `CONS` são **prefixos de verbo de negócio** — descrevem o *que o programa faz*. Os quatro últimos caracteres (`BENEF`, `PGT`, `DOCS`, `ELEG`, `PROG`, `DEPEN`, `CORR`, `DSCT`, `AUDIT`, `NI`, `CP`) parecem ser o **substantivo/entidade** sobre o qual o verbo age. Verbo + entidade em 8 caracteres é a hipótese de leitura de nomes deste sistema — valide-a no primeiro programa que você abrir.

---

## Itens estranhos (top 3)

| # | Caminho do arquivo | O que o torna estranho | Investigação sugerida |
|---|---|---|---|
| 1 | [`FDT-150-BENEFICIARY.txt`](legacy-sifap/adabas-ddms/FDT-150-BENEFICIARY.txt) | Único `.txt` de todo o legado; único nome com **hífens**, com **número infixo** (`150`) e em **inglês**, enquanto os quatro vizinhos `.ddm` usam nomes curtos e sem separador. O `150` tem cara de FNR (número de arquivo Adabas), mas apenas **uma** das quatro entidades ganhou uma listagem de FDT. | Abra-o **antes** dos `.ddm` e confirme se `150` é mesmo o FNR. Depois pergunte: por que só esta entidade tem FDT publicada? Existem descritores (`MU`, `PE`, `SU`) que o `.ddm` correspondente não mostra? Se a FDT e o DDM divergirem, isso vira mistério. |
| 2 | [`SIFAPJ01.jcl`](legacy-sifap/natural-programs/SIFAPJ01.jcl) e [`SIFAPJ02.jcl`](legacy-sifap/natural-programs/SIFAPJ02.jcl) | Únicos artefatos **não-Natural** dentro de uma biblioteca Natural, e únicos com sufixo numérico sequencial. JCL é z/OS, vive fora do Natural — está aqui por conveniência de empacotamento, não por pertencer à biblioteca. | São a **única** fonte de verdade sobre o agendamento de produção: quem roda, em que ordem, com quais arquivos alocados e qual o procedimento de reinício. Leia os comentários de cabeçalho e o passo `CMSYNIN` para descobrir qual programa cada job executa. Trate como a "porta de entrada" do sistema. |
| 3 | [`CCVALCPF.NSC`](legacy-sifap/natural-programs/CCVALCPF.NSC) vs. [`SUBVALCP.NSN`](legacy-sifap/natural-programs/SUBVALCP.NSN) | **Dois membros com semântica de nome sobreposta para a mesma preocupação** (validação de CPF), em mecanismos diferentes: copycode (`INCLUDE`, colado em tempo de compilação) e subprograma (`CALLNAT`, ligação em runtime). Coexistência típica de duas gerações de código. | Faça `grep -n "INCLUDE CCVALCPF\|CALLNAT 'SUBVALCP'" *.NSP *.NSN` e conte os chamadores de cada um. Se ambos tiverem chamadores vivos, verifique se implementam **a mesma** regra de módulo 11 — divergência entre os dois é um mistério de alto impacto para `mysteries-found.md`. |

**Menção honrosa (não entra no top 3, mas vigie):** `legacy-docs/` mantém **três pares `.docx` + `.md` de mesmo nome** (`BUSINESS-RULES-2012`, `ORIGINAL-ARCHITECTURE-1997`, `TECHNICAL-MANUAL-SIFAP-2008`). Conteúdo duplicado em dois formatos tem risco conhecido de divergência, e os anos (1997, 2008, 2012) sugerem três camadas históricas escritas por autores diferentes. Documentação é **pista**, nunca prova: só o código legado é fonte de verdade para requisitos com `source_legacy:`.

---

## Ordem de leitura proposta

> [!WARNING]
> Esta ordem é **hipótese derivada de nomes e estrutura**. Ela vai mudar quando a dupla rodar `/map-dependencies` e descobrir as arestas reais de `CALLNAT`, `INCLUDE` e `USING`. Revise-a depois do mapa de dependências.

**Passo 0 — Ferramental (30 min, antes de tudo):**
[`HOW-TO-READ-NATURAL.md`](legacy-sifap/HOW-TO-READ-NATURAL.md). Sem isso, `DEFINE DATA`, formato `P` (decimal compactado) e `AT BREAK` viram ruído.

**1. Dados antes de lógica — `adabas-ddms/` inteiro.**
`FDT-150-BENEFICIARY.txt` primeiro (item estranho nº 1, revela tipos e descritores crus), depois `BENEFIC.ddm`, `PAYMENT.ddm`, `SOCPROG.ddm`, `AUDIT.ddm`.
*Justificativa:* 4 DDMs para ~15 programas — o modelo de dados é o menor conjunto que explica o maior número de programas. Nomes de campo aparecem em todo `READ`/`FIND` que você ler depois.

**2. Pontos de entrada de produção — os dois `.jcl`, depois os três `BATCH*`.**
`SIFAPJ01.jcl` → `SIFAPJ02.jcl` → `BATCHPGT.NSP`, `BATCHREL.NSP`, `BATCHCON.NSP`.
*Justificativa:* o JCL é o topo real da cadeia de chamadas (nada acima dele neste repositório), e o prefixo `BATCH` marca os programas que rodam sem tela. Começar pelo topo dá a ordem de execução mensal de graça.

**3. Infraestrutura compartilhada — sob demanda, não em bloco.**
`LDASIFAP.NSL`, `PDACALC.NSA`, `PDAVALID.NSA`, `CCVALCPF.NSC`, `CCAUDIT.NSC`, `SUBVALCP.NSN`, `SUBVALNI.NSN`.
*Justificativa:* são os **candidatos a mais conectados** — um `.NSL`/`.NSA` só existe para ser referenciado por `USING`, e um `.NSC` só para ser `INCLUDE`. Abra cada um **no momento** em que um `USING`, `INCLUDE` ou `CALLNAT` do *seu* programa citar o nome. Ler em bloco, fora de contexto, não fixa.

**4. Os três membros atribuídos à sua dupla.**
Consulte a tabela de distribuição em [`natural-programs/README.md`](legacy-sifap/natural-programs/README.md). Sua carga é **3 programas**, nem mais nem menos — os 9 membros de apoio do passo 3 não contam.

**5. Fronteiras entre duplas.**
Quando um `CALLNAT` do seu programa apontar para um membro atribuído a outra dupla, pare e coordene com ela antes de fechar o mapa. É exatamente nessa costura que o desenho do sistema aparece.

---

## Definição de pronto

- [x] O inventário existe com contagens corretas (40 arquivos, 4 diretórios — verificável com `find`).
- [x] 3 padrões de nomenclatura ou mais identificados (11 prefixos, sendo 9 com 2+ membros).
- [x] 3 itens estranhos sinalizados, com caminho, motivo e ação de investigação.
- [x] Ordem de leitura proposta e justificada por padrão de nome ou posição estrutural.
- [ ] Revisão pós-`/map-dependencies` — reordenar o passo 3 com base nas arestas reais.

---

## Próximo passo

Este inventário é o **Passo 1** de 5 do Estágio 1. Não abra programas fora da ordem proposta; use os prompts na sequência:

| Prompt | O que produz |
|---|---|
| `/extract-business-rules` | [`business-rules-catalog.md`](business-rules-catalog.md) — regras com evidência `arquivo#Lxx-Lyy` |
| `/map-dependencies` | [`dependency-map.md`](dependency-map.md) — arestas `CALLNAT` / `INCLUDE` / `USING` / `JCL executa` |
| `/catalog-mysteries` | [`mysteries-found.md`](mysteries-found.md) — os 4 mistérios canônicos `SIFAP-M-NN` |
| `/discovery-report` | [`discovery-report.md`](discovery-report.md) — transição para o Estágio 2 |

---

### Continue lendo

| Anterior | Próximo |
|---|---|
| [GUIDE do Estágio 1](GUIDE.md)<br/><sub>Cronograma passo a passo.</sub> | [Catálogo de Regras](business-rules-catalog.md)<br/><sub>Passo 2 — extração de regras.</sub> |

<sub>[Voltar ao índice do kit](../README.md)</sub>
