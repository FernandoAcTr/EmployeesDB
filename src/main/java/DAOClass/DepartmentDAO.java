package DAOClass;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Department;

import java.sql.*;

public class DepartmentDAO {

    private Connection conn;

    public DepartmentDAO(Connection conn) {
        this.conn = conn;
    }

    public ObservableList<Department> fetchAll(int limit) {
        ObservableList<Department> departments = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM departments limit " + limit;
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            Department d = null;
            while (rs.next()) {
                d = new Department(
                        rs.getString("dept_no"),
                        rs.getString("dept_name")
                );
                departments.add(d);
            }
            rs.close();
            st.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error al recuperar información...");
        }
        return departments;
    }

    public ObservableList<Department> searchByOR(String deptNo, String name) {
        ObservableList<Department> departments = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM departments WHERE dept_no LIKE '"+deptNo+"' OR dept_name LIKE '"+name+"'" ;
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            Department d = null;
            while (rs.next()) {
                d = new Department(
                        rs.getString("dept_no"),
                        rs.getString("dept_name")
                );
                departments.add(d);
            }
            rs.close();
            st.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error al recuperar información...");
        }
        return departments;
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------
     * -                                                CRUD OPERATIONS
     * ------------------------------------------------------------------------------------------------------------------------
     */

    public Boolean insert(Department department) {
        try {
            String query = "insert into departments "
                    + " (dept_no, dept_name)"
                    + " values (?, ?)";
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, department.getDeptNo());
            st.setString(2, department.getName());
            st.execute();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return false;
    }

    public Boolean update(Department department) {
        try {
            String query = "update departments "
                    + " set dept_name = ?"
                    + " where dept_no=?";
            PreparedStatement st = conn.prepareStatement(query);

            st.setString(1, department.getName());
            st.setString(2, department.getDeptNo());
            st.execute();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return false;
    }

    public Boolean delete(String dept_no) {
        try {
            String query = "delete from departments where dept_no = ?";
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, dept_no);
            st.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**----------------------------------------------------------------------------------------------------------------------*/
}
