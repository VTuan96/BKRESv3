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
    double Oxy;
    double Temp;
    double H2S;
    double NH3;
    double NH4Min;
    double NH4Max;
    double NO2Min;
    double NO2Max;
    double SulfideMin;
    double SulfideMax;
    double Alkalinity;
    String NgayTao;

    public Datapackage(int id, int deviceId, String time_Package, double PH, double salt, double temp, double oxy, double h2S, double NH3, double NH4Max, double NH4Min, double NO2Min, double sulfideMin, double NO2Max, double sulfideMax, double alkalinity, String ngayTao) {
        Id = id;
        DeviceId = deviceId;
        Time_Package = time_Package;
        this.PH = PH;
        Salt = salt;
        Temp = temp;
        Oxy = oxy;
        H2S = h2S;
        this.NH3 = NH3;
        this.NH4Max = NH4Max;
        this.NH4Min = NH4Min;
        this.NO2Min = NO2Min;
        SulfideMin = sulfideMin;
        this.NO2Max = NO2Max;
        SulfideMax = sulfideMax;
        NgayTao = ngayTao;
        Alkalinity=alkalinity;
    }

    public double getAlkalinity() {
        return Alkalinity;
    }

    public void setAlkalinity(double alkalinity) {
        Alkalinity = alkalinity;
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

    public double getOxy() {
        return Oxy;
    }

    public void setOxy(double oxy) {
        Oxy = oxy;
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

    public double getNH4Min() {
        return NH4Min;
    }

    public void setNH4Min(double NH4Min) {
        this.NH4Min = NH4Min;
    }

    public double getNH4Max() {
        return NH4Max;
    }

    public void setNH4Max(double NH4Max) {
        this.NH4Max = NH4Max;
    }

    public double getNO2Min() {
        return NO2Min;
    }

    public void setNO2Min(double NO2Min) {
        this.NO2Min = NO2Min;
    }

    public double getNO2Max() {
        return NO2Max;
    }

    public void setNO2Max(double NO2Max) {
        this.NO2Max = NO2Max;
    }

    public double getSulfideMin() {
        return SulfideMin;
    }

    public void setSulfideMin(double sulfideMin) {
        SulfideMin = sulfideMin;
    }

    public double getSulfideMax() {
        return SulfideMax;
    }

    public void setSulfideMax(double sulfideMax) {
        SulfideMax = sulfideMax;
    }

    public String getNgayTao() {
        return NgayTao;
    }

    public void setNgayTao(String ngayTao) {
        NgayTao = ngayTao;
    }
}
