package com.uade.logistics;

import java.util.ArrayList;
import java.util.List;

/**
 * B4: planificación de entregas con menor costo.
 *
 * Estrategia: Backtracking con poda (branch and bound). Se prueba, para cada
 * pedido en orden, asignarlo a cada vehículo disponible; el costo fijo de un
 * vehículo se suma una única vez, la primera vez que recibe un pedido. Se
 * poda una rama en tres casos, que es exactamente lo que pide la consigna
 * ("evitar continuar con asignaciones parciales que no puedan completar una
 * solución válida o mejorar la mejor solución encontrada"):
 *   1. La asignación pedido-vehículo está prohibida -> no puede completar una
 *      solución válida.
 *   2. El vehículo no tiene capacidad para ese pedido -> ídem.
 *   3. El costo parcial acumulado ya iguala o supera el costo de la mejor
 *      solución completa encontrada hasta el momento -> no puede mejorarla
 *      (el costo es monótono no decreciente a medida que se asignan más
 *      pedidos, así que esta cota es válida: ninguna extensión de una rama
 *      ya más cara puede terminar siendo más barata).
 *
 * No se usa Programación Dinámica porque el estado relevante (qué vehículos
 * están "abiertos" y cuánta capacidad les queda) no se reduce a una tabla de
 * tamaño manejable sin perder la cota de poda; y no es un problema greedy
 * porque la asignación localmente más barata para un pedido puede dejar sin
 * capacidad a un vehículo que hacía falta para pedidos posteriores.
 */
public class PlanificacionEntregas {

    private static final int PROHIBIDO = -1;

    private final Pedido[] pedidos;
    private final Vehiculo[] vehiculos;
    private final int[][] costoAtencion; // costoAtencion[pedidoIdx][vehiculoIdx], PROHIBIDO si no permitido

    private int[] cargaVehiculo;
    private boolean[] vehiculoUsado;
    private int[] asignacionActual;
    private int[] mejorAsignacion;
    private int mejorCosto;
    private final List<String> criteriosDescarte = new ArrayList<>();

    public PlanificacionEntregas(Pedido[] pedidos, Vehiculo[] vehiculos, int[][] costoAtencion) {
        if (costoAtencion.length != pedidos.length || (pedidos.length > 0 && costoAtencion[0].length != vehiculos.length)) {
            throw new IllegalArgumentException("costoAtencion debe ser de tamaño pedidos.length x vehiculos.length");
        }
        this.pedidos = pedidos;
        this.vehiculos = vehiculos;
        this.costoAtencion = costoAtencion;
    }

    public ResultadoPlanificacion planificar() {
        cargaVehiculo = new int[vehiculos.length];
        vehiculoUsado = new boolean[vehiculos.length];
        asignacionActual = new int[pedidos.length];
        mejorAsignacion = null;
        mejorCosto = Integer.MAX_VALUE;
        criteriosDescarte.clear();

        backtrack(0, 0);

        if (mejorAsignacion == null) {
            return ResultadoPlanificacion.sinSolucion(List.copyOf(criteriosDescarte));
        }

        List<AsignacionPedido> asignaciones = new ArrayList<>();
        for (int p = 0; p < pedidos.length; p++) {
            int v = mejorAsignacion[p];
            asignaciones.add(new AsignacionPedido(pedidos[p].getId(), vehiculos[v].getId(), costoAtencion[p][v]));
        }
        return new ResultadoPlanificacion(true, asignaciones, mejorCosto, List.copyOf(criteriosDescarte));
    }

    private void backtrack(int pedidoIdx, int costoActual) {
        if (pedidoIdx == pedidos.length) {
            mejorCosto = costoActual;
            mejorAsignacion = asignacionActual.clone();
            return;
        }

        Pedido pedido = pedidos[pedidoIdx];
        for (int v = 0; v < vehiculos.length; v++) {
            int costo = costoAtencion[pedidoIdx][v];
            if (costo == PROHIBIDO) {
                criteriosDescarte.add("Pedido " + pedido.getId() + " -> Vehiculo " + vehiculos[v].getId()
                        + ": asignación prohibida.");
                continue;
            }
            if (cargaVehiculo[v] + pedido.getPesoTotal() > vehiculos[v].getCapacidad()) {
                criteriosDescarte.add("Pedido " + pedido.getId() + " -> Vehiculo " + vehiculos[v].getId()
                        + ": excede capacidad (carga actual " + cargaVehiculo[v] + " + peso " + pedido.getPesoTotal()
                        + " > capacidad " + vehiculos[v].getCapacidad() + ").");
                continue;
            }

            int costoFijoASumar = vehiculoUsado[v] ? 0 : vehiculos[v].getCostoFijo();
            int nuevoCosto = costoActual + costo + costoFijoASumar;
            if (nuevoCosto >= mejorCosto) {
                criteriosDescarte.add("Pedido " + pedido.getId() + " -> Vehiculo " + vehiculos[v].getId()
                        + ": costo parcial " + nuevoCosto + " ya no puede mejorar la mejor solución ("
                        + mejorCosto + ").");
                continue;
            }

            boolean eraUsado = vehiculoUsado[v];
            cargaVehiculo[v] += pedido.getPesoTotal();
            vehiculoUsado[v] = true;
            asignacionActual[pedidoIdx] = v;

            backtrack(pedidoIdx + 1, nuevoCosto);

            asignacionActual[pedidoIdx] = -1;
            vehiculoUsado[v] = eraUsado;
            cargaVehiculo[v] -= pedido.getPesoTotal();
        }
    }
}
