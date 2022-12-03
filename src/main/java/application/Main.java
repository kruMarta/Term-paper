package application;

import database.DatabaseManager;
import global.GlobalVariables;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import static global.GlobalVariables.*;

/**
 * Program starts here. Sets handler to save logger info to text file, connects database and shows user interface.
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        handler = new FileHandler("Program logs.txt");
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(GlobalVariables.handler);
        logger.info("Program started");

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("screen1.fxml")));
        Scene scene = new Scene(root);
        Image icon = new Image(LOGO);
        stage.getIcons().add(icon);
        stage.setTitle("Candy shop");

        DatabaseManager.connectToDB();

        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            event.consume();
            exit(stage);
        });
    }


    public void exit(Stage stage) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("An exit");
        alert.setHeaderText("You`re about to close the app!");
        alert.setContentText("Are you sure tou want to exit?");
        if(alert.showAndWait().get()== ButtonType.OK) {
            DatabaseManager manager = new DatabaseManager();
            manager.clearTable();
            stage.close();
        }
    }
}