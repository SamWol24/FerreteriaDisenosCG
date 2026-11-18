package cg.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Cotizacion(int id, String cliente, String obra, LocalDateTime fecha,
        List<DetalleCotizacion> detalles) {
    public Cotizacion {
        detalles = List.copyOf(detalles);
    }

    public BigDecimal total() {
        return detalles.stream().map(DetalleCotizacion::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
