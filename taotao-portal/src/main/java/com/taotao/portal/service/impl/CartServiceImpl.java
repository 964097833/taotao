package com.taotao.portal.service.impl;

import com.taotao.common.utils.CookieUtils;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.portal.pojo.CartItem;
import com.taotao.portal.service.CartService;
import com.taotao.utils.HttpClientUtil;
import com.taotao.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    //服务基础url
    @Value("${REST_BASE_URL}")
    private String REST_BASE_URL;
    //商品基本信息url
    @Value("${ITEM_INFO_URL}")
    private String ITEM_INFO_URL;


    @Override
    public TaotaoResult addCartItem(long itemId,int num, HttpServletRequest request, HttpServletResponse response) {
        //取商品信息
        CartItem cartItem = null;
        //取购物车商品列表
        List<CartItem> itemList = getCartItemList(request);
        //判断商品列表中是否存在此商品
        for(CartItem cItem : itemList) {
            //如果存在此商品
            if (cItem.getId() == itemId){
                cItem.setNum(cItem.getNum() + num);
                cartItem = cItem;
                break;
            }
        }
        if (cartItem == null) {
            cartItem = new CartItem();
            //通过商品id获取商品信息
            String json = HttpClientUtil.doGet(REST_BASE_URL + ITEM_INFO_URL + itemId);
            //吧json转换成java对象
            TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItem.class);
            if (taotaoResult.getStatus() == 200) {
                TbItem item = (TbItem) taotaoResult.getData();
                cartItem.setId(item.getId());
                cartItem.setTitle(item.getTitle());
                cartItem.setImage(item.getImage() == null?"":item.getImage().split(",")[0]);
                cartItem.setNum(num);
                cartItem.setPrice(item.getPrice());
            }
            //添加到购物车列表
            itemList.add(cartItem);
        }
        //吧购物车列表写入cookie
        CookieUtils.setCookie(request,response,"TT_CART",JsonUtils.objectToJson(itemList),true);
        return TaotaoResult.ok();
    }


    private List<CartItem> getCartItemList(HttpServletRequest request) {
        //从cookie中取商品列表
        String cartJson = CookieUtils.getCookieValue(request, "TT_CART", true);
        if (cartJson == null) {
            return new ArrayList<>();
        }
        //吧json转换成商品列表
        try {
            List<CartItem> list = JsonUtils.jsonToList(cartJson, CartItem.class);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public List<CartItem> getCartItemList(HttpServletRequest request, HttpServletResponse response) {

        List<CartItem> itemList = getCartItemList(request);

        return itemList;
    }

    @Override
    public TaotaoResult deleCartItem(long itemId, HttpServletRequest request, HttpServletResponse response) {

        List<CartItem> cartItems = getCartItemList(request);
        for (CartItem cartItem : cartItems) {
            if (cartItem.getId() == itemId) {
                cartItems.remove(cartItem);
                break;
            }
        }
        //重新写入cookie
        CookieUtils.setCookie(request,response,"TT_CART",JsonUtils.objectToJson(cartItems),true);
        return TaotaoResult.ok();
    }
}
