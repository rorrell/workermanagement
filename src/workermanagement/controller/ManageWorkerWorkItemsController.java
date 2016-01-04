/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workermanagement.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import workermanagement.model.WorkHelper;
import workermanagement.model.Workitem;

/**
 * FXML Controller class
 * Displays all work items for a given worker and allows for performing work item operations
 * @author Rachel Orrell
 */
public class ManageWorkerWorkItemsController implements Initializable, IndexedController {
    @FXML
    private Label titleLbl;
    @FXML
    private Button backBtn;
    @FXML
    private Button addWorkitemBtn;
    @FXML
    private TableView<Workitem> workitemsView;
    @FXML
    private TableColumn<Workitem, String> dateCol;
    @FXML
    private TableColumn<Workitem, String> unitCol;
    @FXML
    private TableColumn<Workitem, String> qtyCol;
    @FXML
    private TableColumn<Workitem, String> rateCol;
    @FXML
    private TableColumn<Workitem, String> totalCol;
    @FXML
    private TableColumn<Workitem, Integer> actionCol;
    @FXML
    private Label totalLbl;
    @FXML
    private TextField startDate;
    @FXML
    private TextField endDate;
    @FXML
    private Button filterBtn;
    
    ObservableList<Workitem> workitems;
    FilteredList<Workitem> filteredData;
    BigDecimal total = new BigDecimal(0);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }   
    
    /**
     * Sets up the controller class (required so that controller parameters are already set)
     */
    @Override
    public void setup() {
        //mainindex parameters should contain current worker's id
        String name = WorkHelper.getWorkerNameById(this.getMainIndex()); //get current worker's name
        if(!name.equals("")) titleLbl.setText(titleLbl.getText() + " for " + name); //customize title
        workitems = FXCollections.observableList(WorkHelper.getWorkitemsByWorkerId(this.getMainIndex()));
        workitems.forEach(wi -> total = total.add(wi.totalProperty().getValue())); //for each work item, add the total dollar value to the total variable
        updateTotalLabel(); //update the total label
        workitems.addListener((ListChangeListener.Change<? extends Workitem> change) -> { //add listener to update the total label when a work item is deleted
            while(change.next()) {
                if(change.wasRemoved()) {
                    for(Workitem wi : change.getRemoved()) {
                        total = total.subtract(wi.totalProperty().getValue());
                    }
                }
            }
            updateTotalLabel();
        });
        filteredData = new FilteredList<>(workitems, p -> true); //wrap workitems in filtered list, initially displaying all data
        workitemsView.setItems(filteredData); //set table view items to all work item for this worker (filtered)
        dateCol.setCellValueFactory(cellData -> SceneUtility.formatAsDateProperty(cellData.getValue().dateProperty().getValue())); //set column to show workitem date, formatted
        unitCol.setCellValueFactory(cellData -> cellData.getValue().unitnameProperty()); //set column to show workitem unit name
        qtyCol.setCellValueFactory(cellData -> SceneUtility.formatAsDecimalProperty(cellData.getValue().qtyStringProperty())); //set column to show workitem quantity formatted as number with 2 decimal places
        rateCol.setCellValueFactory(cellData -> SceneUtility.formatAsCurrencyProperty(cellData.getValue().rateStringProperty())); //set column to show workitem rate formatted as currency
        totalCol.setCellValueFactory(cellData -> SceneUtility.formatAsCurrencyProperty(cellData.getValue().totalStringProperty())); //set column to show workitem total (rate times quantity)
        actionCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject()); //set workitem id to column, not to be displayed, but to be used for button actions
        actionCol.setCellFactory(column -> {
            TableCell<Workitem, Integer> cell = new TableCell<Workitem, Integer>(){
                @Override
                public void updateItem(Integer item, boolean empty) { //the itme should be the workitem id, as set using setCellValueFactory for this column
                    if(item != null && !empty) { //add edit and delete buttons for each workitem
                        HBox hb = new HBox();
                        hb.setAlignment(Pos.CENTER);
                        Button editBtn = new Button("Edit");
                        editBtn.setUserData(item); //set userdata for button as workitem id
                        editBtn.setOnAction(e -> handleButtonAction(e)); //set handler for mouse click action
                        Button deleteBtn = new Button("Delete");
                        deleteBtn.setUserData(item); //set userdata for button as workitem id
                        deleteBtn.setOnAction(e -> handleButtonAction(e));  //set handler for mouse click action
                        hb.getChildren().addAll(editBtn, deleteBtn); //add buttons to hbox
                        setGraphic(hb); //set column graphic as hbox
                    }
                }
            };
            return cell;	
        });
        //have the date formatted when the startDate textbox loses focus
        startDate.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if(!newValue) { //if textfield has lost focus
                    try {
                        startDate.setText(SceneUtility.formatAsDate(startDate.getText()));
                    }
                    catch(IllegalArgumentException iae) {
                        SceneUtility.showError(iae.getMessage());
                    }
                }
        });
        //have the date formatted when the endDate textbox loses focus
        endDate.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if(!newValue) { //if textfield has lost focus
                    try {
                        endDate.setText(SceneUtility.formatAsDate(endDate.getText()));
                    }
                    catch(IllegalArgumentException iae) {
                        SceneUtility.showError(iae.getMessage());
                    }
                }
        });
    } 

    @FXML
    private void handleButtonAction(ActionEvent ae) {
        Button btn = (Button) ae.getTarget();
        int id = -1;
        String sourceFxml = "ManageWorkerWorkItems.fxml"; //current fxml page
        if(btn.getUserData() != null && btn.getUserData() instanceof Integer) //if userdata contains expected data (workitem id)
            id = (int)btn.getUserData(); //set id as userdata
        switch(btn.getText()) { //depending on text of button
            case "Back to Workers":
                //go to ManageWorkers page; no primary id is needed when showing all workers
                (new SceneUtility()).changeScene(ae, sourceFxml, "ManageWorkers.fxml", -1);
                break;
            case "Add Work Item":
                //go to WorkItemInfo page; use worker id as primary id; no secondary id is needed when add new workitem
                (new SceneUtility()).changeScene(ae, sourceFxml, "WorkItemInfo.fxml", this.getMainIndex(), -1, "");
                break;
            case "Edit":
                //go to WorkItemInfo page; use worker id as primary id; use workitem id as secondary id to edit current workitem
                (new SceneUtility()).changeScene(ae, sourceFxml, "WorkItemInfo.fxml", this.getMainIndex(), id, "");
                break;
            case "Delete":
                if(SceneUtility.showConfirmationDialog("delete this work item")) { //have user confirm action
                    if(WorkHelper.deleteWorkItemById(id) == 0) //if no rows are affected by query
                        SceneUtility.showActionError("deleting this work item"); //show user error
                    else
                    {
                        int finalId = id; /*have to assign new variable so that is is effectively final; 
                        otherwise can't be used in lambda*/
                        workitems.removeIf(w -> w.getId() == finalId); //remove the deleted unit from the observable list of units assigned to the table view
                        workitemsView.refresh(); //refresh table view
                    }
                }
                break;
            case "Apply Filter":
                filteredData.setPredicate((Workitem wi) -> {
                    if(startDate.getText().trim().equals("") && endDate.getText().trim().equals("")) //if both startDate and endDate are empty
                        return true; //display all
                    Date start = SceneUtility.parseDateForDB(startDate.getText()); //may return null
                    Date end = SceneUtility.parseDateForDB(endDate.getText()); //may return null
                    if(startDate.getText().trim().equals("")) { //if startDate is empty
                        try {
                            return wi.getDate().compareTo(end) <= 0; //see if workitem date is <= end
                        }
                        catch(NullPointerException npe) { //if end was null
                            return false;
                        }
                    }
                    else if(endDate.getText().trim().equals("")) { //if endDate is empty
                        try {
                            return wi.getDate().compareTo(start) >= 0; //see if workitem date is >= start
                        }
                        catch(NullPointerException npe) { //if start was null
                            return false;
                        }
                    }
                    else { //if both startDate and endDate are populated
                        try {
                            return wi.getDate().compareTo(end) <= 0 && wi.getDate().compareTo(start) >= 0; //see if workitem date is between start and end (inclusive)
                        }
                        catch(NullPointerException npe) { //if start and/or end was null
                            return false;
                        }
                    }
                });
                workitemsView.refresh(); //refresh table view
                break;
                
        }
    }
    
    private void updateTotalLabel() {
        totalLbl.setText("Total: " + SceneUtility.formatAsCurrency(total.toPlainString()));
    }
    
}
