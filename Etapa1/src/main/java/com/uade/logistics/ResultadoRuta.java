package com.uade.logistics;

import java.util.List;

/**
 * Resultado de calcular la ruta más rápida entre dos ubicaciones.
 * Si no es alcanzable, ubicaciones queda vacío y tiempoTotal es -1
 */
public record ResultadoRuta(boolean alcanzable, List<String> ubicaciones, int tiempoTotal, List<String> traza) {

    public static ResultadoRuta sinRuta(List<String> traza) {
        return new ResultadoRuta(false, List.of(), -1, traza);
    }
}
