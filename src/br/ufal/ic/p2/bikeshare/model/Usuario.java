package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.model.pagamento.MetodoPagamento;
import br.ufal.ic.p2.bikeshare.model.pagamento.SaldoPreCarregado;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.io.Serializable;
import java.math.BigDecimal;

/** Identidade e comportamento comuns aos dois tipos de usuário. */
public abstract class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private String nome;
    private String email;
    private String senha;
    private Plano plano;
    private MetodoPagamento pagamento;
    private final SaldoPreCarregado carteira = new SaldoPreCarregado();
    private BigDecimal pendencias = BigDecimal.ZERO;
    private String aluguelAtivo;

    protected Usuario(String id, String nome, String email, String senha, Plano plano) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.plano = plano;
    }

    public String id() { return id; }
    public String email() { return email; }
    public String aluguelAtivo() { return aluguelAtivo; }
    public void nome(String nome) { this.nome = nome; }
    public void email(String email) { this.email = email; }
    public void senha(String senha) { this.senha = senha; }
    public void plano(Plano plano) { this.plano = plano; }
    public void pagamento(MetodoPagamento pagamento) { this.pagamento = pagamento; }
    public void usarCarteira() { this.pagamento = carteira; }
    public void aluguelAtivo(String id) { this.aluguelAtivo = id; }
    public void adicionarPendencia(BigDecimal valor) { pendencias = pendencias.add(valor); }

    public void recarregar(BigDecimal valor) {
        if (pagamento == null) {
            throw new IllegalArgumentException("Usuario nao usa saldo pre-carregado.");
        }
        pagamento.recarregar(valor);
    }

    public String atributo(String nomeAtributo) {
        if (nomeAtributo == null) {
            throw new IllegalArgumentException("Atributo nao existe.");
        }
        switch (nomeAtributo) {
            case "nome": return nome;
            case "email": return email;
            case "senha": return senha;
            case "tipo": return tipo();
            case "cpf": return cpf();
            case "cnpj": return cnpj();
            case "razaoSocial": return razaoSocial();
            case "estado": return pendencias.signum() > 0 ? "inadimplente" : "ativo";
            case "totalPendencias": return Valores.formatar(pendencias);
            case "aluguelAtivo": return aluguelAtivo == null ? "nenhum" : aluguelAtivo;
            case "plano": return plano.nome();
            case "mensalidade": return Valores.formatar(plano.mensalidade());
            case "metodoPagamento": return pagamento == null ? "nenhum" : pagamento.nome();
            case "numeroCartao": return pagamento == null ? semCartao() : pagamento.numeroCartao();
            case "validadeCartao": return pagamento == null ? semCartao() : pagamento.validadeCartao();
            case "chavePix": return pagamento == null ? semPix() : pagamento.chavePix();
            case "saldo": return pagamento == null ? semSaldo() : Valores.formatar(pagamento.saldo());
            default: throw new IllegalArgumentException("Atributo nao existe.");
        }
    }

    private static String semCartao() { throw new IllegalArgumentException("Usuario nao usa cartao."); }
    private static String semPix() { throw new IllegalArgumentException("Usuario nao usa pix."); }
    private static String semSaldo() { throw new IllegalArgumentException("Usuario nao usa saldo pre-carregado."); }

    public abstract String tipo();
    protected String cpf() { throw new IllegalArgumentException("Usuario nao eh pessoa fisica."); }
    protected String cnpj() { throw new IllegalArgumentException("Usuario nao eh pessoa juridica."); }
    protected String razaoSocial() { throw new IllegalArgumentException("Usuario nao eh pessoa juridica."); }
}
