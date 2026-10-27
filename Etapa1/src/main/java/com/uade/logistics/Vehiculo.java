package com.uade.logistics;

public class Vehiculo {

    private final int id;
    private final int capacidad;
    private final int costoFijo;

    public Vehiculo(int id, int capacidad, int costoFijo) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("capacidad debe ser positiva");
        }
        if (costoFijo < 0) {
            throw new IllegalArgumentException("costoFijo no puede ser negativo");
        }
        this.id = id;
        this.capacidad = capacidad;
        this.costoFijo = costoFijo;
    }

    public int getId() {
        return id;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public int getCostoFijo() {
        return costoFijo;
    }

    @Override
    public String toString() {
        return "Vehiculo{id=" + id + ", capacidad=" + capacidad + ", costoFijo=" + costoFijo + '}';
    }
}
