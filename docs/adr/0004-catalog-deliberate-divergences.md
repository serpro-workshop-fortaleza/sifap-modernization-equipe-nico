<!-- markdownlint-disable MD024 -->

# ADR-0004: Divergências deliberadas do Catálogo de Programas Sociais

> **Trilha:** [Kit do Time](../../README.md) › [Documentação](../README.md) › [ADRs](README.md) › **ADR-0004**

| Campo | Valor |
|---|---|
| **Status** | accepted |
| **Data** | 2026-09-10 |
| **Autores** | Arquiteto de Software + Especialista em Requisitos — Equipe Nico |
| **Substitui** | N/A |

---

## Contexto

A fatia 001 corta o Catálogo de Programas Sociais a partir de `01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP`, a única tela de manutenção do arquivo 151. A leitura completa do programa expôs quatro comportamentos que não podem ser reproduzidos sem propagar defeito para o sistema novo:

1. **Fator K aplicado duas vezes.** `CADPROG.NSP:L124-L125` grava `valor × (1,00 + fator × 0,347215)` em `AMT-BASE-INDIVIDUAL`. Depois `BATCHPGT.NSP:432` e `CALCBENF.NSN:262` aplicam `× (1 + fator)` **de novo, por fórmula diferente**. A constante `0,347215` não aparece em nenhum outro membro nem em nenhum documento do corpus. O campo persistido `FACTOR-K` do DDM (`SOCPROG.ddm:L47`), protegido por aviso formal da SENARC, **nunca é gravado por ninguém**.
2. **Truncamento silencioso do valor.** As variáveis de tela são `P9.2` (`CADPROG.NSP:L49`, `:L62`), o campo persistido é `P7.2` (`SOCPROG.ddm:L41`). O `MOVE` de `CADPROG.NSP:L130` descarta dígitos de alta ordem sem sinalizar. O manual de 2008 registra o erro `U4038 — overflow no cálculo`.
3. **Chave inalcançável pela tela.** `COD-PROGRAM` é `A4` e é a chave primária (`SOCPROG.ddm:L28`), mas a tela lê `N4` e converte com zeros à esquerda (`CADPROG.NSP:L110`, `:L157`). Um código com letra é gravável no Adabas e inalcançável pela única tela de manutenção.
4. **Ausência total de validação de entrada.** Nenhum bloco valida o tipo contra o domínio `A/P/T`, a coerência das idades, a coerência das datas nem o código de elegibilidade. O DDM declara os domínios de tipo (`SOCPROG.ddm:L31-L32`) e de situação (`:L37`); a lista de códigos de elegibilidade **não existe em lugar nenhum** (`SOCPROG.ddm:L65` remete a um ticket).

As instruções do repositório exigem validar entradas em todos os limites do sistema. Reproduzir o legado significaria entregar uma API sem validação nenhuma.

---

## Decisão

Divergiremos deliberadamente do legado em quatro pontos, todos rastreados na especificação:

**1. O fator de correção não é aplicado na inclusão.** Persistiremos o valor base exatamente como informado e o fator de ajuste em campo próprio. O cálculo do valor efetivo passa a ser responsabilidade exclusiva do contexto de Cálculo e Folha, que já aplica o seu próprio ajuste. O campo `FACTOR-K` do DDM não entra no modelo do alvo.

**2. Valor base acima do limite legado é recusado, não truncado.** O limite de 99.999,99 é preservado para manter a coexistência com o arquivo 151 durante o Strangler Fig, mas a violação passa a ser um erro de validação explícito na borda da API.

**3. O código do programa é alfanumérico de quatro posições.** Adotaremos a chave real do DDM, não a restrição da tela. Entrada numérica é normalizada com zeros à esquerda, preservando o comportamento observável do legado para os códigos que ele consegue produzir.

**4. As validações que o DDM sustenta são implementadas.** Tipo de programa contra o domínio declarado, coerência da faixa etária respeitando `0=NONE`, e coerência das datas de vigência. O código de elegibilidade **não** é validado contra domínio, porque não existe domínio conhecido: a lacuna permanece como questão aberta.

Não implementaremos verificação explícita de código duplicado. A unicidade é garantida pela chave primária da tabela. A regra legada correspondente está classificada como mistério: `MOVE TRUE TO #FOUND` está no corpo do laço `FIND`, depois do `END-NOREC` e sem `ESCAPE` (`CADPROG.NSP:L113-L115`), o que levanta a hipótese de que a tela nunca conseguiu incluir programa nenhum. Especificar uma mensagem de negócio para um caminho cujo comportamento legado é desconhecido seria invenção.

---

## Alternativas consideradas

| Alternativa | Por que foi rejeitada |
|---|---|
| Reproduzir o fator K fielmente | Propagaria para o sistema novo um ajuste aplicado duas vezes por fórmulas incompatíveis, congelando uma constante sem origem documentada. |
| Persistir valor informado e valor ajustado em colunas separadas | Adia a decisão sem resolvê-la e cria uma coluna cujo significado depende de um mistério aberto. |
| Reproduzir o truncamento silencioso | Reproduz corrupção silenciosa de dado, incompatível com as instruções de validação em todos os limites do sistema. |
| Ampliar o limite do valor base | Aceitaria valores que o arquivo 151 não representa, quebrando a coexistência durante o corte. |
| Restringir o código a quatro dígitos numéricos | Fiel à tela, mas incapaz de ler códigos alfanuméricos que possam existir no arquivo 151. |
| Não validar nada, como o legado | Entregaria uma API sem validação, contrariando as instruções do repositório. |
| Especificar a verificação de duplicidade pelo comportamento pretendido | Exigiria afirmar um comportamento legado que a evidência não sustenta, enquanto a chave primária já garante a integridade. |

---

## Consequências

- **Mais fácil:** entradas inválidas passam a ser recusadas na borda, com mensagem específica; o catálogo passa a aceitar toda a faixa de chaves que o arquivo 151 admite; o valor base deixa de carregar um ajuste implícito de origem desconhecida.
- **Mais difícil:** a migração de dados históricos precisa lidar com valores já ajustados pelo fator K no arquivo 151. Carregá-los sem reverter o ajuste produz uma base com dois significados na mesma coluna.
- **Riscos:** se a área de negócio confirmar que a composição dupla do fator é deliberada, a decisão 1 muda o valor efetivo dos benefícios; a recusa por limite de valor pode rejeitar cargas históricas já truncadas.
- **Mitigações:** a migração de dados fica fora desta fatia e depende da resposta às questões abertas sobre o fator K e sobre a existência de valores truncados; até lá, a fatia opera apenas sobre programas criados no sistema novo.

---

## Relacionados

- REQ-IDs: `REQ-002`, `REQ-005`, `REQ-006`, `REQ-007`, `REQ-008`, `REQ-009` de [`spec.md`](../../specs/001-social-program-catalog/spec.md)
- ADRs: [ADR-0003](0003-audit-trail-integrity-and-persistence.md)
- Arquivos-fonte do legado: [`CADPROG.NSP`](../../01-archaeology/legacy-sifap/natural-programs/CADPROG.NSP) · [`SOCPROG.ddm`](../../01-archaeology/legacy-sifap/adabas-ddms/SOCPROG.ddm)

---

## Referências

- Regras 19 a 35 em [`business-rules-catalog.md`](../../01-archaeology/business-rules-catalog.md)
- Mistérios correspondentes em [`mysteries-found.md`](../../01-archaeology/mysteries-found.md)
- Decisões de validação humana de 2026-09-10, em [`scope-decisions.md`](../../02-modern-spec/scope-decisions.md)
