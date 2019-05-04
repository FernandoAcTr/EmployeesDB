package controller;

import DAOClass.DepartmentDAO;
import DAOClass.EmpDeptDAO;
import DAOClass.GeneralDAO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.*;

import java.net.URL;
import java.sql.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class EmpDeptController implements Initializable {

    @FXML
    private JFXButton btnRefresh, btnSearch, btnAdd, btnDelete, btnNew;

    @FXML
    private JFXComboBox<Department> cmbDepartment, cmbAddEmpDept;

    @FXML
    private TableView<GeneralBean> tableDepartments;

    @FXML
    private JFXTextField txtSearchID, txtEmpID;

    @FXML
    private JFXDatePicker pickerFrom, pickerTo;

    private GeneralDAO generalDAO;
    private DepartmentDAO departmentDAO;
    private EmpDeptDAO empDeptDAO;

    private boolean updateMode, insertMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initData();
        initComponents();
    }

    private void initData() {
        insertMode = true;
        updateMode = false;

        empDeptDAO = new EmpDeptDAO(Conexion.getConnection());
        generalDAO = new GeneralDAO(Conexion.getConnection());
        departmentDAO = new DepartmentDAO(Conexion.getConnection());
    }

    private void initComponents() {
        initTable();
        reloadTable();
        changeFormStatus();

        ObservableList<Department> depList = departmentDAO.fetchAll(1000);
        cmbDepartment.setItems(depList);
        cmbAddEmpDept.setItems(depList);

        cmbDepartment.valueProperty().addListener((ObservableValue<? extends Department> observable, Department oldValue, Department newValue) -> {
            if(newValue != null) {
                ObservableList<GeneralBean> listReuse = generalDAO.getEmpByDept(1000, newValue.getDeptNo());
                tableDepartments.setItems(listReuse);
            }
        });

        btnRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reloadTable();
                cmbDepartment.getSelectionModel().clearSelection();
            }
        });

        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                searchEmployee();
            }
        });

        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                registerEmployee();
            }
        });

        tableDepartments.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2){
                    updateMode = true;
                    insertMode = false;
                    changeFormStatus();
                    onDoubleTableClick();
                }
            }
        });

        btnNew.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                insertMode = true;
                updateMode = false;
                clearAll();
                changeFormStatus();
            }
        });

        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GeneralBean reuse = tableDepartments.getSelectionModel().getSelectedItem();
                Employee emp = new Employee();
                Department dept = new Department();

                emp.setEmp_no(Integer.valueOf(reuse.getValue1()));
                emp.setFirst_name(reuse.getValue2());
                emp.setLast_name(reuse.getValue3());

                dept.setName(reuse.getValue4());
                dept.setDeptNo(reuse.getValue7());

                deleteEmployee(emp, dept);

                reloadTable();
            }
        });
    }

    private void initTable() {
        TableColumn<GeneralBean, String> colEmpNo = new TableColumn<>("ID. Employee");
        TableColumn<GeneralBean, String> colName = new TableColumn<>("First Name");
        TableColumn<GeneralBean, String> colLastName = new TableColumn<>("Last Name");
        TableColumn<GeneralBean, String> colDepartment = new TableColumn<>("Department");
        TableColumn<GeneralBean, String> colFrom = new TableColumn<>("From Date");
        TableColumn<GeneralBean, String> colTo = new TableColumn<>("To Date");

        colEmpNo.setCellValueFactory(new PropertyValueFactory<>("value1"));
        colName.setCellValueFactory(new PropertyValueFactory<>("value2"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("value3"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("value4"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("value5"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("value6"));

        tableDepartments.getColumns().addAll(colEmpNo, colName, colLastName, colDepartment, colFrom, colTo);


    }

    private void reloadTable(){
        ObservableList<GeneralBean> list = generalDAO.getAllEmpByDept(1000);
        tableDepartments.setItems(list);
    }

    private void searchEmployee(){
        String id = txtSearchID.getText().trim();
        ObservableList<GeneralBean> list = generalDAO.getEmpByDept(id);
        tableDepartments.setItems(list);
    }

    private void changeFormStatus() {
        btnAdd.setDisable(!insertMode);
        btnDelete.setDisable(!updateMode);
    }

    private void clearAll(){
        txtEmpID.clear();
        cmbAddEmpDept.getSelectionModel().clearSelection();
        pickerTo.setValue(null);
        pickerFrom.setValue(null);
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------
     * -                                                CRUD OPERATIONS
     * ------------------------------------------------------------------------------------------------------------------------
     */

    private void registerEmployee(){
        int emp_no = Integer.valueOf(txtEmpID.getText());
        Department dep = cmbAddEmpDept.getSelectionModel().getSelectedItem();
        Employee emp = new Employee();
        emp.setEmp_no(emp_no);

        Date from = Date.valueOf(pickerFrom.getValue());
        Date to = Date.valueOf(pickerTo.getValue());

        if(empDeptDAO.addEmpInDept(emp, dep, from, to)){
            MyUtils.makeDialog("Successfull", "Employee registered successfully", null, Alert.AlertType.CONFIRMATION).show();
            clearAll();
        }else
            MyUtils.makeDialog("Successfull", "An error has ocurred while registering employee", null, Alert.AlertType.WARNING).show();


    }

    private void deleteEmployee(Employee employee, Department department){
        Alert confirm = MyUtils.makeDialog("Confirmation", "Are you sure you want to DELETE "+employee.getEmp_no()+" from "+department.getName()+"?",
                null, Alert.AlertType.CONFIRMATION);

        Optional<ButtonType> result = confirm.showAndWait();

        if(result.isPresent())
            if(result.get() == ButtonType.OK)
                if(empDeptDAO.deleteEmployeeFromDept(employee.getEmp_no(), department.getDeptNo())) {
                    MyUtils.makeDialog("Success", "Employee deleted successfully", null, Alert.AlertType.INFORMATION).show();
                    clearAll();
                }
                else
                    MyUtils.makeDialog("Error", "An error has occurred while deleting employee", null, Alert.AlertType.WARNING).show();

    }

    /**------------------------------------------------------------------------------------------------------------------**/

    private void onDoubleTableClick(){
        GeneralBean reuse = tableDepartments.getSelectionModel().getSelectedItem();
        Employee emp = new Employee();
        Department dept = new Department();

        emp.setEmp_no(Integer.valueOf(reuse.getValue1()));
        emp.setFirst_name(reuse.getValue2());
        emp.setLast_name(reuse.getValue3());
        dept.setName(reuse.getValue4());

        Date from = Date.valueOf(reuse.getValue5());
        Date to = Date.valueOf(reuse.getValue6());

        txtEmpID.setText(emp.getEmp_no() +"");
        cmbAddEmpDept.getSelectionModel().select(dept);
        pickerFrom.setValue(from.toLocalDate());
        pickerTo.setValue(to.toLocalDate());

    }
}
