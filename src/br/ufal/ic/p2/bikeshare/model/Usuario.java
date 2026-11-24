package br.ufal.ic.p2.bikeshare.model;

import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;
import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;
import br.ufal.ic.p2.bikeshare.model.pagamento.Cartao;
import br.ufal.ic.p2.bikeshare.model.pagamento.MetodoPagamento;
import br.ufal.ic.p2.bikeshare.model.pagamento.Pix;
import br.ufal.ic.p2.bikeshare.model.pagamento.SaldoPreCarregado;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

// Identidade e comportamento comuns aos dois tipos de usuário
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
        this.id = Valores.obrigatorio(id, "Identificacao do usuario nao pode ser nula.");
        this.nome = Valores.obrigatorio(nome, "Nome nao pode ser nulo.");
        this.email = Valores.email(email);
        this.senha = Valores.obrigatorio(senha, "Senha nao pode ser nula.");
        this.plano = Valores.naoNulo(plano, "Plano nao pode ser nulo.");
    }

    public String id() { return id; }
    public String email() { return email; }
    public String aluguelAtivo() { return aluguelAtivo; }
    public void nome(String nome) { this.nome = Valores.obrigatorio(nome, "Nome nao pode ser nulo."); }
    public void email(String email) { this.email = Valores.email(email); }
    public void senha(String senha) { this.senha = Valores.obrigatorio(senha, "Senha nao pode ser nula."); }
    public void plano(Plano plano) { this.plano = Valores.naoNulo(plano, "Plano nao pode ser nulo."); }
    public void usarCartao(Cartao cartao) {
        pagamento = Valores.naoNulo(cartao, "Metodo de pagamento nao cadastrado.");
    }

    public void usarPix(Pix pix) {
        pagamento = Valores.naoNulo(pix, "Metodo de pagamento nao cadastrado.");
    }
    public void usarCarteira() { this.pagamento = carteira; }
    public void iniciarAluguel(String id) {
        Valores.obrigatorio(id, "Identificacao do aluguel nao pode ser nula.");
        validarNovoAluguel();
        aluguelAtivo = id;
    }

    public void concluirAluguel(String id) {
        validarAluguelAtivo(id);
        aluguelAtivo = null;
    }

    public void validarAluguelAtivo(String id) {
        Valores.obrigatorio(id, "Identificacao do aluguel nao pode ser nula.");
        if (!id.equals(aluguelAtivo)) throw new OperacaoNaoPermitidaException("Aluguel nao pertence ao usuario.");
    }

    public void validarRemocao() {
        if (aluguelAtivo != null) {
            throw new OperacaoNaoPermitidaException("Nao e possivel remover usuario com aluguel em andamento.");
        }
    }

    public void assinar(BigDecimal mensalidade) {
        if (!plano.assinante()) plano(new PlanoAssinante(mensalidade));
    }

    public void validarRecarga() {
        if (pagamento != carteira) throw new OperacaoNaoPermitidaException("Usuario nao usa saldo pre-carregado.");
    }
    public void adicionarPendencia(BigDecimal valor) {
        pendencias = pendencias.add(Valores.monetario(valor, "Valor", false));
    }

    public void validarNovoAluguel() {
        if (pendencias.signum() > 0) throw new OperacaoNaoPermitidaException("Usuario inadimplente.");
        if (aluguelAtivo != null) {
            throw new OperacaoNaoPermitidaException("Usuario ja possui um aluguel em andamento.");
        }
    }

    public BigDecimal calcularTarifa(long minutos, Tarifas tarifas) {
        return plano.calcular(minutos, tarifas);
    }

    public void cobrar(BigDecimal valor) {
        Valores.monetario(valor, "Valor", false);
        if (valor.signum() == 0) return;
        if (pagamento == null) throw new OperacaoNaoPermitidaException("Metodo de pagamento nao cadastrado.");
        pagamento.cobrar(valor);
    }

    public void validarPagamento() {
        if (pagamento == null) throw new OperacaoNaoPermitidaException("Metodo de pagamento nao cadastrado.");
        pagamento.validarRetirada();
    }

    public void recarregar(BigDecimal valor) {
        validarRecarga();
        carteira.recarregar(valor);
    }

    public String atributo(String nomeAtributo) {
        if (nomeAtributo == null) {
            throw new ValidacaoException("Atributo nao existe.");
        }
        switch (nomeAtributo) {
            case "nome": return nome;
            case "email": return email;
            case "senha": return senha;
            case "tipo": return tipo();
            case "cpf": return documento("cpf", "Usuario nao eh pessoa fisica.");
            case "cnpj": return documento("cnpj", "Usuario nao eh pessoa juridica.");
            case "razaoSocial": return documento("razaoSocial", "Usuario nao eh pessoa juridica.");
            case "estado": return pendencias.signum() > 0 ? "inadimplente" : "ativo";
            case "totalPendencias": return Valores.formatar(pendencias);
            case "aluguelAtivo": return aluguelAtivo == null ? "nenhum" : aluguelAtivo;
            case "plano": return plano.nome();
            case "mensalidade": return Valores.formatar(plano.mensalidade()
                    .orElseThrow(() -> new OperacaoNaoPermitidaException("Plano nao possui mensalidade.")));
            case "metodoPagamento": return pagamento == null ? "nenhum" : pagamento.nome();
            case "numeroCartao": return atributoPagamento("numeroCartao", "Usuario nao usa cartao.");
            case "validadeCartao": return atributoPagamento("validadeCartao", "Usuario nao usa cartao.");
            case "chavePix": return atributoPagamento("chavePix", "Usuario nao usa pix.");
            case "saldo": return atributoPagamento("saldo", "Usuario nao usa saldo pre-carregado.");
            default: throw new ValidacaoException("Atributo nao existe.");
        }
    }

    private String atributoPagamento(String atributo, String erro) {
        String valor = pagamento == null ? null : pagamento.atributos().get(atributo);
        if (valor == null) throw new OperacaoNaoPermitidaException(erro);
        return valor;
    }

    private String documento(String atributo, String erro) {
        String valor = documentos().get(atributo);
        if (valor == null) throw new OperacaoNaoPermitidaException(erro);
        return valor;
    }

    public abstract String tipo();
    protected abstract Map<String, String> documentos();
}
