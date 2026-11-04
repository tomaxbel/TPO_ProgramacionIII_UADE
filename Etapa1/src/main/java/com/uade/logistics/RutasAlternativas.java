package com.uade.logistics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
                continue; //poda: ubi prohibida
            }
            if (visitados.contains(siguiente)) {
                continue; //poda: no se repiten ubis
            }
            int nuevoTiempo = tiempoAcumulado + arista.getTiempo();
            if (nuevoTiempo > tiempoMaximo) {
                continue; //poda: se excede el tiempo max
            }

            visitados.add(siguiente);
            caminoActual.add(siguiente);

            backtrack(siguiente, destino, tiempoMaximo, prohibidas, caminoActual, visitados, nuevoTiempo, resultados);

            caminoActual.removeLast();
            visitados.remove(siguiente);
        }
    }
}
