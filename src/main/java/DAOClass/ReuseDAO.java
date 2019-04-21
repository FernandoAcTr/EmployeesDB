package DAOClass;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ReuseBean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class where multitable querys are done
 */
public class ReuseDAO{

    private Connection con;

    public ReuseDAO(Connection con) {
        this.con = con;
    }

    /** get Employees by specific Dept**/
    public ObservableList<ReuseBean> getEmpByDept(int limit, String numDep){

        ObservableList<ReuseBean> list = FXCollections.observableArrayList();
        String query = "SELECT dept_emp.*, d.dept_name as department, e.first_name, e.last_name" +
                " FROM dept_emp JOIN employees e on dept_emp.emp_no = e.emp_no JOIN departments d on dept_emp.dept_no = d.dept_no" +
                " WHERE d.dept_no = '"+numDep+"'" +
                " LIMIT "+limit;

        ReuseBean reuse;
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()){
                reuse = new ReuseBean();
                reuse.setValue1(rs.getString("emp_no"));
                reuse.setValue2(rs.getString("first_name"));
                reuse.setValue3(rs.getString("last_name"));
                reuse.setValue4(rs.getString("department"));
                reuse.setValue5(rs.getDate("from_date").toString());
                reuse.setValue6(rs.getDate("to_date").toString());
                list.add(reuse);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**Get all employees that are register in any Department**/
    public ObservableList<ReuseBean> getAllEmpByDept(int limit){

        ObservableList<ReuseBean> list = FXCollections.observableArrayList();
        String query = "SELECT dept_emp.*, d.dept_name as department, e.first_name, e.last_name" +
                " FROM dept_emp JOIN employees e on dept_emp.emp_no = e.emp_no JOIN departments d on dept_emp.dept_no = d.dept_no" +
                " LIMIT "+limit;

        ReuseBean reuse;
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()){
                reuse = new ReuseBean();
                reuse.setValue1(rs.getString("emp_no"));
                reuse.setValue2(rs.getString("first_name"));
                reuse.setValue3(rs.getString("last_name"));
                reuse.setValue4(rs.getString("department"));
                reuse.setValue5(rs.getDate("from_date").toString());
                reuse.setValue6(rs.getDate("to_date").toString());
                list.add(reuse);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /** Get an specific employee from any Department**/
    public ObservableList<ReuseBean> getEmpByDept(String emp_no){

        ObservableList<ReuseBean> list = FXCollections.observableArrayList();
        String query = "SELECT dept_emp.*, d.dept_name as department, e.first_name, e.last_name" +
                " FROM dept_emp JOIN employees e on dept_emp.emp_no = e.emp_no JOIN departments d on dept_emp.dept_no = d.dept_no" +
                " WHERE dept_emp.emp_no = '"+emp_no+"'";

        ReuseBean reuse;
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()){
                reuse = new ReuseBean();
                reuse.setValue1(rs.getString("emp_no"));
                reuse.setValue2(rs.getString("first_name"));
                reuse.setValue3(rs.getString("last_name"));
                reuse.setValue4(rs.getString("department"));
                reuse.setValue5(rs.getDate("from_date").toString());
                reuse.setValue6(rs.getDate("to_date").toString());
                reuse.setValue7(rs.getString("dept_no"));
                list.add(reuse);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /** Get five employees with max salary from any dept
     * Like this methods are to generate reports i don't need an ObservableList
     * I need a matrix whit data
     * **/
    public String[][] getFiveMaxSalaryFromDept(String dept_no){
        String data[][] = null;

        String query = "select e.first_name, e.last_name, max(s.salary) as max_salary, d.dept_name" +
                " from employees e join salaries s on e.emp_no = s.emp_no join dept_emp de on e.emp_no = de.emp_no join departments d on de.dept_no = d.dept_no" +
                " where d.dept_no = '"+dept_no+"'" +
                " group by e.emp_no" +
                " ORDER BY max_salary DESC" +
                " limit 5;";

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            rs.last();
            int rowsCounter = rs.getRow();
            data = new String[rowsCounter][];

            rs.first();

            for (int row = 0; row < rowsCounter; row++){
                data[row] = new String[4]; //num of columns that query give

                data[row][0] = rs.getString("first_name");
                data[row][1] = rs.getString("last_name");
                data[row][2] = rs.getString("max_salary");
                data[row][3] = rs.getString("dept_name");
                rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Get Employees and Salary For a Department
     * @param dept_no
     * @return
     */
    public String[][] getSalaryFromEmployeesOfDeparment(String dept_no){
        String data[][] = null;

        String query = "select e.first_name, e.last_name, max(s.salary) as salary" +
                " from employees e join dept_emp de on e.emp_no = de.emp_no join salaries s on e.emp_no = s.emp_no" +
                " where de.dept_no = '"+dept_no+"'" +
                " group by e.emp_no, de.dept_no" +
                " limit 20;";

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            rs.last();
            int rowsCounter = rs.getRow();
            data = new String[rowsCounter][];

            rs.first();

            for (int row = 0; row < rowsCounter; row++){
                data[row] = new String[3]; //num of columns that query give

                data[row][0] = rs.getString("first_name");
                data[row][1] = rs.getString("last_name");
                data[row][2] = rs.getString("salary");
                rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Get total of employees for each department
     */
    public String[][] getEmployeesOfDeparment(){
        String data[][] = null;

        String query = "select count(*) as employees, d.dept_name" +
                " from dept_emp de join departments d on de.dept_no = d.dept_no" +
                " group by de.dept_no";

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            rs.last();
            int rowsCounter = rs.getRow();
            data = new String[rowsCounter][];

            rs.first();

            for (int row = 0; row < rowsCounter; row++){
                data[row] = new String[2]; //num of columns that query give

                data[row][0] = rs.getString("dept_name");
                data[row][1] = rs.getString("employees");
                rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }


}
