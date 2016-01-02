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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import workermanagement.model.Unit;
import workermanagement.model.WorkHelper;
import workermanagement.model.Worker;
import workermanagement.model.Workitem;

/**
 * FXML Controller class
 * For creating new or editing work items for workers, depending on whether the subIndex is less than 1
 * @author Rachel Orrell
 */
public class WorkItemInfoController implements Initializable, IndexedController {
    @FXML
    private Label titleLbl;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Label dateLbl;
    @FXML
    private Label rateLbl;
    @FXML
    private Label qtyLbl;
    @FXML
    private Label unitLbl;
    @FXML
    private TextField dateTxt;
    @FXML
    private TextField rateTxt;
    @FXML
    private TextField qtyTxt;
    @FXML
    private ComboBox<Unit> unitSelect = new ComboBox<Unit>();
    
    ArrayList<TextField> requiredFields = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //add the required TextFields to the requiredFields ArrayList
        requiredFields.add(dateTxt);
        requiredFields.add(rateTxt);
        requiredFields.add(qtyTxt);
        //have the date formatted when the textbox loses focus
        dateTxt.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if(!newValue) { //if textfield has lost focus
                    try {
                        dateTxt.setText(SceneUtility.formatAsDate(dateTxt.getText()));
                    }
                    catch(IllegalArgumentException iae) {
                        SceneUtility.showError(iae.getMessage());
                    }
                }
        });
        //have the rate formatted as a number with 2 decimal places when the textbox loses focus
        rateTxt.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if(!newValue) //if textfield has lost focus
                    rateTxt.setText(SceneUtility.formatAsDecimal(rateTxt.getText()));
        });
        //have the qty formatted as a number with 2 decimal places when the textbox loses focus
        qtyTxt.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if(!newValue) //if textfield has lost focus
                    qtyTxt.setText(SceneUtility.formatAsDecimal(qtyTxt.getText()));
        });
        ObservableList<Unit> units = FXCollections.observableList(WorkHelper.getAllUnits());
        unitSelect.setItems(units); //set the unit combobox to hold the list of all units; works because of the toString method override in Unit
    } 
    
    /**
     * Performs setup operations that must occur after controller parameters have been set
     */
    @Override
    public void setup() {
        boolean isNew = this.getSubIndex() < 1; //if secondary id is less than one, this must be a new workitem
        titleLbl.setText(isNew ? "Add Work Item" : "Edit Work Item");
        if(!isNew) { //if this is an existing work item, populate all the fields
            Workitem wi = WorkHelper.getWorkitemById(this.getSubIndex());
            if(wi != null) {
                dateTxt.setText(SceneUtility.formatAsDate(wi.getDate()));
                rateTxt.setText(wi.getRate().toPlainString());
                qtyTxt.setText(wi.getQty().toPlainString());
                unitSelect.getSelectionModel().select(wi.getUnit()); //works because of the equals method override in Unit
            }
        }
        else {
            Worker w = WorkHelper.getWorkerById(this.getMainIndex());
            if(w != null) rateTxt.setText(w.getCurrentrate().toPlainString()); //set rate to worker's default, which user can change if desired
        }
    }   

    @FXML
    private void handleButtonAction(ActionEvent ae) {
        if(!(ae.getTarget() instanceof Button)) return; //if target isn't button, there are no planned actions
        Button btn = (Button) ae.getTarget();
        String sourceFxml = "WorkItemInfo.fxml"; //the current fxml page
        switch(btn.getText()) { //depending on button text
            case "Cancel":
                if(SceneUtility.showConfirmationDialog("cancel")) { //have user confirm action
                    //go back to ManageWorkerWorkItems; mainindex holds worker id to load the correct worker's work items
                    (new SceneUtility()).changeScene(ae, sourceFxml, "ManageWorkerWorkItems.fxml", this.getMainIndex());
                }
                break;
            case "Save":
                if(!SceneUtility.requiredFieldsCompleted(requiredFields) || unitSelect.getSelectionModel().getSelectedItem() == null) {  //check that all the required fields are completed
                    SceneUtility.showMissingFieldError();  //if not, show the user an error
                    return;
                }
                //create a work item from the work item id and the values from the input fields
                Workitem wi = new Workitem(this.getSubIndex(), SceneUtility.parseDateForDB(dateTxt.getText()), 
                        new BigDecimal(SceneUtility.removeNumberFormatting(rateTxt.getText(), new char[]{'.'})),
                        new BigDecimal(SceneUtility.removeNumberFormatting(qtyTxt.getText(), new char[]{'.'})), 
                        this.getMainIndex(), unitSelect.getSelectionModel().getSelectedItem());
                int result = this.getSubIndex() < 1 ? WorkHelper.insertWorkItem(wi) : WorkHelper.updateWorkItem(wi); //insert or update depending on whether the secondary id is < 1
                if(result < 1) //if no rows were affected by the query
                    SceneUtility.showActionError("saving this work item"); //show the user an error
                else 
                    //go back to ManageWorkerWorkItems; mainindex holds worker id to load the correct worker's work items
                    (new SceneUtility()).changeScene(ae, sourceFxml, "ManageWorkerWorkItems.fxml", this.getMainIndex());
                break;
        }
    }
    
}
