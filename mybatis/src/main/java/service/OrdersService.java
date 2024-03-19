package service;

import dao.Orders;

import java.util.List;

public interface OrdersService {
    int saveOrders(List<Orders> orders);
}
