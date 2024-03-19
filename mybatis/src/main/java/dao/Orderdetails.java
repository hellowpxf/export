package dao;

/**
 * @description:Orderdetails
 * @author:pxf
 * @data:2023/02/22
 **/
public class Orderdetails {
  private  int Id;
  private  int OrderId;
  private String   ProductCode;
  private int  Quantity;

    public Orderdetails() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    @Override
    public String toString() {
        return "Orderdetails{" +
                "Id=" + Id +
                ", OrderId=" + OrderId +
                ", ProductCode='" + ProductCode + '\'' +
                ", Quantity=" + Quantity +
                '}';
    }
}
