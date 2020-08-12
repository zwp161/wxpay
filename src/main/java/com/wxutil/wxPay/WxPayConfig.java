package com.wxutil.wxPay;

import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author: zhouwp
 * @date:Create in 16:35 2020/8/1
 */
@Data
public class WxPayConfig {
	private String appId;
	private String mchId;
	private String key;

	private WXPayConstants.SignType signType = WXPayConstants.SignType.MD5;

	/**
	 * 回调URL
	 */
	private String callBackURL;

	/**
	 * 证书
	 */
	private byte[] certData;

	/**
	 * HTTP(S) 连接超时时间，单位毫秒
	 *
	 * @return
	 */
	public int getHttpConnectTimeoutMs() {
		return 6*1000;
	}

	/**
	 * HTTP(S) 读数据超时时间，单位毫秒
	 *
	 * @return
	 */
	public int getHttpReadTimeoutMs() {
		return 8*1000;
	}

	public InputStream getCertStream() {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(this.certData);
		//证书文件流
		return inputStream;
	}
}
