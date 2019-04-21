package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Conexion;
import model.MyUtils;
import model.User;
import DAOClass.UserDAO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    JFXButton btnCancel, btnLogin;

    @FXML
    JFXTextField txtUser;

    @FXML
    JFXPasswordField txtPassword;

    private String userType;

    public static String USERTYPE_ADMIN = "admin";
    public static String USERTYPE_USER = "user";

    private UserDAO userDAO;

    public void initialize(URL location, ResourceBundle resources) {
        initData();
        initComponents();

        txtUser.setText("Fernando Acosta");
        txtPassword.setText("tortugas");
    }

    private void initData() {
        userDAO = new UserDAO(Conexion.getConnection());
    }

    private void initComponents() {
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (!Conexion.isClosed())
                    Conexion.Disconnect();
                System.exit(0);
            }
        });

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                goToMainWindow();
            }
        });
    }

    private boolean validateCredentials() {
        String user = txtUser.getText();
        String password = txtPassword.getText();
        User u = userDAO.validUser(user, password);
        userType = (u != null) ? u.getTypeUser() : null;

        return userType != null;
    }

    private void goToMainWindow() {

        if (validateCredentials()) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_window.fxml"));
            Parent root = null;

            try {
                MainController controller = new MainController(userType);
                loader.setController(controller);
                root = loader.load();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Scene scene = new Scene(root);

            Stage primaryStage = new Stage();
            primaryStage.setTitle("Main Window");
            primaryStage.setScene(scene);

            MyUtils.undecorateWindow(primaryStage, root, true);

            primaryStage.show();

            ((Stage) btnLogin.getScene().getWindow()).close();

        } else
            MyUtils.makeDialog("User invalid", "Please check your credentials", null, Alert.AlertType.WARNING).show();

    }
}
