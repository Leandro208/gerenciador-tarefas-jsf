package io.github.Leandro208.projetoESIG.validacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListaMensagens implements Serializable {

    Collection<MensagemAviso> mensagens = null;

    public ListaMensagens() {
        this.mensagens = new ArrayList<>();
    }

    public ListaMensagens(Collection<MensagemAviso> mensagens) {
        this.mensagens = mensagens;
    }

    public void addMensagem(MensagemAviso msg) {
        this.mensagens.add(msg);
    }

    public Collection<MensagemAviso> getMensagens() {
        return mensagens;
    }

    public boolean isErrorPresent() {
        for(MensagemAviso msg : mensagens) {
            if (msg.isError())
                return true;
        }
        return false;
    }

    public void addErro(String msg) {
        this.mensagens.add(new MensagemAviso(msg, TipoMensagem.ERROR));
    }

    public void addInformation(String msg) {
        this.mensagens.add(new MensagemAviso(msg, TipoMensagem.INFORMATION));
    }

    public void addWarning(String msg) {
        this.mensagens.add(new MensagemAviso(msg, TipoMensagem.WARNING));
    }

    public void addAll(Collection<MensagemAviso> msgs) {
        mensagens.addAll(msgs);
    }

    public void addAll(ListaMensagens lista) {
        this.addAll(lista.getMensagens());
    }

    public List<MensagemAviso> getErrorMessages() {
        List<MensagemAviso> erros = new ArrayList<MensagemAviso>();

        for(MensagemAviso msg : mensagens) {
            if (msg.isError())
                erros.add(msg);
        }

        return erros;
    }

    /**
     * Retorna as mensagens do tipo aviso que estão na lista.
     */
    public List<MensagemAviso> getWarningMessages() {
        List<MensagemAviso> warnings = new ArrayList<MensagemAviso>();

        for(MensagemAviso msg : mensagens) {
            if (msg.isWarning())
                warnings.add(msg);
        }

        return warnings;
    }

    /**
     * Retorna as mensagens do tipo informação que estão na lista.
     */
    public List<MensagemAviso> getInfoMessages() {
        List<MensagemAviso> infos = new ArrayList<MensagemAviso>();

        for(MensagemAviso msg : mensagens) {
            if (msg.isInformation())
                infos.add(msg);
        }

        return infos;
    }
}
