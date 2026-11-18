package cg.dao;

import cg.conexion.Conexion;
import cg.modelo.Cotizacion;
import cg.modelo.DetalleCotizacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class CotizacionDAO {
    public int guardar(Cotizacion cotizacion) throws SQLException {
        try (var cn = Conexion.abrir()) {
            cn.setAutoCommit(false);
            try (var ps = cn.prepareStatement(
                    "INSERT INTO cotizacion (cliente, tipo_obra) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, cotizacion.cliente());
                ps.setString(2, cotizacion.obra());
                ps.executeUpdate();
                int id;
                try (var claves = ps.getGeneratedKeys()) {
                    if (!claves.next()) throw new SQLException("No se generó el ID de la cotización.");
                    id = claves.getInt(1);
                }
                guardarDetalles(cn, id, cotizacion.detalles());
                cn.commit();
                return id;
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            }
        }
    }

    private void guardarDetalles(Connection cn, int id, List<DetalleCotizacion> detalles)
            throws SQLException {
        String sql = """
                INSERT INTO detalle_cotizacion
                  (id_cotizacion, id_producto, nombre_producto, unidad, cantidad, precio_unitario)
                VALUES (?,?,?,?,?,?)
                """;
        try (var ps = cn.prepareStatement(sql)) {
            for (DetalleCotizacion linea : detalles) {
                ps.setInt(1, id);
                ps.setInt(2, linea.productoId());
                ps.setString(3, linea.nombre());
                ps.setString(4, linea.unidad());
                ps.setBigDecimal(5, linea.cantidad());
                ps.setBigDecimal(6, linea.precioUnitario());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<Cotizacion> listar() throws SQLException {
        String sql = "SELECT id_cotizacion, cliente, tipo_obra, fecha FROM cotizacion ORDER BY id_cotizacion DESC";
        var lista = new ArrayList<Cotizacion>();
        try (var cn = Conexion.abrir(); var ps = cn.prepareStatement(sql); var rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt(1);
                lista.add(new Cotizacion(id, rs.getString(2), rs.getString(3),
                        rs.getTimestamp(4).toLocalDateTime(), detalles(cn, id)));
            }
        }
        return lista;
    }

    private List<DetalleCotizacion> detalles(Connection cn, int id) throws SQLException {
        var lista = new ArrayList<DetalleCotizacion>();
        String sql = """
                SELECT id_producto, nombre_producto, unidad, cantidad, precio_unitario
                FROM detalle_cotizacion WHERE id_cotizacion=? ORDER BY id_producto
                """;
        try (var ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DetalleCotizacion(rs.getInt(1), rs.getString(2),
                            rs.getString(3), rs.getBigDecimal(4), rs.getBigDecimal(5)));
                }
            }
        }
        return lista;
    }
}
