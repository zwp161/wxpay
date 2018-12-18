package com.wxutil.wxpay.service.impl;

import com.alibaba.fastjson.JSON;
import com.wxutil.wxpay.entity.OrderQuery;
import com.wxutil.wxpay.entity.WxPayParamVariable;
import com.wxutil.wxpay.sdk.MyWXPayConfig;
import com.wxutil.wxpay.sdk.WXPay;
import com.wxutil.wxpay.sdk.WXPayConstants;
import com.wxutil.wxpay.sdk.WXPayUtil;
import com.wxutil.wxpay.service.WeixinService;
import com.wxutil.wxpay.utils.SpringContextUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Service("weixinService")
public class WeixinServiceImpl implements WeixinService {

    @Override
    public Map<String, String> unifiedOrder(WxPayParamVariable variable) throws Exception {

        Map<String, String> data = new HashMap<String, String>();
        data.put("body", variable.getBody());//商品简单描述
        data.put("out_trade_no", variable.getOutTradeNo());//商户订单号
        data.put("device_info", variable.getDeviceInfo());//设备号：自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
        data.put("fee_type", variable.getFeeType());//标价币种：默认人民币：CNY
        data.put("total_fee", variable.getTotalFee());//标价金额：订单总金额，单位为分
        data.put("spbill_create_ip", variable.getSpBillCreateIp());//终端IP
        data.put("trade_type", variable.getTradeType());  // 交易类型:JSAPI 公众号支付 NATIVE 扫码支付 APP APP支付
        data.put("notify_url", variable.getNotifyUrl()); //通知地址
        if("NATIVE".equals(variable.getTradeType())){
            data.put("product_id", RandomStringUtils.randomNumeric(11));
        }

        if("JSAPI".equals(variable.getTradeType())){
            data.put("openid", variable.getOpenid());//用户openid
        }
        MyWXPayConfig payConfig = (MyWXPayConfig) SpringContextUtils.getBean("myWXPayConfig");
        //是否使用沙箱模式
        boolean useSandBox = payConfig.useSandBox();
        WXPay wxPay = new WXPay(payConfig, false, useSandBox);
        Map<String, String> stringMap = wxPay.unifiedOrder(data);
        //准备返回成功调用后给前端js调用支付时候请求数据
        Map<String, String> jsPayMap = new HashMap<String, String>();
        checkResultErr(payConfig, stringMap,data);

        String tradeType = stringMap.get("trade_type");
        if ("JSAPI".equals(tradeType)) {
            //公众号支付
            jsPayMap.put("appId", payConfig.getAppID());
            jsPayMap.put("nonceStr", WXPayUtil.generateNonceStr());
            jsPayMap.put("package", "prepay_id=" + stringMap.get("prepay_id"));
            if (WXPayConstants.SignType.MD5.equals(payConfig.getSignType())) {
                jsPayMap.put("signType", WXPayConstants.MD5);
            }else if (WXPayConstants.SignType.HMACSHA256.equals(payConfig.getSignType())) {
                jsPayMap.put("signType", WXPayConstants.HMACSHA256);
            }
            jsPayMap.put("timeStamp", WXPayUtil.getCurrentTimestamp()+"");
            String sign = WXPayUtil.generateSignature(jsPayMap,payConfig.getKey(),payConfig.getSignType());
            jsPayMap.put("paySign", sign);
        } else if ("NATIVE".equals(tradeType)) {
            //扫码支付
            jsPayMap.put("codeUrl", stringMap.get("code_url"));
        }
        jsPayMap.put("tradeType", tradeType);
        return jsPayMap;
    }

    @Override
    public Map<String, String> orderQuery(OrderQuery orderQuery) throws Exception {
        Map<String, String> data = new HashMap<String, String>();
        if(orderQuery.getTransactionId() != null){
            data.put("transaction_id",orderQuery.getTransactionId().trim());
        }else if(orderQuery.getOutTradeNo() != null){
            data.put("out_trade_no",orderQuery.getOutTradeNo().trim());
        }else{
            throw new RuntimeException("缺少transaction_id或out_trade_no参数");
        }

        MyWXPayConfig payConfig = (MyWXPayConfig) SpringContextUtils.getBean("myWXPayConfig");
        //是否使用沙箱模式
        boolean useSandBox = payConfig.useSandBox();
        WXPay wxPay = new WXPay(payConfig, false, useSandBox);
        Map<String, String> stringMap = wxPay.orderQuery(data);
        checkResultErr(payConfig, stringMap, data);

        Map<String, String> jsPayMap = new HashMap<String, String>();
        jsPayMap.put("tradeState",stringMap.get("trade_state"));
        jsPayMap.put("outTradeNo",stringMap.get("out_trade_no"));

        return jsPayMap;
    }

    /**
     * 检查结果错误
     * @param payConfig
     * @param stringMap
     * @param data
     * @throws Exception
     */
    private void checkResultErr(MyWXPayConfig payConfig, Map<String, String> stringMap, Object data) throws Exception {
        if (!"SUCCESS".equals(stringMap.get("return_code"))) {
            //通信标识失败
            throw new RuntimeException(MessageFormat.format("通信标识失败：状态码:{0};状态描述:{1};请求参数:{2};响应参数:{3}", stringMap.get("return_code"), stringMap.get("return_msg"),JSON.toJSONString(data),JSON.toJSONString(stringMap)));
        }
        if (!"SUCCESS".equals(stringMap.get("result_code"))) {
            //交易标识失败
            throw new RuntimeException(MessageFormat.format("交易标识失败：错误代码:{0};错误代码描述:{1};请求参数:{2};响应参数:{3}", stringMap.get("err_code"), stringMap.get("err_code_des"),JSON.toJSONString(data),JSON.toJSONString(stringMap)));
        }

        //验证签名
        if (!WXPayUtil.isSignatureValid(stringMap, payConfig.getKey(), payConfig.getSignType())) {
            throw new RuntimeException("签名验证失败！");
        }
    }
}
