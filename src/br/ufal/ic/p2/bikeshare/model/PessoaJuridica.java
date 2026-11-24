package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.util.Valores;
import java.util.Map;

public final class PessoaJuridica extends Usuario {
    private static final long serialVersionUID = 1L;
    private final String cnpj;
    private final String razaoSocial;

    public PessoaJuridica(String id, String nome, String email, String senha, String cnpj,
                          String razaoSocial, Plano plano) {
        super(id, nome, email, senha, plano);
        this.cnpj = Valores.obrigatorio(cnpj, "CNPJ nao pode ser nulo.");
        this.razaoSocial = Valores.obrigatorio(razaoSocial, "Razao social nao pode ser nula.");
    }

    public String tipo() { return "pessoaJuridica"; }
    protected Map<String, String> documentos() {
        return Map.of("cnpj", cnpj, "razaoSocial", razaoSocial);
    }
}
