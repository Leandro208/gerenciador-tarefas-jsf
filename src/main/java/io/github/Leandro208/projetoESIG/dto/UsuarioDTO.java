package io.github.Leandro208.projetoESIG.dto;

import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.dominio.RegistroEntrada;
import io.github.Leandro208.projetoESIG.enums.Funcao;

public class UsuarioDTO {

    private Long id;
    private String nome;
    private String email;
    private Equipe equipe;
    private Long idResponsavel;
    private RegistroEntrada entrada;
    private Funcao funcao;

    public boolean isAdm() {
        return funcao == Funcao.ADM;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public RegistroEntrada getEntrada() {
        return entrada;
    }

    public void setEntrada(RegistroEntrada entrada) {
        this.entrada = entrada;
    }

    public Long getIdResponsavel() {
        return idResponsavel;
    }

    public void setIdResponsavel(Long idResponsavel) {
        this.idResponsavel = idResponsavel;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
