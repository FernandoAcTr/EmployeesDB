package controller;

import DAOClass.DepartmentDAO;
import DAOClass.EmployeeDAO;
import DAOClass.GeneralDAO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.*;

public class MainController implements Initializable {

    @FXML
    private TableView<Employee> tableEmployee;

    @FXML
    private JFXComboBox<String> comboGender;

    @FXML
    private JFXButton btnSave, btnUpdate, btnDelete, btnNew, btnSearch, btnDepartments;

    @FXML
    private JFXTextField txtName, txtLastName, txtID, txtSearchID;

    @FXML
    private JFXDatePicker pickerBirthDate, pickerHireDate;

    @FXML
    private MenuItem mnuExit, mnuLogout, mnuDepartments, mnuEmpByDept;

    @FXML
    private MenuItem mnuReport1, mnuReport2, mnuReport3, mnuReport4;

    private EmployeeDAO employeeDAO;
    private GeneralDAO generalDAO;
    private DepartmentDAO departmentDAO;

    private String userType;
    private boolean insertMode = false;
    private boolean updateMode = false;

    public MainController(String userType) {
        this.userType = userType;
    }

    public void initialize(URL location, ResourceBundle resources) {
        initData();
        initGUI();
    }

    private void initData() {
        comboGender.getItems().addAll("M", "F");
        employeeDAO = new EmployeeDAO(Conexion.getConnection());
        generalDAO = new GeneralDAO(Conexion.getConnection());
        departmentDAO = new DepartmentDAO(Conexion.getConnection());
    }

    private void initGUI() {
        TableColumn colID = new TableColumn("ID");
        TableColumn colName = new TableColumn("NAME");
        TableColumn colLastName = new TableColumn("LAST NAME");
        TableColumn colGender = new TableColumn("GENDER");

        colID.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("emp_no"));
        colName.setCellValueFactory(new PropertyValueFactory<Employee, String>("first_name"));
        colLastName.setCellValueFactory(new PropertyValueFactory<Employee, String>("last_name"));
        colGender.setCellValueFactory(new PropertyValueFactory<Employee, Character>("gender"));

        tableEmployee.getColumns().addAll(colID, colName, colLastName, colGender);

        ObservableList<Employee> listEmployees = employeeDAO.fetchAll(1000);
        tableEmployee.setItems(listEmployees);

        changeFormStatus();

        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                insertEmployee();
                clearAll();
            }
        });

        btnNew.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                insertMode = true;
                updateMode = false;
                clearAll();
                setNextID();
                reloadEmployeeList();
            }
        });

        tableEmployee.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    onDoubleTableClick();
                    changeFormStatus();
                }
            }
        });

        btnUpdate.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                updateEmployee();
                MyUtils.makeDialog("Updated", "Employee updated succesfully", null, Alert.AlertType.CONFIRMATION).show();
            }
        });

        btnDelete.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                deleteEmploye();
            }
        });

        mnuLogout.setOnAction((ActionEvent event) -> {
            logOut();
        });

        mnuExit.setOnAction((ActionEvent event) -> {
            if (!Conexion.isClosed())
                Conexion.Disconnect();
            System.exit(0);
        });

        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                searchEmployee();
            }
        });

        btnDepartments.setOnAction((ActionEvent event) -> {
            goToDepartments();
        });

        mnuDepartments.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                goToDepartments();
            }
        });

        mnuEmpByDept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                goToEmpDept();
            }
        });

        mnuReport1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                generateReportMaxSalaries();
            }
        });

        mnuReport2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                generateReportSalaries();
            }
        });
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------
     * -                                                CRUD OPERATIONS
     * ------------------------------------------------------------------------------------------------------------------------
     */

    private void insertEmployee() {
        int id = Integer.valueOf(txtID.getText());
        String name = txtName.getText().trim();
        String lastName = txtLastName.getText().trim();
        Date bithdate = Date.valueOf(pickerBirthDate.getValue());
        Date hiredate = Date.valueOf(pickerHireDate.getValue());
        char gender = comboGender.getSelectionModel().getSelectedItem().charAt(0);

        Employee employee = new Employee(id, name, lastName, gender, bithdate, hiredate);

        employeeDAO.insert(employee);
        reloadEmployeeList();
    }

    private void updateEmployee() {
        int id = Integer.valueOf(txtID.getText());
        String name = txtName.getText().trim();
        String lastName = txtLastName.getText().trim();
        Date bithdate = Date.valueOf(pickerBirthDate.getValue());
        Date hiredate = Date.valueOf(pickerHireDate.getValue());
        char gender = comboGender.getSelectionModel().getSelectedItem().charAt(0);

        Employee employee = new Employee(id, name, lastName, gender, bithdate, hiredate);

        if (employeeDAO.update(employee))
            reloadEmployeeList();
        else
            MyUtils.makeDialog("Error", null, "An error has ocurred while modify the info", Alert.AlertType.ERROR).show();

    }

    private void deleteEmploye() {
        Alert confirm = MyUtils.makeDialog("DELETE confirmation", null, "Are you sure to DELETE this employee?", Alert.AlertType.CONFIRMATION);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.get() == ButtonType.OK) {
            Employee employee = tableEmployee.getSelectionModel().getSelectedItem();
            employeeDAO.delete(employee.getEmp_no());
            clearAll();
            reloadEmployeeList();
        }
    }

    /*--------------------------------------------------------------------------------------------------------------*/

    private void onDoubleTableClick() {
        insertMode = false;
        updateMode = true;

        Employee employee = tableEmployee.getSelectionModel().getSelectedItem();
        txtID.setText(employee.getEmp_no() + "");
        txtName.setText(employee.getFirst_name());
        txtLastName.setText(employee.getLast_name());
        pickerBirthDate.setValue(employee.getBirth_date().toLocalDate());
        pickerHireDate.setValue(employee.getHire_date().toLocalDate());
        comboGender.getSelectionModel().select(employee.getGender() + "");
    }

    private void reloadEmployeeList() {
        tableEmployee.setItems(employeeDAO.fetchAll(1000));
    }

    private void clearAll() {
        changeFormStatus();

        txtID.setText("");
        txtName.setText("");
        txtLastName.setText("");
        pickerBirthDate.setValue(null);
        pickerHireDate.setValue(null);
        comboGender.getSelectionModel().clearSelection();
        txtSearchID.clear();
        txtID.requestFocus();
    }

    private void changeFormStatus() {
        btnSave.setDisable(!insertMode);
        btnUpdate.setDisable(!updateMode);
        txtID.setDisable(!insertMode);
    }

    private void setNextID() {
        int id = employeeDAO.getNextID();
        txtID.setText((id + 1) + "");
    }

    private void searchEmployee() {
        String id = txtSearchID.getText();
        String name = txtName.getText();
        String lastName = txtLastName.getText();

        ObservableList<Employee> listEmp = null;

        if (id.length() == 0 && name.length() == 0 && lastName.length() == 0)
            reloadEmployeeList();
        else if (id.length() > 0)
            listEmp = employeeDAO.searchByOR(id, "", "");
        else if (name.length() > 0)
            if (lastName.length() > 0)
                listEmp = employeeDAO.searchByAND(name, lastName);
            else
                listEmp = employeeDAO.searchByOR(id, name, lastName);
        else
            listEmp = employeeDAO.searchByOR(id, name, lastName);

        tableEmployee.setItems(listEmp);
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------
     * -                                                Methods to display other windows
     * ------------------------------------------------------------------------------------------------------------------------
     */

    private void logOut() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/login_window.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        MyUtils.undecorateWindow(primaryStage, root, false);
        primaryStage.show();

        btnSave.getScene().getWindow().hide();
    }

    private void goToDepartments() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/departments_window.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Departments");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void goToEmpDept() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/emp_dept_window.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Employees By Departments");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**----------------------------------------------------------------------------------------------------------------------------*/

    /**
     * -----------------------------------------------------------------------------------------------------------------------
     * -                                                Methods to generate Reports
     * ------------------------------------------------------------------------------------------------------------------------
     */

    private void generateReportMaxSalaries() {
        File file = showSaveDialog();

        if(file != null) {

            File Rfile = MyUtils.refactorFileName(file, "pdf");

            Task<Void> reportTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    methodR1(Rfile.getPath());
                    return null;
                }
            };

            reportTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    MyUtils.makeDialog("Succeess", "Report: "+Rfile.getName()+" finished", null,
                            Alert.AlertType.CONFIRMATION).show();
                }
            });

            Thread thread = new Thread(reportTask);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        }
    }

    private void methodR1(String dest) {
        PDFReport pdfReport = null;
        try {
            pdfReport = new PDFReport(dest, "5 Max Salaries by Deparment");

            String[] headers = {"First Name", "Last Name", "Salary", "Department"};

            List<Department> departments = departmentDAO.fetchAll(100);

            for (Department dept : departments) {
                pdfReport.addSubtitle(dept.getName());
                String values[][] = generalDAO.getFiveMaxSalaryFromDept(dept.getDeptNo());
                pdfReport.addTable(headers, values);
            }

            pdfReport.closeDocument();
            System.out.println("Report Finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateReportSalaries(){
        File file = showSaveDialog();

        if(file != null) {

            File Rfile = MyUtils.refactorFileName(file, "pdf");

            Task<Void> reportTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    methodR2(Rfile.getPath());
                    return null;
                }
            };

            reportTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    MyUtils.makeDialog("Succeess", "Report: "+Rfile.getName()+" finished", null, Alert.AlertType.CONFIRMATION).show();
                }
            });

            Thread thread = new Thread(reportTask);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        }
    }

    private void methodR2(String dest){

        PDFReport pdfReport = null;
        try {
            pdfReport = new PDFReport(dest, "Salaries from Department");

            String[] headers1 = {"First Name", "Last Name", "Salary"};
            String[] headers2 = {"Department", "Employees"};

            List<Department> departments = departmentDAO.fetchAll(100);

            for (Department dept : departments) {

                pdfReport.addSubtitle(dept.getName());
                String table1[][] = generalDAO.getSalaryFromEmployeesOfDeparment(dept.getDeptNo());
                pdfReport.addTable(headers1, table1);
                pdfReport.addBlankLine();
            }

            pdfReport.addBlankLine();
            pdfReport.addBlankLine();
            pdfReport.addSubtitle("Total Employees for each Department");
            String table2[][] = generalDAO.getEmployeesOfDeparment();
            pdfReport.addTable(headers2, table2);

            pdfReport.closeDocument();
            System.out.println("Report Finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private File showSaveDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        Stage stage = ((Stage)btnDelete.getParent().getScene().getWindow());
        return fileChooser.showSaveDialog(stage);
    }

}
