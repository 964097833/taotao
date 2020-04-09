package com.taotao.portal.controller;

import com.taotao.pojo.TaotaoResult;
import com.taotao.portal.pojo.CartItem;
import com.taotao.portal.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 购物车Controller
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @RequestMapping("/add/{itemId}")
    public String addCartItem(@PathVariable Long itemId,@RequestParam(defaultValue = "1")Integer num,
                              HttpServletRequest request, HttpServletResponse response) {
        TaotaoResult result = cartService.addCartItem(itemId, num, request, response);
        return "redirect:/cart/cart.html";
    }

    @RequestMapping("/cart")
    public String showCart(HttpServletRequest request, HttpServletResponse response, Model model) {
        List<CartItem> cartItems = cartService.getCartItemList(request,response);
        model.addAttribute("cartList",cartItems);
        return "cart";
    }

    @RequestMapping("/delete/{itemId}")
    public String deleteCartItem(@PathVariable long itemId, HttpServletRequest request, HttpServletResponse response) {
        cartService.deleCartItem(itemId, request, response);
        return "redirect:/cart/cart.html";
    }

}
