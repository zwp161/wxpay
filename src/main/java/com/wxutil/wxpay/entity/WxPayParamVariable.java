package com.wxutil.wxpay.entity;

import cn.jr.cose.api.entity.Base;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信统一下单参数变量
 * @author: zhouwuping
 * @date:Create in 17:12 2018/9/19
 */
@Data
public class WxPayParamVariable implements Serializable {

    /**
     * device_info 设备号：自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
     */
    private String deviceInfo = "WEB";
    /**
     * body 商品简单描述
     */
    private String body;
    /**
     * out_trade_no 订单号
     */
    private String outTradeNo;
    /**
     * fee_type标价币种：默认人民币：CNY
     */
    private String feeType = "CNY";

    /**
     * total_fee 标价金额：订单总金额，单位为分
     */
    private String totalFee;
    /**
     * spbill_create_ip 终端IP
     */
    private String spBillCreateIp;
    /**
     * trade_type 交易类型:JSAPI 公众号支付 NATIVE 扫码支付 APP APP支付
     */
    private String tradeType = "JSAPI";
    /**
     * openid 用户标识:trade_type=JSAPI时（即公众号支付），此参数必传
     */
    private String openid;

    /**
     * notify_url 通知地址
     */
    private String notifyUrl;

}
