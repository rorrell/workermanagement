/* 
 * Copyright (c) 2015, Rachel Orrell <rachel.orrell@gmail.com>
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
import workermanagement.model.WorkHelper;
import workermanagement.model.Worker;

/**
 * Displays all workers and allows for performing worker operations; also provides navigations to unit management
 * @author Rachel Orrell
 */
public class ManageWorkersController implements Initializable, IndexedController {
    
    @FXML
    Label titleLbl;
    @FXML
    TableView<Worker> workerView;
    @FXML
    TableColumn<Worker, String> nameCol;
    @FXML
    TableColumn<Worker, String> phoneCol;
    @FXML
    TableColumn<Worker, String> rateCol;
    @FXML
    TableColumn<Worker, Integer> actionCol;
    @FXML
    Button addWorkerBtn;
    @FXML
    Button manageUnitsBtn;
    
    ObservableList<Worker> workers;
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        workers = FXCollections.observableList(WorkHelper.getAllWorkers());
        workerView.setItems(workers); //set table view items to all workers
        nameCol.setCellValueFactory(cellData -> cellData.getValue().fullnameProperty()); //set column to show fullname (lastname, firstname)
        phoneCol.setCellValueFactory(cellData -> SceneUtility.formatAsPhoneNumberProperty(cellData.getValue().phoneProperty())); //set column to show formatted phone number
        rateCol.setCellValueFactory(cellData -> SceneUtility.formatAsCurrencyProperty(cellData.getValue().currentrateStringProperty())); //set column to show current rate formatted as currency
        actionCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject()); //set column to id, not to display, but for button operations
        actionCol.setCellFactory(column -> {
            TableCell<Worker, Integer> cell = new TableCell<Worker, Integer>(){
                @Override
                public void updateItem(Integer item, boolean empty) { //item is worker id, as set in setCellValueFactory for this column
                    if(item != null && !empty) { //add edit, delete, and manage work buttons
                        HBox hb = new HBox();
                        hb.setAlignment(Pos.CENTER);
                        Button editBtn = new Button("Edit");
                        editBtn.setUserData(item); //set worker id to userdata
                        editBtn.setOnAction(e -> handleButtonAction(e)); //set handler for mouse click action
                        Button deleteBtn = new Button("Delete");
                        deleteBtn.setUserData(item);
                        deleteBtn.setOnAction(e -> handleButtonAction(e));
                        Button manageWorkItemsBtn = new Button("Manage Work");
                        manageWorkItemsBtn.setUserData(item);
                        manageWorkItemsBtn.setOnAction(e -> handleButtonAction(e));
                        hb.getChildren().addAll(editBtn, deleteBtn, manageWorkItemsBtn); //add all buttons to hbox
                        setGraphic(hb); //set column graphic to hbox
                    }
                }
            };
            return cell;	
        });
    }  
    
    @FXML
    private void handleButtonAction(ActionEvent ae) {
        if(!(ae.getTarget() instanceof Button)) return; //if target isn't button, there are no planned actions
        Button btn = (Button) ae.getTarget();
        int id = -1;
        String sourceFxml = "ManageWorkers.fxml"; //current fxml page
        if(btn.getUserData() != null && btn.getUserData() instanceof Integer) //if userdata holds expected data (worker id)
            id = (int)btn.getUserData(); //set id to userdata
        switch(btn.getText()) {
            case "Add Worker":
                //go to WorkerInfo page; no primary id needed when adding new worker
                (new SceneUtility()).changeScene(ae, sourceFxml, "WorkerInfo.fxml", -1);
                break;
            case "Manage Work Units":
                //go to ManageUnits page; no primary id needed when showing all units
                (new SceneUtility()).changeScene(ae, sourceFxml, "ManageUnits.fxml", -1);
                break;
            case "Edit":
                //go to WorkerInfo page; set worker id as primary id to edit this worker
                (new SceneUtility()).changeScene(ae, sourceFxml, "WorkerInfo.fxml", id);
                break;
            case "Delete":
                if(SceneUtility.showConfirmationDialog("delete this worker")) { //have user confirm
                    if(WorkHelper.deleteWorkerById(id) == 0) //if no rows affected by query
                        SceneUtility.showActionError("deleting this worker"); //show user error
                    else
                    {
                        int finalId = id;/*have to assign new variable so that is is effectively final; 
                        otherwise can't be used in lambda*/
                        workers.removeIf(w -> w.getId() == finalId); //remove the deleted unit from the observable list of units assigned to the table view
                        workerView.refresh(); //refresh table view
                    }
                }
                break;
            case "Manage Work":
                //go to ManageWorkerWorkItems; set worker id as primary id to show work items for this worker
                (new SceneUtility()).changeScene(ae, sourceFxml, "ManageWorkerWorkItems.fxml", id);
                break;
        }
    }
    
}
