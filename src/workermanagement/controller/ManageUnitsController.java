/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workermanagement.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import workermanagement.model.Unit;
import workermanagement.model.WorkHelper;

/**
 * FXML Controller class
 * Displays all units in a table view and allows for performing unit operations
 * @author Rachel Orrell
 */
public class ManageUnitsController implements Initializable, IndexedController {
    @FXML
    private Label titleLbl; 
    @FXML
    private Button backBtn;
    @FXML
    private Button addUnitBtn;
    @FXML
    private TableView<Unit> unitsView;
    @FXML
    private TableColumn<Unit, String> nameCol;
    @FXML
    private TableColumn<Unit, Integer> actionCol;
    
    private ObservableList<Unit> unititems;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        unititems = FXCollections.observableList(WorkHelper.getAllUnits());
        unitsView.setItems(unititems);  //set table view items to all units from database
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty()); //set column to show unit name
        actionCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject()); //assign unit id to action column, not to be displayed, but for use in button actions
        actionCol.setCellFactory(column -> {
            TableCell<Unit, Integer> cell = new TableCell<Unit, Integer>(){
                @Override
                public void updateItem(Integer item, boolean empty) { //item is the unit id
                    if(item != null && !empty) { //add edit and delete buttons to column
                        HBox hb = new HBox();
                        hb.setAlignment(Pos.CENTER);
                        Button editBtn = new Button("Edit");
                        editBtn.setUserData(item); //set user data for button as unit id
                        editBtn.setOnAction(e -> handleButtonAction(e)); //set handler for mouse click action
                        Button deleteBtn = new Button("Delete");
                        deleteBtn.setUserData(item); //set user data for button as unit id
                        deleteBtn.setOnAction(e -> handleButtonAction(e)); //set handler for mouse click action
                        hb.getChildren().addAll(editBtn, deleteBtn);  //add buttons to hbox
                        setGraphic(hb); //set column graphic as hbox
                    }
                }
            };
            return cell;	
        });
    } 

    @FXML
    private void handleButtonAction(ActionEvent ae) {
        if(!(ae.getTarget() instanceof Button)) return; //if the target isn't a button there is no planned action
        Button btn = (Button) ae.getTarget(); 
        int id = -1;
        String sourceFxml = "ManageUnits.fxml"; //the current fxml page
        if(btn.getUserData() != null && btn.getUserData() instanceof Integer) //if userdata contains expected data (user id)
            id = (int)btn.getUserData(); //set id to userdata
        switch(btn.getText()) { //depending on button text
            case "Back to Workers":
                //go to ManageWorkers page; no primary id is needed when showing all workers
                (new SceneUtility()).changeScene(ae, sourceFxml, "ManageWorkers.fxml", -1);
                break;
            case "Add Unit":
                //go to UnitInfo page; no primary id is needed when adding a new unit
                (new SceneUtility()).changeScene(ae, sourceFxml, "UnitInfo.fxml", -1);
                break;
            case "Edit":
                //go to UnitInfo page; use unit id as primary id in order to edit the selected unit
                (new SceneUtility()).changeScene(ae, sourceFxml, "UnitInfo.fxml", id);
                break;
            case "Delete":
                if(SceneUtility.showConfirmationDialog("delete this unit")) { //have user confirm action
                    if(WorkHelper.deleteUnitById(id) == 0) //if no records were affected by delete query
                        SceneUtility.showActionError("deleting this unit"); //show user error message
                    else
                    {
                        int finalId = id; /*have to assign new variable so that is is effectively final; 
                        otherwise can't be used in lambda*/
                        unititems.removeIf(u -> u.getId() == finalId); //remove the deleted unit from the observable list of units assigned to the table view
                        unitsView.refresh(); //refresh the table view
                    }
                }
                break;
        }
    }
    
}
