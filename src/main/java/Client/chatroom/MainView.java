package main.java.Client.chatroom;
//注意：用户和群聊绝对不能重名！！！
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

/**
 * @ClassName MainView
 * @Description TODO
 * @date 2021/6/13 19:40
 * @Version 1.0
 */
//问题：群聊怎么更新chatreccder
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.Client.Model.ClientModel;
import main.java.Client.stage.ControlledStage;
import main.java.Client.stage.StageController;
import main.java.bean.ClientUser;
import main.java.bean.Group;
import main.java.bean.Message;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    private ListView<Group> groupChatlistView;

    @FXML
    private ListView<ClientUser> friendlistview;

    @FXML
    public Label labUserName;
    @FXML
    public Label labChatTip;
    @FXML
    public Label labUserCoumter;
    @FXML
    private TitledPane GroupChatTitlePane;

    @FXML
    public Button btnSend;
    private Gson gson = new Gson();
    private StageController stageController;
    private ClientModel model;
    private static MainView instance;
    private boolean pattern = GROUP; //chat model，用来标记通讯类型
    private static String seletUser = "";
//    private static String seletGroup="";
    private static String thisUser;
    private ObservableList<ClientUser> uselist;
    private ObservableList<ClientUser> friendlist;
    private ObservableList<Group> grouplist;
//    private HashMap<String, ArrayList<Message>> chatRecord;
//    三个列表共享chatReccder
    private static ObservableList<Message> chatReccder;
    public MainView(){
        super();
        instance=this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model=ClientModel.getInstance(thisUser);
        ClientModel.userList.remove(0);
        uselist=ClientModel.userList;
        friendlist=ClientModel.friendlist;
        grouplist=ClientModel.grouplist;
//        chatRecord.putAll(ClientModel.chatRecorderSum);
//        chatRecord.putAll(ClientModel.groupChatRecord);
        chatReccder=model.getChatRecoder();
        userGroup.setItems(uselist);
        groupChatlistView.setItems(grouplist);
        chatWindow.setItems(chatReccder);
        thisUser=model.getThisUser();
        labUserName.setText("Welcome " + model.getThisUser() + "!");
        //更新friendlist

        friendlistview.setItems(friendlist);
//        btnSend.addEventHandler(EventType< MouseEvent.MOUSE_CLICKED >,send);
        btnSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                send();
            }
        });
        btnHistory.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                Iterator<Map.Entry<String, ArrayList<Message>>> iterator= model.userSession.entrySet().iterator();
//                while(iterator.hasNext())
//                {
//                    Map.Entry entry = iterator.next();
//                    System.out.println(entry.getKey()+":"+entry.getValue());
//                    ArrayList<Message> a=(ArrayList<Message>) entry.getValue();
//                    for(Message m:a){
//                        System.out.println(m.getSpeaker()+"对"+model.getThisUser()+"说"+m.getContent());
//                    }
//                }
                try {
                    System.out.println("这里查看chatReccder");
                    System.out.println(chatReccder);
                    for(Message m:chatReccder)
                        System.out.println(m.getSpeaker()+"说"+m.getContent());
                    System.out.println("看完了");
                }catch (Exception e){

                }
            }
        });
//        这个事件需要改，因为我把私聊和群聊分开了
        ChangeListener<ClientUser> changeListenerSingle=(observable, oldValue, newValue) -> {
            ClientUser user = (ClientUser) newValue;
            System.out.println("You are selecting " + user.getUserName());
                pattern = SINGLE;
                if (!seletUser.equals(user.getUserName())) {
                    model.setChatUser(user.getUserName());
                    seletUser = user.getUserName();
                    labChatTip.setText("Chatting with " + seletUser);
                }
            chatReccder.clear();
            for (int i=0;i<model.getChatRecorderSum(newValue.getUserName()).size();i++){
                chatReccder.add(model.getChatRecorderSum(newValue.getUserName()).get(i));
            }
        };
        ChangeListener<Group> changeListenerGroup=(observable, oldValue, newValue) -> {
            Group group = (Group) newValue;
            System.out.println("You are selecting " + group.getGroupName());
            pattern = GROUP;
            if (!seletUser.equals(group.getGroupName())) {
                model.setChatUser(group.getGroupName());
                seletUser = group.getGroupName();
                labChatTip.setText("Chatting with " + seletUser);
            }
            chatReccder.clear();
//            这里是群聊，清除界面就好了。添加的记录应该是群聊记录，跟用户记录分开
            for (int i=0;i<model.getGroupchatRecorder(newValue.getGroupName()).size();i++){
                chatReccder.add(model.groupChatRecord.get(newValue.getGroupName()).get(i));
            }
//注意：在这里添加群聊记录
        };

        userGroup.getSelectionModel().selectedItemProperty().addListener(changeListenerSingle);
        friendlistview.getSelectionModel().selectedItemProperty().addListener(changeListenerSingle);
        groupChatlistView.getSelectionModel().selectedItemProperty().addListener(changeListenerGroup);
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
        friendlistview.setCellFactory(new Callback<ListView<ClientUser>, ListCell<ClientUser>>() {
            @Override
            public ListCell<ClientUser> call(ListView<ClientUser> param) {
                return new UserCell();
            }
        });
        groupChatlistView.setCellFactory(new Callback<ListView<Group>, ListCell<Group>>() {
            @Override
            public ListCell<Group> call(ListView<Group> param) {
                return new GroupCell();
            }
        });
//        扩展群聊功能
        GridPane gridpane=new GridPane();
        gridpane.add(new Label("群聊列表\t"),0,0);
        Button addGroup=new Button("+");
        gridpane.add(addGroup,1,0);
        GroupChatTitlePane.setGraphic(gridpane);
        addGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
//                点击标签则新建建立群聊界面
                String fxmlPath="/main/java/Client/stage/addGroupView.fxml";
                Stage stage=new Stage();
                try {
                    Scene scene = new Scene((Parent) FXMLLoader.load(getClass().getResource(fxmlPath)));
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void send(){
        if(pattern==GROUP){
//                    如果目前是群聊模式
            HashMap map=new HashMap();
            map.put(COMMAND,COM_CHATALL);
            map.put(GROUPNAME,seletUser);
            map.put(SPEAKER,model.getThisUser());
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
     * @Description: 填充chatWindow的人物
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
                        //分情况选择是删除还是新增
                        Button button=new Button("添加");
                        button.setCursor(Cursor.HAND);
                        button.setFont(Font.font(10));
                        button.setUnderline(true);
                        button.setStyle("-fx-background-color:#ffffff; -fx-text-fill: blue");
                        javafx.scene.image.ImageView imageHead = new ImageView(new Image("/main/resource/image/head.png"));
                        imageHead.setFitHeight(20);
                        imageHead.setFitWidth(20);
                        ClientUser user=(ClientUser) item;
                        ImageView imageStatus;
//                        判断是否为好友
                        if(ClientModel.friendlist.contains(item)){
                            button.setText("删除");
                        }else{
                            //在用户列表
                            for(ClientUser c:ClientModel.friendlist){
                                if(item.getUserName().equals(c.getUserName())){
                                    button.setDisable(true);
                                    break;
                                }
                            }
                        }
                        if(user.getUserName().equals("[group]")){
                            imageStatus = new ImageView(new Image("/main/resource/image/online.png"));
                        } else if(user.isNotify()==true){
                            imageStatus = new ImageView(new Image("/main/resource/image/message.png"));
                        }else {
                            if(user.getStatus()==null){
                                for(ClientUser c:ClientModel.userList){
                                    if(user.getUserName().equals(c.getUserName())){
                                        user.setStatus(c.getStatus());
                                        break;
                                    }
                                }
                            }
                            if(user.getStatus().equals("online")){
                                imageStatus = new ImageView(new Image("/main/resource/image/online.png"));
                            }else{
                                imageStatus = new ImageView(new Image("/main/resource/image/offline.png"));
                            }
                        }
                        imageStatus.setFitWidth(20);
                        imageStatus.setFitHeight(20);
                        Label label=new Label(user.getUserName());
                        hbox.getChildren().addAll(imageHead,label,imageStatus,button);
                        setGraphic(hbox);

                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                //在本地处理
//                                ClientModel.chatRecorderSum.get(seletUser).remove(item);
//                                ClientModel.friendlist.remove(item);
                                if(button.getText().equals("添加")){
                                    ClientModel.friendlist.add(item);
                                }else {
                                    ClientModel.friendlist.remove(item);
                                }
                                //更新好友列表
                                System.out.println("hello");

                            }
                        });
                    }else {
                        setGraphic(null);
                    }
                }
            });
        }
    }

    public class ChatCell extends ListCell<Message>{
        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if(item!=null){
                        VBox box=new VBox();
                        HBox hBox=new HBox();
                        Button button=new Button("删除");
                        button.setCursor(Cursor.HAND);
                        button.setFont(Font.font(10));
                        button.setUnderline(true);
                        button.setStyle("-fx-background-color:#ffffff; -fx-text-fill: blue");
                        TextFlow txtContent=new TextFlow(new Text(item.getContent()));
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
                        box.getChildren().addAll(hBox,txtContent,button);
                        setGraphic(box);
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                //在本地删除
                                ClientModel.chatRecorderSum.get(seletUser).remove(item);
                                //更新聊天信息
                                MainView.chatReccder.clear();
                                if(pattern==SINGLE)
                                for (int i=0;i<ClientModel.chatRecorderSum.get(seletUser).size();i++){
                                    chatReccder.add(ClientModel.chatRecorderSum.get(seletUser).get(i));
                                }
                                else
                                    for (int i=0;i<model.getGroupchatRecorder(seletUser).size();i++){
                                        chatReccder.add(model.groupChatRecord.get(seletUser).get(i));
                                    }
                            }
                        });
                    }else {
                        setGraphic(null);
                    }
                }
            });
        }
    }

    public static class GroupCell extends ListCell<Group>{
        @Override
        protected void updateItem(Group item, boolean empty) {
            super.updateItem(item, empty);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if(item!=null){
                        Group user=(Group) item;
                        Label label=new Label(user.getGroupName());
                        setGraphic(label);
                    }else {
                        setGraphic(null);
                    }
                }
            });
        }
    }
}