package com.taotao.portal.service.impl;

import com.taotao.pojo.TaotaoResult;
import com.taotao.portal.pojo.Order;
import com.taotao.portal.service.OrderService;
import com.taotao.utils.HttpClientUtil;
import com.taotao.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Value("${ORDER_BASE_URL}")
    private String ORDER_BASE_URL;
    @Value("${ORDER_CREATE_URL}")
    private String ORDER_CREATE_URL;

    @Override
    public TaotaoResult createOrder(Order order) {

        String json = HttpClientUtil.doPostJson(ORDER_BASE_URL + ORDER_CREATE_URL, JsonUtils.objectToJson(order));
        //把json转换成taotaoResult
        TaotaoResult result = TaotaoResult.format(json);

        return result;
    }
}
