package com.pdp.bkresv2.model;

/**
 * Created by Phung Dinh Phuc on 29/07/2017.
 */

public class Datapackage {
    int Id ;
    int DeviceId;
    String Time_Package ;
    double PH;
    double Salt;
    double Temp;
    double H2S;
    double NH4;
    double DO;
    double TUR;

    public Datapackage() {
    }

    public Datapackage(int id, int deviceId, String time_Package, double PH, double salt, double temp, double h2S, double NH4, double DO, double TUR) {
        Id = id;
        DeviceId = deviceId;
        Time_Package = time_Package;
        this.PH = PH;
        Salt = salt;
        Temp = temp;
        H2S = h2S;
        this.NH4 = NH4;
        this.DO = DO;
        this.TUR = TUR;
    }

    public double getTUR() {
        return TUR;
    }

    public void setTUR(double TUR) {
        this.TUR = TUR;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(int deviceId) {
        DeviceId = deviceId;
    }

    public String getTime_Package() {
        return Time_Package;
    }

    public void setTime_Package(String time_Package) {
        Time_Package = time_Package;
    }

    public double getPH() {
        return PH;
    }

    public void setPH(double PH) {
        this.PH = PH;
    }

    public double getSalt() {
        return Salt;
    }

    public void setSalt(double salt) {
        Salt = salt;
    }

    public double getTemp() {
        return Temp;
    }

    public void setTemp(double temp) {
        Temp = temp;
    }

    public double getH2S() {
        return H2S;
    }

    public void setH2S(double h2S) {
        H2S = h2S;
    }

    public double getNH4() {
        return NH4;
    }

    public void setNH4(double NH4) {
        this.NH4 = NH4;
    }

    public double getDO() {
        return DO;
    }

    public void setDO(double DO) {
        this.DO = DO;
    }
}
