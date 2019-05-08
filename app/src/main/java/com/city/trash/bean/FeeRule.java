package com.city.trash.bean;


import java.util.List;

public class FeeRule extends BaseEntity {


    private List<FeeRulesBean> FeeRules;
    private List<PadMenusBean> PadMenus;
    private AutoUpdateInfoBean AutoUpdateInfo;

    public AutoUpdateInfoBean getAutoUpdateInfo() {
        return AutoUpdateInfo;
    }

    public void setAutoUpdateInfo(AutoUpdateInfoBean AutoUpdateInfo) {
        this.AutoUpdateInfo = AutoUpdateInfo;
    }

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

    public static class AutoUpdateInfoBean extends BaseEntity{
        /**
         * Version : sample string 1
         * UpdateInfo : sample string 2
         * FilePath : sample string 3
         */

        private String Version;
        private String UpdateInfo;
        private String FilePath;

        public String getVersion() {
            return Version;
        }

        public void setVersion(String Version) {
            this.Version = Version;
        }

        public String getUpdateInfo() {
            return UpdateInfo;
        }

        public void setUpdateInfo(String UpdateInfo) {
            this.UpdateInfo = UpdateInfo;
        }

        public String getFilePath() {
            return FilePath;
        }

        public void setFilePath(String FilePath) {
            this.FilePath = FilePath;
        }
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
