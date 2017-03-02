package com.zhy.smail;

import com.zhy.smail.cabinet.view.BoxListController;
import com.zhy.smail.cabinet.view.CabinetListController;
import com.zhy.smail.common.controller.RootController;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.TimeoutTimer;
import com.zhy.smail.component.keyboard.control.KeyBoardPopup;
import com.zhy.smail.component.keyboard.control.KeyBoardPopupBuilder;
import com.zhy.smail.component.music.Speaker;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.config.LocalConfig;
import com.zhy.smail.delivery.view.*;
import com.zhy.smail.manager.view.ManagerController;
import com.zhy.smail.pickup.view.PickupController;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.serial.GatewayException;
import com.zhy.smail.serial.SerialGateway;
import com.zhy.smail.serial.UdpGateway;
import com.zhy.smail.setting.entity.SystemOption;
import com.zhy.smail.setting.service.OptionService;
import com.zhy.smail.task.ResponseManager;
import com.zhy.smail.task.SendManager;
import com.zhy.smail.user.service.UserService;
import com.zhy.smail.user.view.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Locale;

public class MainApp extends Application {
    private Scene rootScene;
    private Stage rootStage;
    private MainController mainController;
    private TimeoutTimer timer=null;
    private Thread responseThread;
    private ResponseManager responseManager;


    public TimeoutTimer getTimer() {
        return timer;
    }

    public void setTimer(TimeoutTimer timer) {
        this.timer = timer;
    }

    public Scene getRootScene() {
        return rootScene;
    }

    public Stage getRootStage() {
        return rootStage;
    }



    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = fxmlLoader.load();
        mainController = fxmlLoader.getController();
        mainController.setApp(this);
        primaryStage.setTitle("ZY Mail");
        rootScene = new Scene(root, 1280, 1024);
        rootScene.getStylesheets().add("style.css");
        primaryStage.setScene(rootScene);
        rootStage = primaryStage;
        //primaryStage.setMaximized(true);
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        rootScene.getWindow().centerOnScreen();
        initVK(rootStage);
        initCom();
        //initUDP();
        testConnection();
        Speaker.welcome();


        rootStage.addEventFilter(KeyEvent.KEY_PRESSED, keyEventEventHandler);
        rootStage.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventEventHandler);
        rootStage.addEventFilter(MouseEvent.MOUSE_MOVED, mouseEventEventHandler);
    }

    private void initVK(Stage primaryStage){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double height = rootScene.getWindow().getHeight();
        double width = rootScene.getWindow().getWidth();
        double scale =Math.min(height/500, width/600);

        KeyBoardPopupBuilder builder = KeyBoardPopupBuilder.create();
        builder.initScale(scale);
        builder.initLocale(Locale.CHINESE);
        KeyBoardPopup popup = builder.build();

        popup.addDoubleClickEventFilter(primaryStage);
        popup.addFocusListener(rootScene);
        popup.addGlobalFocusListener();
    }

    private void testConnection(){
        Task<Integer> testTask = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                updateMessage("正在连接服务器...");
                UserService.testConnection(new RestfulResult() {
                    @Override
                    public void doResult(RfResultEvent event) {
                        updateValue(0);
                        loadSetting();
                    }

                    @Override
                    public void doFault(RfFaultEvent event) {
                        if(event.getErrorNo() == -1){
                            updateMessage(event.getMessage());
                        }
                        else {
                            updateMessage("连接服务器(" + GlobalOption.serverIP + ")失败.本机进入维护模式，只有管理员才能登录.");
                            GlobalOption.serverIP = "127.0.0.1";
                        }
                        updateValue(-1);
                    }
                });
                return 1;

            }
        };
        SimpleDialog.showDialog(rootStage, testTask,"正在连接到服务器...", "连接");
    }

    public void loadSetting(){
        OptionService.getById(SystemOption.APP_TITLE_ID, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                SystemOption option = (SystemOption)event.getData();
                if(option!=null){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mainController.setAppTitle(option.getCharValue());
                        }
                    });

                }
            }

            @Override
            public void doFault(RfFaultEvent event) {

            }
        });

    }

    private void initUDP(){
        if(SendManager.gateway==null) {
            SendManager.gateway = new UdpGateway(8010);
        }
        if(SendManager.gateway.isOpened()) {
            SendManager.gateway.close();
        }
        else {
            UdpGateway gateway = (UdpGateway) SendManager.gateway;
            try {
                gateway.startGateway();
                responseManager = new ResponseManager();
                responseThread = new Thread(responseManager);
                responseThread.start();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private void initCom(){
        if(SendManager.gateway==null) {
            SendManager.gateway = new SerialGateway();
        }
        if(SendManager.gateway.isOpened()) {
            SendManager.gateway.close();
        }
        else {
            SerialGateway gateway = (SerialGateway) SendManager.gateway;
            String portName = gateway.getPortName();
            try {
                gateway.connect(gateway.getPortName());

               /* ParamValueList paramList = ParamManager.getCheckParams();
                SendingTask progressTask = new SendingTask(paramList, BusinessType.GET_MODULE_BASE, true);
                progressTask.valueProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        if(newValue == null) return;

                        Integer realValue = (Integer) newValue;
                        if(realValue == 0){
                            //connectButton.setText("关闭端口");


                        }
                        else if(realValue == -1){
                            SendManager.gateway.close();
                        }
                    }
                });
                SimpleDialog.modelDialog(progressTask,"正在检查设备有效性...", "检查设备有效性");*/
                responseManager = new ResponseManager();
                responseThread = new Thread(responseManager);
                responseThread.start();


            } catch (GatewayException e) {
                SimpleDialog.showMessageDialog(getRootStage(), "打开端口"+portName+"出错:" + e.getMessage(), "打开失败");
            }
        }
    }


    public static void main(String[] args) {
        LocalConfig local = LocalConfig.getInstance();
        GlobalOption.appMode = local.getAppMode();
        if(local.getAppMode() == LocalConfig.APP_MASTER){
            GlobalOption.serverIP = "127.0.0.1";
        }
        else{
            GlobalOption.serverIP = local.getServerIP();
        }
        launch(args);

    }

    public void goHome(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
            Parent root = fxmlLoader.load();
            mainController = fxmlLoader.getController();
            mainController.setApp(this);
            rootScene.setRoot(root);
        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(getRootStage(),e.getMessage(),"错误");
        }

    }

    public LoginController goLogin(boolean isDelivery){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("user/view/login.fxml"));
            Parent root = fxmlLoader.load();
            LoginController controller = fxmlLoader.getController();
            getRootScene().setRoot(root);
            controller.setDelivery(isDelivery);
            controller.setApp(this);
            return controller;
        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(getRootStage(),e.getMessage(),"错误");
        }
        return null;
    }

    public void goManager(){
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("manager/view/manager.fxml"));
            Parent root = fxmlLoader.load();
            ManagerController controller = fxmlLoader.getController();
            getRootStage().getScene().setRoot(root);
            controller.setApp(this);

        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(getRootStage(),e.getMessage(),"错误");
        }
    }

    public void goOwner(){
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("pickup/view/pickup.fxml"));
            Parent root = fxmlLoader.load();
            getRootStage().getScene().setRoot(root);
            PickupController controller = fxmlLoader.getController();
            controller.setApp(this);

        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(this.getRootStage(),e.getMessage(),"错误");
        }
    }

    public void goDelivery(){
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("delivery/view/delivery.fxml"));
            Parent root = fxmlLoader.load();
            getRootStage().getScene().setRoot(root);
            DeliveryController controller = fxmlLoader.getController();
            controller.setApp(this);

        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(this.getRootStage(),e.getMessage(),"错误");
        }
    }

    public ChangePasswordController goChangePassword(){
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("user/view/ChangePassword.fxml"));
            Parent root = fxmlLoader.load();
            getRootStage().getScene().setRoot(root);
            ChangePasswordController controller = fxmlLoader.getController();
            controller.setApp(this);
            return  controller;
        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(this.getRootStage(),e.getMessage(),"错误");
            return null;
        }
    }

    public void goCabinetList(){
        try {
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("cabinet/view/CabinetList.fxml"));
            Parent root = fxmlLoader.load();
            CabinetListController controller = (CabinetListController) fxmlLoader.getController();

            getRootStage().getScene().setRoot(root);
            controller.setApp(this);
        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(getRootStage(),e.getMessage(),"错误");
        }
    }

    public void goBoxList(){
        try{
            FXMLLoader fxmlLoader;

            fxmlLoader = new FXMLLoader(getClass().getResource("cabinet/view/BoxList.fxml"));

            Parent root = fxmlLoader.load();
            BoxListController controller = (BoxListController) fxmlLoader.getController();
            getRootStage().getScene().setRoot(root);
            controller.setApp(this);
        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(getRootStage(),e.getMessage(),"错误");
            e.printStackTrace();
        }
    }

    public UserListController goUserList(){
        try {
            FXMLLoader fxmlLoader;

            fxmlLoader = new FXMLLoader(getClass().getResource("user/view/UserList.fxml"));

            Parent root = fxmlLoader.load();
            UserListController controller = (UserListController) fxmlLoader.getController();
            controller.setApp(this);
            getRootStage().getScene().setRoot(root);
            return controller;
        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(getRootStage(),e.getMessage(),"错误");
            return null;
        }

    }
    public void goHelp(){
        loadFxml("user/view/Help.fxml");
    }

    public PutdownController goPutdown(){
        return  (PutdownController) loadFxml("delivery/view/putdown.fxml");
    }

    public PutmailController goPutmail(){
        return (PutmailController)loadFxml("delivery/view/putmail.fxml");
    }

    public UserChoiceController goUserChoice(){
        return (UserChoiceController) loadFxml("user/view/userChoice.fxml");
    }

    public ConfirmDeliveryController goConfirmDelivery(){
        return (ConfirmDeliveryController) loadFxml("delivery/view/confirmDelivery.fxml");
    }

    public UserViewController goUserView(){
        return (UserViewController)loadFxml("user/view/UserView.fxml");
    }

    public OccupyBoxController goOccupyBox( ){
        return (OccupyBoxController)loadFxml("delivery/view/occupyBox.fxml");
    }

    private RootController loadFxml(String path){
        try {
            FXMLLoader fxmlLoader;

            fxmlLoader = new FXMLLoader(getClass().getResource(path));

            Parent root = fxmlLoader.load();
            RootController controller = (RootController) fxmlLoader.getController();
            controller.setApp(this);
            getRootStage().getScene().setRoot(root);
            return controller;
        }
        catch (Exception e){
            SimpleDialog.showMessageDialog(getRootStage(),e.getMessage(),"错误");
            return null;
        }
    }




    EventHandler<KeyEvent> keyEventEventHandler = new EventHandler<KeyEvent>(){
        public  void handle(final KeyEvent keyEvent){
            if(timer != null){
                timer.restart();
            }
            Speaker.keyTypeSound();
        }
    };

    EventHandler<MouseEvent> mouseEventEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(timer !=null){
                timer.restart();
            }
        }
    };

    public void createTimeout(Label lblTimer){
        if(timer !=null){
           timer.cancel();
        }
        timer = new TimeoutTimer(lblTimer, GlobalOption.TimeoutTotal, new TimeoutTimer.TimeoutCallback() {
            @Override
            public void run() {
                goHome();
            }
        });
        timer.start();
    }

    public void stopApplication(){

        Platform.exit();
    }

    public void stop() throws Exception {
        if(timer !=null){
            timer.cancel();

            timer = null;
        }

        if(responseManager!=null) {
            responseManager.setCanceled(true);
            try {
                responseThread.join();
            } catch (InterruptedException e) {

            }
        }
        if(SendManager.gateway != null ) {
            if(SendManager.gateway.isOpened()) {
                SendManager.gateway.close();
            }
            SendManager.gateway = null;
        }

    }
}