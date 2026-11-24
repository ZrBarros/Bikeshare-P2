package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;

public enum EstadoBicicleta {
    DISPONIVEL("disponivel"),
    EM_USO("emUso"),
    EM_MANUTENCAO("emManutencao");

    private final String texto;

    EstadoBicicleta(String texto) { this.texto = texto; }
    public String texto() { return texto; }

    public static EstadoBicicleta porTexto(String texto) {
        for (EstadoBicicleta estado : values()) {
            if (estado.texto.equals(texto)) return estado;
        }
        throw new ValidacaoException("Estado invalido: " + texto);
    }
}
