package com.uade.logistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Grafo dirigido de depósitos, centros comunitarios y las conexiones
 * habilitadas entre ellos. Es solo la estructura de datos: el cálculo de
 * rutas vive en {@link RutaMasRapida}.
 */
public class RedVial {

    private final List<String> vertices = new ArrayList<>();
    private final Map<String, List<Arista>> adyacencia = new HashMap<>();

    public void agregarVertice(String nombre) {
        if (!adyacencia.containsKey(nombre)) {
            adyacencia.put(nombre, new ArrayList<>());
            vertices.add(nombre);
        }
    }

    public void agregarArista(String origen, String destino, int tiempo) {
        agregarVertice(origen);
        agregarVertice(destino);
        adyacencia.get(origen).add(new Arista(origen, destino, tiempo));
    }

    /** Marca como no habilitadas todas las aristas origen->destino. No las elimina. */
    public void deshabilitarArista(String origen, String destino) {
        for (Arista arista : adyacencia.getOrDefault(origen, List.of())) {
            if (arista.getDestino().equals(destino)) {
                arista.setHabilitada(false);
            }
        }
    }

    public void habilitarArista(String origen, String destino) {
        for (Arista arista : adyacencia.getOrDefault(origen, List.of())) {
            if (arista.getDestino().equals(destino)) {
                arista.setHabilitada(true);
            }
        }
    }

    public boolean contieneVertice(String nombre) {
        return adyacencia.containsKey(nombre);
    }

    public List<String> getVertices() {
        return List.copyOf(vertices);
    }

    public List<Arista> adyacentes(String vertice) {
        return adyacencia.getOrDefault(vertice, List.of());
    }
}
