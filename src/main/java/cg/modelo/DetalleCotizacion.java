package cg.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record DetalleCotizacion(int productoId, String nombre, String unidad,
        BigDecimal cantidad, BigDecimal precioUnitario) {
    public BigDecimal subtotal() {
        return cantidad.multiply(precioUnitario).setScale(2, RoundingMode.HALF_UP);
    }
}
