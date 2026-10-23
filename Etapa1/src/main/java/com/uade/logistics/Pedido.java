package com.uade.logistics;

public class Pedido {

    private final int id;
    private final String centroDestino;
    private final int fechaLimite;
    private final int prioridad;
    private final int pesoTotal;

    public Pedido(int id, String centroDestino, int fechaLimite, int prioridad, int pesoTotal) {
        if (centroDestino == null || centroDestino.isBlank()) {
            throw new IllegalArgumentException("centroDestino no puede ser vacío");
        }
        if (pesoTotal <= 0) {
            throw new IllegalArgumentException("pesoTotal debe ser positivo");
        }
        if (prioridad < 0) {
            throw new IllegalArgumentException("prioridad no puede ser negativa");
        }
        this.id = id;
        this.centroDestino = centroDestino;
        this.fechaLimite = fechaLimite;
        this.prioridad = prioridad;
        this.pesoTotal = pesoTotal;
    }

    public int getId() {
        return id;
    }

    public String getCentroDestino() {
        return centroDestino;
    }

    public int getFechaLimite() {
        return fechaLimite;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public int getPesoTotal() {
        return pesoTotal;
    }

    @Override
    public String toString() {
        return "Pedido{id=" + id +
                ", centroDestino='" + centroDestino + '\'' +
                ", fechaLimite=" + fechaLimite +
                ", prioridad=" + prioridad +
                ", pesoTotal=" + pesoTotal +
                '}';
    }
}
