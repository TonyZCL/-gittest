package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService{

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${notifyurl}")
    private String notifyurl;

    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map<String, String> createNative(String outTradeNo, String totalFee) {
        Map<String,String> map = new HashMap<>();
        try {
            //1.组合要发送到微信支付的参数
            Map<String,String> param = new HashMap<>();
            param.put("appid",appid);//从微信申请的公众账号 ID
            param.put("mch_id",partner);//从微信申请的商户号
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            //param.put("sign","");//微信 sdk 提供有工具类包生成
            param.put("body","品优购");//商品描述-可以设置为商品的标题
            param.put("out_trade_no",outTradeNo);//订单号
            param.put("total_fee",totalFee);//交易总金额
            param.put("spbill_create_ip","127.0.0.1");//当前机器ip
            param.put("notify_url",notifyurl);//回调地址
            param.put("trade_type","NATIVE");//交易类型：扫描支付

            //2、将参数 map 转换为微信支付需要的 xml
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("发送到微信统一下单的参数为：" + signedXml);

            //3、创建 httpCient 对象并发送信息到微信支付
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //4、获取微信支付返回的数据
            String content = httpClient.getContent();
            System.out.println("微信统一下单返回的内容为：" + content);

            //5、转换内容为 map 并设置返回结果
            Map<String,String> resultMap = WXPayUtil.xmlToMap(content);
            map.put("result_code",resultMap.get("result_code"));//业务结果
            map.put("code_url",resultMap.get("code_url"));//二维码支付地址
            //以下是自己用的，key所以可以使用驼峰形式
            map.put("outTradeNo",outTradeNo);
            map.put("totalFee",totalFee);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        try {
            //1、组合要发送的参数
            Map<String,String> param = new HashMap<>();
            param.put("appid", appid);//从微信申请的公众账号 ID
            param.put("mch_id", partner);//从微信申请的商户号
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            //param.put("sign","");//微信 sdk 提供有工具类包生成
            param.put("out_trade_no", outTradeNo);//订单号

            //2、将参数map转换为微信支付需要的xml
            String signedXml = WXPayUtil.generateSignedXml(param,partnerkey);
            System.out.println("发送到微信支付系统 查询订单 的参数为：" + signedXml);

            //3、创建 httpCient 对象并发送信息到微信支付
            HttpClient httpClient = new
                    HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //4、获取微信支付返回的数据
            String content = httpClient.getContent();
            System.out.println("发送到微信支付系统 查询订单 的返回结果为：" + content);

            //5、转换内容为 map 并设置返回结果
            return WXPayUtil.xmlToMap(content);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
