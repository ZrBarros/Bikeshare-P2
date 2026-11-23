package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.model.Usuario;
import br.ufal.ic.p2.bikeshare.model.Estacao;
import br.ufal.ic.p2.bikeshare.model.Bicicleta;
import br.ufal.ic.p2.bikeshare.model.Aluguel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/** Apenas dados persistentes; os serviços podem ser reconstruídos a cada execução. */
public final class EstadoSistema implements Serializable {
    private static final long serialVersionUID = 1L;
    final Map<String, Usuario> usuarios = new LinkedHashMap<>();
    int proximoUsuario = 1;
    Map<String, Estacao> estacoes = new LinkedHashMap<>();
    int proximaEstacao = 1;
    Map<String, Bicicleta> bicicletas = new LinkedHashMap<>();

    Map<String, Aluguel> alugueis = new LinkedHashMap<>();
    int proximoAluguel = 1;

    private void readObject(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        if (alugueis == null) alugueis = new LinkedHashMap<>();
        if (proximoAluguel == 0) proximoAluguel = 1;
        if (bicicletas == null) bicicletas = new LinkedHashMap<>();
        if (estacoes == null) estacoes = new LinkedHashMap<>();
        if (proximaEstacao == 0) proximaEstacao = 1;
    }
}
