package cg.modelo;

public record Marca(int id, String nombre) {
    @Override public String toString() { return nombre; }
}
