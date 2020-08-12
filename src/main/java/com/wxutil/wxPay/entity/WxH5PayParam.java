package com.wxutil.wxPay.entity;

import lombok.Data;

/**
 * H5支付参数：注意！只包含H5支付必填参数
 * @author: zhouwp
 * @date:Create in 9:31 2020/8/3
 */
@Data
public class WxH5PayParam extends WxPayParam {
	/**
	 * H5支付的交易类型为MWEB
	 */
	private static final String TRADE_TYPE = "MWEB";


	public final String getTradeType() {
		return TRADE_TYPE;
	}

}
