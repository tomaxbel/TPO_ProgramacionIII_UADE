package com.uade.logistics;

/**
 * Conexión dirigida de la red vial. "habilitada" se puede alternar sin
 * remover la arista de la red, para poder recalcular rutas tras una baja.
 */
public class Arista {

    private final String origen;
    private final String destino;
    private final int tiempo;
    private boolean habilitada;

    public Arista(String origen, String destino, int tiempo) {
        if (tiempo <= 0) {
            throw new IllegalArgumentException("tiempo debe ser positivo");
        }
        this.origen = origen;
        this.destino = destino;
        this.tiempo = tiempo;
        this.habilitada = true;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public int getTiempo() {
        return tiempo;
    }

    public boolean isHabilitada() {
        return habilitada;
    }

    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }

    @Override
    public String toString() {
        return origen + " -> " + destino + " (" + tiempo + " min)" + (habilitada ? "" : " [deshabilitada]");
    }
}
