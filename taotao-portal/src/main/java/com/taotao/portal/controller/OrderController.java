package com.taotao.portal.controller;

import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.portal.pojo.CartItem;
import com.taotao.portal.pojo.Order;
import com.taotao.portal.service.CartService;
import com.taotao.portal.service.OrderService;
import com.taotao.utils.ExceptionUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    @RequestMapping("/order-cart")
    public String showOrderCart(HttpServletRequest request, HttpServletResponse response, Model model) {

        List<CartItem> list = cartService.getCartItemList(request, response);

        model.addAttribute("cartList", list);

        return "order-cart";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createOrder(Order order, Model model, HttpServletRequest request) {
        TaotaoResult result = null;
        try {
            //从request中获取用户信息
            TbUser user = (TbUser) request.getAttribute("user");
            //补全order中的用户信息
            order.setUserId(user.getId());
            order.setBuyerNick(user.getUsername());
            //提交订单

            result = orderService.createOrder(order);
            if (result.getStatus() == 200) {
                model.addAttribute("orderId", result.getData());
                model.addAttribute("payment", order.getPayment());
                model.addAttribute("date", new DateTime().plusDays(3).toString("yyyy-MM-dd"));
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }
        //订单创建失败
        model.addAttribute("message", result.getMsg());
        return "error/exception";
    }

}
