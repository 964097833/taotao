package com.taotao.portal.service;

import com.taotao.pojo.TaotaoResult;
import com.taotao.portal.pojo.Order;

public interface OrderService {
    TaotaoResult createOrder(Order order);
}
