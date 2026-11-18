package br.ufal.ic.p2.bikeshare.model.pagamento;

public final class Pix implements MetodoPagamento {
    private static final long serialVersionUID = 1L;
    private final String chave;

    public Pix(String chave) {
        this.chave = chave;
    }

    public String nome() {
        return "pix";
    }

    public String chavePix() {
        return chave;
    }
}
