package io.github.Leandro208.projetoESIG.validacao;

public enum TipoMensagem {
    ERROR,

    WARNING,

    INFORMATION,

    ORIENTATION;

    public static TipoMensagem valueOf(int ordinal) {
        return values()[ordinal];
    }
}
