package com.zhy.smail.user.view;

import com.zhy.smail.MainApp;
import com.zhy.smail.common.utils.KeySecurity;
import com.zhy.smail.component.SimpleDialog;
import com.zhy.smail.component.keyboard.control.VkProperties;
import com.zhy.smail.config.GlobalOption;
import com.zhy.smail.restful.RestfulResult;
import com.zhy.smail.restful.RfFaultEvent;
import com.zhy.smail.restful.RfResultEvent;
import com.zhy.smail.user.entity.UserInfo;
import com.zhy.smail.user.service.UserService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wenliz on 2017/2/14.
 */
public class UserEditController implements Initializable{
    private MainApp app;
    @FXML
    private Label lblTimer;
    @FXML
    private  Label lblTitle;
    @FXML
    private ToolBar topToolBar;
    @FXML
    private Region topLeft;
    @FXML
    private Region topRight;
    @FXML
    private TextField txtBuildingNo;
    @FXML
    private Label lblBuildingNo;
    @FXML
    private TextField txtUnitNo;
    @FXML
    private Label lblUnitNo;
    @FXML
    private TextField txtFloorNo;
    @FXML
    private Label lblFloorNo;
    @FXML
    private TextField txtRoomNo;
    @FXML
    private Label lblRoomNo;
    @FXML
    private TextField txtPhoneNo;
    @FXML
    private TextField txtCard1No;
    @FXML
    private TextField txtCard2No;
    @FXML
    private TextField txtCard3No;
    @FXML
    private TextField txtCard4No;
    @FXML
    private TextField txtCard5No;
    @FXML
    private TextField txtCard6No;
    @FXML
    private TextField txtCard7No;
    @FXML
    private TextField txtCard8No;
    @FXML
    private TextField txtCard9No;
    @FXML
    private TextField txtCard10No;
    @FXML
    private Label lblUserType;
    @FXML
    private RadioButton rdoManager;
    @FXML
    private RadioButton rdoAdvanceManager;
    @FXML
    private RadioButton rdoFactory;
    @FXML
    private HBox userNameTool;
    @FXML
    private Button saveAndAddButton;

    private UserInfo user;
    private int userClass;
    private List<TextField> txtCards = new ArrayList<>();

    public int getUserClass() {
        return userClass;
    }

    public void setUserClass(int userClass) {

        this.userClass = userClass;
        switch (userClass){
            case 0:
                showOwner();
                break;
            case 1:
                hideOwner();
                showDelivery();
                break;
            case 2:
                hideOwner();
                showManager();
                break;
        }
    }
    private void showOwner(){
        txtBuildingNo.setPrefWidth(100);
        txtBuildingNo.requestFocus();
        txtUnitNo.setPrefWidth(100);
        txtFloorNo.setPrefWidth(100);
        txtRoomNo.setPrefWidth(150);

        userNameTool.getChildren().remove(rdoAdvanceManager);
        userNameTool.getChildren().remove(rdoManager);
        userNameTool.getChildren().remove(rdoFactory);
        userNameTool.getChildren().remove(lblUserType);
    }

    private void showDelivery(){
        lblUserType.setVisible(true);
        rdoManager.setVisible(true);
        rdoAdvanceManager.setVisible(true);

        rdoManager.setText("普通快递员");
        rdoAdvanceManager.setText("邮政投递员");

    }

    private void showManager(){
        lblUserType.setVisible(true);
        rdoManager.setVisible(true);
        rdoAdvanceManager.setVisible(true);
        if(GlobalOption.currentUser.getUserType() == UserInfo.FACTORY_USER) {
            rdoFactory.setVisible(true);
        }
        else{
            rdoFactory.setVisible(false);
        }
    }

    private void hideOwner(){
        lblBuildingNo.setText("用户名:");
        userNameTool.getChildren().remove(lblRoomNo);
        userNameTool.getChildren().remove(lblUnitNo);
        userNameTool.getChildren().remove(lblFloorNo);
        userNameTool.getChildren().remove(txtFloorNo);
        userNameTool.getChildren().remove(txtRoomNo);
        userNameTool.getChildren().remove(txtUnitNo);

        txtBuildingNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_TEXT);
        txtBuildingNo.setPrefWidth(275);
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
        lblTitle.setText("修改用户");
        txtBuildingNo.setDisable(true);
        txtUnitNo.setDisable(true);
        txtFloorNo.setDisable(true);
        txtRoomNo.setDisable(true);
        saveAndAddButton.setVisible(false);
        showUser();
    }

    public MainApp getApp() {
        return app;
    }

    public void setApp(MainApp app) {
        this.app = app;
        app.createTimeout(lblTimer);
        txtBuildingNo.requestFocus();
    }

    private UserInfo createNewUser(){
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(-1);
        userInfo.setPassword(KeySecurity.encrypt("123456"));
        return userInfo;
    }

    public void initialize(URL location, ResourceBundle resources){
        user = createNewUser();
        HBox.setHgrow(topLeft, Priority.ALWAYS);
        HBox.setHgrow(topRight, Priority.ALWAYS);
        txtCards.add(txtCard1No);
        txtCards.add(txtCard2No);
        txtCards.add(txtCard3No);
        txtCards.add(txtCard4No);
        txtCards.add(txtCard5No);
        txtCards.add(txtCard6No);
        txtCards.add(txtCard7No);
        txtCards.add(txtCard8No);
        txtCards.add(txtCard9No);
        txtCards.add(txtCard10No);


        txtBuildingNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtUnitNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtFloorNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtRoomNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        txtPhoneNo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
        for(int i=0; i<txtCards.size(); i++){
            TextField txtCard = txtCards.get(i);
            txtCard.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
            txtCard.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if(event.getCode() == KeyCode.ENTER){
                        String cardNo = txtCard.getText();
                        if(cardNo !=null && cardNo.length()>0 && (cardNo.substring(0,1).equals(";")||cardNo.substring(0,1).equals("；"))){
                            txtCard.setText(cardNo.substring(1));
                        }
                    }
                }
            });
        }

        lblUserType.setVisible(false);
        rdoManager.setVisible(false);
        rdoAdvanceManager.setVisible(false);
        rdoFactory.setVisible(false);
        txtFloorNo.setDisable(true);
        txtBuildingNo.requestFocus();
    }

    @FXML
    private void onBackAction(ActionEvent event){
        UserListController controller =app.goUserList();
        controller.selectTab(userClass);
    }

    @FXML
    private void onSave(ActionEvent event){
        saveUser(false);
    }
    @FXML
    private void onSaveAndAdd(ActionEvent event){
        saveUser(true);
    }

    private void saveUser(boolean addNew){
        if(!assignUser()) return;

        UserService.save(user, new RestfulResult() {
            @Override
            public void doResult(RfResultEvent event) {
                if (event.getResult() == 0) {
                    SimpleDialog.showAutoCloseInfo(app.getRootStage(), "保存成功");
                    if (addNew) {
                        addNewUser();
                    } else {
                        UserListController controller = app.goUserList();
                        controller.selectTab(userClass);
                    }
                }
                // Modified By 罗鹏 Mar 21 2017
                else if (event.getResult() == -2) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "用户名 (" + user.getUserName() + ") 已经存在，请重新输入用户名！", "保存失败");
                } else if (event.getResult() == -3) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "电话号码 (" + user.getPhoneNo() + ") 已经属于其它用户，请重新输入电话号码！", "保存失败");
                    txtPhoneNo.requestFocus();
                } else if (event.getResult() == -11) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "卡号 (" + user.getCardNo1() + ") 已经属于其它用户，请重新输入卡号！", "保存失败");
                    txtCard1No.requestFocus();
                } else if (event.getResult() == -12) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "卡号 (" + user.getCardNo2() + ") 已经属于其它用户，请重新输入卡号！", "保存失败");
                    txtCard2No.requestFocus();
                } else if (event.getResult() == -13) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "卡号 (" + user.getCardNo3() + ") 已经属于其它用户，请重新输入卡号！", "保存失败");
                    txtCard3No.requestFocus();
                } else if (event.getResult() == -14) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "卡号 (" + user.getCardNo4() + ") 已经属于其它用户，请重新输入卡号！", "保存失败");
                    txtCard4No.requestFocus();
                } else if (event.getResult() == -15) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "卡号 (" + user.getCardNo5() + ") 已经属于其它用户，请重新输入卡号！", "保存失败");
                    txtCard5No.requestFocus();
                } else if (event.getResult() == -16) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "卡号 (" + user.getCardNo6() + ") 已经属于其它用户，请重新输入卡号！", "保存失败");
                    txtCard6No.requestFocus();
                } else if (event.getResult() == -17) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "卡号 (" + user.getCardNo7() + ") 已经属于其它用户，请重新输入卡号！", "保存失败");
                    txtCard7No.requestFocus();
                } else if (event.getResult() == -18) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "卡号 (" + user.getCardNo8() + ") 已经属于其它用户，请重新输入卡号！", "保存失败");
                    txtCard8No.requestFocus();
                } else if (event.getResult() == -19) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "卡号 (" + user.getCardNo9() + ") 已经属于其它用户，请重新输入卡号！", "保存失败");
                    txtCard9No.requestFocus();
                } else if (event.getResult() == -20) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "卡号 (" + user.getCardNo10() + ") 已经属于其它用户，请重新输入卡号！", "保存失败");
                    txtCard10No.requestFocus();
                } else {
                    SimpleDialog.showMessageDialog(app.getRootStage(), event.getMessage(), "保存失败");
                }
                // Ended By 罗鹏 Mar 21 2017
            }

            @Override
            public void doFault(RfFaultEvent event) {
                SimpleDialog.showMessageDialog(app.getRootStage(), event.getMessage(), "保存失败");
            }
        });
    }

    private void addNewUser(){
        UserInfo newUser = createNewUser();
        newUser.setBuildingNo(user.getBuildingNo());
        newUser.setUnitNo(user.getUnitNo());
        //newUser.setFloorNo(user.getFloorNo());
        if((user.getRoomNo()==null) || (user.getRoomNo().length()==0)){
            newUser.setRoomNo("1");
        }
        else{
            try {
                Integer roomId = Integer.valueOf(user.getRoomNo()) + 1;
                newUser.setRoomNo(String.valueOf(roomId));
            }
            catch (Exception e){
                newUser.setRoomNo("1");
            }
        }
        user = newUser;
        showUser();
    }

    private boolean assignUser(){
        switch (userClass){
            case 0:
                user.setBuildingNo(txtBuildingNo.getText());
                user.setUnitNo(txtUnitNo.getText());
                //user.setFloorNo(txtFloorNo.getText());
                user.setRoomNo(txtRoomNo.getText());
                user.setUserName(user.getBuildingNo()+user.getUnitNo()+user.getRoomNo());
                // Modified By 罗鹏 Mar 22 2017
                if (isEmpty(user.getUserName())) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "楼栋号、单元号、房号不能全部为空。", "");
                    txtRoomNo.requestFocus();
                    return false;
                }
                // Ended By 罗鹏 Mar 22 2017
                user.setUserType(UserInfo.OWNER);
                user.setPhoneNo(txtPhoneNo.getText());
                break;
            case 1:
                if(rdoManager.isSelected()) {
                    user.setUserType(UserInfo.DELIVERY);
                }
                else{
                    user.setUserType(UserInfo.MAILMAN);
                }
                user.setUserName(txtBuildingNo.getText());
                // Added By 罗鹏 Mar 22 2017
                user.setPhoneNo(txtPhoneNo.getText());
                // Ended By 罗鹏 Mar 22 2017
                break;
            case 2:
                if(rdoManager.isSelected()){
                    user.setUserType(UserInfo.ADMIN);
                }
                else if(rdoAdvanceManager.isSelected()){
                    user.setUserType(UserInfo.ADVANCED_ADMIN);
                }
                else {
                    user.setUserType(UserInfo.FACTORY_USER);
                }
                user.setUserName(txtBuildingNo.getText());
                user.setPhoneNo(txtPhoneNo.getText());
                break;
        }
        // Modified By 罗鹏 Mar 22 2017
        if (isEmpty(user.getUserName())) {
            SimpleDialog.showMessageDialog(app.getRootStage(), "用户名不能为空。", "");
            txtBuildingNo.requestFocus();
            return false;
        }
        if (userClass == 1) {
            if (isEmpty(user.getPhoneNo())) {
                SimpleDialog.showMessageDialog(app.getRootStage(), "电话号码不能为空。", "");
                txtPhoneNo.requestFocus();
                return false;
            }
        } else {
            if (!isEmpty(user.getPhoneNo())) {
                boolean flag = isValiedMobileNo(user.getPhoneNo());
                if (!flag) {
                    SimpleDialog.showMessageDialog(app.getRootStage(), "您输入的电话号码格式不正确。", "");
                    txtPhoneNo.requestFocus();
                    return false;
                }
            }
        }
        // Ended By 罗鹏 Mar 22 2017
        user.setPhoneNo(txtPhoneNo.getText());
        user.setCardNo1(txtCard1No.getText());
        user.setCardNo2(txtCard2No.getText());
        user.setCardNo3(txtCard3No.getText());
        user.setCardNo4(txtCard4No.getText());
        user.setCardNo5(txtCard5No.getText());
        user.setCardNo6(txtCard6No.getText());
        user.setCardNo7(txtCard7No.getText());
        user.setCardNo8(txtCard8No.getText());
        user.setCardNo9(txtCard9No.getText());
        user.setCardNo10(txtCard10No.getText());
        return true;
    }

    // Added By 罗鹏 Mar 22 2017
    // 判断输入的电话号码是否合法
    private boolean isValiedMobileNo(String phoneNo) {
        boolean flag = false;
        Pattern p = Pattern.compile("^((13[0-9])|(17[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNo);
        flag = m.matches();
        return flag;
    }

    private boolean isEmpty(String str){
        if("".equals(str)){
            return true;
        }else if(str == null){
            return true;
        }else if("".equals(str.trim())){
            return true;
        }else if(str.trim() == null){
            return true;
        }else {
            return false;
        }
    }
    // Ended By 罗鹏 Mar 22 2017

    private void showUser(){
        txtBuildingNo.requestFocus();
        switch (userClass){
            case 0:
                txtBuildingNo.setText(user.getBuildingNo());
                txtUnitNo.setText(user.getUnitNo());
                txtFloorNo.setText(user.getFloorNo());
                txtRoomNo.setText(user.getRoomNo());
                break;
            case 1:
                txtBuildingNo.setText(user.getUserName());
                if(user.getUserType() == UserInfo.DELIVERY){
                    rdoManager.setSelected(true);
                }
                else{
                    rdoAdvanceManager.setSelected(true);
                }
                break;
            case 2:
                txtBuildingNo.setText(user.getUserName());
                if(user.getUserType() == UserInfo.ADMIN){
                    rdoManager.setSelected(true);
                }
                else if(user.getUserType() == UserInfo.ADVANCED_ADMIN){
                    rdoAdvanceManager.setSelected(true);
                }
                else{
                    rdoFactory.setSelected(true);
                }

                break;
        }
        txtPhoneNo.setText(user.getPhoneNo());
        txtCard1No.setText(user.getCardNo1());
        txtCard2No.setText(user.getCardNo2());
        txtCard3No.setText(user.getCardNo3());
        txtCard4No.setText(user.getCardNo4());
        txtCard5No.setText(user.getCardNo5());
        txtCard6No.setText(user.getCardNo6());
        txtCard7No.setText(user.getCardNo7());
        txtCard8No.setText(user.getCardNo8());
        txtCard9No.setText(user.getCardNo9());
        txtCard10No.setText(user.getCardNo10());
    }
}
