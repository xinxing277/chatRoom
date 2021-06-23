package main.java.Client.Login;

/**
 * @ClassName LoginViewController
 * @Description TODO
 * @date 2021/6/13 19:38
 * @Version 1.0
 */

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.java.Client.MainApp;
import main.java.Client.Model.ClientModel;
import main.java.Client.stage.ControlledStage;
import main.java.Client.stage.StageController;
import main.java.Dao.DbUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController implements ControlledStage, Initializable {

    @FXML
    TextField txtUsername;

    @FXML
    Button minimizeBtn;

    @FXML
    Button btn_signIn;

    @FXML
    TextField textPassword;

    @FXML
    Button closeBtn;

    @FXML
    ImageView imageView;

    @FXML
    Button btn_login;

    StageController myController;
    ClientModel model;

    public void setStageController(StageController stageController) {
        this.myController=stageController;
        model=ClientModel.getInstance(txtUsername.getText());
    }

    @FXML
    void signUp(ActionEvent event) {

    }

/**
 * @Description: 最小化窗口,还不是很了解怎么实现
 * @param event:

 **/
    @FXML
    void minBtnAction(ActionEvent event) {
        Stage stage = myController.getStage(MainApp.loginViewID);
        stage.setIconified(true);
    }

    @FXML
    void closeBtnAction(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    /**
     * @Description: 从登陆界面转去主界面
     **/
    public void goToMain(){
        myController.loadStage(MainApp.mainViewID,MainApp.mainViewRes);
        myController.setStage(MainApp.mainViewID,MainApp.loginViewID);
        myController.getStage(MainApp.mainViewID).setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                model.backup();
                model.disConnect();
            }
        });
    }

    public void showError(String error){
//        Alert需要了解是什么
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wechat");
        alert.setContentText("登录失败 " + error);
        alert.show();
    }

    public void logIn(ActionEvent event) {
        StringBuffer result=new StringBuffer();
       // model=ClientModel.getInstance(txtUsername.getText());
        if(model.CheckLogin(txtUsername.getText(),textPassword.getText(),result,0)){
            goToMain();
        }else {
            showError(result.toString());
        }
    }
}

