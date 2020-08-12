package com.wxutil.wxpaysdk.sdk;

//import cn.jr.cose.api.exception.BusinessException;
//import cn.jr.cose.provider.mapper.SystemConfigMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.text.MessageFormat;

/**
 * 微信支付配置对象<br>
 * 把配置放在此处果在此处获取赋值即可<br>
 * 数据库中修改appId等支付配置字段需要重启服务
 * @author: zhouwuping
 * @date:Create in 17:31 2018/9/12
 */
@Component
public class MyWXPayConfig extends WXPayConfig {

    @Autowired
    //private SystemConfigMapper systemConfigMapper;

    private String appId;
    private String mchId;
    private String key;

    private byte[] certData;

    private IWXPayDomain wxPayDomain = new WxPayDomainImpl();
    //测试的时候可以改成true,代表使用沙箱模式
    private boolean useSandBox = false;

    public MyWXPayConfig(){

    }

    @PostConstruct
    public void initMethod(){
        appId = null;//systemConfigMapper.selectConfigByName("wxAppID");
        mchId = null;//systemConfigMapper.selectConfigByName("wxMchID");
        key = null;//systemConfigMapper.selectConfigByName("wxKey");
        if(StringUtils.isEmpty(appId)||StringUtils.isEmpty(mchId)|| StringUtils.isEmpty(key)){
            throw new RuntimeException(MessageFormat.format("支付配置信息不完善！appId:{0},mchId:{1},key:{2}",appId,mchId,key));
        }
        //暂时不需要证书
        //String certPath = "/path/to/apiclient_cert.p12";
        //File file = new File(certPath);
        //InputStream certStream = new FileInputStream(file);
        //this.certData = new byte[(int) file.length()];
        //certStream.read(this.certData);
        //certStream.close();
    }

    @Override
    public String getAppID() {
        return appId;
    }

    @Override
    public String getMchID() {
        return mchId;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public boolean shouldAutoReport() {
        //取消报告
        return false;
    }

    @Override
    InputStream getCertStream() {
        return null;
        //ByteArrayInputStream inputStream = new ByteArrayInputStream(this.certData);
        //证书文件流
        //return inputStream;
    }

    @Override
    IWXPayDomain getWXPayDomain() {
        return this.wxPayDomain;
    }

    /**
     * 是否使用沙箱模式
     * @return
     */
    public boolean useSandBox() {
        //是否使用沙箱模式
        return useSandBox;
    }

    /**
     * 获取签名类型：使用沙箱环境使用MD5；否则使用HMACSHA256
     * <br>——SDK内置规则
     * <br>——以上不用  全部修改为md5签名类型
     * @return
     */
    public WXPayConstants.SignType getSignType() {
        //使用沙箱环境使用MD5；否则使用HMACSHA256
        //boolean useSandBox = useSandBox();
        //WXPayConstants.SignType signType = useSandBox ? WXPayConstants.SignType.MD5 : WXPayConstants.SignType.HMACSHA256;
        WXPayConstants.SignType signType = WXPayConstants.SignType.MD5;
        return signType;
    }
}

/**
 * @Author: zhouwuping
 * @Date:Create in 2018/9/12 20:05
 */
class WxPayDomainImpl implements IWXPayDomain{
    //设置支付主域名
    private DomainInfo domainInfo = new DomainInfo(WXPayConstants.DOMAIN_API,true);

    //@Override
    public void report(String domain, long elapsedTimeMillis, Exception ex) {
        //啥也不干
    }

    //@Override
    public DomainInfo getDomain(WXPayConfig config) {
        return domainInfo;
    }
}