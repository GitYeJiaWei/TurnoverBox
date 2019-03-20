package com.city.trash.bean;

public class ReturnBean extends BaseEntity{
        /**
         * ProductTypeId : 01
         * ProductTypeName : A类
         * Qty : 4
         * OvertimeQty : 4
         * OvertimeDays : 0
         * ReturnAmount : 76.0
         */

        private String ProductTypeId;
        private String ProductTypeName;
        private int Qty;
        private int OvertimeQty;
        private int OvertimeDays;
        private double ReturnAmount;

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

        public int getQty() {
            return Qty;
        }

        public void setQty(int Qty) {
            this.Qty = Qty;
        }

        public int getOvertimeQty() {
            return OvertimeQty;
        }

        public void setOvertimeQty(int OvertimeQty) {
            this.OvertimeQty = OvertimeQty;
        }

        public int getOvertimeDays() {
            return OvertimeDays;
        }

        public void setOvertimeDays(int OvertimeDays) {
            this.OvertimeDays = OvertimeDays;
        }

        public double getReturnAmount() {
            return ReturnAmount;
        }

        public void setReturnAmount(double ReturnAmount) {
            this.ReturnAmount = ReturnAmount;
        }

}
