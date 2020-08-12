package com.wxutil.wxpaysdk.service;

import com.wxutil.wxpaysdk.entity.OrderQuery;
import com.wxutil.wxpaysdk.entity.WxPayParamVariable;

import java.util.Map;

public interface WeixinService {
    public Map<String, String> unifiedOrder(WxPayParamVariable variable) throws Exception;
    public Map<String, String> orderQuery(OrderQuery orderQuery) throws Exception;
}
