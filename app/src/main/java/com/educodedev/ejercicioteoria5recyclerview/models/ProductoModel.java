package com.educodedev.ejercicioteoria5recyclerview.models;

public class ProductoModel {
    private String nombre;
    private int cantidad;
    private float importe;
    private float total;
    public ProductoModel() {
    }

    public ProductoModel(String nombre, int cantidad, float importe) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.importe = importe;
        this.total = this.cantidad * this.importe;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getPrecio() {
        return importe;
    }

    public void setPrecio(float importe) {
        this.importe = importe;
    }

    public float getImporteTotal() {
        return total;
    }

    public void setImporteTotal(float total) {
        this.total = total;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "nombre='" + nombre + '\'' +
                ", importe=" + importe +
                ", total=" + total +
                ", cantidad=" + cantidad +
                '}';
    }

    public void actualizarTotal() {
        this.total = this.cantidad * this.importe;

    }
}
