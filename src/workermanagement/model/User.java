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
package workermanagement.model;

import org.jasypt.util.password.BasicPasswordEncryptor;

/**
 * User class for authentication
 * @author Rachel Orrell
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String challengeQuestion;
    private String challengeAnswer;
    private BasicPasswordEncryptor encryptor;
    
    /**
     * User constructor
     * @param id user id
     * @param username User's username (up to 30 characters)
     * @param password User's password
     * @param question User's challenge question
     * @param answer User's challenge answer
     */
    public User(int id, String username, String password, String question, String answer) {
        encryptor = new BasicPasswordEncryptor();
        this.id = id;
        this.username = username;
        this.password = this.encrypt(password);
        this.challengeQuestion = question;
        this.challengeAnswer = this.encrypt(answer);
    }
    
    /**
     * User constructor
     * @param username User's username
     * @param password User's password
     * @param question User's challenge question
     * @param answer  User's challeng answer
     */
    public User(String username, String password, String question, String answer) {
        this(0, username, password, question, answer);
    }
    
    /**
     * Default constructor
     */
    public User() {
        this(0, "", "", "", "");
    }
    
    /**
     * 
     * @return User's id
     */
    public int getId() {
        return id;
    }
    
    /**
     * 
     * @param id the value to assign to User's id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 
     * @return User's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @param username the value to assign to the User's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 
     * @return the User's password (encrypted) 
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set User's password (will be encrypted)
     * @param password the value to assign to the User's password
     */
    public void setPassword(String password) {
        this.password = this.encrypt(password);
    }

    /**
     * 
     * @return User's challenge question
     */
    public String getChallengeQuestion() {
        return challengeQuestion;
    }

    /**
     * 
     * @param challengeQuestion the value to assign to User's challenge question
     */
    public void setChallengeQuestion(String challengeQuestion) {
        this.challengeQuestion = challengeQuestion;
    }

    /**
     * Get user's challenge answer (encrypted)
     * @return User's challenge answer
     */
    public String getChallengeAnswer() {
        return challengeAnswer;
    }

    /**
     * Set user's challenge answer (will be encrypted)
     * @param challengeAnswer the value to assign to the User's challenge answer
     */
    public void setChallengeAnswer(String challengeAnswer) {
        this.challengeAnswer = this.encrypt(challengeAnswer);
    }
    
    private String encrypt(String password) {
        return this.id == 0 ? encryptor.encryptPassword(password) : password;
    }
    
    /**
     * Encrypt text
     * @param text the text to encrypt
     * @return encrypted text
     */
    public String encryptText(String text) {
        return encryptor.encryptPassword(text);
    }
    
    /**
     * 
     * @param password the password to check
     * @return true if the password is correct; false otherwise
     */
    public boolean checkPassword(String password) {
        return encryptor.checkPassword(password, this.password);
    }
    
    /**
     * 
     * @param answer the answer to check
     * @return true if the answer is correct; false otherwise
     */
    public boolean checkAnswer(String answer) {
        return encryptor.checkPassword(answer, this.challengeAnswer);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof User)) return false;
        
        User u = (User) obj;
        if(u.getId() == this.id && u.getUsername().equals(this.username) 
                && u.getChallengeQuestion().equals(this.challengeQuestion))
            return true;
        return false;
    }
}
