package com.uade.logistics;

import java.util.List;

public record ResultadoPlanificacion(boolean factible, List<AsignacionPedido> asignaciones, int costoTotal,
                                      List<String> criteriosDescarte) {

    public static ResultadoPlanificacion sinSolucion(List<String> criteriosDescarte) {
        return new ResultadoPlanificacion(false, List.of(), -1, criteriosDescarte);
    }
}
