package br.ufal.ic.p2.bikeshare.model.pagamento;

import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.util.Map;

public final class Cartao implements MetodoPagamento {
    private static final long serialVersionUID = 1L;
    private final String numero;
    private final String validade;

    public Cartao(String numero, String validade) {
        Valores.obrigatorio(numero, "Numero do cartao nao pode ser nulo.");
        Valores.obrigatorio(validade, "Validade do cartao nao pode ser nula.");
        if (!validade.matches("(0[1-9]|1[0-2])/\\d{4}")) {
            throw new ValidacaoException("Validade do cartao invalida.");
        }
        this.numero = numero;
        this.validade = validade;
    }

    public String nome() {
        return "cartao";
    }

    public Map<String, String> atributos() {
        return Map.of("numeroCartao", numero, "validadeCartao", validade);
    }

    public String numeroCartao() {
        return numero;
    }

    public String validadeCartao() {
        return validade;
    }
}
