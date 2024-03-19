package tax;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:Tax
 * @author:pxf
 * @data:2023/04/18
 **/
    public class Tax implements Serializable {
        private  String  companyAddress;
        private  String company;
    private  String taxNumber;
    private  Integer roomNumber;
    private  String  telephone;
        private  String  bankName;
        private  String  bankNumber;
        private  Double money;
        private String email;
        private String remark;
        private Date date;
        private  Long id;
        private String  phoneNumber;
        private  String dateString;
        private  String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "Tax{" +
                "company='" + company + '\'' +
                ", taxNumber='" + taxNumber + '\'' +
                ", roomNumber=" + roomNumber +
                ", companyAddress='" + companyAddress + '\'' +
                ", telephone='" + telephone + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankNumber='" + bankNumber + '\'' +
                ", money=" + money +
                ", email='" + email + '\'' +
                ", remark='" + remark + '\'' +
                ", date=" + date +
                ", id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

}
