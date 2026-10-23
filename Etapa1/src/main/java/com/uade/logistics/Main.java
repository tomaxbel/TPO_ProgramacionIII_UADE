package com.uade.logistics;

//Demo de las tres funcionalidades obligatorias de la primera entrega.
public class Main {

    public static void main(String[] args) {
        demoF1();
        demoF2();
        demoF3();
    }

    private static void demoF1() {
        System.out.println("Organizacion y consulta de pedidos:");

        Pedido[] pedidos = {
                new Pedido(1, "Centro Norte", 20261130, 3, 120),
                new Pedido(2, "Centro Sur", 20261125, 5, 80),
                new Pedido(3, "Centro Este", 20261125, 5, 60),
                new Pedido(4, "Centro Oeste", 20261201, 1, 200),
                new Pedido(5, "Centro Norte", 20261125, 2, 40),
        };

        OrganizadorPedidos organizador = new OrganizadorPedidos(pedidos);

        System.out.println("--Por fecha limite mas cercana--");
        for (Pedido p : organizador.ordenarPorFechaLimite()) {
            System.out.println("  " + p);
        }

        System.out.println("--Por prioridad descendente--");
        for (Pedido p : organizador.ordenarPorPrioridad()) {
            System.out.println("  " + p);
        }

        System.out.println("--Busquedas por id--");
        System.out.println("  id=3 : " + organizador.buscarPorId(3));
        System.out.println("  id=99 : " + organizador.buscarPorId(99));
        System.out.println();
    }

    private static void demoF2() {
        System.out.println("Ruta mas rapida: ");

        RedVial red = new RedVial();
        red.agregarArista("Deposito", "CentroA", 10);
        red.agregarArista("Deposito", "CentroB", 15);
        red.agregarArista("CentroA", "CentroB", 3);
        red.agregarArista("CentroA", "CentroC", 20);
        red.agregarArista("CentroB", "CentroC", 5);
        red.agregarArista("CentroC", "CentroD", 2);

        RutaMasRapida calculadora = new RutaMasRapida(red);

        ResultadoRuta resultado = calculadora.calcular("Deposito", "CentroD");
        System.out.println("--Deposito -> CentroD--");
        imprimirRuta(resultado);

        System.out.println("--Deshabilitando CentroB -> CentroC--");
        red.deshabilitarArista("CentroB", "CentroC");
        ResultadoRuta resultadoTrasBaja = calculadora.calcular("Deposito", "CentroD");
        imprimirRuta(resultadoTrasBaja);

        System.out.println("--Caso sin ruta (destino aislado)--");
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
        System.out.println("Seleccion de cargas prioritarias: ");

        Lote[] lotes = {
                new Lote(1, 10, 60),
                new Lote(2, 20, 100),
                new Lote(3, 30, 120),
                new Lote(4, 5, 30),
        };
        int capacidad = 50;

        ResultadoMochila resultado = MochilaLotes.seleccionar(lotes, capacidad);

        System.out.println("--Capacidad del vehiculo: " + capacidad + " --");
        System.out.println("  Seleccionados:");
        for (Lote lote : resultado.seleccionados()) {
            System.out.println("    " + lote);
        }
        System.out.println("  Peso total: " + resultado.pesoTotal());
        System.out.println("  Prioridad total: " + resultado.prioridadTotal());
        System.out.println("  Capacidad restante: " + resultado.capacidadRestante());
    }
}