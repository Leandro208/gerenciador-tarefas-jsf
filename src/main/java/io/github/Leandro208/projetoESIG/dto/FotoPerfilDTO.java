package io.github.Leandro208.projetoESIG.dto;

import java.io.Serializable;

public class FotoPerfilDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final byte[] conteudo;
    private final String tipoMime;

    public FotoPerfilDTO(byte[] conteudo, String tipoMime) {
        this.conteudo = conteudo;
        this.tipoMime = tipoMime;
    }

    public byte[] getConteudo() { return conteudo; }
    public String getTipoMime() { return tipoMime; }
}
