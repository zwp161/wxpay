package com.wxutil.wxPay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: zhouwp
 * @date:Create in 11:11 2020/8/12
 */
@Data
public class WxPayQueryParam implements Serializable {

	/**
	 * 微信的订单号，建议优先使用
	 */
	private String transactionId;

	/**
	 * 商户系统内部订单号
	 */
	private String outTradeNo;
}
