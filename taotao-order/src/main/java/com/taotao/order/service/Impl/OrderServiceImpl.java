package com.taotao.order.service.Impl;

import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.dao.JedisClient;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbOrderShippingMapper shippingMapper;
    @Autowired
    private JedisClient jedisClient;
    @Value("${ORDER_GEN_KEY}")
    private String ORDER_GEN_KEY;
    @Value("${ORDER_GEN_KEY_VALUE}")
    private String ORDER_GEN_KEY_VALUE;
    @Value("${ORDER_DETAIL_GEN_KEY}")
    private String ORDER_DETAIL_GEN_KEY;

    @Override
    public TaotaoResult createOrder(TbOrder order, List<TbOrderItem> orderItemList, TbOrderShipping orderShipping) {
        //向订单表中插入数据
        //获得订单号
        String json = jedisClient.get(ORDER_GEN_KEY);
        if (StringUtils.isBlank(json)) {
            jedisClient.set(ORDER_GEN_KEY, ORDER_GEN_KEY_VALUE);
        }
        long orderId = jedisClient.incr(ORDER_GEN_KEY);
        //补全pojo的属性
        order.setOrderId(orderId + "");
        //'状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭',
        order.setStatus(1);
        Date date = new Date();
        order.setCreateTime(date);
        order.setUpdateTime(date);
        //0:未评价，1：以评价
        order.setBuyerRate(0);
        orderMapper.insert(order);

        //插入订单明细
        for (TbOrderItem orderItem : orderItemList) {
            long orderItemId = jedisClient.incr(ORDER_DETAIL_GEN_KEY);
            orderItem.setId(orderItemId + "");
            orderItem.setOrderId(orderId + "");
            orderItemMapper.insert(orderItem);
        }

        //插入物流表
        orderShipping.setOrderId(orderId + "");
        orderShipping.setCreated(date);
        orderShipping.setUpdated(date);
        shippingMapper.insert(orderShipping);
        return TaotaoResult.ok(orderId + "");
    }
}
