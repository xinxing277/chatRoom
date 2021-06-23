package main.java.Client.chatroom;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.Client.Model.ClientModel;
import main.java.Dao.DbUtils;
import main.java.bean.ClientUser;
import main.java.bean.Group;

/**
 * @ClassName addGroupController
 * @Description TODO
 * @date 2021/6/21 10:26
 * @Version 1.0
 */

public class addGroupController implements Initializable {
    @FXML
    private ListView<ClientUser> groupChatMenberListView;

    @FXML
    private TextField groupNameTextField;

    @FXML
    private Button ensureButton;
    private ObservableList<ClientUser> friendlist;
    private ObservableList<ClientUser> groupChatMember;
    private String groupChatName;
    public addGroupController() {
        this.friendlist=FXCollections.observableArrayList();
        this.groupChatMember = FXCollections.observableArrayList();
    }

    @FXML
    void createGroup(ActionEvent event) {
//        groupChatName=groupNameTextField.getText();
        Group g=new Group(groupNameTextField.getText());
//        g.setMember((ArrayList<ClientUser>) groupChatMember);
        for(ClientUser c:groupChatMember){
            g.addMember(c);
        }
        ClientModel.grouplist.add(g);
        //暂时只增加，如需要删除群则需要修改
        DbUtils.insert_group(groupNameTextField.getText(),g.getMember());
        Stage stage = (Stage) ensureButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        friendlist= ClientModel.friendlist;
        groupChatMenberListView.setItems(friendlist);
        groupChatMenberListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        groupChatMenberListView.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                groupChatMember =  groupChatMenberListView.getSelectionModel().getSelectedItems();
            }

        });
        groupChatMenberListView.setCellFactory(new Callback<ListView<ClientUser>, ListCell<ClientUser>>() {
            @Override
            public ListCell<ClientUser> call(ListView<ClientUser> param) {
                return new UserCell();
            }
        });

    }
    public ObservableList<ClientUser> getGroupChatMember(){
        return groupChatMember;
    }
    public static class UserCell extends ListCell<ClientUser>{
        @Override
        protected void updateItem(ClientUser item, boolean empty) {
            super.updateItem(item, empty);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if(item!=null){
                        ClientUser user=(ClientUser) item;
                        Label label=new Label(user.getUserName());
                        setGraphic(label);
                    }else {
                        setGraphic(null);
                    }
                }
            });
        }
    }
}
