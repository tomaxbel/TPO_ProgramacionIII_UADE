package com.uade.logistics;

import java.util.List;

public record ResultadoMochila(List<Lote> seleccionados, int pesoTotal, int prioridadTotal, int capacidadRestante) {
}
