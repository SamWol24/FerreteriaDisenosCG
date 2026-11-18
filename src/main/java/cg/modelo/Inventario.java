package cg.modelo;

public record Inventario(int cantidad, int minimo) {
    public boolean stockBajo() {
        return cantidad <= minimo;
    }
}
