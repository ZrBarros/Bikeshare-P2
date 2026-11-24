package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.exception.EntidadeNaoEncontradaException;
import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;
import br.ufal.ic.p2.bikeshare.exception.ValidacaoException;
import br.ufal.ic.p2.bikeshare.model.PessoaFisica;
import br.ufal.ic.p2.bikeshare.model.PessoaJuridica;
import br.ufal.ic.p2.bikeshare.model.Plano;
import br.ufal.ic.p2.bikeshare.model.PlanoAssinante;
import br.ufal.ic.p2.bikeshare.model.PlanoAvulso;
import br.ufal.ic.p2.bikeshare.model.Usuario;
import br.ufal.ic.p2.bikeshare.model.pagamento.Cartao;
import br.ufal.ic.p2.bikeshare.model.pagamento.Pix;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.math.BigDecimal;

// Regras de cadastro, consulta, alteração e exclusão de usuários.
public final class CadastroUsuarios {
    private final EstadoSistema estado;
    private final BigDecimal mensalidadePadrao;

    public CadastroUsuarios(EstadoSistema estado, BigDecimal mensalidadePadrao) {
        this.estado = estado;
        this.mensalidadePadrao = mensalidadePadrao;
    }

    public String criarPessoaFisica(String nome, String email, String senha, String tipo,
                                    String cpf, String plano, String mensalidade) {
        validarComuns(nome, email, senha);
        if (!"pessoaFisica".equals(tipo)) throw erro("Tipo invalido.");
        Valores.obrigatorio(cpf, "CPF nao pode ser nulo.");
        Plano escolhido = escolherPlano(plano, mensalidade);
        garantirEmailLivre(email, null);
        return estado.cadastrarUsuario(id -> new PessoaFisica(id, nome, email, senha, cpf, escolhido));
    }

    public String criarPessoaJuridica(String nome, String email, String senha, String tipo,
                                      String cnpj, String razaoSocial, String plano, String mensalidade) {
        validarComuns(nome, email, senha);
        if (!"pessoaJuridica".equals(tipo)) throw erro("Tipo invalido.");
        Valores.obrigatorio(cnpj, "CNPJ nao pode ser nulo.");
        Valores.obrigatorio(razaoSocial, "Razao social nao pode ser nula.");
        Plano escolhido = escolherPlano(plano, mensalidade);
        garantirEmailLivre(email, null);
        return estado.cadastrarUsuario(id ->
                new PessoaJuridica(id, nome, email, senha, cnpj, razaoSocial, escolhido));
    }

    public String atributo(String id, String atributo) { return buscar(id).atributo(atributo); }

    public String porEmail(String email) {
        Valores.obrigatorio(email, "Email nao pode ser nulo.");
        for (Usuario usuario : estado.usuarios()) {
            if (usuario.email().equalsIgnoreCase(email)) return usuario.id();
        }
        throw new EntidadeNaoEncontradaException("Usuario nao existe.");
    }

    public void alterar(String id, String atributo, String valor) {
        Usuario usuario = buscar(id);
        if (atributo == null) throw new OperacaoNaoPermitidaException("Atributo nao pode ser alterado.");
        switch (atributo) {
            case "nome": usuario.nome(valor); break;
            case "email": alterarEmail(usuario, valor); break;
            case "senha": usuario.senha(valor); break;
            case "plano": alterarPlano(usuario, valor); break;
            case "saldo": recarregar(usuario, valor); break;
            case "metodoPagamento": selecionarCarteira(usuario, valor); break;
            default: throw new OperacaoNaoPermitidaException("Atributo nao pode ser alterado.");
        }
    }

    private void alterarEmail(Usuario usuario, String email) {
        Valores.email(email);
        garantirEmailLivre(email, usuario.id());
        usuario.email(email);
    }

    private void alterarPlano(Usuario usuario, String plano) {
        if ("avulso".equals(plano)) usuario.plano(new PlanoAvulso());
        else if ("assinante".equals(plano)) usuario.assinar(mensalidadePadrao);
        else throw erro("Plano invalido.");
    }

    private void recarregar(Usuario usuario, String texto) {
        usuario.validarRecarga();
        BigDecimal valor = Valores.numero(texto, "Valor nao pode ser nulo.", "Valor deve ser numerico.");
        usuario.recarregar(valor);
    }

    private void selecionarCarteira(Usuario usuario, String metodo) {
        if (!"saldoPreCarregado".equals(metodo)) throw erro("Valor de metodo de pagamento invalido.");
        usuario.usarCarteira();
    }

    public void alterarPix(String id, String atributo, String metodo, String chave) {
        Usuario usuario = buscar(id);
        if (!"metodoPagamento".equals(atributo)) {
            throw new OperacaoNaoPermitidaException("Atributo nao pode ser alterado.");
        }
        if (!"pix".equals(metodo)) throw erro("Valor de metodo de pagamento invalido.");
        usuario.usarPix(new Pix(chave));
    }

    public void alterarCartao(String id, String atributo, String metodo, String numero, String validade) {
        Usuario usuario = buscar(id);
        if (!"metodoPagamento".equals(atributo)) {
            throw new OperacaoNaoPermitidaException("Atributo nao pode ser alterado.");
        }
        if (!"cartao".equals(metodo)) throw erro("Valor de metodo de pagamento invalido.");
        usuario.usarCartao(new Cartao(numero, validade));
    }

    public void remover(String id) { estado.removerUsuario(id); }

    Usuario buscar(String id) { return estado.usuario(id); }

    private void validarComuns(String nome, String email, String senha) {
        Valores.obrigatorio(nome, "Nome nao pode ser nulo.");
        Valores.email(email);
        Valores.obrigatorio(senha, "Senha nao pode ser nula.");
    }

    private void garantirEmailLivre(String email, String idAtual) {
        for (Usuario usuario : estado.usuarios()) {
            if (!usuario.id().equals(idAtual) && usuario.email().equalsIgnoreCase(email)) {
                throw new OperacaoNaoPermitidaException("Email ja cadastrado.");
            }
        }
    }

    private Plano escolherPlano(String plano, String mensalidade) {
        if (plano == null) return new PlanoAvulso();
        if ("avulso".equals(plano)) {
            if (!Valores.vazio(mensalidade)) throw new OperacaoNaoPermitidaException("Plano nao possui mensalidade.");
            return new PlanoAvulso();
        }
        if ("assinante".equals(plano)) return new PlanoAssinante(Valores.decimal(mensalidade, "Mensalidade", false));
        throw erro("Plano invalido.");
    }

    private static ValidacaoException erro(String mensagem) { return new ValidacaoException(mensagem); }
}
