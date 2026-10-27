package com.uade.logistics;

import java.util.Set;

/** Demo de las funcionalidades de la Etapa 1 (F1, F2, F3) y la Etapa 2 (A1, B3, B4). */
public class Main {

    public static void main(String[] args) {
        demoF1();
        demoF2();
        demoF3();
        demoA1();
        demoB3();
        demoB4();
    }

    private static void demoF1() {
        System.out.println("=== F1: Organización y consulta de pedidos ===");

        Pedido[] pedidos = {
                new Pedido(1, "Centro Norte", 20261130, 3, 120),
                new Pedido(2, "Centro Sur", 20261125, 5, 80),
                new Pedido(3, "Centro Este", 20261125, 5, 60),
                new Pedido(4, "Centro Oeste", 20261201, 1, 200),
                new Pedido(5, "Centro Norte", 20261125, 2, 40),
        };

        OrganizadorPedidos organizador = new OrganizadorPedidos(pedidos);

        System.out.println("-- Por fecha límite más cercana --");
        for (Pedido p : organizador.ordenarPorFechaLimite()) {
            System.out.println("  " + p);
        }

        System.out.println("-- Por prioridad descendente --");
        for (Pedido p : organizador.ordenarPorPrioridad()) {
            System.out.println("  " + p);
        }

        System.out.println("-- Búsquedas por id --");
        System.out.println("  id=3 -> " + organizador.buscarPorId(3));
        System.out.println("  id=99 -> " + organizador.buscarPorId(99));
        System.out.println();
    }

    private static void demoF2() {
        System.out.println("=== F2: Ruta más rápida ===");

        RedVial red = new RedVial();
        red.agregarArista("Deposito", "CentroA", 10);
        red.agregarArista("Deposito", "CentroB", 15);
        red.agregarArista("CentroA", "CentroB", 3);
        red.agregarArista("CentroA", "CentroC", 20);
        red.agregarArista("CentroB", "CentroC", 5);
        red.agregarArista("CentroC", "CentroD", 2);

        RutaMasRapida calculadora = new RutaMasRapida(red);

        ResultadoRuta resultado = calculadora.calcular("Deposito", "CentroD");
        System.out.println("-- Deposito -> CentroD --");
        imprimirRuta(resultado);

        System.out.println("-- Deshabilitando CentroB -> CentroC --");
        red.deshabilitarArista("CentroB", "CentroC");
        ResultadoRuta resultadoTrasBaja = calculadora.calcular("Deposito", "CentroD");
        imprimirRuta(resultadoTrasBaja);

        System.out.println("-- Caso sin ruta (destino aislado) --");
        red.agregarVertice("CentroAislado");
        ResultadoRuta sinRuta = calculadora.calcular("Deposito", "CentroAislado");
        imprimirRuta(sinRuta);
        System.out.println();
    }

    private static void imprimirRuta(ResultadoRuta resultado) {
        if (!resultado.alcanzable()) {
            System.out.println("  Sin ruta disponible.");
        } else {
            System.out.println("  Ruta: " + String.join(" -> ", resultado.ubicaciones()));
            System.out.println("  Tiempo total: " + resultado.tiempoTotal());
        }
        System.out.println("  Traza:");
        for (String paso : resultado.traza()) {
            System.out.println("    " + paso);
        }
    }

    private static void demoF3() {
        System.out.println("=== F3: Selección de cargas prioritarias ===");

        Lote[] lotes = {
                new Lote(1, 10, 60),
                new Lote(2, 20, 100),
                new Lote(3, 30, 120),
                new Lote(4, 5, 30),
        };
        int capacidad = 50;

        ResultadoMochila resultado = MochilaLotes.seleccionar(lotes, capacidad);

        System.out.println("-- Capacidad del vehículo: " + capacidad + " --");
        System.out.println("  Seleccionados:");
        for (Lote lote : resultado.seleccionados()) {
            System.out.println("    " + lote);
        }
        System.out.println("  Peso total: " + resultado.pesoTotal());
        System.out.println("  Prioridad total: " + resultado.prioridadTotal());
        System.out.println("  Capacidad restante: " + resultado.capacidadRestante());
        System.out.println();
    }

    private static void demoA1() {
        System.out.println("=== A1: Consultas de rutas entre todos los centros ===");

        RedVial red = new RedVial();
        red.agregarArista("Deposito", "CentroA", 10);
        red.agregarArista("Deposito", "CentroB", 15);
        red.agregarArista("CentroA", "CentroB", 3);
        red.agregarArista("CentroA", "CentroC", 20);
        red.agregarArista("CentroB", "CentroC", 5);
        red.agregarArista("CentroC", "CentroD", 2);

        ConsultasRutas consultas = new ConsultasRutas(red);

        System.out.println("-- Deposito -> CentroD --");
        imprimirRuta(consultas.consultar("Deposito", "CentroD"));

        System.out.println("-- CentroB -> Deposito (sin conexión, aristas dirigidas) --");
        imprimirRuta(consultas.consultar("CentroB", "Deposito"));
        System.out.println();
    }

    private static void demoB3() {
        System.out.println("=== B3: Rutas alternativas con restricciones ===");

        RedVial red = new RedVial();
        red.agregarArista("Deposito", "CentroA", 10);
        red.agregarArista("Deposito", "CentroB", 15);
        red.agregarArista("CentroA", "CentroB", 3);
        red.agregarArista("CentroA", "CentroC", 20);
        red.agregarArista("CentroB", "CentroC", 5);
        red.agregarArista("CentroC", "CentroD", 2);

        RutasAlternativas buscador = new RutasAlternativas(red);
        ResultadoRutasAlternativas resultado = buscador.buscar("Deposito", "CentroD", 30, Set.of());

        System.out.println("-- Todas las rutas Deposito -> CentroD con tiempo máximo 30 --");
        for (RutaEncontrada ruta : resultado.rutas()) {
            System.out.println("  " + String.join(" -> ", ruta.ubicaciones()) + " (tiempo " + ruta.tiempoTotal() + ")");
        }
        System.out.println("  Mejor: " + resultado.mejor());
        System.out.println();
    }

    private static void demoB4() {
        System.out.println("=== B4: Planificación de entregas con menor costo ===");

        Pedido[] pedidos = {
                new Pedido(1, "Centro Norte", 20261130, 3, 40),
                new Pedido(2, "Centro Sur", 20261125, 5, 30),
                new Pedido(3, "Centro Este", 20261125, 5, 20),
        };
        Vehiculo[] vehiculos = {
                new Vehiculo(1, 60, 100),
                new Vehiculo(2, 50, 80),
        };
        // costoAtencion[pedido][vehiculo], -1 = prohibido
        int[][] costoAtencion = {
                {20, 15},
                {18, -1},
                {10, 12},
        };

        PlanificacionEntregas planificador = new PlanificacionEntregas(pedidos, vehiculos, costoAtencion);
        ResultadoPlanificacion resultado = planificador.planificar();

        if (!resultado.factible()) {
            System.out.println("  No existe una distribución válida.");
        } else {
            System.out.println("  Asignaciones: " + resultado.asignaciones());
            System.out.println("  Costo total: " + resultado.costoTotal());
        }
        System.out.println("  Criterios de descarte aplicados: " + resultado.criteriosDescarte().size());
        for (String criterio : resultado.criteriosDescarte()) {
            System.out.println("    " + criterio);
        }
    }
}
