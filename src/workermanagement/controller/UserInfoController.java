/*
 * Copyright (c) 2016, Rachel Orrell <rachel.orrell@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     - Neither the name of Rachel Orrell,  nor the names of its 
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package workermanagement.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import workermanagement.model.User;
import workermanagement.model.WorkHelper;

/**
 * FXML Controller class
 *
 * @author Rachel Orrell
 */
public class UserInfoController implements Initializable, IndexedController {
    @FXML
    private Button submitBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private TextField usernameTxt;
    @FXML
    private PasswordField passwordTxt;
    @FXML
    private TextField questionTxt;
    @FXML
    private TextField answerTxt;
    
    ArrayList<TextField> requiredFields = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        requiredFields.add(usernameTxt);
        requiredFields.add(passwordTxt);
        requiredFields.add(questionTxt);
        requiredFields.add(answerTxt);
        usernameTxt.textProperty().addListener(new TextFieldChangeListener(usernameTxt, 30));
        questionTxt.textProperty().addListener(new TextFieldChangeListener(questionTxt, 255));
        answerTxt.textProperty().addListener(new TextFieldChangeListener(answerTxt, 255));
    }    

    @FXML
    private void handleButtonAction(ActionEvent ae) {
        if(!(ae.getTarget() instanceof Button)) return; //if target isn't button, there are no planned actions
        Button btn = (Button) ae.getTarget();
        String sourceFxml = "UserInfo.fxml"; //the current fxml page
        switch(btn.getText()) { //depending on button text
            case "Submit":
                if(!SceneUtility.requiredFieldsCompleted(requiredFields)) { //check that all the required fields are completed
                    SceneUtility.showMissingFieldError(); //if not, show the user an error
                    return;
                }
                //create a user from entered data
                User u = new User(usernameTxt.getText(), passwordTxt.getText(), questionTxt.getText(), answerTxt.getText().toLowerCase());
                int result = WorkHelper.insertUser(u); //attempt to insert into database
                if(result < 1) //if no rows were affected in the query
                    SceneUtility.showActionError("saving this user"); //show user an error
                else
                    //go to ManageWorkers; no primary id needed to show all workers
                    (new SceneUtility()).changeScene(ae, sourceFxml, "ManageWorkers.fxml", -1);
                break;
            case "Cancel":
                if(SceneUtility.showConfirmationDialog("cancel")) //have user confirm action
                    //go to Login page; no primary id needed when logging in
                    (new SceneUtility()).changeScene(ae, sourceFxml, "Login.fxml", -1);
                break;
        }
    }
    
}
