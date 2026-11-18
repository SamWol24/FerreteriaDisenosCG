package cg.controlador;

import cg.dao.CotizacionDAO;
import cg.modelo.*;
import cg.vista.CotizacionVista;
import cg.vista.Estilo;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class CotizacionControlador {
    private final CotizacionVista vista;
    private final CotizacionDAO dao;
    private final List<DetalleCotizacion> detalles = new ArrayList<>();

    public CotizacionControlador(CotizacionVista vista, CotizacionDAO dao) {
        this.vista = vista;
        this.dao = dao;
        vista.agregar.addActionListener(e -> calcular());
        vista.guardar.addActionListener(e -> guardar());
        vista.historial.addActionListener(e -> Acciones.consultar(vista, dao::listar, this::historial));
        vista.quitar.addActionListener(e -> quitar());
    }

    public void materiales(List<Producto> productos) {
        vista.producto.removeAllItems();
        productos.forEach(vista.producto::addItem);
    }

    private void calcular() {
        try {
            var producto = (Producto) vista.producto.getSelectedItem();
            if (producto == null) throw new IllegalArgumentException("Selecciona un producto activo.");
            var area = Acciones.decimal(vista.area, "Área", 6, 3);
            var tasa = Acciones.decimal(vista.tasa, "Unidades por m²", 6, 3);
            var desperdicio = Acciones.decimal(vista.desperdicio, "Desperdicio", 3, 2);
            var linea = CalculadoraMateriales.estimar(producto, area, tasa,
                    desperdicio, vista.entera.isSelected());
            detalles.removeIf(anterior -> anterior.productoId() == linea.productoId());
            detalles.add(linea);
            resumen();
        } catch (IllegalArgumentException ex) {
            Acciones.error(vista, ex.getMessage());
        }
    }

    private void quitar() {
        int fila = vista.tabla.getSelectedRow();
        if (fila < 0) {
            Acciones.error(vista, "Selecciona una fila de materiales.");
            return;
        }
        detalles.remove(fila);
        resumen();
    }

    private void resumen() {
        var tabla = (DefaultTableModel) vista.tabla.getModel();
        tabla.setRowCount(0);
        detalles.forEach(d -> tabla.addRow(new Object[]{d.nombre(), d.unidad(),
            d.cantidad().stripTrailingZeros().toPlainString(), Estilo.cop(d.precioUnitario()),
            Estilo.cop(d.subtotal())}));
        vista.total.setText("Total estimado  " + Estilo.cop(new Cotizacion(0, "", "", null, detalles).total()));
    }

    private void guardar() {
        try {
            String cliente = Acciones.texto(vista.cliente, "Cliente", 120);
            if (detalles.isEmpty()) throw new IllegalArgumentException("Agrega al menos un material.");
            var cotizacion = new Cotizacion(0, cliente, vista.obra.getSelectedItem().toString(), null, detalles);
            Acciones.consultar(vista, () -> dao.guardar(cotizacion), id -> {
                detalles.clear();
                vista.cliente.setText("");
                resumen();
                JOptionPane.showMessageDialog(vista, "Cotización guardada con el número " + id + ".");
            });
        } catch (IllegalArgumentException ex) {
            Acciones.error(vista, ex.getMessage());
        }
    }

    private void historial(List<Cotizacion> cotizaciones) {
        var modelo = Estilo.columnas("Número", "Cliente", "Obra", "Fecha", "Total");
        DateTimeFormatter fecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        cotizaciones.forEach(c -> modelo.addRow(new Object[]{c.id(), c.cliente(), c.obra(),
            c.fecha().format(fecha), Estilo.cop(c.total())}));
        JTable tabla = new JTable(modelo);
        tabla.setPreferredScrollableViewportSize(new java.awt.Dimension(660, 180));
        var lineas = Estilo.columnas("Material", "Unidad", "Cantidad", "Precio", "Subtotal");
        JTable tablaLineas = new JTable(lineas);
        tablaLineas.setPreferredScrollableViewportSize(new java.awt.Dimension(660, 120));
        tabla.getSelectionModel().addListSelectionListener(e -> {
            lineas.setRowCount(0);
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                cotizaciones.get(fila).detalles().forEach(d -> lineas.addRow(new Object[]{
                    d.nombre(), d.unidad(), d.cantidad(), Estilo.cop(d.precioUnitario()), Estilo.cop(d.subtotal())}));
            }
        });
        JPanel panel = new JPanel(new java.awt.GridLayout(2, 1, 0, 10));
        panel.add(Estilo.tabla(tabla));
        panel.add(Estilo.tabla(tablaLineas));
        if (!cotizaciones.isEmpty()) tabla.setRowSelectionInterval(0, 0);
        JOptionPane.showMessageDialog(vista, panel, "Historial de cotizaciones", JOptionPane.PLAIN_MESSAGE);
    }
}
