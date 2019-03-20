package com.city.trash.bean;


public class FeeRule extends BaseEntity {


    /**
     * Deposit : 20
     * Fee : 1
     * ProductTypeId : 01
     * ProductTypeName : A类
     */

    private double Deposit;
    private int Fee;
    private String ProductTypeId;
    private String ProductTypeName;

    public double getDeposit() {
        return Deposit;
    }

    public void setDeposit(double deposit) {
        Deposit = deposit;
    }

    public int getFee() {
        return Fee;
    }

    public void setFee(int Fee) {
        this.Fee = Fee;
    }

    public String getProductTypeId() {
        return ProductTypeId;
    }

    public void setProductTypeId(String ProductTypeId) {
        this.ProductTypeId = ProductTypeId;
    }

    public String getProductTypeName() {
        return ProductTypeName;
    }

    public void setProductTypeName(String ProductTypeName) {
        this.ProductTypeName = ProductTypeName;
    }
}
