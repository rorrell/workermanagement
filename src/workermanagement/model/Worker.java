package workermanagement.model;
// Generated Dec 5, 2015 10:26:39 AM by Hibernate Tools 4.3.1


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Worker generated by hbm2java and modified to contain properties for display in table views 
 */
public class Worker implements java.io.Serializable {


    private final IntegerProperty id;
    private final StringProperty firstname;
    private final StringProperty  lastname;
    private final StringProperty  fullname;
    private final StringProperty  phone;
    private final SimpleObjectProperty<BigDecimal> currentrate;
    
    /**
     * Worker constructor
     * @param id the id for the Worker
     * @param firstname the first name for the Worker
     * @param lastname the last name for the Worker
     * @param phone the phone number for the Worker
     * @param currentrate the current rate for the Worker
     */
    public Worker(int id, String firstname, String lastname, String phone, BigDecimal currentrate) {
        this.id = new SimpleIntegerProperty(id);
        this.firstname = new SimpleStringProperty(firstname);
        this.lastname = new SimpleStringProperty(lastname);
        this.fullname = new SimpleStringProperty(lastname + ", " + firstname);
        this.phone = new SimpleStringProperty(phone);
        this.currentrate = new SimpleObjectProperty<BigDecimal>(currentrate);
    }
    
    /**
     * Worker constructor
     * @param firstname the first name for the Worker
     * @param lastname the last name for the Worker
     * @param phone the phone number for the Worker
     * @param currentrate the current rate for the Worker
     */
    public Worker(String firstname, String lastname, String phone, BigDecimal currentrate) {
        this(0, firstname, lastname, phone, currentrate);
    }
     
    /**
     * Default worker constructor
     */
    public Worker() {
        this(0, "", "", "", new BigDecimal(0));
    }

    /**
     * Worker constructor
     * @param id the id for the Worker
     * @param firstname the first name for the Worker
     * @param lastname the last name for the Worker
     * @param currentrate the current rate for the Worker
     */
    public Worker(int id, String firstname, String lastname, BigDecimal currentrate) {
        this(id, firstname, lastname, "", currentrate);
    }
    
    /**
     *
     * @return IntegerProperty
     */
    public IntegerProperty idProperty() {
        return this.id;
    }
   
    /**
     *
     * @return int
     */
    public int getId() {
        return this.idProperty().get();
    }
    
    /**
     *
     * @param id the value to assign to the id
     */
    public void setId(int id) {
        this.idProperty().set(id);
    }
    
    /**
     *
     * @return StringProperty
     */
    public StringProperty firstnameProperty() {
        return this.firstname;
    }
    
    /**
     *
     * @return String
     */
    public String getFirstname() {
        return this.firstnameProperty().get();
    }
    
    /**
     *
     * @param firstname the value to assign to the first name
     */
    public void setFirstname(String firstname) {
        this.firstnameProperty().set(firstname);
        this.setFullname();
    }
    
    /**
     *
     * @return StringProperty
     */
    public StringProperty lastnameProperty() {
        return this.lastname;
    }
    
    /**
     *
     * @return String
     */
    public String getLastname() {
        return this.lastnameProperty().get();
    }
    
    /**
     *
     * @param lastname the value to assign to the last name
     */
    public void setLastname(String lastname) {
        this.lastnameProperty().set(lastname);
        this.setFullname();
    }
    
    /**
     *
     * @return StringProperty
     */
    public StringProperty fullnameProperty() {
        return this.fullname;
    }
    
    /**
     *
     * @return String
     */
    public String getFullname() {
        return this.fullnameProperty().get();
    }
    
    private void setFullname() {
        this.fullnameProperty().set(this.lastnameProperty().get() + ", " +
            this.firstnameProperty().get());
    }
    
    /**
     *
     * @return StringProperty
     */
    public StringProperty phoneProperty() {
        return this.phone;
    }
    
    /**
     *
     * @return String
     */
    public String getPhone() {
        return this.phoneProperty().get();
    }
    
    /**
     *
     * @param phone the value to assign to the phone number
     */
    public void setPhone(String phone) {
        this.phoneProperty().set(phone);
    }
    
    /**
     *
     * @return SimpleObjectProperty of BigDecimal type
     */
    public SimpleObjectProperty<BigDecimal> currentrateProperty() {
        return this.currentrate;
    }
    
    /**
     *
     * @return SimpleStringProperty
     */
    public SimpleStringProperty currentrateStringProperty() {
        return new SimpleStringProperty(this.currentrate.getValue().toString());
    }
    
    /**
     *
     * @return BigDecimal
     */
    public BigDecimal getCurrentrate() {
        return this.currentrateProperty().get();
    }
    
    /**
     *
     * @param currentrate the value to assign to the current rate
     */
    public void setCurrentrate(BigDecimal currentrate) {
        this.currentrateProperty().set(currentrate);
    }

}


