package main.java.Client.chatroom;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;

/**
 * @ClassName MainView
 * @Description TODO
 * @date 2021/6/13 19:40
 * @Version 1.0
 */

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import main.java.Client.Model.ClientModel;
import main.java.Client.stage.ControlledStage;
import main.java.Client.stage.StageController;
import main.java.bean.ClientUser;
import main.java.bean.Message;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import static main.java.Utils.Constants.*;

public class MainView implements ControlledStage, Initializable {

    @FXML
    public Button btnHistory;

    @FXML
    public ListView<Message> chatWindow;

    @FXML
    public TextArea textSend;

    @FXML
    public ListView<ClientUser> userGroup;

    @FXML
    public Label labUserName;
    @FXML
    public Label labChatTip;
    @FXML
    public Label labUserCoumter;

    @FXML
    public Button btnSend;
    private Gson gson = new Gson();
    private StageController stageController;
    private ClientModel model;
    private static MainView instance;
    private boolean pattern = GROUP; //chat model
    private String seletUser = "[group]";
    private static String thisUser;
    private ObservableList<ClientUser> uselist;
    private ObservableList<Message> chatReccder;

    public MainView(){
        super();
        instance=this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model=ClientModel.getInstance();
        uselist=model.getUserList();
        chatReccder=model.getChatRecoder();
        userGroup.setItems(uselist);
        chatWindow.setItems(chatReccder);
        thisUser=model.getThisUser();
        labUserName.setText("Welcome " + model.getThisUser() + "!");
        btnSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(pattern==GROUP){
//                    如果目前是群聊模式
                    HashMap map=new HashMap();
                    map.put(COMMAND,COM_CHATALL);
                    map.put(CONTENT,textSend.getText().trim());
                    model.sentMessage(gson.toJson(map));
                }else if(pattern==SINGLE){
                    //如果是私聊
                    HashMap map=new HashMap();
                    map.put(COMMAND,COM_CHATWITH);
                    map.put(RECEIVER,seletUser);
                    map.put(SPEAKER,model.getThisUser());
                    map.put(CONTENT,textSend.getText().trim());
                    model.sentMessage(gson.toJson(map));
                }
                textSend.setText("");
            }
        });
//        这个事件十分让人迷惑
        userGroup.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ClientUser user = (ClientUser) newValue;
            System.out.println("You are selecting " + user.getUserName());
            if (user.getUserName().equals("[group]")) {
                pattern = GROUP;
                if (!seletUser.equals("[group]")) {
                    model.setChatUser("[group]");
                    seletUser = "[group]";
                    labChatTip.setText("Group Chat");
                }
            } else {
                pattern = SINGLE;
                if (!seletUser.equals(user.getUserName())) {
                    model.setChatUser(user.getUserName());
                    seletUser = user.getUserName();
                    labChatTip.setText("Chatting with " + seletUser);
                }
            }
        });

        chatWindow.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> param) {
                return new ChatCell();
            }
        });

        userGroup.setCellFactory(new Callback<ListView<ClientUser>, ListCell<ClientUser>>() {
            @Override
            public ListCell<ClientUser> call(ListView<ClientUser> param) {
                return new UserCell();
            }
        });
    }
    public static MainView getInstance() {
        return instance;
    }
/**
 * @Description:返回发送的内容
* @return javafx.scene.control.TextArea
 **/
    public TextArea getMessageBoxTextArea(){
        return textSend;
    }
    public Label getLabUserCoumter(){
        return labUserCoumter;
    }
    @Override
    public void setStageController(StageController stageController) {
        this.stageController=stageController;
    }
    /**
     * @Description:填充chatWindow的人物
     **/
    public static class UserCell extends ListCell<ClientUser>{
        @Override
        protected void updateItem(ClientUser item, boolean empty) {
            super.updateItem(item, empty);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if(item!=null){
                        HBox hbox=new HBox();
                        javafx.scene.image.ImageView imageHead = new ImageView(new Image("/main/resource/image/head.png"));
                        imageHead.setFitHeight(20);
                        imageHead.setFitWidth(20);
                        ClientUser user=(ClientUser) item;
                        ImageView imageStatus;
                        if(user.getUserName().equals("[group]")){
                            imageStatus = new ImageView(new Image("/main/resource/image/online.png"));
                        } else if(user.isNotify()==true){
                            imageStatus = new ImageView(new Image("/main/resource/image/message.png"));
                        }else {
                            if(user.getStatus().equals("online")){
                                imageStatus = new ImageView(new Image("/main/resource/image/online.png"));
                            }else{
                                imageStatus = new ImageView(new Image("/main/resource/image/offline.png"));
                            }
                        }
                        imageStatus.setFitWidth(20);
                        imageStatus.setFitHeight(20);
                        Label label=new Label(user.getUserName());
                        hbox.getChildren().addAll(imageHead,label,imageStatus);
                        setGraphic(hbox);
                    }else {
                        setGraphic(null);
                    }
                }
            });
        }
    }
    public static class ChatCell extends ListCell<Message>{
        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if(item!=null){
                        VBox box=new VBox();
                        HBox hBox=new HBox();
                        TextFlow txtContent=new TextFlow();
                        Label labUser=new Label(item.getSpeaker()+ "[" + item.getTimer() + "]");
                        labUser.setStyle("-fx-background-color: #7bc5cd; -fx-text-fill: white;");
                        ImageView image = new ImageView(new Image("/main/resource/image/head.png"));
                        image.setFitWidth(20);
                        image.setFitHeight(20);
                        hBox.getChildren().addAll(image,labUser);
                        if(item.getSpeaker().equals(thisUser)){
                            txtContent.setTextAlignment(TextAlignment.RIGHT);
                            hBox.setAlignment(Pos.CENTER_RIGHT);
                            box.setAlignment(Pos.CENTER_RIGHT);
                        }
                        box.getChildren().addAll(hBox,txtContent);
                        setGraphic(box);
                    }else {
                        setGraphic(null);
                    }
                }
            });
        }
    }
}
