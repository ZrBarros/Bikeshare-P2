package br.ufal.ic.p2.bikeshare.model;

public final class BicicletaConvencional extends Bicicleta {
    private static final long serialVersionUID = 1L;

    public BicicletaConvencional(String id, String estacao) {
        super(id, estacao);
    }

    public String tipo() { return "convencional"; }
}
