package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;

public enum EstadoAluguel {
    EM_ANDAMENTO("emAndamento"),
    FINALIZADO("finalizado"),
    CANCELADO("cancelado"),
    EXPIRADO("expirado");

    private final String texto;

    EstadoAluguel(String texto) { this.texto = texto; }
    public String texto() { return texto; }

    public static EstadoAluguel porTexto(String texto) {
        for (EstadoAluguel estado : values()) {
            if (estado.texto.equals(texto)) return estado;
        }
        throw new ValidacaoException("Estado invalido: " + texto);
    }
}
