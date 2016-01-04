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
package workermanagement.model;

import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import workermanagement.HibernateUtil;

/**
 * Utility class for database and formatting operations relating to project objects
 * @author Rachel Orrell
 */
public class WorkHelper {
    
    static Session session = HibernateUtil.getSessionFactory().openSession();
    private static final Logger logger = Logger.getLogger(WorkHelper.class.getName());
    static {
        try {
            FileHandler fh = new FileHandler("log.xml");
            logger.addHandler(fh);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Default constructor
     */
    public WorkHelper() {
    }
    
    /**
     * Closes and reopens the session; used when making changes to the database
     */
    public static void resetSession() {
        session.close();
        session = HibernateUtil.getSessionFactory().openSession();
    }
    
// <editor-fold defaultstate="collapsed" desc=" Utility Methods ">
    /**
     * Rolls back changes to the database
     * @param tx the transaction to roll back
     */
    private static void rollbackChanges(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
    
    /**
     * Gets a list of items from a database table
     * @param <T> the type of items in the list to be returned
     * @param query the query to execute
     * @param type the class for the list item type
     * @param useSql whether or not the query should be run as sql (rather than hql)
     * @return a list of items of the type provided by the caller
     */
    private static <T> List<T> getList(String query, Class<T> type, boolean useSql) {
        List<T> list = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = useSql ? session.createSQLQuery(query) : session.createQuery(query);
            list = (List<T>) q.list();
            tx.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }
    
    /**
     * Gets a list of items from a database table, narrowed by a parameter named id
     * @param <T> the type for the List to be returned
     * @param query the hql query to be executed; must include a parameter named id
     * @param id the value for the id parameter in the query
     * @param type the class for the list type
     * @return a list of items of the type specified by the caller
     */
    private static <T> List<T> getListById(String query, int id, Class<T> type) {
        List<T> list = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery(query).setParameter("id", id);
            list = (List<T>) q.list();
            tx.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }
    
    /**
     * Gets an item from a database table narrowed by a table column named ID
     * @param <T> the type of item to be returned
     * @param table the database table from which to get the result
     * @param id the value of the ID column
     * @param type the class for the type specified by the caller
     * @return object of the type specified by the caller
     */
    private static <T> T getById(String table, int id, Class<T> type) {
        return get("FROM " + table + " WHERE ID = " + id, type);
    }
    
    /**
     * Gets an item from a database
     * @param <T> the type of item to return
     * @param query the hql query to run
     * @param type the class of the specified type
     * @return an object of the type specified by the caller
     */
    private static <T> T get(String query, Class<T> type) {
        T obj = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery(query);
            obj = type.cast(q.uniqueResult());
            tx.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return obj; 
    }
    
    /**
     * Insert or update a record to a database
     * @param <T> the type of object being inserted/updated
     * @param query the hql query to run
     * @param obj the object to insert/update
     * @param type the class of the object being inserted/updated
     * @return the number of rows affected
     */
    private static <T> int insertOrUpdate(String query, T obj, Class<T> type) {
        int result = 0;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createSQLQuery(query).setProperties(type.cast(obj));
            result = q.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            rollbackChanges(tx);
        }
        resetSession();
        return result;
    }
    
    /**
     * Delete an object from a database table narrowed by id
     * @param table the name of the database table
     * @param idName the name of the column containing the pertinent id
     * @param id 
     * @return the number of rows affected
     */
    private static int deleteById(String table, String idName, int id) {
        int result = 0;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Query q = session.createQuery("DELETE FROM " + table + " WHERE " + idName + " = :id")
                    .setParameter("id", id);
            result = q.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            rollbackChanges(tx);
        }
        resetSession();
        return result;
    }
    
    /**
     * Delete an object from a database table, narrowed by an id column named ID 
     * @param table the name of the database table
     * @param id
     * @return the number of rows affected
     */
    private static int deleteById(String table, int id) {
        return deleteById(table, "ID", id);
    }

// </editor-fold>
    
// <editor-fold defaultstate="collapsed" desc=" Worker Methods ">

    /**
     * Get all of the workers
     * @return a List of type Worker
     */
        public static List<Worker> getAllWorkers() {
        return getList("FROM Worker ORDER BY LASTNAME ASC", Worker.class, false);
    }
    
    /**
     * Get a worker by id
     * @param id the worker id
     * @return a Worker
     */
    public static Worker getWorkerById(int id) {
        return getById("Worker", id, Worker.class);
    }
    
    /**
     * Get a Worker's name by id
     * @param id the worker id
     * @return String (firstName lastName)
     */
    public static String getWorkerNameById(int id) {
        String name = "";
        if(id > 0) {
            Worker w = getWorkerById(id);
            if(w != null)
                name = w.getFirstname() + " " + w.getLastname();
        }
        return name;
    }
    
    /**
     * Insert new Worker
     * @param worker the Worker object
     * @return number of rows affected
     */
    public static int insertWorker(Worker worker) {
        return insertOrUpdate("INSERT INTO Worker (FIRSTNAME, LASTNAME, PHONE, CURRENTRATE) "
            + "VALUES (:firstname, :lastname, :phone, :currentrate)", worker, Worker.class);
    }
    
    /**
     * Update existing Worker
     * @param worker the Worker object
     * @return number of rows affected
     */
    public static int updateWorker(Worker worker) {
        return insertOrUpdate("UPDATE Worker SET FIRSTNAME = :firstname, "
            + "LASTNAME = :lastname, PHONE = :phone, CURRENTRATE = :currentrate "
            + "WHERE ID = :id", worker, Worker.class);
    }
    
    /**
     * Delete Worker by id; also deletes Worker's Workitems
     * @param id the worker id
     * @return number of rows affected
     */
    public static int deleteWorkerById(int id) {
        deleteById("Workitem", "WORKERID", id);
        int result = deleteById("Worker", id);
        return result;
    }

// </editor-fold>
    
// <editor-fold defaultstate="collapsed" desc=" Unit Methods ">

    /**
     * Get all Units
     * @return List of type Unit
     */
    public static List<Unit> getAllUnits() {
        return getList("FROM Unit ORDER BY NAME ASC", Unit.class, false);
    }
    
    /**
     * Get Unit by id
     * @param id the unit id
     * @return Unit
     */
    public static Unit getUnitById(int id) {
        return getById("Unit", id, Unit.class);
    }
    
    /**
     * Get Unit name by id
     * @param id the unit id
     * @return String
     */
    public static String getUnitNameById(int id) {
        String name = "";
        if(id > 0) {
            Unit u = getUnitById(id);
            if(u != null)
                name = u.getName();
        }
        return name;
    }
    
    /**
     * Insert new Unit
     * @param unit the unit object
     * @return number of rows affected
     */
    public static int insertUnit(Unit unit) {
        return insertOrUpdate("INSERT INTO Unit (NAME) VALUES (:name)", unit, Unit.class);
    }
    
    /**
     * Update existing Unit
     * @param unit the unit object
     * @return number of rows affected
     */
    public static int updateUnit(Unit unit) {
        return insertOrUpdate("UPDATE Unit SET NAME = :name WHERE ID = :id", unit, Unit.class);
    }
    
    /**
     * Delete Unit by id
     * @param id the unit id
     * @return number of rows affected
     */
    public static int deleteUnitById(int id) {
        return deleteById("Unit", id);
    }

// </editor-fold>
    
// <editor-fold defaultstate="collapsed" desc=" Workitem Methods ">

    /**
     * Get all Workitems for a single Worker
     * @param id the worker id
     * @return List of type Workitem
     */
        public static List<Workitem> getWorkitemsByWorkerId(int id) {
      return getList("FROM Workitem WHERE WORKERID=" + id + " ORDER BY DATE ASC", Workitem.class, false);
    }
    
    /**
     * Get Workitem by id
     * @param id the workitem id
     * @return Workitem
     */
    public static Workitem getWorkitemById(int id) {
        return getById("Workitem", id, Workitem.class);
    }
    
    /**
     * Get Workitem Unit
     * @param work the workitem object
     * @return Unit
     */
    public static Unit getWorkitemUnit(Workitem work) {
        return getById("Unit", work.getUnitid(), Unit.class);
    }
    
    /**
     * Get Workitem Worker
     * @param work the workitem object
     * @return Worker
     */
    public static Worker getWorkitemWorker(Workitem work) {
        return getById("FROM Worker WHERE ID = :id", work.getWorkerid(), Worker.class);
    }
    
    /**
     * Insert new Workitem
     * @param work the workitem object
     * @return number of rows affected
     */
    public static int insertWorkItem(Workitem work) {
        return insertOrUpdate("INSERT INTO Workitem (DATE, RATE, QTY, WORKERID, UNITID) "
                + "VALUES (:date, :rate, :qty, :workerid, :unitid)", work, Workitem.class);
    }
    
    /**
     * Update existing workitem
     * @param work the workitem object
     * @return number of rows affected
     */
    public static int updateWorkItem(Workitem work) {
        return insertOrUpdate("UPDATE Workitem SET DATE = :date, RATE = :rate, "
                + "QTY = :qty, WORKERID = :workerid, UNITID = :unitid WHERE ID = :id", work, Workitem.class);
    }
    
    /**
     * Delete workitem by id
     * @param id the workitem id
     * @return number of rows affected
     */
    public static int deleteWorkItemById(int id) {
        return deleteById("Workitem", id);
    }
    
    /**
     * Delete all Workitems for a Worker 
     * @param id Worker id
     * @return number of rows affected
     */
    public static int deleteWorkItemsByWorkerId(int id) {
        return deleteById("Workitem", "WORKERID", id);
    }

// </editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="User Methods">
    /**
     * Get User by id
     * @param id the user's id
     * @return User object
     */
    public static User getUserById(int id) {
        return getById("User", id, User.class);
    }
    
    /**
     * Get User by username
     * @param username the User's username
     * @return User object
     */
    public static User getUserByUsername(String username) {
        return get("FROM User WHERE USERNAME='" + username + "'", User.class);
    }
    
    /**
     * Insert new User
     * @param u the User to insert
     * @return number of rows affected
     */
    public static int insertUser(User u) {
        return insertOrUpdate("INSERT INTO Users (USERNAME, PASSWORD, CHALLENGEQUESTION, CHALLENGEANSWER) " +
                "VALUES (:username, :password, :challengeQuestion, :challengeAnswer)", u, User.class);
    }
    
    /**
     * Update existing User
     * @param u the updated User object
     * @return number of rows affected
     */
    public static int updateUser(User u) {
        return insertOrUpdate("UPDATE Users SET USERNAME=:username, PASSWORD=:password, " +
                "CHALLENGEQUESTION=:challengeQuestion, CHALLENGEANSWER=:challengeAnswer WHERE ID=:id", u, User.class);
    }
    
    /**
     * Delete User by id
     * @param id user id
     * @return number of rows affected
     */
    public static int deleteUserById(int id) {
        return deleteById("User", id);
    }
//</editor-fold>
    
}
