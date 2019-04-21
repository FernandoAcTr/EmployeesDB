package model;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.List;

public class MyUtils {

    private static double xOffset = 0;
    private static double yOffset = 0;

    public static void undecorateWindow(final Stage stage, Parent root, boolean enableFullScreen){
        stage.initStyle(StageStyle.UNDECORATED);

        root.setOnMousePressed(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        root.setOnMouseDragged(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        if(enableFullScreen) {
            stage.setFullScreen(enableFullScreen);
            root.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == 2)
                        stage.setFullScreen(!stage.isFullScreen());
                }
            });
        }
    }

    public static Alert makeDialog(String title, String header, String content, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.setContentText(content);
        return alert;
    }

    /**
     * Add extension to a File
     * @param file
     * @param extension pdf, func, docx...
     * @return
     */
    public static File refactorFileName(File file, String extension){
        File refactorFile = file;
        if (file != null)
            if (!file.getName().endsWith("."+extension))
                refactorFile = new File(file.getPath() + "."+extension);

        return refactorFile;
    }
}
