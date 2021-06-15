package main.java.Client;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.Client.stage.StageController;

/**
 * @ClassName Main
 * @Description TODO
 * @date 2021/6/15 9:31
 * @Version 1.0
 */

public class MainApp extends Application {
    public static String mainViewID = "MainView";
    public static String mainViewRes = "MainView.fxml";

    public static String loginViewID = "LoginView";
    public static String loginViewRes = "LoginView.fxml";

    private StageController stageController;
    @Override
    public void start(Stage primaryStage) throws Exception {
        stageController=new StageController();
        stageController.setPrimaryStage("primaryStage",primaryStage);
        stageController.loadStage(loginViewID,loginViewRes, StageStyle.UNDECORATED);
        stageController.setStage(loginViewID);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
