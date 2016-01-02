/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workermanagement.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import workermanagement.model.WorkHelper;
import workermanagement.model.Worker;

/**
 * FXML Controller class
 * For creating or editing workers, depending on whether the mainIndex is less than 1
 * @author Rachel Orrell
 */
public class WorkerInfoController implements Initializable, IndexedController {
    @FXML
    private Label titleLbl;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Label firstnameLbl;
    @FXML
    private Label lastnameLbl;
    @FXML
    private Label phoneLbl;
    @FXML
    private Label rateLbl;
    @FXML
    private TextField firstnameTxt;
    @FXML
    private TextField lastnameTxt;
    @FXML
    private TextField phoneTxt;
    @FXML
    private TextField rateTxt;
    
    ArrayList<TextField> requiredFields = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        requiredFields.add(firstnameTxt);
        requiredFields.add(lastnameTxt);
        requiredFields.add(rateTxt);
        //have the rate formatted as a number with 2 decimal places when it loses focus
        rateTxt.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if(!newValue) //if textfield has lost focus
                    rateTxt.setText(SceneUtility.formatAsDecimal(rateTxt.getText()));
        });
    }    
    
    /**
     * Performs setup operations that must occur after controller parameters have been set
     */
    @Override
    public void setup() {
        boolean isNew = this.getMainIndex() < 1; //if primary id is lest than 1, this must be a new woker
        titleLbl.setText(isNew ? "Add Worker" : "Edit Worker");
        if(!isNew) { //if this is an existing worker, populate the fields
            Worker w = WorkHelper.getWorkerById(this.getMainIndex());
            if(w != null) {
                firstnameTxt.setText(w.getFirstname());
                lastnameTxt.setText(w.getLastname());
                phoneTxt.setText(SceneUtility.formatAsPhoneNumber(w.getPhone()));
                rateTxt.setText(w.getCurrentrate().toPlainString());
            }
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent ae) {
        if(!(ae.getTarget() instanceof Button)) return; //if target isn't button, there are no planned actions
        Button btn = (Button) ae.getTarget();
        String sourceFxml = "WorkerInfo.fxml"; //the current fxml page
        switch(btn.getText()) { //depending on button text
            case "Cancel":
                if(SceneUtility.showConfirmationDialog("cancel")) //have the user confirm
                    //go back to ManageWorkers; no primary id needed to show all workers
                    (new SceneUtility()).changeScene(ae, sourceFxml, "ManageWorkers.fxml", -1);
                break;
            case "Save":
                if(!SceneUtility.requiredFieldsCompleted(requiredFields)) { //check that all the required fields are completed
                    SceneUtility.showMissingFieldError(); //if not, show the user an error
                    return;
                }
                //create a worker from the worker id and the values from the input fields
                Worker w = new Worker(this.getMainIndex(), firstnameTxt.getText(), lastnameTxt.getText(), 
                        SceneUtility.removeNumberFormatting(phoneTxt.getText(), null), 
                        new BigDecimal(SceneUtility.removeNumberFormatting(rateTxt.getText(), new char[]{'.'})));
                int result = this.getMainIndex() < 1 ? WorkHelper.insertWorker(w) : WorkHelper.updateWorker(w); //update or insert depending on the primary id
                if(result < 1) //if no rows are affected by the query
                    SceneUtility.showActionError("saving this worker"); //show the user an error
                else 
                    //go back to ManageWorkers; no primary id needed to show all workers
                    (new SceneUtility()).changeScene(ae, sourceFxml, "ManageWorkers.fxml", 0);
                break;
        }
    }

    @FXML
    private void handleKeyReleasedAction(KeyEvent ke) {
        TextField txt = (TextField) ke.getTarget();
        switch(txt.getId()) {
            case "phone":
                //format phone number
                txt.setText(SceneUtility.formatAsPhoneNumber(txt.getText()));
                break;
        }
        txt.end();
    }
    
}
