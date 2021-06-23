package main.java.Client.stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.HashMap;

/**
 * @ClassName StageController
 * @Description 因为有多个stage存在，所以需要一个StageController对Stage进行管理
 * 内部包含对Stage进行操作如设置，加载等操作
 * 其实这个类就是个Stage管理员
 * @date 2021/6/15 8:38
 * @Version 1.0
 */

public class StageController {
    //存储stage的map
    private HashMap<String, Stage> stages = new HashMap<String, Stage>();

    /**
     * @param name:  Stage的名称
     * @param stage: Stage对象
     * @Description:把加载好的Stage放到Map
     **/
    public void addStage(String name, Stage stage) {
        stages.put(name, stage);
    }

    public Stage getStage(String name) {
        return stages.get(name);
    }

    /**
     * @param primaryStageName:
     * @param primaryStage:
     * @Description:保存主舞台对象
     **/
    public void setPrimaryStage(String primaryStageName, Stage primaryStage) {
        this.addStage(primaryStageName, primaryStage);
    }

    public boolean loadStage(String name, String resources, StageStyle... styles) {
        try {
            //加载FXML资源文件
//            System.out.println(getClass().getResource(resources));
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resources));
            Pane tempPane = (Pane) loader.load();

            //通过Loader获取FXML对应的ViewCtr，并将本StageController注入到ViewCtr中
            ControlledStage controlledStage = (ControlledStage) loader.getController();
            controlledStage.setStageController(this);
            //构造对应的Stage
            Scene tempScene = new Scene(tempPane);
            Stage tempStage = new Stage();
            tempStage.setScene(tempScene);
            //配置initStyle
            for (StageStyle style : styles) {
                tempStage.initStyle(style);
            }

            //将设置好的Stage放到HashMap中
            this.addStage(name, tempStage);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * @Description:只显示Satge而不隐藏
     * @param name:
     * @return boolean
     **/
    public boolean setStage(String name){
        this.getStage(name).show();
        return true;
    }
    /**
     * @Description:显示Stage并隐藏给定的Stage
     * @param show:
     * @param close:
     * @return boolean
     **/
    public boolean setStage(String show,String close){
        getStage(close).close();
        setStage(show);
        return true;
    }
    /**
     * @Description:从Map中删除Stage对象
     * @param name:
     * @return boolean
     **/
    public boolean unloadStage(String name){
        if(stages.remove(name)==null){
            System.out.println("窗口不存在，请检查名称");
            return false;
        }else {
            stages.remove(name);
            System.out.println("窗口移除成功");
            return true;
        }
    }
}