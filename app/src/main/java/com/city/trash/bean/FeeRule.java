package com.city.trash.bean;


import java.util.List;

public class FeeRule extends BaseEntity {


    private List<FeeRulesBean> FeeRules;
    private List<PadMenusBean> PadMenus;

    public List<FeeRulesBean> getFeeRules() {
        return FeeRules;
    }

    public void setFeeRules(List<FeeRulesBean> FeeRules) {
        this.FeeRules = FeeRules;
    }

    public List<PadMenusBean> getPadMenus() {
        return PadMenus;
    }

    public void setPadMenus(List<PadMenusBean> PadMenus) {
        this.PadMenus = PadMenus;
    }

    public static class FeeRulesBean extends BaseEntity{
        /**
         * Deposit : 1.0
         * Fee : 1.0
         * ProductTypeId : sample string 1
         * ProductTypeName : sample string 2
         */

        private double Deposit;
        private double Fee;
        private String ProductTypeId;
        private String ProductTypeName;

        public double getDeposit() {
            return Deposit;
        }

        public void setDeposit(double Deposit) {
            this.Deposit = Deposit;
        }

        public double getFee() {
            return Fee;
        }

        public void setFee(double Fee) {
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

    public static class PadMenusBean extends BaseEntity{
        /**
         * Id : sample string 1
         */

        private String Id;

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }
    }
}
