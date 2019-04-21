package DAOClass;

import model.Department;
import model.Employee;

import java.sql.*;

public class EmpDeptDAO {

    Connection conn;

    public EmpDeptDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean addEmpInDept(Employee employee, Department department, Date from, Date to) {
        String query = "insert into dept_emp "
                + " (emp_no, dept_no, from_date, to_date)"
                + " values (?, ?, ?, ?)";
        try {
            PreparedStatement st = conn.prepareStatement(query);

            st.setInt(1, employee.getEmp_no());
            st.setString(2, department.getDeptNo());
            st.setDate(3, from);
            st.setDate(4, to);

            st.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteEmployeeFromDept(int numEmp, String numDept){
        String query = "DELETE FROM dept_emp" +
                " WHERE emp_no = ? AND dept_no = ?";
        try {
            PreparedStatement st = conn.prepareStatement(query);
            st.setInt(1, numEmp);
            st.setString(2, numDept);
            st.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


}
