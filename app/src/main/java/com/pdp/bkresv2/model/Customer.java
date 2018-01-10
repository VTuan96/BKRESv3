package com.pdp.bkresv2.model;

import java.io.Serializable;

/**
 * Created by Phung Dinh Phuc on 28/07/2017.
 */
@SuppressWarnings("serial")
public class Customer implements Serializable {
    public int Id;
    public String CustomerGuid;
    public String Username;
    public String Email;
    public int PasswordFormatId;
    public String PasswordSalt;
    public String Password;
    public int HomeId;
    public int TinhId;

    public Customer(int id, String customerGuid, String username, String email, int passwordFormatId, String passwordSalt, String password, int homeId, int tinhId) {
        Id = id;
        CustomerGuid = customerGuid;
        Username = username;
        Email = email;
        PasswordFormatId = passwordFormatId;
        PasswordSalt = passwordSalt;
        Password = password;
        HomeId = homeId;
        TinhId = tinhId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCustomerGuid() {
        return CustomerGuid;
    }

    public void setCustomerGuid(String customerGuid) {
        CustomerGuid = customerGuid;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getPasswordFormatId() {
        return PasswordFormatId;
    }

    public void setPasswordFormatId(int passwordFormatId) {
        PasswordFormatId = passwordFormatId;
    }

    public String getPasswordSalt() {
        return PasswordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        PasswordSalt = passwordSalt;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getHomeId() {
        return HomeId;
    }

    public void setHomeId(int homeId) {
        HomeId = homeId;
    }

    public int getTinhId() {
        return TinhId;
    }

    public void setTinhId(int tinhId) {
        TinhId = tinhId;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "Id=" + Id +
                ", CustomerGuid='" + CustomerGuid + '\'' +
                ", Username='" + Username + '\'' +
                ", Email='" + Email + '\'' +
                ", PasswordFormatId=" + PasswordFormatId +
                ", PasswordSalt='" + PasswordSalt + '\'' +
                ", Password='" + Password + '\'' +
                ", HomeId=" + HomeId +
                ", TinhId=" + TinhId +
                '}';
    }
}
