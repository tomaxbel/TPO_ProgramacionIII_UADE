package com.uade.logistics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * B3: rutas alternativas con restricciones.
 *
 * Estrategia: Backtracking (enumeración con poda). A diferencia de F2 (que busca
 * UNA ruta óptima), acá la consigna pide TODOS los recorridos válidos, así que
 * un algoritmo greedy tipo Dijkstra no sirve: hay que explorar el espacio de
 * caminos. Se arma el camino vértice a vértice con DFS, y se poda apenas una
 * rama deja de ser viable:
 *   - el tiempo acumulado ya superó el tiempo máximo,
 *   - la próxima ubicación está prohibida,
 *   - la próxima ubicación ya fue visitada en este camino (no se repiten
 *     ubicaciones).
 * Al llegar al destino se registra el camino encontrado y se retrocede
 * (backtrack) para seguir probando otras ramas.
 */
public class RutasAlternativas {

    private final RedVial red;

    public RutasAlternativas(RedVial red) {
        this.red = red;
    }

    public ResultadoRutasAlternativas buscar(String origen, String destino, int tiempoMaximo, Set<String> prohibidas) {
        List<RutaEncontrada> resultados = new ArrayList<>();
        if (!red.contieneVertice(origen) || !red.contieneVertice(destino)) {
            return new ResultadoRutasAlternativas(resultados, null);
        }

        LinkedList<String> caminoActual = new LinkedList<>();
        Set<String> visitados = new HashSet<>();
        caminoActual.add(origen);
        visitados.add(origen);

        backtrack(origen, destino, tiempoMaximo, prohibidas, caminoActual, visitados, 0, resultados);

        RutaEncontrada mejor = resultados.stream()
                .min(Comparator.comparingInt(RutaEncontrada::tiempoTotal))
                .orElse(null);

        return new ResultadoRutasAlternativas(resultados, mejor);
    }

    private void backtrack(String actual, String destino, int tiempoMaximo, Set<String> prohibidas,
                            LinkedList<String> caminoActual, Set<String> visitados, int tiempoAcumulado,
                            List<RutaEncontrada> resultados) {
        if (actual.equals(destino)) {
            resultados.add(new RutaEncontrada(List.copyOf(caminoActual), tiempoAcumulado));
            return;
        }

        for (Arista arista : red.adyacentes(actual)) {
            if (!arista.isHabilitada()) {
                continue;
            }
            String siguiente = arista.getDestino();

            if (prohibidas.contains(siguiente)) {
                continue; // poda: ubicación prohibida
            }
            if (visitados.contains(siguiente)) {
                continue; // poda: no se repiten ubicaciones
            }
            int nuevoTiempo = tiempoAcumulado + arista.getTiempo();
            if (nuevoTiempo > tiempoMaximo) {
                continue; // poda: se excede el tiempo máximo
            }

            visitados.add(siguiente);
            caminoActual.add(siguiente);

            backtrack(siguiente, destino, tiempoMaximo, prohibidas, caminoActual, visitados, nuevoTiempo, resultados);

            caminoActual.removeLast();
            visitados.remove(siguiente);
        }
    }
}
