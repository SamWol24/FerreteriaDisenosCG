package cg.controlador;

import javax.swing.*;
import java.awt.Component;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

final class Acciones {
    private Acciones() {}

    static <T> void consultar(Component padre, Callable<T> operacion, Consumer<T> resultado) {
        JDialog espera = new JDialog(SwingUtilities.getWindowAncestor(padre),
                "MariaDB", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        espera.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        espera.add(new JLabel("Procesando…", SwingConstants.CENTER));
        espera.setSize(240, 90);
        espera.setLocationRelativeTo(padre);
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return operacion.call(); }

            @Override protected void done() {
                espera.dispose();
                try {
                    resultado.accept(get());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    causa.printStackTrace();
                    String mensaje = "Revisa que MariaDB esté iniciado y que db.properties y los scripts SQL estén configurados.";
                    if (causa instanceof SQLException sql && sql.getErrorCode() == 1062) {
                        mensaje = "Ya existe un producto con ese código.";
                    }
                    error(padre, mensaje);
                }
            }
        }.execute();
        espera.setVisible(true);
    }

    static String texto(JTextField campo, String etiqueta, int maximo) {
        String valor = campo.getText().strip();
        if (valor.isEmpty() || valor.length() > maximo) {
            throw new IllegalArgumentException(etiqueta + " debe tener entre 1 y " + maximo + " caracteres.");
        }
        return valor;
    }

    static BigDecimal decimal(JTextField campo, String etiqueta, int enteros, int decimales) {
        String valor = campo.getText().strip().replace(',', '.');
        String fraccion = decimales == 0 ? "" : "(\\.[0-9]{1," + decimales + "})?";
        if (!valor.matches("[0-9]{1," + enteros + "}" + fraccion)) {
            throw new IllegalArgumentException(etiqueta + " requiere un número válido sin separador de miles.");
        }
        return new BigDecimal(valor);
    }

    static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Revisar datos", JOptionPane.WARNING_MESSAGE);
    }
}
