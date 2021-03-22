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
    double NH3;
    double DO;
    double TSS;
    double COD;

    public Datapackage() {
    }

    public Datapackage(int id, int deviceId, String time_Package, double PH, double salt, double temp, double h2S, double NH3, double DO, double TSS, double COD) {
        this.Id = id;
        this.DeviceId = deviceId;
        this.Time_Package = time_Package;
        this.PH = PH;
        this.Salt = salt;
        this.Temp = temp;
        this.H2S = h2S;
        this.NH3 = NH3;
        this.DO = DO;
        this.TSS = TSS;
        this.COD = COD;
    }

    public double getTSS() {
        return TSS;
    }

    public void setTSS(double TSS) {
        this.TSS = TSS;
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

    public double getNH3() {
        return NH3;
    }

    public void setNH3(double NH3) {
        this.NH3 = NH3;
    }

    public double getDO() {
        return DO;
    }

    public void setDO(double DO) {
        this.DO = DO;
    }

    public double getCOD() { return COD; }

    public void setCOD(double COD) { this.COD = COD; }
}
