package cn.ucai.fulicenter.bean;

/**
 * Created by sks on 2016/8/11.
 */
public class BillBean {
    private String orderPhone;
    private String orderUserName;
    private String orderStreet;
    private String orderProvince;

    public BillBean(String orderAddress, String orderPhone, String orderProvince, String orderStreet) {
        this.orderUserName = orderAddress;
        this.orderPhone = orderPhone;
        this.orderProvince = orderProvince;
        this.orderStreet = orderStreet;
    }

    public String getOrderUserName() {
        return orderUserName;
    }

    public void setOrderUserName(String orderUserName) {
        this.orderUserName = orderUserName;
    }

    public String getOrderPhone() {
        return orderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        this.orderPhone = orderPhone;
    }

    public String getOrderProvince() {
        return orderProvince;
    }

    public void setOrderProvince(String orderProvince) {
        this.orderProvince = orderProvince;
    }

    public String getOrderStreet() {
        return orderStreet;
    }

    public void setOrderStreet(String orderStreet) {
        this.orderStreet = orderStreet;
    }

    @Override
    public String toString() {
        return "BillBean{" +
                "orderUserName='" + orderUserName + '\'' +
                ", orderPhone='" + orderPhone + '\'' +
                ", orderStreet='" + orderStreet + '\'' +
                ", orderProvince='" + orderProvince + '\'' +
                '}';
    }
}
