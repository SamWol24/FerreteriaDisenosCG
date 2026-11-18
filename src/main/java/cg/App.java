package cg;

import cg.controlador.CatalogoControlador;
import cg.controlador.CotizacionControlador;
import cg.dao.ProductoDAO;
import cg.dao.CotizacionDAO;
import cg.vista.CatalogoVista;
import cg.vista.CotizacionVista;
import javax.swing.*;
import java.awt.*;

public final class App {
    private static final Color AZUL_OSCURO = new Color(17, 34, 52);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::mostrar);
    }

    private static void mostrar() {
        JFrame ventana = new JFrame("Ferretería y Diseños CG");
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JLabel titulo = new JLabel("Ferretería y Diseños CG");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        titulo.setOpaque(true);
        titulo.setBackground(AZUL_OSCURO);

        CatalogoVista catalogo = new CatalogoVista();
        CotizacionVista cotizacion = new CotizacionVista();
        var controlCotizacion = new CotizacionControlador(cotizacion, new CotizacionDAO());
        var controlCatalogo = new CatalogoControlador(catalogo, new ProductoDAO(), controlCotizacion::materiales);
        JTabbedPane navegacion = new JTabbedPane(JTabbedPane.LEFT);
        navegacion.addTab("Materiales e inventario", catalogo);
        navegacion.addTab("Calculadora y cotizaciones", cotizacion);
        navegacion.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        ventana.add(titulo, BorderLayout.NORTH);
        ventana.add(navegacion, BorderLayout.CENTER);
        ventana.setSize(1150, 650);
        ventana.setMinimumSize(new Dimension(940, 500));
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
        controlCatalogo.cargar();
    }
}
