/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workermanagement.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import workermanagement.model.Unit;
import workermanagement.model.WorkHelper;

/**
 * FXML Controller class
 * For creating new or editing existing units, depending on whether the mainIndex is less than 1
 * @author Rachel Orrell
 */
public class UnitInfoController implements Initializable, IndexedController {
    @FXML
    private Label titleLbl;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Label nameLbl;
    @FXML
    private TextField nameTxt;
    
    ArrayList<TextField> requiredFields = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        requiredFields.add(nameTxt); //add name textfield to requiredFields ArrayList
    }
    
    /**
     * Performs setup operations that must occur after controller parameters have been set
     */
    @Override
    public void setup() {
        boolean isNew = this.getMainIndex() < 1; //if primary id is less than 1, this must be a new unit
        titleLbl.setText(isNew ? "Add Unit" : "Edit Unit"); 
        if(!isNew) //if this is an existing unit, populate fields
            nameTxt.setText(WorkHelper.getUnitNameById(this.getMainIndex()));
    }

    @FXML
    private void handleButtonAction(ActionEvent ae) {
        if(!(ae.getTarget() instanceof Button)) return; //if target isn't button, there are no planned actions
        Button btn = (Button) ae.getTarget();
        String sourceFxml = "UnitInfo.fxml"; //current fxml file
        switch(btn.getText()) { //depending on button text
            case "Cancel":
                if(SceneUtility.showConfirmationDialog("cancel")) //have user confirm action
                    //go to ManageUnits page; no primary id needed when showing all units
                    (new SceneUtility()).changeScene(ae, sourceFxml, "ManageUnits.fxml", -1);
                break;
            case "Save":
                if(!SceneUtility.requiredFieldsCompleted(requiredFields)) { //check that required fields are completed
                    SceneUtility.showMissingFieldError(); //if not, show user an error
                    return; //no further action in this method needed
                }
                Unit u = new Unit(this.getMainIndex(), nameTxt.getText()); //create a unit from the primary id and entered data
                int result = this.getMainIndex() < 1 ? WorkHelper.insertUnit(u) : WorkHelper.updateUnit(u); //insert or update depending on the primary id
                if(result < 1) //if no rows were affected in the query
                    SceneUtility.showActionError("saving this unit"); //show user an error
                else 
                    //go to ManageUnits page; no primary id needed when showing all units
                    (new SceneUtility()).changeScene(ae, sourceFxml, "ManageUnits.fxml", 0);
                break;
        }
    }
    
}
