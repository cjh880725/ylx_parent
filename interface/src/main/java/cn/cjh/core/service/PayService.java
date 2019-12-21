package cn.cjh.core.service;

import java.util.Map;

public interface PayService {
    /**
     * 生成支付宝支付二维码
     * @param out_trade_no 订单号
     * @param total_fee 金额(分)
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);

    Map queryPayStatus(String out_trade_no);
}
