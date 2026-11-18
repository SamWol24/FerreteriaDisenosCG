package cg.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CalculadoraMateriales {
    private CalculadoraMateriales() {}

    public static DetalleCotizacion estimar(Producto producto, BigDecimal area,
            BigDecimal rendimiento, BigDecimal desperdicio, boolean unidadEntera) {
        if (area.signum() <= 0 || rendimiento.signum() <= 0
                || desperdicio.signum() < 0 || desperdicio.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Revisa el área, el rendimiento y el desperdicio (0 a 100 %).");
        }
        BigDecimal cantidad = area.multiply(rendimiento)
                .multiply(BigDecimal.ONE.add(desperdicio.movePointLeft(2)))
                .setScale(unidadEntera ? 0 : 3, RoundingMode.CEILING);
        if (cantidad.compareTo(new BigDecimal("999999999.999")) > 0) {
            throw new IllegalArgumentException("La cantidad calculada supera el límite.");
        }
        return new DetalleCotizacion(producto.id(), producto.nombre(), producto.unidad(),
                cantidad, producto.venta());
    }
}
