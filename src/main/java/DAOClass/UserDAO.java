package DAOClass;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author niluxer
 */
public class UserDAO {

    Connection conn;
    public UserDAO(Connection conn)
    {
        this.conn = conn;
    }

    public User validUser(String user, String password) {
        ResultSet rs = null;
        User u = null;

        try {
            String query = "SELECT * FROM users where user = '" + user + "' AND password = md5('" + password + "')";

            Statement st = conn.createStatement();
            rs = st.executeQuery(query);

            if(rs.next()) {
                String typeUser = rs.getString("user_type");
                u = new User(typeUser);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error al recuperar información...");
        }

        return  u;
    }

}
