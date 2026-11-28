package cg.dao;

import cg.conexion.Conexion;
import cg.modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class ProductoDAO {
    public List<Categoria> categorias() throws SQLException {
        var lista = new ArrayList<Categoria>();
        try (var cn = Conexion.abrir();
             var ps = cn.prepareStatement("SELECT id_categoria, nombre FROM categoria ORDER BY nombre");
             var rs = ps.executeQuery()) {
            while (rs.next()) lista.add(new Categoria(rs.getInt(1), rs.getString(2)));
        }
        return lista;
    }

    public List<Marca> marcas() throws SQLException {
        var lista = new ArrayList<Marca>();
        try (var cn = Conexion.abrir();
             var ps = cn.prepareStatement("SELECT id_marca, nombre FROM marca ORDER BY nombre");
             var rs = ps.executeQuery()) {
            while (rs.next()) lista.add(new Marca(rs.getInt(1), rs.getString(2)));
        }
        return lista;
    }

    public List<Producto> listar() throws SQLException {
        String sql = """
        SELECT p.*, c.nombre AS categoria, m.nombre AS marca,
            COALESCE(i.cantidad, 0) AS cantidad,
            COALESCE(i.stock_minimo, 0) AS stock_minimo
        FROM producto p
        JOIN categoria c USING (id_categoria)
        LEFT JOIN marca m USING (id_marca)
        LEFT JOIN inventario i USING (id_producto)
        WHERE p.estado = TRUE
        ORDER BY p.nombre
        """;
        var lista = new ArrayList<Producto>();
        try (var cn = Conexion.abrir(); var ps = cn.prepareStatement(sql); var rs = ps.executeQuery()) {
            while (rs.next()) {
                Marca marca = rs.getString("marca") == null ? null
                        : new Marca(rs.getInt("id_marca"), rs.getString("marca"));
                lista.add(new Producto(rs.getInt("id_producto"), rs.getString("codigo"),
                        rs.getString("nombre"), rs.getBigDecimal("precio_compra"),
                        rs.getBigDecimal("precio_venta"), rs.getString("unidad_medida"),
                        new Categoria(rs.getInt("id_categoria"), rs.getString("categoria")),
                        marca, new Inventario(rs.getInt("cantidad"), rs.getInt("stock_minimo")),
                        rs.getBoolean("estado")));
            }
        }
        return lista;
    }

    public void guardar(Producto producto) throws SQLException {
        boolean nuevo = producto.id() == 0;
        String sql = nuevo
                ? """
                  INSERT INTO producto (codigo, nombre, precio_compra, precio_venta,
                    unidad_medida, id_categoria, id_marca, estado) VALUES (?,?,?,?,?,?,?,?)
                  """
                : """
                  UPDATE producto SET codigo=?, nombre=?, precio_compra=?, precio_venta=?,
                    unidad_medida=?, id_categoria=?, id_marca=?, estado=? WHERE id_producto=?
                  """;
        try (var cn = Conexion.abrir()) {
            cn.setAutoCommit(false);
            try (var ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, producto.codigo());
                ps.setString(2, producto.nombre());
                ps.setBigDecimal(3, producto.compra());
                ps.setBigDecimal(4, producto.venta());
                ps.setString(5, producto.unidad());
                ps.setInt(6, producto.categoria().id());
                if (producto.marca() == null) {
                    ps.setNull(7, Types.INTEGER);
                } else {
                    ps.setInt(7, producto.marca().id());
                }
                ps.setBoolean(8, producto.activo());
                if (!nuevo) ps.setInt(9, producto.id());
                ps.executeUpdate();
                int id = producto.id();
                if (nuevo) {
                    try (var claves = ps.getGeneratedKeys()) {
                        if (!claves.next()) throw new SQLException("No se generó el ID del producto.");
                        id = claves.getInt(1);
                    }
                }
                guardarInventario(cn, id, producto.inventario());
                cn.commit();
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            }
        }
    }

    private void guardarInventario(Connection cn, int id, Inventario inventario) throws SQLException {
        String sql = """
                INSERT INTO inventario (id_producto, cantidad, stock_minimo) VALUES (?,?,?)
                ON DUPLICATE KEY UPDATE cantidad=VALUES(cantidad), stock_minimo=VALUES(stock_minimo)
                """;
        try (var ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, inventario.cantidad());
            ps.setInt(3, inventario.minimo());
            ps.executeUpdate();
        }
    }
    public void eliminar(int id) throws SQLException {
    String sql = """
        UPDATE producto
        SET estado = FALSE
        WHERE id_producto = ?
        """;

    try (var cn = Conexion.abrir();
         var ps = cn.prepareStatement(sql)) {

        ps.setInt(1, id);

        if (ps.executeUpdate() == 0) {
            throw new SQLException("No se encontró el producto.");
        }
    }
}
}
