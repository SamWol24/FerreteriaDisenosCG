# Diagrama de clases implementado

El diagrama original describe el sistema completo. Esta vista corresponde al código Swing/JDBC de la segunda entrega.

```mermaid
classDiagram
    direction TB
    Categoria "1" <-- "0..*" Producto : clasifica
    Marca "0..1" <-- "0..*" Producto : identifica
    Producto "1" *-- "1" Inventario : controla
    Cotizacion "1" *-- "1..*" DetalleCotizacion : contiene
    DetalleCotizacion "0..*" --> "1" Producto : referencia por ID
    CalculadoraMateriales ..> Producto : toma precio y unidad
    CalculadoraMateriales ..> DetalleCotizacion : estima
    class Inventario {
        +cantidad()
        +minimo()
        +stockBajo()
    }
    class Cotizacion {
        +cliente()
        +obra()
        +total()
    }
    class DetalleCotizacion {
        +cantidad()
        +precioUnitario()
        +subtotal()
    }
```

El tipo de obra se implementó como `enum TipoObra` con tres opciones. El modelo registra el nombre del cliente al cotizar. El detalle guarda el ID de producto y una copia de su nombre, unidad y precio para conservar el historial.
