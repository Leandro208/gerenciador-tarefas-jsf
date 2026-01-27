package io.github.Leandro208.projetoESIG.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "log_db", schema = "auditoria")
public class LogDB {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** Tipo da operação de log. (I) Insert, (U) Update, (D) Delete*/
    private char operacao;

    @Column(name = "id_elemento")
    private Long idElemento;

    private String alteracao;

    @ManyToOne
    @JoinColumn(name = "id_registro_acesso")
    private RegistroAcesso registroAcesso;

    @Temporal(TemporalType.TIMESTAMP)
    private Date data;

    private String tabela;

    @Column(name = "cod_comando")
    private int codComando;

    public int getCodComando() {
        return codComando;
    }

    public void setCodComando(int codComando) {
        this.codComando = codComando;
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public RegistroAcesso getRegistroAcesso() {
        return registroAcesso;
    }

    public void setRegistroAcesso(RegistroAcesso registroAcesso) {
        this.registroAcesso = registroAcesso;
    }

    public Long getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Long idElemento) {
        this.idElemento = idElemento;
    }

    public char getOperacao() {
        return operacao;
    }

    public void setOperacao(char operacao) {
        this.operacao = operacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlteracao() {
        return alteracao;
    }

    public void setAlteracao(String alteracao) {
        this.alteracao = alteracao;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LogDB logDB = (LogDB) o;
        return Objects.equals(id, logDB.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
