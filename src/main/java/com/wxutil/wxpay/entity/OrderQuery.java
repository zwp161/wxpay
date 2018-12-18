package com.wxutil.wxpay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: zhouwuping
 * @date:Create in 10:11 2018/9/25
 */
@Data
public class OrderQuery implements Serializable {
    /*api:
    微信订单号  transaction_id  二选一 String(32)  1009660380201506130728806387 微信的订单号，建议优先使用
    商户订单号  out_trade_no  String(32) 20150806125346  商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 详见商户订单号
    */
    //微信订单号
    private String transactionId;
    //商户订单号
    private String outTradeNo;

}
