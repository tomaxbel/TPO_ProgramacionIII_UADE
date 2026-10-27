<p align="center">
  <img src="assets/uade-logo.png" alt="UADE" width="220">
</p>

<h1 align="center">Trabajo Práctico Obligatorio</h1>
<h2 align="center">Programación III</h2>
<h3 align="center">Propuesta 2 — Planificación de Rutas y Distribución de Ayuda</h3>
<h3 align="center">Informe Parcial — Etapa 1</h3>

<br>

**Carrera:** Ingeniería en Informática
**Materia:** Programación III
**Turno/Comisión:** Viernes Tarde
**Profesora:** Leon Maria Angela

**Integrantes:**
- Belanger, Tomás — 1192961
- Piñango, Antonio — 1161399
- Soto, Juan Pablo — 1194889

**Fecha de Entrega:** 23/11/2026

---

# Informe — Etapa 1: Planificación de Rutas y Distribución de Ayuda

Propuesta 2. Este informe cubre las tres funcionalidades obligatorias de la Etapa 1
— organización y consulta de pedidos (F1), ruta más rápida (F2) y selección de
cargas prioritarias (F3) — y las tres funcionalidades elegidas para la Etapa 2:
consultas de rutas entre todos los centros (A1), rutas alternativas con
restricciones (B3) y planificación de entregas con menor costo (B4).

## F1. Organización y consulta de pedidos

### Análisis del problema

Cada `Pedido` tiene `id`, `centroDestino`, `fechaLimite` (entero `AAAAMMDD`, para que
el orden numérico coincida con el orden cronológico sin parsear fechas), `prioridad`
y `pesoTotal`. Se necesitan dos órdenes totales distintos sobre el mismo conjunto
(fecha límite ascendente, prioridad descendente) con reglas de desempate explícitas,
y una búsqueda puntual por `id`. El volumen de pedidos crece con el tiempo, así que
el costo de ordenar y buscar debe escalar mejor que lineal/cuadrático.

No se permite usar `Arrays.sort`, `Collections.sort` ni `Arrays.binarySearch`: hay
que implementar el ordenamiento y la búsqueda a mano.

### Estrategia elegida y justificación

**Divide y Conquista** para ambos órdenes, vía **merge sort**. Se descartó un
ordenamiento cuadrático (inserción, burbuja) porque no escala con "cantidades
crecientes de pedidos" como pide la consigna; **merge sort** garantiza O(n log n)
en el **peor caso** (a diferencia de quicksort, que degrada a O(n²)) y es **estable**,
lo cual simplifica el desempate: alcanza con una comparación compuesta sin necesidad
de lógica adicional para preservar el orden relativo en caso de empate total.

Para la búsqueda por `id` se usa **búsqueda binaria**, O(log n), que requiere el
arreglo ordenado por `id`. Como ese orden no es ninguno de los dos pedidos por el
enunciado, se calcula aparte y se cachea: la primera búsqueda paga el ordenamiento
(O(n log n)); las siguientes, mientras el conjunto de pedidos no cambie, solo pagan
O(log n).

### Organización de los datos

- Para los dos listados: no se requiere estructura auxiliar persistente, el merge
  sort trabaja sobre copias del arreglo de entrada en cada llamada.
- Para la búsqueda: un arreglo ordenado por `id`, calculado una sola vez y reutilizado
  (atributo de instancia `ordenPorIdCache`, invalidado implícitamente porque
  `OrganizadorPedidos` encapsula una copia inmutable del conjunto de pedidos recibido
  en el constructor).

### Complejidad temporal

| Operación | Complejidad |
|---|---|
| `ordenarPorFechaLimite` / `ordenarPorPrioridad` | O(n log n) tiempo, O(n) espacio auxiliar |
| `buscarPorId` (primera vez) | O(n log n) |
| `buscarPorId` (subsiguientes, sin cambios) | O(log n) |

### Resultados de las pruebas

Ver `Pruebas.java` (`ordenarPorFechaLimite_desempataPorPrioridadYLuegoId`,
`ordenarPorPrioridad_desempataPorFechaYLuegoId`, `buscarPorId_existenteYInexistente`,
`organizador_listaVaciaYUnElemento`, `organizador_noModificaElArregloOriginal`).
13/13 pruebas pasan (`mvn test`). Se verificó a mano que los desempates (prioridad
luego id para el orden por fecha; fecha luego id para el orden por prioridad)
coinciden con el orden esperado, que listas vacías y de un elemento no rompen el
algoritmo, y que el arreglo original pasado al constructor nunca se modifica.

---

## F2. Ruta más rápida

### Análisis del problema

La red vial es un grafo **dirigido** con tiempos de viaje **positivos** y fijos
durante cada consulta. Hay que encontrar el camino de menor tiempo total entre un
origen y un destino, poder deshabilitar una conexión y recalcular, informar cuando
no hay ruta, y exponer las decisiones principales tomadas durante el cálculo.

### Estrategia elegida y justificación

**Greedy**, mediante el **algoritmo de Dijkstra**. En cada paso se "cierra"
(finaliza) el vértice no visitado con menor distancia tentativa conocida, sin
reconsiderar esa elección nunca más. Esa decisión greedy es **correcta** —no solo
una heurística— precisamente porque todos los pesos de las aristas son positivos:
no puede existir un camino más corto hacia ese vértice pasando por un vértice aún
no cerrado, dado que cualquier camino alternativo solo podría sumar más tiempo.

Se descartó **Bellman-Ford** (admite pesos negativos, pero es O(V·E), innecesario
acá) y una búsqueda exhaustiva tipo backtracking (explota combinatoriamente y no
hace falta, porque no se piden *todos* los caminos sino el mínimo).

La extracción del vértice de menor distancia se implementa con un **min-heap
binario propio** (arreglo dinámico + `siftUp`/`siftDown`), en vez de
`java.util.PriorityQueue`, con **lazy deletion**: al mejorar la distancia de un
vértice no se busca ni actualiza su entrada existente en el heap (costoso), sino
que se inserta una entrada nueva; las entradas obsoletas se descartan al
extraerlas, comparando su distancia contra la mejor distancia conocida en ese
momento (`dist[v]`) y contra si el vértice ya fue visitado.

Deshabilitar una conexión solo marca la arista (`habilitada = false`) sin tocar la
estructura del grafo; Dijkstra ignora las aristas deshabilitadas al relajar, así
que un recálculo es simplemente volver a correr el algoritmo sobre el mismo grafo.

### Organización de los datos

- `RedVial`: lista de adyacencia (`Map<String, List<Arista>>`) más una lista de
  vértices para asignar índices 0..n-1 usados en los arreglos `dist`, `previo` y
  `visitado` de Dijkstra.
- `Arista`: origen, destino, tiempo y la bandera `habilitada`, mutable para poder
  deshabilitar/habilitar sin reconstruir el grafo.
- Traza: lista de strings que registra cada extracción del heap y cada relajación
  (mejoró o no), expuesta en `ResultadoRuta.traza()`.

### Complejidad temporal

Con el min-heap propio: **O((V + E) log V)** — cada vértice se inserta en el heap
al menos una vez por cada relajación exitosa que lo mejora (como máximo una vez por
arista entrante), y cada inserción/extracción del heap es O(log V) (V = cantidad de
entradas acumuladas en el heap, que en el peor caso es O(E)). Espacio: O(V + E) para
el grafo más O(V) para `dist`/`previo`/`visitado` y O(E) en el peor caso para el heap.

### Resultados de las pruebas

Ver `Pruebas.java` (`rutaMasRapida_origenIgualDestino`,
`rutaMasRapida_caminoMinimoVerificadoAMano`,
`rutaMasRapida_sinRutaTrasDeshabilitarUnicaConexion`,
`rutaMasRapida_aristasSonDirigidas`). Se verificó a mano, sobre el grafo de ejemplo
de `Main.java` (Depósito, CentroA, CentroB, CentroC, CentroD), que el camino mínimo
Depósito→CentroD es Depósito→A→B→C→D con tiempo 20 (10+3+5+2), mejor que las
alternativas Depósito→A→C→D (10+20+2=32) o Depósito→B→C→D (15+5+2=22); y que tras
deshabilitar B→C, el nuevo mínimo pasa a ser Depósito→A→C→D (32). También se
verificó el caso origen = destino (tiempo 0), el caso sin ruta disponible, y que las
aristas son dirigidas (B→A no existe si solo se cargó A→B).

---

## F3. Selección de cargas prioritarias

### Análisis del problema

Es el problema de la **mochila 0/1**: cada `Lote` tiene un peso entero positivo y
una prioridad entera no negativa, no se puede fraccionar y se usa a lo sumo una vez.
Hay que maximizar la prioridad total sin superar la capacidad `C` del vehículo.

### Estrategia elegida y justificación

**Programación Dinámica**. El problema tiene **subestructura óptima** (la solución
óptima para `n` lotes y capacidad `C` se construye a partir de soluciones óptimas de
subproblemas con `n-1` lotes) y **subproblemas superpuestos** (la misma combinación
`(i, c)` aparece en múltiples ramas de decisión), lo que hace rentable memoizar en
una tabla en vez de recalcular.

Se descartó explícitamente un criterio **Greedy** por razón prioridad/peso (tomar
primero los lotes con mejor prioridad por kilo): es óptimo para la mochila
**fraccionaria**, pero no para la variante **0/1**, porque un lote con peor razón
puede ser imprescindible para aprovechar mejor la capacidad restante exacta (contraejemplo
clásico: con capacidad 50 y lotes de (peso 20, prioridad 100) y (peso 30, prioridad
120), la razón favorece al primero, pero la combinación óptima real usa ambos). Un
enfoque de **backtracking puro** (probar todos los subconjuntos) sería
exponencial, O(2ⁿ), y la tabla de PD lo evita acotando el espacio de estados a
`n × C`.

### Organización de los datos

Tabla `V[i][c]` de tamaño `(n+1) × (C+1)`, donde `V[i][c]` es la máxima prioridad
alcanzable usando los primeros `i` lotes con capacidad `c`:

```
V[0][c] = 0
V[i][c] = V[i-1][c]                                 si peso(lote_i) > c
V[i][c] = max(V[i-1][c], V[i-1][c-peso(lote_i)] + prioridad(lote_i))   en otro caso
```

La selección final se reconstruye recorriendo la tabla desde `V[n][C]` hacia atrás:
si `V[i][c] != V[i-1][c]`, el lote `i` fue incluido y se retrocede a
`V[i-1][c - peso(lote_i)]`.

### Complejidad temporal

**O(n · C)** en tiempo y en espacio (pseudo-polinomial: depende del valor de `C`,
no solo de la cantidad de lotes `n`).

### Resultados de las pruebas

Ver `Pruebas.java` (`mochila_casoDeEjemploResueltoAMano`,
`mochila_capacidadCeroONingunLoteCabe`, `mochila_todosLosLotesCaben`,
`mochila_pesoSeleccionadoNuncaSuperaCapacidad`). Se verificó a mano que con
capacidad 50 y lotes (10,60), (20,100), (30,120), la combinación óptima es
(20,100)+(30,120) = peso 50, prioridad 220 (incluir el lote de peso 10 en lugar de
alguno de los otros dos siempre da un total menor). También se verificaron los
bordes: capacidad 0, todos los lotes caben, y que el peso total seleccionado nunca
supera la capacidad dada.

---

## Resumen de estrategias aplicadas (Etapa 1)

| Funcionalidad | Estrategia |
|---|---|
| F1 — Organización y consulta de pedidos | Divide y Conquista (merge sort) + búsqueda binaria |
| F2 — Ruta más rápida | Greedy (Dijkstra) |
| F3 — Selección de cargas prioritarias | Programación Dinámica (mochila 0/1) |

## Funcionalidades seleccionadas e implementadas para la Etapa 2

Para cubrir la cuarta estrategia pedida por la cátedra, **Backtracking**, y completar
el conjunto de seis funcionalidades exigido por la consigna, el equipo eligió e
implementó:

| Bloque | Funcionalidad | Estrategia aplicada | Motivo de la elección |
|---|---|---|---|
| A | **A1 — Consultas de rutas entre todos los centros** | Programación Dinámica (Floyd-Warshall) | Reutiliza directamente el grafo (`RedVial`) ya construido para F2; responde múltiples consultas de tiempo mínimo sin recalcular todo cada vez. |
| B | **B3 — Rutas alternativas con restricciones** | Backtracking (enumeración con poda) | Backtracking "clásico": recorrido DFS que descarta ramas por tiempo máximo excedido o ubicaciones prohibidas, sobre redes chicas donde el resultado se puede verificar a mano. |
| B | **B4 — Planificación de entregas con menor costo** | Backtracking (branch and bound con poda) | La propia consigna pide explícitamente "evitar continuar con asignaciones parciales que no puedan completar una solución válida o mejorar la mejor solución encontrada", es decir, poda por cota — la variante optimizadora de backtracking. Conecta con el dominio de Pedidos y vehículos ya trabajado en F1 y F3. |

Con esta selección, el conjunto final de seis funcionalidades cubre las cuatro
estrategias de diseño de algoritmos vistas en la materia: Divide y Conquista (F1),
Greedy (F2), Programación Dinámica (F3 y A1) y Backtracking (B3 y B4), con dos
variantes distintas de esta última (enumeración exhaustiva vs. optimización con poda).

**Alternativas consideradas y descartadas para el bloque A:**
- *A2 — Asignación de vehículos a recorridos:* también es un buen candidato a
  Backtracking con poda (similar a B4), pero se descartó para no concentrar las
  tres electivas en la misma estrategia y priorizar la cobertura de Programación
  Dinámica fuera de F3.
- *A3 — Entrega de vales con disponibilidad limitada:* resoluble con Programación
  Dinámica (similar a una mochila acotada), pero se descartó por ser más redundante
  conceptualmente con F3 que A1, que además reutiliza infraestructura ya construida.

**Alternativas consideradas y descartadas para el bloque B:**
- *B1 — Recorrido con múltiples entregas (TSP):* buen ejercicio de Backtracking/poda,
  pero de mayor complejidad de implementación y verificación que B3/B4 dentro del
  tiempo disponible para la Etapa 2.
- *B2 — Distribución de cargas entre vehículos:* también viable por Backtracking,
  pero B4 cubre un problema de asignación con optimización de costo más completo y
  mejor vinculado a las funcionalidades ya implementadas.

---

## A1. Consultas de rutas entre todos los centros

### Análisis del problema

La plataforma necesita responder muchas consultas de tiempo mínimo entre pares de
ubicaciones. Resolverlas una por una con Dijkstra (como en F2) funciona, pero
recalcula todo desde cero en cada consulta. El enunciado pide explícitamente un
procesamiento inicial que deje la información lista para consultar sin repetir el
cálculo completo cada vez, y poder reconstruir el camino además del tiempo.

### Estrategia elegida y justificación

**Programación Dinámica (Floyd-Warshall)**. El problema de "todos los pares" tiene
subestructura óptima: el camino mínimo de `i` a `j` usando como intermediarios
únicamente los vértices `{0..k}` se construye a partir de la misma cantidad pero
con intermediarios `{0..k-1}` (o pasando por `k`), y esa combinación se repite para
muchísimos pares `(i, j)` — subproblemas superpuestos — así que conviene tabular
en vez de recalcular.

Se descartó correr Dijkstra una vez por cada vértice de origen (`V` ejecuciones de
Dijkstra, O(V·E·log V) en total): funciona, pero Floyd-Warshall es más simple de
implementar correctamente para el caso "todos contra todos" y evita mantener `V`
estructuras de heap en paralelo. También se descartó recalcular con Dijkstra en
cada consulta (lo que ya hace F2): no cumple el requisito de "sin ejecutar
nuevamente el cálculo completo para cada consulta".

### Organización de los datos

- Matriz `dist[i][j]`: tiempo mínimo precomputado entre cada par de vértices
  (índices asignados a partir de `RedVial.getVertices()`).
- Matriz `siguiente[i][j]`: primer salto del camino óptimo de `i` a `j`, para
  reconstruir la ruta completa sin guardar cada camino explícitamente.
- Solo se consideran las aristas habilitadas al momento de construir
  `ConsultasRutas`; si la red cambia después, hay que construir una instancia
  nueva para que el precálculo quede al día (coherente con la idea de precomputar
  una sola vez).

### Complejidad temporal

**Precomputación: O(V³)** tiempo, O(V²) espacio (las dos matrices). **Cada
consulta: O(1)** para el tiempo mínimo y **O(V)** para reconstruir el camino
siguiendo la matriz `siguiente`.

### Resultados de las pruebas

Ver `Pruebas.java` (`consultasRutas_coincideConCaminoMinimoCalculadoAMano`,
`consultasRutas_tomaElCaminoIndirectoMasCortoEntreDosCentros`,
`consultasRutas_sinConexionInformaNoAlcanzable`,
`consultasRutas_origenIgualDestinoEsCero`). Sobre el grafo de ejemplo se verificó a
mano que Depósito→CentroD da 20 (mismo resultado que Dijkstra en F2), que
CentroA→CentroC toma el camino indirecto por CentroB (8) en vez del directo (20), y
que los pares sin conexión y origen=destino se manejan igual que en F2.

---

## B3. Rutas alternativas con restricciones

### Análisis del problema

Dados un origen, un destino, un tiempo máximo y un conjunto de ubicaciones
prohibidas, hay que encontrar **todos** los recorridos válidos (sin repetir
ubicaciones), no solo el mejor. Al pedir el conjunto completo de soluciones, un
algoritmo greedy como Dijkstra no sirve — Dijkstra descarta activamente los
caminos subóptimos, que acá son justamente parte de la respuesta.

### Estrategia elegida y justificación

**Backtracking** (enumeración con poda). Se arma el camino vértice a vértice con
una búsqueda en profundidad (DFS): en cada paso se prueba extender el camino
actual por cada arista habilitada, y se **poda** la rama apenas deja de ser
viable — exactamente la misma idea de backtracking que B4, pero acá la poda es
por restricciones de factibilidad en vez de por una cota de costo:

- el tiempo acumulado ya superaría el tiempo máximo al tomar esa arista,
- la ubicación destino de la arista está en el conjunto de prohibidas,
- la ubicación ya fue visitada en el camino actual (no se repiten ubicaciones).

Al llegar al destino se registra el camino y se retrocede (backtrack) para seguir
probando otras ramas, hasta agotar el espacio de búsqueda. Se descartó Programación
Dinámica porque no hay una única cantidad a optimizar que se pueda tabular — acá
el objetivo es enumerar todas las soluciones válidas, no una óptima.

### Organización de los datos

- Pila del camino actual (`LinkedList<String>`) y un conjunto de visitados
  (`HashSet<String>`) para detectar repeticiones en O(1).
- Lista de resultados (`List<RutaEncontrada>`), cada uno con el camino completo y
  su tiempo total; al final se calcula el de menor tiempo con un recorrido lineal.

### Complejidad temporal

**Exponencial en el peor caso**, O(V!) en el límite (cantidad de caminos simples
posibles en un grafo denso), acotada en la práctica por las podas de tiempo máximo
y ubicaciones prohibidas. La consigna pide explícitamente usar redes chicas por
esta razón, para poder examinar y verificar los resultados a mano.

### Resultados de las pruebas

Ver `Pruebas.java` (`rutasAlternativas_encuentraTodasLasRutasValidasYLaMejor`,
`rutasAlternativas_respetaElTiempoMaximo`,
`rutasAlternativas_respetaUbicacionesProhibidas`,
`rutasAlternativas_noRepiteUbicacionesAunConCiclos`). Sobre el grafo de ejemplo se
verificaron a mano las tres rutas posibles de Depósito a CentroD (20, 22 y 32), que
bajar el tiempo máximo a 21 deja solo la de 20, que prohibir CentroB deja solo la
de 32, y que agregar una arista que cierra un ciclo no produce caminos con
ubicaciones repetidas.

---

## B4. Planificación de entregas con menor costo

### Análisis del problema

Hay que asignar todos los pedidos a vehículos, respetando la capacidad de cada
vehículo y las asignaciones prohibidas, minimizando el costo total (costo fijo de
cada vehículo usado, contado una sola vez, más el costo variable de atender cada
pedido con el vehículo que le tocó).

### Estrategia elegida y justificación

**Backtracking con poda (branch and bound)**. Se prueba, pedido por pedido, cada
vehículo posible, y se poda una rama en los tres casos que la propia consigna
describe como "asignaciones parciales que no puedan completar una solución válida
o mejorar la mejor solución encontrada":

1. La asignación está prohibida → no puede completar una solución válida.
2. El vehículo no tiene capacidad para ese pedido → ídem.
3. El costo parcial acumulado ya iguala o supera el costo de la mejor solución
   completa encontrada hasta el momento → no puede mejorarla. Esta cota es válida
   porque el costo es monótono no decreciente a medida que se asignan más
   pedidos: ninguna extensión de una rama ya más cara puede terminar siendo más
   barata que una solución completa ya encontrada.

Se descartó un enfoque **Greedy** (asignar cada pedido al vehículo más barato
disponible en el momento): puede dejar sin capacidad a un vehículo que hacía falta
para pedidos posteriores, llevando a una solución inválida o subóptima — no hay
forma de garantizar que la elección localmente óptima sea parte de la solución
global óptima. También se descartó Programación Dinámica: el estado relevante
(qué vehículos están "abiertos" y cuánta capacidad le queda a cada uno) no se
reduce a una tabla de tamaño manejable sin perder justamente la cota que permite
podar.

### Organización de los datos

- Matriz `costoAtencion[pedido][vehiculo]` (con un valor centinela para
  asignaciones prohibidas).
- Arreglos de estado durante la búsqueda: `cargaVehiculo` (peso acumulado por
  vehículo), `vehiculoUsado` (si ya se contó su costo fijo) y `asignacionActual`
  (qué vehículo tiene cada pedido en la rama actual) — se arman y se deshacen a
  medida que el backtracking avanza y retrocede.
- Lista de criterios de descarte aplicados, para poder mostrar y justificar la
  poda (qué asignaciones se evaluaron y por qué se cortó esa rama).

### Complejidad temporal

**Exponencial en el peor caso**, O(Vᴺ) (N pedidos, V vehículos por pedido), igual
que cualquier backtracking sobre un problema de asignación NP-difícil en general.
En la práctica la poda por capacidad, prohibiciones y cota de costo reduce
drásticamente el árbol explorado, como se ve en el ejemplo de `Main.java` (de 8
combinaciones posibles para 3 pedidos y 2 vehículos, se descartan 4 ramas antes de
explorarlas completas).

### Resultados de las pruebas

Ver `Pruebas.java` (`planificacionEntregas_casoDeEjemploResueltoAMano`,
`planificacionEntregas_sinSolucionSiTodasLasAsignacionesEstanProhibidas`,
`planificacionEntregas_sinSolucionSiNingunVehiculoTieneCapacidad`,
`planificacionEntregas_costoFijoSeContabilizaUnaSolaVezPorVehiculo`). Se verificó a
mano, con 3 pedidos y 2 vehículos (uno de ellos prohibido para un pedido), que la
única combinación que respeta capacidad da costo total 223; que si todas las
asignaciones de un pedido están prohibidas, o si ningún vehículo tiene capacidad
para un pedido, el sistema informa que no hay solución; y que el costo fijo de un
vehículo se contabiliza una sola vez aunque reciba varios pedidos.

---

## Resumen final: las seis funcionalidades

| Funcionalidad | Estrategia | Reutiliza |
|---|---|---|
| F1 — Organización y consulta de pedidos | Divide y Conquista (merge sort) + búsqueda binaria | — |
| F2 — Ruta más rápida | Greedy (Dijkstra) | — |
| F3 — Selección de cargas prioritarias | Programación Dinámica (mochila 0/1) | — |
| A1 — Consultas de rutas entre todos los centros | Programación Dinámica (Floyd-Warshall) | `RedVial`, `Arista` (de F2) |
| B3 — Rutas alternativas con restricciones | Backtracking (enumeración con poda) | `RedVial`, `Arista` (de F2) |
| B4 — Planificación de entregas con menor costo | Backtracking (branch and bound con poda) | `Pedido` (de F1) |

Las cuatro estrategias de diseño de algoritmos vistas en la materia están cubiertas:
Divide y Conquista (F1), Greedy (F2), Programación Dinámica (F3, A1) y Backtracking
(B3, B4).
