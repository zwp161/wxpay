package com.wxutil.wxpay.service;

import com.wxutil.wxpay.entity.OrderQuery;
import com.wxutil.wxpay.entity.WxPayParamVariable;

import java.util.Map;

public interface WeixinService {
    public Map<String, String> unifiedOrder(WxPayParamVariable variable) throws Exception;
    public Map<String, String> orderQuery(OrderQuery orderQuery) throws Exception;
}
