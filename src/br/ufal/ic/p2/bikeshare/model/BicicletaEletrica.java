package br.ufal.ic.p2.bikeshare.model;

public final class BicicletaEletrica extends Bicicleta {
    private static final long serialVersionUID = 1L;
    private final int bateria;

    public BicicletaEletrica(String id, String estacao, int bateria) {
        super(id, estacao);
        this.bateria = bateria;
    }

    public String tipo() { return "eletrica"; }
    protected String nivelBateria() { return Integer.toString(bateria); }

    public void validarRetirada(int bateriaMinima) {
        super.validarRetirada(bateriaMinima);
        if (bateria < bateriaMinima) throw new IllegalArgumentException("Nivel de bateria insuficiente.");
    }

    public boolean disponivel(int bateriaMinima) {
        return super.disponivel(bateriaMinima) && bateria >= bateriaMinima;
    }
}
