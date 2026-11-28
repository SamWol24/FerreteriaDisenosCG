/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cg.conexion;

import java.sql.Connection;
import java.sql.SQLException;

public class PruebaConexion {

    public static void main(String[] args) {

        try (Connection conexion = Conexion.abrir()) {

            System.out.println("CONEXION EXITOSA");
            System.out.println("Base de datos: "
                    + conexion.getCatalog());
            System.out.println("Estado: "
                    + !conexion.isClosed());

        } catch (SQLException ex) {

            System.out.println("ERROR DE CONEXION");
            ex.printStackTrace();

        }
    }
}