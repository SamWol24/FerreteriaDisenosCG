package cg.controlador;

import cg.dao.ProductoDAO;
import cg.modelo.*;
import cg.vista.CatalogoVista;
import cg.vista.Estilo;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public final class CatalogoControlador {
    private final CatalogoVista vista;
    private final ProductoDAO dao;
    private final Consumer<List<Producto>> actualizarCalculadora;
    private List<Producto> productos = List.of();
    private final List<Producto> visibles = new ArrayList<>();
    private int id;
    private boolean actualizando;

    private record Catalogo(List<Categoria> categorias, List<Marca> marcas,
            List<Producto> productos) {}

    public CatalogoControlador(CatalogoVista vista, ProductoDAO dao,
            Consumer<List<Producto>> actualizarCalculadora) {
        this.vista = vista;
        this.dao = dao;
        this.actualizarCalculadora = actualizarCalculadora;
        vista.nuevo.addActionListener(e -> limpiar());
        vista.cargar.addActionListener(e -> cargar());
        vista.guardar.addActionListener(e -> guardar());
        vista.tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = vista.tabla.getSelectedRow();
            if (!actualizando && !e.getValueIsAdjusting() && fila >= 0) {
                editar(visibles.get(fila));
            }
        });
        vista.buscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        limpiar();
    }

    public void cargar() {
        Acciones.consultar(vista, () -> new Catalogo(dao.categorias(), dao.marcas(), dao.listar()),
                this::mostrar);
    }

    private void mostrar(Catalogo datos) {
        vista.categoria.removeAllItems();
        datos.categorias().forEach(vista.categoria::addItem);
        vista.marca.removeAllItems();
        vista.marca.addItem(new Marca(0, "Sin marca"));
        datos.marcas().forEach(vista.marca::addItem);
        productos = datos.productos();
        filtrar();
        limpiar();
        actualizarCalculadora.accept(productos.stream().filter(Producto::activo).toList());
    }

    private void filtrar() {
        actualizando = true;
        String busqueda = vista.buscar.getText().strip().toLowerCase(Locale.ROOT);
        var tabla = (DefaultTableModel) vista.tabla.getModel();
        tabla.setRowCount(0);
        visibles.clear();
        for (Producto p : productos) {
            String marca = p.marca() == null ? "" : p.marca().nombre();
            String clave = String.join(" ", p.codigo(), p.nombre(), p.categoria().nombre(), marca);
            if (clave.toLowerCase(Locale.ROOT).contains(busqueda)) {
                visibles.add(p);
                tabla.addRow(new Object[]{p.codigo(), p.nombre(), p.categoria().nombre(), marca,
                    p.unidad(), Estilo.cop(p.venta()), p.inventario().cantidad(),p.inventario().minimo(),
                    !p.activo() ? "Inactivo" : p.inventario().stockBajo() ? "Stock bajo" : "Activo"});
            }
        }
        actualizando = false;
    }

    private void limpiar() {
        id = 0;
        vista.tabla.clearSelection();
        vista.codigo.setText("");
        vista.nombre.setText("");
        vista.compra.setText("0");
        vista.venta.setText("");
        vista.unidad.setText("unidad");
        vista.stock.setText("0");
        vista.minimo.setText("0");
        vista.activo.setSelected(true);
        if (vista.categoria.getItemCount() > 0) vista.categoria.setSelectedIndex(0);
        if (vista.marca.getItemCount() > 0) vista.marca.setSelectedIndex(0);
    }

    private void editar(Producto p) {
        id = p.id();
        vista.codigo.setText(p.codigo());
        vista.nombre.setText(p.nombre());
        vista.compra.setText(p.compra().toPlainString());
        vista.venta.setText(p.venta().toPlainString());
        vista.unidad.setText(p.unidad());
        vista.stock.setText(Integer.toString(p.inventario().cantidad()));
        vista.minimo.setText(Integer.toString(p.inventario().minimo()));
        vista.activo.setSelected(p.activo());
        vista.categoria.setSelectedItem(p.categoria());
        if (p.marca() == null) vista.marca.setSelectedIndex(0);
        else vista.marca.setSelectedItem(p.marca());
    }

    private void guardar() {
        try {
            String codigo = Acciones.texto(vista.codigo, "Código", 30);
            String nombre = Acciones.texto(vista.nombre, "Nombre", 120);
            String unidad = Acciones.texto(vista.unidad, "Unidad", 30);
            var compra = Acciones.decimal(vista.compra, "Compra", 10, 2);
            var venta = Acciones.decimal(vista.venta, "Venta", 10, 2);
            int stock = Acciones.decimal(vista.stock, "Stock", 9, 0).intValueExact();
            int minimo = Acciones.decimal(vista.minimo, "Stock mínimo", 9, 0).intValueExact();
            var categoria = (Categoria) vista.categoria.getSelectedItem();
            var marca = (Marca) vista.marca.getSelectedItem();
            if (categoria == null || venta.signum() <= 0) {
                throw new IllegalArgumentException("Selecciona una categoría y escribe un precio de venta mayor a cero.");
            }
            var producto = new Producto(id, codigo, nombre, compra, venta, unidad, categoria,
                    marca != null && marca.id() == 0 ? null : marca,
                    new Inventario(stock, minimo), vista.activo.isSelected());
            Acciones.consultar(vista, () -> {
                dao.guardar(producto);
                return producto.codigo();
            }, codigoGuardado -> {
                javax.swing.JOptionPane.showMessageDialog(vista, "Producto guardado: " + codigoGuardado);
                cargar();
            });
        } catch (IllegalArgumentException ex) {
            Acciones.error(vista, ex.getMessage());
        }
    }
}
