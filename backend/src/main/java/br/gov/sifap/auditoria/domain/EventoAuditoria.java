package br.gov.sifap.auditoria.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Evento imutavel da trilha de auditoria (REQ-014).
 *
 * <p>A classe nao expoe nenhuma operacao que altere estado apos a criacao: o unico caminho
 * de construcao e {@link #registrar}. A imutabilidade tambem e imposta no banco pelo gatilho
 * {@code tg_evento_auditoria_imutavel}, conforme a decisao 1 do ADR-0003.
 */
@Entity
@Table(name = "evento_auditoria")
public class EventoAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "data_evento", nullable = false, updatable = false)
    private LocalDate dataEvento;

    @Column(name = "hora_evento", nullable = false, updatable = false)
    private LocalTime horaEvento;

    @Column(name = "marca_temporal", nullable = false, updatable = false)
    private LocalDateTime marcaTemporal;

    @Column(name = "codigo_acao", nullable = false, updatable = false, length = 2)
    private AcaoAuditoria acao;

    @Column(name = "modulo_origem", nullable = false, updatable = false, length = 8)
    private String moduloOrigem;

    @Column(name = "descricao_acao", nullable = false, updatable = false, length = 80)
    private String descricaoAcao;

    @Column(name = "tipo_entidade", nullable = false, updatable = false, length = 4)
    private TipoEntidade tipoEntidade;

    @Column(name = "id_entidade", nullable = false, updatable = false, length = 15)
    private String idEntidade;

    @Column(name = "cpf_afetado", updatable = false, length = 11)
    private String cpfAfetado;

    @Column(name = "usuario", nullable = false, updatable = false, length = 8)
    private String usuario;

    @Column(name = "nome_job_batch", updatable = false, length = 16)
    private String nomeJobBatch;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "situacao_batch", updatable = false, length = 1)
    private String situacaoBatch;

    /** Exigido pelo Hibernate; nenhum codigo de aplicacao deve usar. */
    protected EventoAuditoria() {
    }

    private EventoAuditoria(LocalDateTime momento, AcaoAuditoria acao, String moduloOrigem,
                            String descricaoAcao, TipoEntidade tipoEntidade, String idEntidade,
                            String cpfAfetado, String usuario) {
        this.dataEvento = momento.toLocalDate();
        this.horaEvento = momento.toLocalTime().withNano(0);
        this.marcaTemporal = momento;
        this.acao = acao;
        this.moduloOrigem = moduloOrigem;
        this.descricaoAcao = descricaoAcao;
        this.tipoEntidade = tipoEntidade;
        this.idEntidade = idEntidade;
        this.cpfAfetado = cpfAfetado;
        this.usuario = usuario;
    }

    /**
     * Unico caminho de criacao de um evento. O identificador e atribuido pelo banco (REQ-014,
     * decisao 11 do Estagio 2), portanto permanece nulo ate a persistencia.
     */
    public static EventoAuditoria registrar(LocalDateTime momento, AcaoAuditoria acao,
                                            String moduloOrigem, String descricaoAcao,
                                            TipoEntidade tipoEntidade, String idEntidade,
                                            String cpfAfetado, String usuario) {
        Objects.requireNonNull(momento, "momento");
        Objects.requireNonNull(acao, "acao");
        Objects.requireNonNull(moduloOrigem, "moduloOrigem");
        Objects.requireNonNull(descricaoAcao, "descricaoAcao");
        Objects.requireNonNull(tipoEntidade, "tipoEntidade");
        Objects.requireNonNull(idEntidade, "idEntidade");
        Objects.requireNonNull(usuario, "usuario");
        return new EventoAuditoria(momento, acao, moduloOrigem, descricaoAcao,
                tipoEntidade, idEntidade, cpfAfetado, usuario);
    }

    public Long id() {
        return id;
    }

    public LocalDate dataEvento() {
        return dataEvento;
    }

    public LocalTime horaEvento() {
        return horaEvento;
    }

    public LocalDateTime marcaTemporal() {
        return marcaTemporal;
    }

    public AcaoAuditoria acao() {
        return acao;
    }

    public String moduloOrigem() {
        return moduloOrigem;
    }

    public String descricaoAcao() {
        return descricaoAcao;
    }

    public TipoEntidade tipoEntidade() {
        return tipoEntidade;
    }

    public String idEntidade() {
        return idEntidade;
    }

    public Optional<String> cpfAfetado() {
        return Optional.ofNullable(cpfAfetado);
    }

    public String usuario() {
        return usuario;
    }

    public Optional<String> nomeJobBatch() {
        return Optional.ofNullable(nomeJobBatch);
    }

    public Optional<String> situacaoBatch() {
        return Optional.ofNullable(situacaoBatch);
    }
}
