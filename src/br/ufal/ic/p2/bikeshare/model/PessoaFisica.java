package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.util.Valores;
import java.util.Map;

public final class PessoaFisica extends Usuario {
    private static final long serialVersionUID = 1L;
    private final String cpf;

    public PessoaFisica(String id, String nome, String email, String senha, String cpf, Plano plano) {
        super(id, nome, email, senha, plano);
        this.cpf = Valores.obrigatorio(cpf, "CPF nao pode ser nulo.");
    }

    public String tipo() { return "pessoaFisica"; }
    protected Map<String, String> documentos() { return Map.of("cpf", cpf); }
}
