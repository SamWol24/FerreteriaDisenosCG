package cg.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public final class Estilo {
    private Estilo() {}

    public static JPanel formulario() {
        JPanel panel = new JPanel(new GridLayout(0, 4, 10, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 4, 12, 4));
        return panel;
    }

    public static void campo(JPanel panel, String etiqueta, JComponent componente) {
        JLabel label = new JLabel(etiqueta);
        label.setLabelFor(componente);
        panel.add(label);
        panel.add(componente);
    }

    public static DefaultTableModel columnas(String... nombres) {
        return new DefaultTableModel(nombres, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
    }

    public static JScrollPane tabla(JTable tabla) {
        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(tabla);
    }

    public static String cop(Number cantidad) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO")).format(cantidad);
    }
}
