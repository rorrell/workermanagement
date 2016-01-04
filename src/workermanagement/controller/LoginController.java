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
public class LoginController implements Initializable, IndexedController {
    @FXML
    private TextField usernameTxt;
    @FXML
    private PasswordField passwordTxt;
    @FXML
    private Button newUserBtn;
    @FXML
    private Button loginBtn;
    @FXML
    private Button forgotPasswordBtn;
    
    ArrayList<TextField> requiredFields = new ArrayList<>();
    ArrayList<TextField> requiredFields2 = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       requiredFields.add(usernameTxt);
       requiredFields.add(passwordTxt);
       requiredFields2.add(usernameTxt);
       usernameTxt.textProperty().addListener(new TextFieldChangeListener(usernameTxt, 30));
    }    

    @FXML
    private void handleButtonAction(ActionEvent ae) {
        if(!(ae.getTarget() instanceof Button)) return; //if target isn't button, there are no planned actions
        Button btn = (Button) ae.getTarget();
        String sourceFxml = "Login.fxml"; //the current fxml page
        switch(btn.getText()) { //depending on button text
            case "New User":
                //go to UserInfo; no primary id needed to add new user
                (new SceneUtility()).changeScene(ae, sourceFxml, "UserInfo.fxml", -1);
                break;
            case "Login":
                if(!SceneUtility.requiredFieldsCompleted(requiredFields)) { //check that all the required fields are completed
                    SceneUtility.showMissingFieldError(); //if not, show the user an error
                    return;
                }
                User u = WorkHelper.getUserByUsername(usernameTxt.getText());
                if(u == null) {
                    SceneUtility.showError("No user found with that username.");
                    WorkHelper.resetSession();
                }
                else if(!u.checkPassword(passwordTxt.getText())) {
                    SceneUtility.showError("Invalid password.");
                    WorkHelper.resetSession();
                }
                else //username is found and password is correct
                    //go to ManageWorkers; no primary id needed to show all workers
                    (new SceneUtility()).changeScene(ae, sourceFxml, "ManageWorkers.fxml", -1);
                break;
            case "Forgot Password?":
                if(!SceneUtility.requiredFieldsCompleted(requiredFields2)) { //check that all the required fields are completed
                    SceneUtility.showMissingFieldError(); //if not, show the user an error
                    return;
                }
                else
                    //go to Challenge; pass username as custom message
                    (new SceneUtility()).changeScene(ae, sourceFxml, "Challenge.fxml", -1, usernameTxt.getText());
                break;
        }
    }
    
}
