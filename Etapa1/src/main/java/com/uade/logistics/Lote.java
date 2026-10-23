package com.uade.logistics;

public class Lote {

    private final int id;
    private final int peso;
    private final int prioridad;

    public Lote(int id, int peso, int prioridad) {
        if (peso <= 0) {
            throw new IllegalArgumentException("peso debe ser positivo");
        }
        if (prioridad < 0) {
            throw new IllegalArgumentException("prioridad no puede ser negativa");
        }
        this.id = id;
        this.peso = peso;
        this.prioridad = prioridad;
    }

    public int getId() {
        return id;
    }

    public int getPeso() {
        return peso;
    }

    public int getPrioridad() {
        return prioridad;
    }

    @Override
    public String toString() {
        return "Lote{id=" + id + ", peso=" + peso + ", prioridad=" + prioridad + '}';
    }
}
