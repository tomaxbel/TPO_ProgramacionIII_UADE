package com.uade.logistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//A1: consultas de rutas entre todos los centros, con Floyd-Warshall (Programacion Dinamica).
public class ConsultasRutas {

    private static final int INFINITO = Integer.MAX_VALUE / 2;

    private final List<String> vertices;
    private final Map<String, Integer> indice;
    private final int[][] dist;
    private final int[][] siguiente;

    public ConsultasRutas(RedVial red) {
        this.vertices = red.getVertices();
        int n = vertices.size();
        this.indice = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indice.put(vertices.get(i), i);
        }

        this.dist = new int[n][n];
        this.siguiente = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = (i == j) ? 0 : INFINITO;
                siguiente[i][j] = -1;
            }
        }

        for (String origen : vertices) {
            int i = indice.get(origen);
            for (Arista arista : red.adyacentes(origen)) {
                if (!arista.isHabilitada()) {
                    continue;
                }
                int j = indice.get(arista.getDestino());
                if (arista.getTiempo() < dist[i][j]) {
                    dist[i][j] = arista.getTiempo();
                    siguiente[i][j] = j;
                }
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] >= INFINITO) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] >= INFINITO) {
                        continue;
                    }
                    int porK = dist[i][k] + dist[k][j];
                    if (porK < dist[i][j]) {
                        dist[i][j] = porK;
                        siguiente[i][j] = siguiente[i][k];
                    }
                }
            }
        }
    }

    //Tiempo minimo y camino reconstruido entre origen y destino.
    public ResultadoRuta consultar(String origen, String destino) {
        if (!indice.containsKey(origen) || !indice.containsKey(destino)) {
            return ResultadoRuta.sinRuta(List.of("Origen o destino no pertenecen a la red."));
        }
        int i = indice.get(origen);
        int j = indice.get(destino);

        if (dist[i][j] >= INFINITO) {
            return ResultadoRuta.sinRuta(List.of("No existe conexion entre " + origen + " y " + destino + "."));
        }

        List<String> camino = new ArrayList<>();
        int actual = i;
        camino.add(vertices.get(actual));
        while (actual != j) {
            actual = siguiente[actual][j];
            camino.add(vertices.get(actual));
        }

        return new ResultadoRuta(true, camino, dist[i][j],
                List.of("Consulta resuelta en O(1) + O(V) sobre la matriz precomputada por Floyd-Warshall."));
    }
}
