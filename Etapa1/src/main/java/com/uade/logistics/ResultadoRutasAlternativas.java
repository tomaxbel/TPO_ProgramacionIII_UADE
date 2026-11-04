package com.uade.logistics;

import java.util.List;

public record ResultadoRutasAlternativas(List<RutaEncontrada> rutas, RutaEncontrada mejor) {
}
