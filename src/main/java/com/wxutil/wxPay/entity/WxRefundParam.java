package com.wxutil.wxPay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信退款参数<br/>
 * 注意:参数大部分是必传项目，有部分非必填的，没有加入，如果有需要，请参考微信支付文档
 * @author: zhouwp
 * @date:Create in 17:24 2020/8/5
 */
@Data
public class WxRefundParam implements Serializable {
	/**
	 * 微信订单号
	 */
	private String transactionId;
	/**
	 * 商户订单号
	 */
	private String outTradeNo;
	/**
	 * 商户退款单号：同一退款单号多次请求只退一笔。
	 */
	private String outRefundNo;
	/**
	 * 订单金额(分)
	 */
	private String totalFee;
	/**
	 * 退款金额(分)
	 */
	private String refundFee;
	/**
	 * 退款结果通知url
	 */
	private String notifyUrl;
}
