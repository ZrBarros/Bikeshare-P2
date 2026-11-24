package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.exception.EntidadeNaoEncontradaException;
import br.ufal.ic.p2.bikeshare.exception.OperacaoNaoPermitidaException;
import br.ufal.ic.p2.bikeshare.model.Aluguel;
import br.ufal.ic.p2.bikeshare.model.Bicicleta;
import br.ufal.ic.p2.bikeshare.model.Estacao;
import br.ufal.ic.p2.bikeshare.model.Usuario;
import br.ufal.ic.p2.bikeshare.util.Valores;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Dados persistentes e acesso controlado aos cadastros. */
public final class EstadoSistema implements Serializable {
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("serial")
    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();
    private int proximoUsuario = 1;
    @SuppressWarnings("serial")
    private Map<String, Estacao> estacoes = new LinkedHashMap<>();
    private int proximaEstacao = 1;
    @SuppressWarnings("serial")
    private Map<String, Bicicleta> bicicletas = new LinkedHashMap<>();

    @SuppressWarnings("serial")
    private Map<String, Aluguel> alugueis = new LinkedHashMap<>();
    private int proximoAluguel = 1;

    String cadastrarUsuario(Function<String, Usuario> criar) {
        String id = "U" + proximoUsuario;
        Usuario usuario = criar.apply(id);
        usuarios.put(id, usuario);
        proximoUsuario++;
        return id;
    }

    String cadastrarEstacao(Function<String, Estacao> criar) {
        String id = "E" + proximaEstacao;
        Estacao estacao = criar.apply(id);
        estacoes.put(id, estacao);
        proximaEstacao++;
        return id;
    }

    String cadastrarAluguel(Function<String, Aluguel> criar) {
        String id = "A" + proximoAluguel;
        Aluguel aluguel = criar.apply(id);
        alugueis.put(id, aluguel);
        proximoAluguel++;
        return id;
    }

    void adicionarBicicleta(Bicicleta bicicleta) {
        if (bicicletas.containsKey(bicicleta.id())) {
            throw new OperacaoNaoPermitidaException("Bicicleta ja existe.");
        }
        bicicletas.put(bicicleta.id(), bicicleta);
    }

    Usuario usuario(String id) {
        Valores.obrigatorio(id, "Identificacao do usuario nao pode ser nula.");
        Usuario usuario = usuarios.get(id);
        if (usuario == null) throw new EntidadeNaoEncontradaException("Usuario nao existe.");
        return usuario;
    }

    Estacao estacao(String id) {
        Valores.obrigatorio(id, "Identificacao da estacao nao pode ser nula.");
        Estacao estacao = estacoes.get(id);
        if (estacao == null) throw new EntidadeNaoEncontradaException("Estacao nao existe.");
        return estacao;
    }

    Bicicleta bicicleta(String id) {
        Valores.obrigatorio(id, "Identificacao da bicicleta nao pode ser nula.");
        Bicicleta bicicleta = bicicletas.get(id);
        if (bicicleta == null) throw new EntidadeNaoEncontradaException("Bicicleta nao existe.");
        return bicicleta;
    }

    Aluguel aluguel(String id) {
        Valores.obrigatorio(id, "Identificacao do aluguel nao pode ser nula.");
        Aluguel aluguel = alugueis.get(id);
        if (aluguel == null) throw new EntidadeNaoEncontradaException("Aluguel nao existe.");
        return aluguel;
    }

    boolean temBicicleta(String id) { return bicicletas.containsKey(id); }
    List<Usuario> usuarios() { return List.copyOf(usuarios.values()); }
    List<Bicicleta> bicicletas() { return List.copyOf(bicicletas.values()); }

    void removerUsuario(String id) {
        usuario(id).validarRemocao();
        usuarios.remove(id);
    }

    void removerBicicleta(String id) {
        bicicleta(id);
        bicicletas.remove(id);
    }

    private void readObject(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        if (alugueis == null) alugueis = new LinkedHashMap<>();
        if (proximoAluguel == 0) proximoAluguel = 1;
        if (bicicletas == null) bicicletas = new LinkedHashMap<>();
        if (estacoes == null) estacoes = new LinkedHashMap<>();
        if (proximaEstacao == 0) proximaEstacao = 1;
    }
}
