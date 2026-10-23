# TPO Programación III — Propuesta 2 (Rutas y Distribución de Ayuda)

Repo del trabajo práctico obligatorio de Programación III. Elegimos la Propuesta 2:
un sistema de distribución de ayuda que organiza pedidos, calcula rutas y selecciona
cargas para los vehículos.

Por ahora está la Etapa 1: las tres funcionalidades obligatorias.

## Por dónde arrancar a revisar

1. **`Informe Trabajo Practico.pdf`** — acá está todo explicado: qué estrategia
   usamos en cada funcionalidad, por qué, y cómo lo probamos.
2. **`Etapa1/src/main/java/com/uade/logistics/`** — el código. Los nombres de las
   clases son bastante directos:
   - `Pedido.java` / `OrganizadorPedidos.java` → ordenar y buscar pedidos (merge
     sort y búsqueda binaria hechos a mano).
   - `RedVial.java` / `Arista.java` / `RutaMasRapida.java` → ruta más rápida con
     Dijkstra.
   - `Lote.java` / `MochilaLotes.java` → selección de cargas con mochila 0/1
     (programación dinámica).
   - `Main.java` → corre una demo de las tres funcionalidades juntas.
3. **`Etapa1/src/test/java/com/uade/logistics/Pruebas.java`** — los casos de
   prueba (13 en total, con JUnit).

## Cómo correrlo

Hace falta tener Java 21 y Maven instalados. Parados en `Etapa1/`:

```bash
mvn test          # corre los tests
mvn exec:java      # corre la demo (Main)
```
