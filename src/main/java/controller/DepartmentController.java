package controller;

import DAOClass.DepartmentDAO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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
import model.Conexion;
import model.Department;
import model.MyUtils;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DepartmentController  implements Initializable {

    @FXML
    private JFXButton btnSave, btnDelete, btnEdit, btnRefresh, btnSearch, btnNew;

    @FXML
    private TableView<Department> tableDepartments;

    @FXML
    private JFXTextField txtNo, txtName;

    private DepartmentDAO departmentDAO;
    private boolean insertMode, updateMode, searchMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initData();
        initComponents();
        changeFormStatus();
    }

    private void initData(){
        insertMode = false;
        updateMode = false;
        searchMode = true;
        departmentDAO = new DepartmentDAO(Conexion.getConnection());
    }

    private void initComponents(){
        initTableView();

        tableDepartments.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2)
                    onDoubleTableClick();
            }
        });

        btnNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insertMode = true;
                updateMode = false;
                searchMode = false;
                changeFormStatus();
                clearAll();
            }
        });

        btnRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insertMode = false;
                updateMode = false;
                searchMode = true;
                changeFormStatus();
                reloadTableView();
            }
        });

        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insertDepartment();
                reloadTableView();
            }
        });

        btnEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateDaparment();
                reloadTableView();
            }
        });

        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteDepartment(tableDepartments.getSelectionModel().getSelectedItem());
                reloadTableView();
            }
        });

        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                searchDepartment();
            }
        });
    }

    private void initTableView(){
        TableColumn<Department, String> colID = new TableColumn<>("No.");
        TableColumn<Department, String> colName = new TableColumn<>("Name");

        colID.setCellValueFactory(new PropertyValueFactory<>("deptNo"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        tableDepartments.getColumns().addAll(colID, colName);
        reloadTableView();
    }

    private void reloadTableView(){
        ObservableList<Department> departments = departmentDAO.fetchAll(100);
        tableDepartments.setItems(departments);
    }

    private void onDoubleTableClick() {
        insertMode = false;
        updateMode = true;
        searchMode = false;

        Department department = tableDepartments.getSelectionModel().getSelectedItem();
        txtNo.setText(department.getDeptNo());
        txtName.setText(department.getName());

        changeFormStatus();
    }

    private void changeFormStatus(){
        if(insertMode){
            txtNo.setDisable(false);
            btnSave.setDisable(false);
            btnDelete.setDisable(true);
            btnEdit.setDisable(true);
        }else if(updateMode){
            txtNo.setDisable(true);
            btnSave.setDisable(true);
            btnDelete.setDisable(false);
            btnEdit.setDisable(false);
        }else if(searchMode){
            txtNo.setDisable(false);
            btnSave.setDisable(true);
            btnDelete.setDisable(true);
            btnEdit.setDisable(true);
        }
    }

    private void clearAll(){
        txtNo.clear();
        txtName.clear();
        txtNo.requestFocus();
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------
     * -                                                CRUD OPERATIONS
     * ------------------------------------------------------------------------------------------------------------------------
     */

    private void insertDepartment(){
        String deptNo = txtNo.getText();
        String name = txtName.getText();
        if(departmentDAO.insert(new Department(deptNo, name))) {
            MyUtils.makeDialog("Success", "Correctly added department", null, Alert.AlertType.INFORMATION).show();
            clearAll();
        }
        else
            MyUtils.makeDialog("Error", "An error has occurred while adding department", null, Alert.AlertType.WARNING).show();
    }

    private void updateDaparment(){
        String deptNo = txtNo.getText();
        String name = txtName.getText();

        if(departmentDAO.update(new Department(deptNo, name))) {
            MyUtils.makeDialog("Success", "Correctly updated department", null, Alert.AlertType.INFORMATION).show();
            clearAll();
        }
        else
            MyUtils.makeDialog("Error", "An error has occurred while updating department", null, Alert.AlertType.WARNING).show();
    }

    private void deleteDepartment(Department department){
        Alert confirm = MyUtils.makeDialog("Confirmation", "Are you sure you want to DELETE "+department.getDeptNo()+"?",
                null, Alert.AlertType.CONFIRMATION);

        Optional<ButtonType> result = confirm.showAndWait();

        if(result.isPresent())
            if(result.get() == ButtonType.OK)
                if(departmentDAO.delete(department.getDeptNo())) {
                    MyUtils.makeDialog("Success", "Department deleted successfully", null, Alert.AlertType.INFORMATION).show();
                    clearAll();
                }
                else
                    MyUtils.makeDialog("Error", "An error has occurred while deleting department", null, Alert.AlertType.WARNING).show();

    }

    private void searchDepartment(){
        String deptNo = txtNo.getText();
        String name = txtName.getText();

        ObservableList<Department> list = departmentDAO.searchByOR(deptNo, name);
        tableDepartments.setItems(list);
    }

    /**------------------------------------------------------------------------------------------------------------------------**/


}
