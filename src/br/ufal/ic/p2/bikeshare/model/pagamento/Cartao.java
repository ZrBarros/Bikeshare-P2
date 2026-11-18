package br.ufal.ic.p2.bikeshare.model.pagamento;

public final class Cartao implements MetodoPagamento {
    private static final long serialVersionUID = 1L;
    private final String numero;
    private final String validade;

    public Cartao(String numero, String validade) {
        this.numero = numero;
        this.validade = validade;
    }

    public String nome() {
        return "cartao";
    }

    public String numeroCartao() {
        return numero;
    }

    public String validadeCartao() {
        return validade;
    }
}
