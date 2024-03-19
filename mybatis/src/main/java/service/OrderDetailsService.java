package service;

import dao.Orderdetails;

import java.util.List;

public interface OrderDetailsService {
    int insertOrderDetails(List<Orderdetails> orderDetail);
    int selectOrderDetail(Orderdetails orderDetail);
}
