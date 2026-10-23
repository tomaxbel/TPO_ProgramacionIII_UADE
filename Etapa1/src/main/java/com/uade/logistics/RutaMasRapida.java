package com.uade.logistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * F2: ruta más rápida entre dos ubicaciones de la red vial.
 *
 * Estrategia: Greedy (Dijkstra). Siempre se finaliza el vértice no visitado
 * con menor distancia conocida, lo cual es correcto porque los tiempos son
 * positivos. El mínimo se extrae de un min-heap propio con lazy deletion:
 * las entradas obsoletas se descartan al sacarlas.
 */
public class RutaMasRapida {

    private static final int INFINITO = Integer.MAX_VALUE;

    private final RedVial red;

    public RutaMasRapida(RedVial red) {
        this.red = red;
    }

    public ResultadoRuta calcular(String origen, String destino) {
        List<String> traza = new ArrayList<>();

        if (!red.contieneVertice(origen) || !red.contieneVertice(destino)) {
            traza.add("Origen o destino no pertenecen a la red.");
            return ResultadoRuta.sinRuta(traza);
        }

        List<String> vertices = red.getVertices();
        Map<String, Integer> indice = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            indice.put(vertices.get(i), i);
        }
        int n = vertices.size();
        int origenIdx = indice.get(origen);
        int destinoIdx = indice.get(destino);

        int[] dist = new int[n];
        int[] previo = new int[n];
        boolean[] visitado = new boolean[n];
        for (int i = 0; i < n; i++) {
            dist[i] = INFINITO;
            previo[i] = -1;
        }
        dist[origenIdx] = 0;

        MinHeapDijkstra heap = new MinHeapDijkstra();
        heap.insertar(origenIdx, 0);

        while (!heap.vacio()) {
            MinHeapDijkstra.Entry actual = heap.extraerMin();
            int v = actual.vertice();

            if (visitado[v] || actual.distancia() != dist[v]) {
                continue; // entrada obsoleta (lazy deletion)
            }
            visitado[v] = true;
            traza.add("Extraído " + vertices.get(v) + " con distancia " + dist[v]);

            if (v == destinoIdx) {
                break;
            }

            for (Arista arista : red.adyacentes(vertices.get(v))) {
                if (!arista.isHabilitada()) {
                    continue;
                }
                int u = indice.get(arista.getDestino());
                if (visitado[u]) {
                    continue;
                }
                int nuevaDist = dist[v] + arista.getTiempo();
                if (nuevaDist < dist[u]) {
                    dist[u] = nuevaDist;
                    previo[u] = v;
                    heap.insertar(u, nuevaDist);
                    traza.add("Relajada " + arista + ": nueva distancia a " + vertices.get(u) + " = " + nuevaDist);
                } else {
                    traza.add("Arista " + arista + " evaluada: sin mejora (mejor actual a "
                            + vertices.get(u) + " = " + (dist[u] == INFINITO ? "infinito" : dist[u]) + ")");
                }
            }
        }

        if (dist[destinoIdx] == INFINITO) {
            traza.add("Destino no alcanzable desde el origen.");
            return ResultadoRuta.sinRuta(traza);
        }

        List<String> camino = new ArrayList<>();
        int actual = destinoIdx;
        while (actual != -1) {
            camino.add(vertices.get(actual));
            actual = previo[actual];
        }
        java.util.Collections.reverse(camino);

        return new ResultadoRuta(true, camino, dist[destinoIdx], traza);
    }

    /** Min-heap binario propio (vector + sift up/down) con lazy deletion por distancia obsoleta. */
    private static final class MinHeapDijkstra {

        record Entry(int vertice, int distancia) {
        }

        private final List<Entry> datos = new ArrayList<>();

        boolean vacio() {
            return datos.isEmpty();
        }

        void insertar(int vertice, int distancia) {
            datos.add(new Entry(vertice, distancia));
            siftUp(datos.size() - 1);
        }

        Entry extraerMin() {
            Entry min = datos.get(0);
            Entry ultimo = datos.remove(datos.size() - 1);
            if (!datos.isEmpty()) {
                datos.set(0, ultimo);
                siftDown(0);
            }
            return min;
        }

        private void siftUp(int i) {
            while (i > 0) {
                int padre = (i - 1) / 2;
                if (datos.get(padre).distancia() <= datos.get(i).distancia()) {
                    break;
                }
                swap(padre, i);
                i = padre;
            }
        }

        private void siftDown(int i) {
            int n = datos.size();
            while (true) {
                int izq = 2 * i + 1;
                int der = 2 * i + 2;
                int menor = i;
                if (izq < n && datos.get(izq).distancia() < datos.get(menor).distancia()) {
                    menor = izq;
                }
                if (der < n && datos.get(der).distancia() < datos.get(menor).distancia()) {
                    menor = der;
                }
                if (menor == i) {
                    break;
                }
                swap(i, menor);
                i = menor;
            }
        }

        private void swap(int a, int b) {
            Entry tmp = datos.get(a);
            datos.set(a, datos.get(b));
            datos.set(b, tmp);
        }
    }
}
