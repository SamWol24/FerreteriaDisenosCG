package cg.vista;

import cg.modelo.Producto;
import cg.modelo.TipoObra;
import javax.swing.*;
import java.awt.*;

public final class CotizacionVista extends JPanel {
    private static final long serialVersionUID = 1L;
    public final JTextField cliente = new JTextField();
    public final JTextField area = new JTextField("12");
    public final JTextField tasa = new JTextField("50");
    public final JTextField desperdicio = new JTextField("10");
    public final JComboBox<Producto> producto = new JComboBox<>();
    public final JComboBox<TipoObra> obra = new JComboBox<>(TipoObra.values());
    public final JCheckBox entera = new JCheckBox("Redondear a unidad entera", true);
    public final JButton agregar = new JButton("Calcular y agregar");
    public final JButton quitar = new JButton("Quitar fila");
    public final JButton guardar = new JButton("Guardar cotización");
    public final JButton historial = new JButton("Ver historial");
    public final JLabel total = new JLabel("Total estimado  $0");
    public final JTable tabla = new JTable(Estilo.columnas(
            "Material", "Unidad", "Cantidad", "Precio", "Subtotal"));

    public CotizacionVista() {
        super(new BorderLayout(0, 8));
        JPanel datos = Estilo.formulario();
        Estilo.campo(datos, "Cliente", cliente);
        Estilo.campo(datos, "Tipo de obra", obra);
        Estilo.campo(datos, "Material", producto);
        Estilo.campo(datos, "Área neta (m²)", area);
        Estilo.campo(datos, "Unidades por m²", tasa);
        Estilo.campo(datos, "Desperdicio (%)", desperdicio);
        datos.add(entera);
        datos.add(agregar);
        add(datos, BorderLayout.NORTH);
        add(Estilo.tabla(tabla), BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout());
        pie.add(new JLabel("Estimación de materiales. Mano de obra y transporte se calculan por separado."),
                BorderLayout.NORTH);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(total);
        acciones.add(quitar);
        acciones.add(historial);
        acciones.add(guardar);
        pie.add(acciones, BorderLayout.SOUTH);
        add(pie, BorderLayout.SOUTH);
    }
}
