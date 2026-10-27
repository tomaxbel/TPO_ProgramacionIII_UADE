package com.uade.logistics;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Casos de prueba de las funcionalidades de la Etapa 1 (F1-F3) y Etapa 2 (A1, B3, B4). */
class Pruebas {

    // ---------------- F1: OrganizadorPedidos ----------------

    @Test
    void ordenarPorFechaLimite_desempataPorPrioridadYLuegoId() {
        Pedido[] pedidos = {
                new Pedido(3, "C", 20261125, 5, 10),
                new Pedido(1, "A", 20261125, 5, 10),
                new Pedido(2, "B", 20261125, 7, 10),
                new Pedido(4, "D", 20261120, 1, 10),
        };
        OrganizadorPedidos organizador = new OrganizadorPedidos(pedidos);

        Pedido[] ordenado = organizador.ordenarPorFechaLimite();

        int[] idsEsperados = {4, 2, 1, 3};
        for (int i = 0; i < idsEsperados.length; i++) {
            assertEquals(idsEsperados[i], ordenado[i].getId());
        }
    }

    @Test
    void ordenarPorPrioridad_desempataPorFechaYLuegoId() {
        Pedido[] pedidos = {
                new Pedido(1, "A", 20261130, 5, 10),
                new Pedido(2, "B", 20261120, 5, 10),
                new Pedido(3, "C", 20261120, 5, 10),
                new Pedido(4, "D", 20261101, 9, 10),
        };
        OrganizadorPedidos organizador = new OrganizadorPedidos(pedidos);

        Pedido[] ordenado = organizador.ordenarPorPrioridad();

        int[] idsEsperados = {4, 2, 3, 1};
        for (int i = 0; i < idsEsperados.length; i++) {
            assertEquals(idsEsperados[i], ordenado[i].getId());
        }
    }

    @Test
    void buscarPorId_existenteYInexistente() {
        Pedido[] pedidos = {
                new Pedido(10, "A", 20261125, 5, 10),
                new Pedido(5, "B", 20261126, 3, 20),
                new Pedido(7, "C", 20261127, 1, 30),
        };
        OrganizadorPedidos organizador = new OrganizadorPedidos(pedidos);

        assertEquals(5, organizador.buscarPorId(5).getId());
        assertNull(organizador.buscarPorId(999));
    }

    @Test
    void organizador_listaVaciaYUnElemento() {
        OrganizadorPedidos vacio = new OrganizadorPedidos(new Pedido[0]);
        assertEquals(0, vacio.ordenarPorFechaLimite().length);
        assertNull(vacio.buscarPorId(1));

        Pedido[] unPedido = {new Pedido(1, "A", 20261125, 5, 10)};
        OrganizadorPedidos uno = new OrganizadorPedidos(unPedido);
        assertEquals(1, uno.ordenarPorPrioridad().length);
        assertEquals(1, uno.buscarPorId(1).getId());
    }

    @Test
    void organizador_noModificaElArregloOriginal() {
        Pedido[] pedidos = {
                new Pedido(2, "B", 20261126, 1, 10),
                new Pedido(1, "A", 20261125, 2, 10),
        };
        Pedido[] copiaOriginal = pedidos.clone();
        OrganizadorPedidos organizador = new OrganizadorPedidos(pedidos);

        organizador.ordenarPorFechaLimite();
        organizador.ordenarPorPrioridad();
        organizador.buscarPorId(1);

        assertArrayEquals(copiaOriginal, pedidos);
    }

    // ---------------- F2: RutaMasRapida ----------------

    @Test
    void rutaMasRapida_origenIgualDestino() {
        RedVial red = new RedVial();
        red.agregarArista("A", "B", 5);
        RutaMasRapida calculadora = new RutaMasRapida(red);

        ResultadoRuta resultado = calculadora.calcular("A", "A");

        assertTrue(resultado.alcanzable());
        assertEquals(0, resultado.tiempoTotal());
        assertEquals(List.of("A"), resultado.ubicaciones());
    }

    @Test
    void rutaMasRapida_caminoMinimoVerificadoAMano() {
        RedVial red = new RedVial();
        red.agregarArista("Deposito", "CentroA", 10);
        red.agregarArista("Deposito", "CentroB", 15);
        red.agregarArista("CentroA", "CentroB", 3);
        red.agregarArista("CentroA", "CentroC", 20);
        red.agregarArista("CentroB", "CentroC", 5);
        RutaMasRapida calculadora = new RutaMasRapida(red);

        // A mano: Deposito->A->B->C = 10+3+5 = 18, mejor que Deposito->A->C=30 o Deposito->B->C=20
        ResultadoRuta resultado = calculadora.calcular("Deposito", "CentroC");

        assertTrue(resultado.alcanzable());
        assertEquals(18, resultado.tiempoTotal());
        assertEquals(List.of("Deposito", "CentroA", "CentroB", "CentroC"), resultado.ubicaciones());
    }

    @Test
    void rutaMasRapida_sinRutaTrasDeshabilitarUnicaConexion() {
        RedVial red = new RedVial();
        red.agregarArista("A", "B", 5);
        RutaMasRapida calculadora = new RutaMasRapida(red);

        red.deshabilitarArista("A", "B");
        ResultadoRuta resultado = calculadora.calcular("A", "B");

        assertFalse(resultado.alcanzable());
        assertEquals(-1, resultado.tiempoTotal());
    }

    @Test
    void rutaMasRapida_aristasSonDirigidas() {
        RedVial red = new RedVial();
        red.agregarArista("A", "B", 5);
        RutaMasRapida calculadora = new RutaMasRapida(red);

        ResultadoRuta resultado = calculadora.calcular("B", "A");

        assertFalse(resultado.alcanzable());
    }

    // ---------------- F3: MochilaLotes ----------------

    @Test
    void mochila_casoDeEjemploResueltoAMano() {
        Lote[] lotes = {
                new Lote(1, 10, 60),
                new Lote(2, 20, 100),
                new Lote(3, 30, 120),
        };
        // A mano: capacidad 50 -> lotes 2+3 (peso 50, prioridad 220) es la mejor combinación
        ResultadoMochila resultado = MochilaLotes.seleccionar(lotes, 50);

        assertEquals(220, resultado.prioridadTotal());
        assertEquals(50, resultado.pesoTotal());
        assertEquals(0, resultado.capacidadRestante());
    }

    @Test
    void mochila_capacidadCeroONingunLoteCabe() {
        Lote[] lotes = {new Lote(1, 10, 60)};

        ResultadoMochila resultado = MochilaLotes.seleccionar(lotes, 0);

        assertTrue(resultado.seleccionados().isEmpty());
        assertEquals(0, resultado.prioridadTotal());
    }

    @Test
    void mochila_todosLosLotesCaben() {
        Lote[] lotes = {
                new Lote(1, 5, 10),
                new Lote(2, 5, 20),
        };

        ResultadoMochila resultado = MochilaLotes.seleccionar(lotes, 100);

        assertEquals(2, resultado.seleccionados().size());
        assertEquals(10, resultado.pesoTotal());
        assertEquals(30, resultado.prioridadTotal());
    }

    @Test
    void mochila_pesoSeleccionadoNuncaSuperaCapacidad() {
        Lote[] lotes = {
                new Lote(1, 7, 15),
                new Lote(2, 11, 24),
                new Lote(3, 3, 7),
                new Lote(4, 9, 18),
                new Lote(5, 2, 5),
        };
        int capacidad = 17;

        ResultadoMochila resultado = MochilaLotes.seleccionar(lotes, capacidad);

        assertTrue(resultado.pesoTotal() <= capacidad);
    }

    // ---------------- A1: ConsultasRutas ----------------

    private static RedVial redDeEjemplo() {
        RedVial red = new RedVial();
        red.agregarArista("Deposito", "CentroA", 10);
        red.agregarArista("Deposito", "CentroB", 15);
        red.agregarArista("CentroA", "CentroB", 3);
        red.agregarArista("CentroA", "CentroC", 20);
        red.agregarArista("CentroB", "CentroC", 5);
        red.agregarArista("CentroC", "CentroD", 2);
        return red;
    }

    @Test
    void consultasRutas_coincideConCaminoMinimoCalculadoAMano() {
        ConsultasRutas consultas = new ConsultasRutas(redDeEjemplo());

        // A mano: Deposito->A->B->C->D = 10+3+5+2 = 20
        ResultadoRuta resultado = consultas.consultar("Deposito", "CentroD");

        assertTrue(resultado.alcanzable());
        assertEquals(20, resultado.tiempoTotal());
        assertEquals(List.of("Deposito", "CentroA", "CentroB", "CentroC", "CentroD"), resultado.ubicaciones());
    }

    @Test
    void consultasRutas_tomaElCaminoIndirectoMasCortoEntreDosCentros() {
        ConsultasRutas consultas = new ConsultasRutas(redDeEjemplo());

        // A mano: CentroA->CentroC directo = 20, pero CentroA->CentroB->CentroC = 3+5 = 8, que es mejor.
        ResultadoRuta resultado = consultas.consultar("CentroA", "CentroC");

        assertTrue(resultado.alcanzable());
        assertEquals(8, resultado.tiempoTotal());
    }

    @Test
    void consultasRutas_sinConexionInformaNoAlcanzable() {
        ConsultasRutas consultas = new ConsultasRutas(redDeEjemplo());

        // Las aristas son dirigidas: no hay forma de volver de CentroB a Deposito.
        ResultadoRuta resultado = consultas.consultar("CentroB", "Deposito");

        assertFalse(resultado.alcanzable());
    }

    @Test
    void consultasRutas_origenIgualDestinoEsCero() {
        ConsultasRutas consultas = new ConsultasRutas(redDeEjemplo());

        ResultadoRuta resultado = consultas.consultar("CentroA", "CentroA");

        assertTrue(resultado.alcanzable());
        assertEquals(0, resultado.tiempoTotal());
    }

    // ---------------- B3: RutasAlternativas ----------------

    @Test
    void rutasAlternativas_encuentraTodasLasRutasValidasYLaMejor() {
        RutasAlternativas buscador = new RutasAlternativas(redDeEjemplo());

        // A mano, de Deposito a CentroD: A->B->C->D=20, B->C->D=22, A->C->D=32.
        ResultadoRutasAlternativas resultado = buscador.buscar("Deposito", "CentroD", 100, Set.of());

        assertEquals(3, resultado.rutas().size());
        assertEquals(20, resultado.mejor().tiempoTotal());
    }

    @Test
    void rutasAlternativas_respetaElTiempoMaximo() {
        RutasAlternativas buscador = new RutasAlternativas(redDeEjemplo());

        // Con tiempo máximo 21 solo entra la ruta de 20 (A->B->C->D); las de 22 y 32 se podan.
        ResultadoRutasAlternativas resultado = buscador.buscar("Deposito", "CentroD", 21, Set.of());

        assertEquals(1, resultado.rutas().size());
        assertEquals(20, resultado.rutas().get(0).tiempoTotal());
    }

    @Test
    void rutasAlternativas_respetaUbicacionesProhibidas() {
        RutasAlternativas buscador = new RutasAlternativas(redDeEjemplo());

        // Prohibiendo CentroB solo queda Deposito->A->C->D = 32.
        ResultadoRutasAlternativas resultado = buscador.buscar("Deposito", "CentroD", 100, Set.of("CentroB"));

        assertEquals(1, resultado.rutas().size());
        assertEquals(32, resultado.rutas().get(0).tiempoTotal());
    }

    @Test
    void rutasAlternativas_noRepiteUbicacionesAunConCiclos() {
        RedVial red = redDeEjemplo();
        red.agregarArista("CentroD", "CentroA", 1); // crea un ciclo CentroA..CentroD..CentroA

        RutasAlternativas buscador = new RutasAlternativas(red);
        ResultadoRutasAlternativas resultado = buscador.buscar("Deposito", "CentroD", 1000, Set.of());

        for (RutaEncontrada ruta : resultado.rutas()) {
            assertEquals(ruta.ubicaciones().size(), Set.copyOf(ruta.ubicaciones()).size());
        }
    }

    // ---------------- B4: PlanificacionEntregas ----------------

    @Test
    void planificacionEntregas_casoDeEjemploResueltoAMano() {
        Pedido[] pedidos = {
                new Pedido(1, "Centro Norte", 20261130, 3, 40),
                new Pedido(2, "Centro Sur", 20261125, 5, 30),
                new Pedido(3, "Centro Este", 20261125, 5, 20),
        };
        Vehiculo[] vehiculos = {
                new Vehiculo(1, 60, 100),
                new Vehiculo(2, 50, 80),
        };
        int[][] costoAtencion = {
                {20, 15},
                {18, -1},
                {10, 12},
        };

        // A mano: única combinación que respeta capacidad es P1->V2, P2->V1, P3->V1.
        // Costo = fijoV1(100) + fijoV2(80) + costo(P1,V2)=15 + costo(P2,V1)=18 + costo(P3,V1)=10 = 223.
        ResultadoPlanificacion resultado = new PlanificacionEntregas(pedidos, vehiculos, costoAtencion).planificar();

        assertTrue(resultado.factible());
        assertEquals(223, resultado.costoTotal());
    }

    @Test
    void planificacionEntregas_sinSolucionSiTodasLasAsignacionesEstanProhibidas() {
        Pedido[] pedidos = {new Pedido(1, "Centro Norte", 20261130, 3, 10)};
        Vehiculo[] vehiculos = {new Vehiculo(1, 100, 50)};
        int[][] costoAtencion = {{-1}};

        ResultadoPlanificacion resultado = new PlanificacionEntregas(pedidos, vehiculos, costoAtencion).planificar();

        assertFalse(resultado.factible());
        assertFalse(resultado.criteriosDescarte().isEmpty());
    }

    @Test
    void planificacionEntregas_sinSolucionSiNingunVehiculoTieneCapacidad() {
        Pedido[] pedidos = {new Pedido(1, "Centro Norte", 20261130, 3, 1000)};
        Vehiculo[] vehiculos = {new Vehiculo(1, 50, 20), new Vehiculo(2, 30, 10)};
        int[][] costoAtencion = {{5, 5}};

        ResultadoPlanificacion resultado = new PlanificacionEntregas(pedidos, vehiculos, costoAtencion).planificar();

        assertFalse(resultado.factible());
    }

    @Test
    void planificacionEntregas_costoFijoSeContabilizaUnaSolaVezPorVehiculo() {
        Pedido[] pedidos = {
                new Pedido(1, "Centro Norte", 20261130, 3, 10),
                new Pedido(2, "Centro Sur", 20261125, 5, 10),
        };
        Vehiculo[] vehiculos = {new Vehiculo(1, 100, 50)};
        int[][] costoAtencion = {{5}, {7}};

        ResultadoPlanificacion resultado = new PlanificacionEntregas(pedidos, vehiculos, costoAtencion).planificar();

        assertTrue(resultado.factible());
        assertEquals(62, resultado.costoTotal()); // 50 (fijo, una sola vez) + 5 + 7
    }
}
