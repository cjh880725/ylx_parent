package cn.cjh.core.service;

import cn.cjh.core.pojo.log.PayLog;

public interface PayLogService {

    /**
     * 根据用户查询payLog
     * @param userId
     * @return
     */
    public PayLog searchPayLogFromRedis(String userId);

    /**
     * 修改订单状态
     * @param out_trade_no 支付订单号
     * @param trade_no 微信返回的交易流水号
     */
    public void updateOrderStatus(String out_trade_no,String trade_no);


}
