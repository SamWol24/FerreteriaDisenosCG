package cg.vista;

import cg.modelo.Categoria;
import cg.modelo.Marca;
import javax.swing.*;
import java.awt.*;

public final class CatalogoVista extends JPanel {
    private static final long serialVersionUID = 1L;
    public final JTextField codigo = new JTextField();
    public final JTextField nombre = new JTextField();
    public final JTextField compra = new JTextField();
    public final JTextField venta = new JTextField();
    public final JTextField unidad = new JTextField();
    public final JTextField stock = new JTextField();
    public final JTextField minimo = new JTextField();
    public final JTextField buscar = new JTextField(25);
    public final JComboBox<Categoria> categoria = new JComboBox<>();
    public final JComboBox<Marca> marca = new JComboBox<>();
    public final JCheckBox activo = new JCheckBox("Activo", true);
    public final JButton nuevo = new JButton("Nuevo");
    public final JButton guardar = new JButton("Guardar");
    public final JButton cargar = new JButton("Recargar");
    public final JButton eliminar = new JButton("Eliminar");
    public final JTable tabla = new JTable(Estilo.columnas(
            "Código", "Material", "Categoría", "Marca", "Unidad", "Precio", "Stock","Mínimo", "Estado"));

    public CatalogoVista() {
        super(new BorderLayout(0, 8));
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Estilo.campo(filtro, "Buscar material", buscar);
        filtro.add(cargar);
        add(filtro, BorderLayout.NORTH);
        add(Estilo.tabla(tabla), BorderLayout.CENTER);
        JPanel datos = Estilo.formulario();
        Estilo.campo(datos, "Código", codigo);
        Estilo.campo(datos, "Nombre", nombre);
        Estilo.campo(datos, "Compra (COP)", compra);
        Estilo.campo(datos, "Venta (COP)", venta);
        Estilo.campo(datos, "Unidad", unidad);
        Estilo.campo(datos, "Categoría", categoria);
        Estilo.campo(datos, "Marca", marca);
        Estilo.campo(datos, "Stock", stock);
        Estilo.campo(datos, "Stock mínimo", minimo);
        datos.add(activo);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(nuevo);
        botones.add(eliminar);
        botones.add(guardar);
        JPanel inferior = new JPanel(new BorderLayout());
        inferior.add(datos, BorderLayout.CENTER);
        inferior.add(botones, BorderLayout.SOUTH);
        add(inferior, BorderLayout.SOUTH);
    }
}
