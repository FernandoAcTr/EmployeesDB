package model;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexion {

    private static java.sql.Connection connection = null;
    private static final String database = "employees";
    private static final String user = "root";
    private static final String password = "root";
    private static final String hostname = "localhost";
    private static final String url = "jdbc:mysql://"+ hostname +":3306/" + database +"?serverTimezone=UTC";

    public static java.sql.Connection getConnection() {
        if(connection == null) Connect();
        return connection;
    }

    public static void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conection initialized...");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    public static void Disconnect() {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isClosed(){
        try {
            return connection.isClosed();
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
    }
}

