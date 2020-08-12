package com.wxutil.wxPay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信统一下单基础参数实体：注意！只包含必填参数项
 * @Author: zhouwuping
 * @Date:Create in 2020/8/3 9:25
 */
@Data
public class WxPayParam implements Serializable {

    /**
     * body 商品简单描述
     */
    private String body;
    /**
     * out_trade_no 订单号
     */
    private String outTradeNo;

    /**
     * total_fee 标价金额：订单总金额，单位为分
     */
    private String totalFee;
    /**
     * spbill_create_ip 终端IP
     */
    private String spBillCreateIp;

    /**
     * notify_url 通知地址
     */
    private String notifyUrl;

	/**
	 * 场景信息：例<br/>
	 * //WAP网站应用
	 * {"h5_info": {"type":"Wap","wap_url": "https://pay.qq.com","wap_name": "腾讯充值"}}<br/>
	 * //IOS移动应用
	 * {"h5_info": {"type":"IOS","app_name": "王者荣耀","bundle_id": "com.tencent.wzryIOS"}}<br/>
	 *
	 * //安卓移动应用
	 * {"h5_info": {"type":"Android","app_name": "王者荣耀","package_name": "com.tencent.tmgp.sgame"}}
	 */
	private String sceneInfo;



}
