package com.wxutil.wxPay;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springblade.common.utils.wxPay.entity.WxH5PayParam;
import org.springblade.common.utils.wxPay.entity.WxPayParam;
import org.springblade.common.utils.wxPay.entity.WxPayQueryParam;
import org.springblade.common.utils.wxPay.entity.WxRefundParam;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhouwp
 * @date:Create in 16:48 2020/8/1
 */
@Slf4j
public class WxPay {

	private WxPayConfig config;
	private final WXPayRequest wxPayRequest;

	public WxPay(WxPayConfig config) throws Exception {
		this.config = config;
		this.wxPayRequest = new WXPayRequest(config);
	}

	public WxPayConfig getConfig() {
		return config;
	}

	/**
	 * H5支付统一下单
	 *
	 * @param h5PayParam
	 * @return
	 */
	public Map<String, String> h5UnifiedOrder(WxH5PayParam h5PayParam) throws Exception {
		Map<String, String> params = new HashMap<>(12);
		params.put("trade_type", h5PayParam.getTradeType());

		Map<String, String> commonParams = unifiedOrderCommonParamsBuild(h5PayParam);
		params.putAll(commonParams);

		return unifiedOrder(params);
	}


	/**
	 * 微信申请退款接口
	 *
	 * @param refundParam
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> requestRefund(WxRefundParam refundParam) throws Exception {
		Map<String, String> params = new HashMap<>(12);

		//订单号二选一
		if (StringUtils.isEmpty(refundParam.getTransactionId()) && StringUtils.isEmpty(refundParam.getOutTradeNo())) {
			throw new Exception("wxRefund 参数 transaction_id 或 out_trade_no 二选一");
		} else {
			if (StringUtils.isEmpty(refundParam.getTransactionId())) {
				params.put("out_trade_no", refundParam.getOutTradeNo());
			} else {
				params.put("transaction_id", refundParam.getTransactionId());
			}
		}

		if (StringUtils.isEmpty(refundParam.getOutRefundNo())) {
			throw new Exception("wxRefund 缺少 out_refund_no 参数");
		}
		params.put("out_refund_no", refundParam.getOutRefundNo());

		if (StringUtils.isEmpty(refundParam.getTotalFee())) {
			throw new Exception("wxRefund 缺少 total_fee 参数");
		}
		params.put("total_fee", refundParam.getTotalFee());
		if (StringUtils.isEmpty(refundParam.getRefundFee())) {
			throw new Exception("wxRefund 缺少 refund_fee 参数");
		}
		params.put("refund_fee", refundParam.getRefundFee());

		//可选参数
		if (!StringUtils.isEmpty(refundParam.getNotifyUrl())) {
			params.put("refund_fee", refundParam.getNotifyUrl());
		}

		fixedParams(params);

		String xmlBody = WXPayUtil.mapToXml(params);
		String body = wxPayRequest.requestOnce(
			WXPayConstants.DOMAIN_API,
			WXPayConstants.REFUND_URL_SUFFIX,
			xmlBody,
			config.getHttpConnectTimeoutMs(),
			config.getHttpReadTimeoutMs(),
			true
		);

		Map<String, String> resultMap = WXPayUtil.xmlToMap(body);
		checkResultErr(resultMap,params);
		return resultMap;
//		String url = "https://" + WXPayConstants.DOMAIN_API + WXPayConstants.REFUND_URL_SUFFIX;
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.parseMediaType("text/xml; charset=UTF-8"));
//		headers.add(HttpHeaders.ACCEPT, MediaType.TEXT_XML.toString());
//
//		ResponseEntity<String> responseEntity = RestTemplateUtil.executePost(url, xmlBody, headers);
//		HttpStatus statusCode = responseEntity.getStatusCode();
//		if (statusCode == HttpStatus.OK) {
//			String body = responseEntity.getBody();
//			return WXPayUtil.xmlToMap(body);
//		}
//		log.error(url + System.lineSeparator() + "HTTP 响应状态：" + statusCode.toString());
//		return null;
	}

	/**
	 * 统一下单
	 *
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> unifiedOrder(Map<String, String> params) throws Exception {
		fixedParams(params);

		String xmlBody = WXPayUtil.mapToXml(params);
		String body = wxPayRequest.requestOnce(
			WXPayConstants.DOMAIN_API,
			WXPayConstants.UNIFIEDORDER_URL_SUFFIX,
			xmlBody,
			config.getHttpConnectTimeoutMs(),
			config.getHttpReadTimeoutMs(),
			false
		);
		Map<String, String> resultMap = WXPayUtil.xmlToMap(body);
		checkResultErr(resultMap,params);
		return resultMap;

//		String url = "https://" + WXPayConstants.DOMAIN_API + WXPayConstants.UNIFIEDORDER_URL_SUFFIX;
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.parseMediaType("text/xml; charset=UTF-8"));
//		headers.add(HttpHeaders.ACCEPT, MediaType.TEXT_XML.toString());
//
//		ResponseEntity<String> responseEntity = RestTemplateUtil.executePost(url, xmlBody, headers);
//		HttpStatus statusCode = responseEntity.getStatusCode();
//		if (statusCode == HttpStatus.OK) {
//			String body = responseEntity.getBody();
//			return WXPayUtil.xmlToMap(body);
//		}
//		log.error(url + System.lineSeparator() + "HTTP 响应状态：" + statusCode.toString());
//		return null;
	}

	/**
	 * 固定参数
	 *
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private void fixedParams(Map<String, String> params) throws Exception {
		//固定参数开始
		params.put("appid", config.getAppId());
		params.put("mch_id", config.getMchId());
		params.put("nonce_str", WXPayUtil.generateNonceStr());
		//默认MD5
		params.put("sign_type",WXPayConstants.MD5);

		if (config.getSignType() == WXPayConstants.SignType.HMACSHA256){
			params.put("sign_type",WXPayConstants.HMACSHA256);
		}

		params.put("sign", WXPayUtil.generateSignature(params, config.getKey()));
		//固定参数结束
	}

	/**
	 * 检查结果错误
	 * @param stringMap
	 * @param data
	 * @throws Exception
	 */
	private void checkResultErr(Map<String, String> stringMap, Object data) throws Exception {
		if (!"SUCCESS".equals(stringMap.get("return_code"))) {
			//通信标识失败
			throw new RuntimeException(MessageFormat.format("通信标识失败：状态码:{0};状态描述:{1};请求参数:{2};响应参数:{3}", stringMap.get("return_code"), stringMap.get("return_msg"), JSON.toJSONString(data), JSON.toJSONString(stringMap)));
		}
		if (!"SUCCESS".equals(stringMap.get("result_code"))) {
			//交易标识失败
			throw new RuntimeException(MessageFormat.format("交易标识失败：错误代码:{0};错误代码描述:{1};请求参数:{2};响应参数:{3}", stringMap.get("err_code"), stringMap.get("err_code_des"), JSON.toJSONString(data), JSON.toJSONString(stringMap)));
		}

		//验证签名
		if (!WXPayUtil.isSignatureValid(stringMap, config.getKey(), config.getSignType())) {
			throw new RuntimeException("签名验证失败！");
		}
	}

//	/**
//	 * 生成签名
//	 *
//	 * @param params
//	 * @return
//	 * @throws Exception
//	 */
//	public String generateSignature(Map<String, String> params) throws Exception {
//		return WXPayUtil.generateSignature(params, config.getKey());
//	}

	/**
	 * 统一下单通用支付参数构建
	 *
	 * @param payParam
	 * @return
	 */
	private Map<String, String> unifiedOrderCommonParamsBuild(WxPayParam payParam) throws Exception {
		Map<String, String> params = new HashMap<>(6);

		if (StringUtils.isEmpty(payParam.getBody())) {
			throw new Exception("wxPay 缺少 body 参数");
		}
		params.put("body", payParam.getBody());
		if (StringUtils.isEmpty(payParam.getOutTradeNo())) {
			throw new Exception("wxPay 缺少 out_trade_no 参数");
		}
		params.put("out_trade_no", payParam.getOutTradeNo());
		if (StringUtils.isEmpty(payParam.getSceneInfo())) {
			throw new Exception("wxPay 缺少 scene_info 参数");
		}
		params.put("scene_info", payParam.getSceneInfo());
		if (StringUtils.isEmpty(payParam.getTotalFee())) {
			throw new Exception("wxPay 缺少 total_fee 参数");
		}
		params.put("total_fee", payParam.getTotalFee());
		if (StringUtils.isEmpty(payParam.getNotifyUrl())) {
			throw new Exception("wxPay 缺少 notify_url 参数");
		}
		params.put("notify_url", payParam.getNotifyUrl());
		if (StringUtils.isEmpty(payParam.getSpBillCreateIp())) {
			throw new Exception("wxPay 缺少 spbill_create_ip 参数");
		}
		params.put("spbill_create_ip", payParam.getSpBillCreateIp());

		return params;
	}

	/**
	 * 订单查询
	 *
	 * @param queryParam
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> orderQuery(WxPayQueryParam queryParam) throws Exception {

		Map<String, String> params = new HashMap<>(12);

		//订单号二选一
		if (StringUtils.isEmpty(queryParam.getTransactionId()) && StringUtils.isEmpty(queryParam.getOutTradeNo())) {
			throw new Exception("wxRefund 参数 transaction_id 或 out_trade_no 二选一");
		} else {
			if (StringUtils.isEmpty(queryParam.getTransactionId())) {
				params.put("out_trade_no", queryParam.getOutTradeNo());
			} else {
				params.put("transaction_id", queryParam.getTransactionId());
			}
		}

		fixedParams(params);

		String xmlBody = WXPayUtil.mapToXml(params);
		String body = wxPayRequest.requestOnce(
			WXPayConstants.DOMAIN_API,
			WXPayConstants.ORDERQUERY_URL_SUFFIX,
			xmlBody,
			config.getHttpConnectTimeoutMs(),
			config.getHttpReadTimeoutMs(),
			false
		);
		Map<String, String> resultMap = WXPayUtil.xmlToMap(body);
		checkResultErr(resultMap,params);
		return resultMap;
	}

}
