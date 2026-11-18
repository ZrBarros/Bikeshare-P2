package br.ufal.ic.p2.bikeshare.model;

public final class PessoaFisica extends Usuario {
    private static final long serialVersionUID = 1L;
    private final String cpf;

    public PessoaFisica(String id, String nome, String email, String senha, String cpf, Plano plano) {
        super(id, nome, email, senha, plano);
        this.cpf = cpf;
    }

    public String tipo() { return "pessoaFisica"; }
    protected String cpf() { return cpf; }
}
