package br.gov.sifap.catalogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * REQ-001, REQ-003 e REQ-005 — programa social do catalogo.
 * Nasce ativo e guarda o fator de ajuste sem aplica-lo ao valor base.
 */
@Entity
@Table(name = "programa_social")
public class ProgramaSocial {

    private static final int CASAS_DO_FATOR = 4;

    @EmbeddedId
    private CodigoPrograma codigo;

    @Column(name = "nome", nullable = false, length = 60)
    private String nome;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "tipo", nullable = false, length = 1)
    private TipoPrograma tipo;

    @Embedded
    private ValorBase valorBase;

    @Column(name = "fator_ajuste", nullable = false, precision = 7, scale = 4)
    private BigDecimal fatorAjuste;

    @Column(name = "codigo_elegibilidade", length = 5)
    private String codigoElegibilidade;

    @Embedded
    private Vigencia vigencia;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "situacao", nullable = false, length = 1)
    private SituacaoPrograma situacao;

    @Column(name = "renda_percapita_maxima", precision = 7, scale = 2)
    private BigDecimal rendaPercapitaMaxima;

    @Embedded
    private FaixaEtaria faixaEtaria;

    /** Exigido pelo Hibernate; nenhum codigo de aplicacao deve usar. */
    protected ProgramaSocial() {
    }

    private ProgramaSocial(CodigoPrograma codigo, String nome, TipoPrograma tipo, ValorBase valorBase,
                           BigDecimal fatorAjuste, String codigoElegibilidade, Vigencia vigencia,
                           SituacaoPrograma situacao, BigDecimal rendaPercapitaMaxima, FaixaEtaria faixaEtaria) {
        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
        this.valorBase = valorBase;
        this.fatorAjuste = fatorAjuste;
        this.codigoElegibilidade = codigoElegibilidade;
        this.vigencia = vigencia;
        this.situacao = situacao;
        this.rendaPercapitaMaxima = rendaPercapitaMaxima;
        this.faixaEtaria = faixaEtaria;
    }

    /**
     * REQ-003 — a situacao e atribuida pelo sistema, nunca aceita da solicitacao.
     */
    public static ProgramaSocial incluir(CodigoPrograma codigo, String nome, TipoPrograma tipo,
                                         ValorBase valorBase, BigDecimal fatorAjuste,
                                         String codigoElegibilidade, Vigencia vigencia,
                                         BigDecimal rendaPercapitaMaxima, FaixaEtaria faixaEtaria) {
        if (nome == null || nome.isBlank()) {
            throw new DadoInvalidoException("nome", "o nome do programa e obrigatorio");
        }
        Objects.requireNonNull(codigo, "codigo");
        Objects.requireNonNull(tipo, "tipo");
        Objects.requireNonNull(valorBase, "valorBase");
        Objects.requireNonNull(vigencia, "vigencia");
        Objects.requireNonNull(faixaEtaria, "faixaEtaria");

        BigDecimal informado = Optional.ofNullable(fatorAjuste).orElse(BigDecimal.ZERO);
        if (informado.signum() < 0) {
            throw new DadoInvalidoException("fatorAjuste", "o fator de ajuste nao pode ser negativo");
        }
        if (informado.stripTrailingZeros().scale() > CASAS_DO_FATOR) {
            throw new DadoInvalidoException("fatorAjuste",
                    "o fator de ajuste admite no maximo quatro casas decimais");
        }
        BigDecimal fator = informado.setScale(CASAS_DO_FATOR, RoundingMode.UNNECESSARY);

        return new ProgramaSocial(codigo, nome.strip(), tipo, valorBase, fator,
                codigoElegibilidade, vigencia, SituacaoPrograma.ATIVO, rendaPercapitaMaxima, faixaEtaria);
    }

    public CodigoPrograma codigo() {
        return codigo;
    }

    public String nome() {
        return nome;
    }

    public TipoPrograma tipo() {
        return tipo;
    }

    public ValorBase valorBase() {
        return valorBase;
    }

    public BigDecimal fatorAjuste() {
        return fatorAjuste;
    }

    public Optional<String> codigoElegibilidade() {
        return Optional.ofNullable(codigoElegibilidade);
    }

    public Vigencia vigencia() {
        return vigencia;
    }

    public SituacaoPrograma situacao() {
        return situacao;
    }

    public Optional<BigDecimal> rendaPercapitaMaxima() {
        return Optional.ofNullable(rendaPercapitaMaxima);
    }

    public FaixaEtaria faixaEtaria() {
        return faixaEtaria;
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof ProgramaSocial programa && Objects.equals(codigo, programa.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
