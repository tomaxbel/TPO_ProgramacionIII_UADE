package com.uade.logistics;

import java.util.Comparator;

public class OrganizadorPedidos {

    private final Pedido[] pedidos;
    private Pedido[] ordenPorIdCache;

    //Comparador para "fecha límite más cercana": fecha ascendente, prioridad descendente, id ascendente.
    private static final Comparator<Pedido> POR_FECHA_LIMITE = (a, b) -> {
        if (a.getFechaLimite() != b.getFechaLimite()) {
            return Integer.compare(a.getFechaLimite(), b.getFechaLimite());
        }
        if (a.getPrioridad() != b.getPrioridad()) {
            return Integer.compare(b.getPrioridad(), a.getPrioridad());
        }
        return Integer.compare(a.getId(), b.getId());
    };

    //Comparador para "prioridad descendente": prioridad descendente, fecha ascendente, id ascendente.
    private static final Comparator<Pedido> POR_PRIORIDAD = (a, b) -> {
        if (a.getPrioridad() != b.getPrioridad()) {
            return Integer.compare(b.getPrioridad(), a.getPrioridad());
        }
        if (a.getFechaLimite() != b.getFechaLimite()) {
            return Integer.compare(a.getFechaLimite(), b.getFechaLimite());
        }
        return Integer.compare(a.getId(), b.getId());
    };

    private static final Comparator<Pedido> POR_ID = (a, b) -> Integer.compare(a.getId(), b.getId());

    public OrganizadorPedidos(Pedido[] pedidos) {
        if (pedidos == null) {
            throw new IllegalArgumentException("pedidos no puede ser null");
        }
        this.pedidos = pedidos.clone();
    }

    //Devuelve una copia ordenada por fecha límite más cercana.
    public Pedido[] ordenarPorFechaLimite() {
        return mergeSort(pedidos.clone(), POR_FECHA_LIMITE);
    }

    //Devuelve una copia ordenada por prioridad descendente.
    public Pedido[] ordenarPorPrioridad() {
        return mergeSort(pedidos.clone(), POR_PRIORIDAD);
    }

    public Pedido buscarPorId(int id) {
        if (ordenPorIdCache == null) {
            ordenPorIdCache = mergeSort(pedidos.clone(), POR_ID);
        }
        int idx = busquedaBinariaPorId(ordenPorIdCache, id);
        return idx == -1 ? null : ordenPorIdCache[idx];
    }


    //Divide y Conquista: merge sort
    private static Pedido[] mergeSort(Pedido[] arr, Comparator<Pedido> cmp) {
        if (arr.length <= 1) {
            return arr;
        }
        int medio = arr.length / 2;
        Pedido[] izquierda = new Pedido[medio];
        Pedido[] derecha = new Pedido[arr.length - medio];
        System.arraycopy(arr, 0, izquierda, 0, medio);
        System.arraycopy(arr, medio, derecha, 0, arr.length - medio);

        izquierda = mergeSort(izquierda, cmp);
        derecha = mergeSort(derecha, cmp);

        return merge(izquierda, derecha, cmp);
    }

    private static Pedido[] merge(Pedido[] izquierda, Pedido[] derecha, Comparator<Pedido> cmp) {
        Pedido[] resultado = new Pedido[izquierda.length + derecha.length];
        int i = 0, j = 0, k = 0;
        while (i < izquierda.length && j < derecha.length) {
            if (cmp.compare(izquierda[i], derecha[j]) <= 0) {
                resultado[k++] = izquierda[i++];
            } else {
                resultado[k++] = derecha[j++];
            }
        }
        while (i < izquierda.length) {
            resultado[k++] = izquierda[i++];
        }
        while (j < derecha.length) {
            resultado[k++] = derecha[j++];
        }
        return resultado;
    }

    //Búsqueda binaria por id
    private static int busquedaBinariaPorId(Pedido[] ordenadoPorId, int id) {
        int lo = 0, hi = ordenadoPorId.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int idMid = ordenadoPorId[mid].getId();
            if (idMid == id) {
                return mid;
            } else if (idMid < id) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return -1;
    }
}
