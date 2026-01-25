package io.github.Leandro208.projetoESIG.exception;

import io.github.Leandro208.projetoESIG.validacao.ListaMensagens;
import io.github.Leandro208.projetoESIG.validacao.MensagemAviso;

import java.util.Collection;

public class NegocioException extends Exception {

    private ListaMensagens listaMensagens = new ListaMensagens();

    public NegocioException(String message) {
        super(message);
    }

    public NegocioException() {
    }

    public void addMensagens(Collection<MensagemAviso> mensagens) {
        listaMensagens.addAll(mensagens);
    }
    public ListaMensagens getListaMensagens() {
        return listaMensagens;
    }
}
