package br.ufal.ic.p2.bikeshare.model.pagamento;

import br.ufal.ic.p2.bikeshare.util.Valores;
import java.util.Map;

public final class Pix implements MetodoPagamento {
    private static final long serialVersionUID = 1L;
    private final String chave;

    public Pix(String chave) {
        this.chave = Valores.obrigatorio(chave, "Chave pix nao pode ser nula.");
    }

    public String nome() {
        return "pix";
    }

    public Map<String, String> atributos() { return Map.of("chavePix", chave); }

    public String chavePix() {
        return chave;
    }
}
