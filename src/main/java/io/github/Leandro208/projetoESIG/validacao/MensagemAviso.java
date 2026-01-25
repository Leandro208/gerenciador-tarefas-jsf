package io.github.Leandro208.projetoESIG.validacao;

import java.io.Serializable;

public class MensagemAviso implements Serializable {

    private TipoMensagem tipo;

    private String mensagem;

    public MensagemAviso(String mensagem, TipoMensagem tipo) {
        this.mensagem = mensagem;
        this.tipo = tipo;
    }

    /**
     * Identifica se a mensagem é uma mensagem de erro
     */
    public boolean isError() {
        return TipoMensagem.ERROR.equals(tipo);
    }

    /**
     * Identifica se a mensagem é uma mensagem de aviso
     */
    public boolean isWarning() {
        return TipoMensagem.WARNING.equals(tipo);
    }

    /**
     * Identifica se a mensagem é uma mensagem de informação
     */
    public boolean isInformation() {
        return TipoMensagem.INFORMATION.equals(tipo);
    }

    public String getMensagem() {
        return mensagem;
    }
}
