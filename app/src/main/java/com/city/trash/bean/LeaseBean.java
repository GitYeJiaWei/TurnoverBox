package com.city.trash.bean;

public class LeaseBean extends BaseEntity {
    /**
     * Id : 1
     * Name : 网宿科技
     * Account : 825
     * Tel : 0592-0000001
     * ContactName : 网宿人
     * Address : 软件园三区B19
     * Memo : null
     * TaxNumber : null
     * Bank : 中国农业银行
     * InvoiceAddress : 软件园三期B19
     * BankAccount : null
     * InvoiceTel : null
     * InvoiceName : 网宿科技
     * Status : null
     */

    private String Id;
    private String Name;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

}
