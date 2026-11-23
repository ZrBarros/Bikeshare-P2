package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.model.*;
import br.ufal.ic.p2.bikeshare.model.pagamento.*;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.math.BigDecimal;
import java.util.Locale;

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
        String id = "U" + estado.proximoUsuario++;
        estado.usuarios.put(id, new PessoaFisica(id, nome, email, senha, cpf, escolhido));
        return id;
    }

    public String criarPessoaJuridica(String nome, String email, String senha, String tipo,
                                      String cnpj, String razaoSocial, String plano, String mensalidade) {
        validarComuns(nome, email, senha);
        if (!"pessoaJuridica".equals(tipo)) throw erro("Tipo invalido.");
        Valores.obrigatorio(cnpj, "CNPJ nao pode ser nulo.");
        Valores.obrigatorio(razaoSocial, "Razao social nao pode ser nula.");
        Plano escolhido = escolherPlano(plano, mensalidade);
        garantirEmailLivre(email, null);
        String id = "U" + estado.proximoUsuario++;
        estado.usuarios.put(id, new PessoaJuridica(id, nome, email, senha, cnpj, razaoSocial, escolhido));
        return id;
    }

    public String atributo(String id, String atributo) { return buscar(id).atributo(atributo); }

    public String porEmail(String email) {
        Valores.obrigatorio(email, "Email nao pode ser nulo.");
        for (Usuario usuario : estado.usuarios.values()) {
            if (usuario.email().equalsIgnoreCase(email)) return usuario.id();
        }
        throw erro("Usuario nao existe.");
    }

    public void alterar(String id, String atributo, String valor) {
        Usuario usuario = buscar(id);
        if (atributo == null) throw erro("Atributo nao pode ser alterado.");
        switch (atributo) {
            case "nome": usuario.nome(Valores.obrigatorio(valor, "Nome nao pode ser nulo.")); break;
            case "email":
                validarEmail(valor);
                garantirEmailLivre(valor, id);
                usuario.email(valor);
                break;
            case "senha": usuario.senha(Valores.obrigatorio(valor, "Senha nao pode ser nula.")); break;
            case "plano":
                if ("avulso".equals(valor)) usuario.plano(new PlanoAvulso());
                else if ("assinante".equals(valor)) {
                    if (!"assinante".equals(usuario.atributo("plano"))) {
                        usuario.plano(new PlanoAssinante(mensalidadePadrao));
                    }
                } else throw erro("Plano invalido.");
                break;
            case "saldo":
                if (!"saldoPreCarregado".equals(usuario.atributo("metodoPagamento"))) {
                    throw erro("Usuario nao usa saldo pre-carregado.");
                }
                if (Valores.vazio(valor)) throw erro("Valor nao pode ser nulo.");
                BigDecimal recarga;
                try { recarga = new BigDecimal(valor.trim().replace(',', '.')); }
                catch (NumberFormatException ex) { throw erro("Valor deve ser numerico."); }
                if (recarga.signum() <= 0) throw erro("Valor deve ser positivo.");
                usuario.recarregar(recarga);
                break;
            case "metodoPagamento":
                if ("saldoPreCarregado".equals(valor)) usuario.usarCarteira();
                else throw erro("Valor de metodo de pagamento invalido.");
                break;
            default: throw erro("Atributo nao pode ser alterado.");
        }
    }

    public void alterarPix(String id, String atributo, String metodo, String chave) {
        Usuario usuario = buscar(id);
        if (!"metodoPagamento".equals(atributo)) throw erro("Atributo nao pode ser alterado.");
        if (!"pix".equals(metodo)) throw erro("Valor de metodo de pagamento invalido.");
        usuario.pagamento(new Pix(Valores.obrigatorio(chave, "Chave pix nao pode ser nula.")));
    }

    public void alterarCartao(String id, String atributo, String metodo, String numero, String validade) {
        Usuario usuario = buscar(id);
        if (!"metodoPagamento".equals(atributo)) throw erro("Atributo nao pode ser alterado.");
        if (!"cartao".equals(metodo)) throw erro("Valor de metodo de pagamento invalido.");
        Valores.obrigatorio(numero, "Numero do cartao nao pode ser nulo.");
        Valores.obrigatorio(validade, "Validade do cartao nao pode ser nula.");
        if (!validade.matches("(0[1-9]|1[0-2])/\\d{4}")) throw erro("Validade do cartao invalida.");
        usuario.pagamento(new Cartao(numero, validade));
    }

    public void remover(String id) {
        Usuario usuario = buscar(id);
        if (usuario.aluguelAtivo() != null) throw erro("Nao e possivel remover usuario com aluguel em andamento.");
        estado.usuarios.remove(id);
    }

    Usuario buscar(String id) {
        Valores.obrigatorio(id, "Identificacao do usuario nao pode ser nula.");
        Usuario usuario = estado.usuarios.get(id);
        if (usuario == null) throw erro("Usuario nao existe.");
        return usuario;
    }

    private void validarComuns(String nome, String email, String senha) {
        Valores.obrigatorio(nome, "Nome nao pode ser nulo.");
        validarEmail(email);
        Valores.obrigatorio(senha, "Senha nao pode ser nula.");
    }

    private void validarEmail(String email) {
        Valores.obrigatorio(email, "Email nao pode ser nulo.");
        if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) throw erro("Email invalido.");
    }

    private void garantirEmailLivre(String email, String idAtual) {
        for (Usuario usuario : estado.usuarios.values()) {
            if (!usuario.id().equals(idAtual) && usuario.email().toLowerCase(Locale.ROOT)
                    .equals(email.toLowerCase(Locale.ROOT))) throw erro("Email ja cadastrado.");
        }
    }

    private Plano escolherPlano(String plano, String mensalidade) {
        if (plano == null) return new PlanoAvulso();
        if ("avulso".equals(plano)) {
            if (!Valores.vazio(mensalidade)) throw erro("Plano nao possui mensalidade.");
            return new PlanoAvulso();
        }
        if ("assinante".equals(plano)) return new PlanoAssinante(Valores.decimal(mensalidade, "Mensalidade", false));
        throw erro("Plano invalido.");
    }

    private static IllegalArgumentException erro(String mensagem) { return new IllegalArgumentException(mensagem); }
}
