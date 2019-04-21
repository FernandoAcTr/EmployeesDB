package DAOClass;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Employee;

/**
 * @author niluxer
 */
public class EmployeeDAO {

    Connection conn;

    public EmployeeDAO(Connection conn) {
        this.conn = conn;
    }

    public ObservableList<Employee> fetchAll(int limit) {
        ObservableList<Employee> employees = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM employees limit " + limit;
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            Employee p = null;
            while (rs.next()) {
                p = new Employee(
                        rs.getInt("emp_no"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("gender").charAt(0),
                        rs.getDate("birth_date"),
                        rs.getDate("hire_date")
                );
                employees.add(p);
            }
            rs.close();
            st.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error al recuperar información...");
        }
        return employees;
    }


    public ObservableList<Employee> searchByOR(String no_emp, String name, String lastName) {
        ObservableList<Employee> listEmployees = FXCollections.observableArrayList();

        ResultSet rs = null;
        Employee e = null;
        try {
            String query = "SELECT * FROM employees WHERE emp_no = '" + no_emp + "'" +
                    " OR first_name LIKE '"+name+"'" +
                    " OR last_name LIKE '"+lastName+"'";

            Statement st = conn.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()){
                e = new Employee(
                        rs.getInt("emp_no"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("gender").charAt(0),
                        rs.getDate("birth_date"),
                        rs.getDate("hire_date")
                );
                listEmployees.add(e);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error al recuperar información...");
        }
        return listEmployees;
    }

    public ObservableList<Employee> searchByAND(String name, String lastName) {
        ObservableList<Employee> listEmployees = FXCollections.observableArrayList();

        ResultSet rs = null;
        Employee e = null;
        try {
            String query = "SELECT * FROM employees WHERE first_name LIKE '"+name+"'" +
                    " AND last_name LIKE '"+lastName+"'";

            Statement st = conn.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()){
                e = new Employee(
                        rs.getInt("emp_no"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("gender").charAt(0),
                        rs.getDate("birth_date"),
                        rs.getDate("hire_date")
                );
                listEmployees.add(e);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error al recuperar información...");
        }
        return listEmployees;
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------
     * -                                                CRUD OPERATIONS
     * ------------------------------------------------------------------------------------------------------------------------
     */
    public Boolean delete(int no_employee) {
        try {
            String query = "delete from employees where emp_no = ?";
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, no_employee);
            st.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Boolean insert(Employee employee) {
        try {
            String query = "insert into employees "
                    + " (emp_no, birth_date, first_name, last_name, gender, hire_date)"
                    + " values (?, ?, ?, ?, ?, ?)";
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, employee.getEmp_no());
            st.setDate(2, employee.getBirth_date());
            st.setString(3, employee.getFirst_name());
            st.setString(4, employee.getLast_name());
            st.setString(5, String.valueOf(employee.getGender()));
            st.setDate(6, employee.getHire_date());
            st.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return false;
    }

    public Boolean update(Employee employee) {
        try {
            String query = "update employees "
                    + " set birth_date = ?, first_name = ?, last_name = ?, gender = ?, hire_date = ?"
                    + " where emp_no=?";
            PreparedStatement st = conn.prepareStatement(query);

            st.setDate(1, employee.getBirth_date());
            st.setString(2, employee.getFirst_name());
            st.setString(3, employee.getLast_name());
            st.setString(4, String.valueOf(employee.getGender()));
            st.setDate(5, employee.getHire_date());
            st.setInt(6, employee.getEmp_no());
            st.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return false;
    }

    /**
     * ----------------------------------------------------------------------------------------------------------------
     */

    public int getNextID(){
        ResultSet rs = null;
        int id = -1;
        try {
            String query = "SELECT MAX(emp_no) as max_id FROM employees";
            Statement st = conn.createStatement();
            rs = st.executeQuery(query);
            rs.next();
            id = rs.getInt("max_id");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error al recuperar información...");
        }
        return id;
    }

}
