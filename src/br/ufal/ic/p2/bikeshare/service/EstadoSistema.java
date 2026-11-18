package br.ufal.ic.p2.bikeshare.service;

import br.ufal.ic.p2.bikeshare.model.Usuario;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/** Apenas dados persistentes; os serviços podem ser reconstruídos a cada execução. */
public final class EstadoSistema implements Serializable {
    private static final long serialVersionUID = 1L;
    final Map<String, Usuario> usuarios = new LinkedHashMap<>();
    int proximoUsuario = 1;
}
