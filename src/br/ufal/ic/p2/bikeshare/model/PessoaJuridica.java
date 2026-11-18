package br.ufal.ic.p2.bikeshare.model;

public final class PessoaJuridica extends Usuario {
    private static final long serialVersionUID = 1L;
    private final String cnpj;
    private final String razaoSocial;

    public PessoaJuridica(String id, String nome, String email, String senha, String cnpj,
                          String razaoSocial, Plano plano) {
        super(id, nome, email, senha, plano);
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
    }

    public String tipo() { return "pessoaJuridica"; }
    protected String cnpj() { return cnpj; }
    protected String razaoSocial() { return razaoSocial; }
}
