package cg.modelo;

import java.math.BigDecimal;

public record Producto(int id, String codigo, String nombre, BigDecimal compra,
        BigDecimal venta, String unidad, Categoria categoria, Marca marca,
        Inventario inventario, boolean activo) {
    @Override public String toString() {
        return nombre + " (" + unidad + ")";
    }
}
