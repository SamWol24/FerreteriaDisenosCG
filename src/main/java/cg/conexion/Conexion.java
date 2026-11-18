package cg.conexion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Conexion {
    private Conexion() {}

    public static Connection abrir() throws SQLException {
        Properties config = new Properties();
        try (var entrada = Files.newInputStream(Path.of("db.properties"))) {
            config.load(entrada);
        } catch (IOException ex) {
            throw new SQLException("Copia db.properties.example a db.properties y revisa la configuración.", ex);
        }
        return DriverManager.getConnection(config.getProperty("url"),
                config.getProperty("usuario"), config.getProperty("clave", ""));
    }
}
