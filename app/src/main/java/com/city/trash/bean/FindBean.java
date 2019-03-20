package com.city.trash.bean;

public class FindBean extends BaseEntity {

    /**
     * Epc : AB0101201901010000000011
     * RentTime : 2019-03-13T18:25:28
     * LastCustomerName : 网宿科技
     * LastCustomerMobile : 13222010122
     * LastRentTime : 2019-03-13T18:25:28
     * CustomerName : 网宿科技
     * CustomerMobile : 13222010122
     * RentDays : 0
     * Deposit : 20
     * Fee : 1
     * OvertimeDays : 0
     * OvertimeFee : 0.1
     */

    private String Epc;
    private String RentTime;
    private String LastCustomerName;
    private String LastCustomerMobile;
    private String LastRentTime;
    private String CustomerName;
    private String CustomerMobile;
    private int RentDays;
    private int Deposit;
    private int Fee;
    private int OvertimeDays;
    private double OvertimeFee;

    public String getEpc() {
        return Epc;
    }

    public void setEpc(String Epc) {
        this.Epc = Epc;
    }

    public String getRentTime() {
        return RentTime;
    }

    public void setRentTime(String RentTime) {
        this.RentTime = RentTime;
    }

    public String getLastCustomerName() {
        return LastCustomerName;
    }

    public void setLastCustomerName(String LastCustomerName) {
        this.LastCustomerName = LastCustomerName;
    }

    public String getLastCustomerMobile() {
        return LastCustomerMobile;
    }

    public void setLastCustomerMobile(String LastCustomerMobile) {
        this.LastCustomerMobile = LastCustomerMobile;
    }

    public String getLastRentTime() {
        return LastRentTime;
    }

    public void setLastRentTime(String LastRentTime) {
        this.LastRentTime = LastRentTime;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String CustomerName) {
        this.CustomerName = CustomerName;
    }

    public String getCustomerMobile() {
        return CustomerMobile;
    }

    public void setCustomerMobile(String CustomerMobile) {
        this.CustomerMobile = CustomerMobile;
    }

    public int getRentDays() {
        return RentDays;
    }

    public void setRentDays(int RentDays) {
        this.RentDays = RentDays;
    }

    public int getDeposit() {
        return Deposit;
    }

    public void setDeposit(int Deposit) {
        this.Deposit = Deposit;
    }

    public int getFee() {
        return Fee;
    }

    public void setFee(int Fee) {
        this.Fee = Fee;
    }

    public int getOvertimeDays() {
        return OvertimeDays;
    }

    public void setOvertimeDays(int OvertimeDays) {
        this.OvertimeDays = OvertimeDays;
    }

    public double getOvertimeFee() {
        return OvertimeFee;
    }

    public void setOvertimeFee(double OvertimeFee) {
        this.OvertimeFee = OvertimeFee;
    }
}
