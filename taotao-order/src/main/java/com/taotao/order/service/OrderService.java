package com.taotao.order.service;

import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;

import java.util.List;

public interface OrderService {

    TaotaoResult createOrder(TbOrder order, List<TbOrderItem> orderItemList, TbOrderShipping orderShipping);

}
