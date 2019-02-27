package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

public interface CartService {
    /**
     *  根据商品 id 查询商品和购买数量加入到 cartList
     * @param cartList  购物车列表
     * @param itemId  商品 id
     * @param num  购买数量
     * @return  购物车列表
     */
    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 根据用户id查询在redis对应的购物车列表
     * @param username 用户id
     * @return
     */
    List<Cart> findCartListByUsername(String username);

    /**
     * 将用户对应购物车列表保存到redis中
     * @param cartList 购物车列表
     * @param username 用户id
     */
    void saveCartListByUsername(List<Cart> cartList, String username);

    /**
     * 合并两个购物车列表
     * @param cartList1 购物车列表1
     * @param cartList2 购物车列表2
     * @return 合并后的购物车列表
     */
    List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);

}
