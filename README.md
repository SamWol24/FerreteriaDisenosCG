# Ferretería y Diseños CG

Proyecto integrador de Programación II, UNIAJC. Equipo de trabajo: Samir Agudelo Curaca y Dayana Katerine Córdoba Garzón.

Aplicación de escritorio para administrar materiales e inventario, estimar cantidades y guardar cotizaciones. Parte de la guía de Ferretería y Diseños CG y de los mockups de FerreMax.

## Ejecutar en NetBeans y XAMPP

1. Instalen JDK 17 o superior. En NetBeans abran la carpeta que contiene `pom.xml` con **File → Open Project**. Maven descarga el controlador JDBC en la primera ejecución.
2. En XAMPP arranquen **MySQL** (MariaDB). Arranquen **Apache** si usarán [phpMyAdmin](http://localhost/phpmyadmin). Importen primero `sql/01-esquema.sql` y después `sql/02-datos.sql`. Cada integrante importa ambos scripts en su equipo.
3. En la raíz del proyecto, copien `db.properties.example` a `db.properties`. Ajusten el puerto, el usuario y la clave a su instalación. El archivo local está excluido por Git. La configuración de ejemplo está pensada para un XAMPP local con usuario raíz y clave vacía.
4. En NetBeans elijan **Run Project** y seleccionen `cg.App` si el IDE solicita la clase principal. Con Maven instalado también pueden usar `mvn compile exec:java` desde la raíz.
5. En **Materiales e inventario** deben aparecer Ladrillo muro, Cemento gris y Arena. Si obtienen un error de conexión, revisen el puerto que muestra XAMPP, las credenciales, la ubicación de `db.properties` y la salida de NetBeans. Una vez corregido, pulsen **Recargar**.

Si ya tienen tablas de otro avance con el mismo nombre de base de datos, hagan un respaldo y prueben en una instalación limpia o en una base de pruebas con otro nombre. Los scripts crean las tablas que falten y cargan datos de ejemplo. Las estructuras previas requieren una migración propia.

## Demostración corta

1. Busquen **LAD-01** por código. Seleccionen una fila para editar los campos. Para crear otro producto, pulsen **Nuevo**, completen los campos y guarden. Para desactivar un producto, seleccionen su fila, desmarquen **Activo** y guarden.
2. En **Calculadora y cotizaciones**, escojan el tipo de obra y escriban un cliente de prueba. Seleccionen cada material y den clic en **Calcular y agregar**. Para recalcular un mismo material, agréguenlo nuevamente. Eso actualiza su fila.
3. Con área neta de **12 m²** y **10 %** de desperdicio, prueben estos rendimientos didácticos.

   | Material | Unidades por m² | Redondear a entero | Cantidad esperada | Subtotal COP |
   |---|---:|---|---:|---:|
   | Ladrillo muro | 50 | Sí | 660 | 792.000 |
   | Cemento gris | 0,25 | Sí | 4 | 128.000 |
   | Arena | 0,04 | No | 0,528 | 47.520 |
   | **Total** | | | | **967.520** |

4. Guarden, anoten el número y búsquenlo en **Ver historial**. Cierren y vuelvan a abrir para demostrar persistencia. Cada cotización guarda precio y nombre de cada renglón al momento de armarla. El cálculo es una estimación con rendimientos de ejemplo y redondeo hacia arriba según la unidad elegida. Una cotización no descuenta existencias.

## Arquitectura y alcance

- `cg.modelo` contiene entidades, relaciones y cálculo.
- `cg.vista` contiene los paneles Swing. `cg.controlador` recibe eventos, valida entradas, coordina operaciones y refresca las vistas.
- `cg.dao` encapsula lecturas y escrituras con JDBC. `cg.conexion` abre la conexión. Producto e inventario se guardan en una transacción. Cotización y detalles se guardan en otra.
- La entrega de tres días incluye alta, consulta, edición y desactivación de productos, inventario con alerta, búsqueda por código, nombre, categoría o marca, cálculo de cantidades y cotizaciones con historial. Categorías y marcas se administran por SQL en esta versión. El cliente de la cotización se registra por nombre.
- El alcance de la entrega final y la matriz de evidencias se encuentran en [PLAN_DE_ENTREGA.md](docs/PLAN_DE_ENTREGA.md).

## Verificación de esta base

Se compiló el código con compatibilidad Java 17 y se ejecutaron los dos scripts dos veces sobre MariaDB 11.8.6 con Connector/J 3.5.3. Se verificaron por JDBC el catálogo, la detección de códigos duplicados, la marca opcional, las existencias, la desactivación, el total del ejemplo, el precio histórico y la reversión de una cotización cuyo detalle falló. Se instanciaron los paneles Swing. El recorrido visual con clics en NetBeans y XAMPP queda para la prueba cruzada de los integrantes.
