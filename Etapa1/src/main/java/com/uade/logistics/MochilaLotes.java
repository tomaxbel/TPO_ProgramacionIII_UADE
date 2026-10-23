package com.uade.logistics;

import java.util.ArrayList;
import java.util.List;

/**
 * F3: selección de cargas prioritarias (mochila 0/1).
 *
 * Estrategia: Programación Dinámica. Se construye la tabla clásica
 * V[i][c] = mayor prioridad alcanzable usando los primeros i lotes con
 * capacidad c, con la recurrencia:
 *   V[0][c] = 0
 *   V[i][c] = V[i-1][c]                                           si peso(i) > c
 *   V[i][c] = max(V[i-1][c], V[i-1][c-peso(i)] + prioridad(i))    en otro caso
 * Tiempo y espacio O(n * C). No se usa un criterio Greedy (por ejemplo
 * razón prioridad/peso) porque en la variante 0/1 no garantiza el óptimo:
 * un lote "peor" por razón puede ser necesario para completar mejor la
 * capacidad disponible.
 */
public class MochilaLotes {

    public static ResultadoMochila seleccionar(Lote[] lotes, int capacidad) {
        if (lotes == null) {
            throw new IllegalArgumentException("lotes no puede ser null");
        }
        if (capacidad < 0) {
            throw new IllegalArgumentException("capacidad no puede ser negativa");
        }

        int n = lotes.length;
        int[][] v = new int[n + 1][capacidad + 1];

        for (int i = 1; i <= n; i++) {
            int peso = lotes[i - 1].getPeso();
            int prioridad = lotes[i - 1].getPrioridad();
            for (int c = 0; c <= capacidad; c++) {
                if (peso > c) {
                    v[i][c] = v[i - 1][c];
                } else {
                    v[i][c] = Math.max(v[i - 1][c], v[i - 1][c - peso] + prioridad);
                }
            }
        }

        List<Lote> seleccionados = new ArrayList<>();
        int c = capacidad;
        for (int i = n; i > 0; i--) {
            if (v[i][c] != v[i - 1][c]) {
                Lote lote = lotes[i - 1];
                seleccionados.add(lote);
                c -= lote.getPeso();
            }
        }
        java.util.Collections.reverse(seleccionados);

        int pesoTotal = 0;
        for (Lote lote : seleccionados) {
            pesoTotal += lote.getPeso();
        }

        return new ResultadoMochila(seleccionados, pesoTotal, v[n][capacidad], capacidad - pesoTotal);
    }
}
