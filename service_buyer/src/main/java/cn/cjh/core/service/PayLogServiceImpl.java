package cn.cjh.core.service;

import cn.cjh.core.dao.log.PayLogDao;
import cn.cjh.core.dao.order.OrderDao;
import cn.cjh.core.pojo.log.PayLog;
import cn.cjh.core.pojo.order.Order;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

@Service
public class PayLogServiceImpl implements PayLogService{

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PayLogDao payLogDao;

    @Autowired
    private OrderDao orderDao;
    @Override
    public PayLog searchPayLogFromRedis(String userId) {
        return (PayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String trade_no) {
        //1.修改支付日志状态
        PayLog payLog = payLogDao.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());
        payLog.setTradeState("1");//已支付
        payLog.setTransactionId(trade_no);//交易号
        payLogDao.updateByPrimaryKey(payLog);
        //2.修改订单状态
        String orderList = payLog.getOrderList();//获取订单号列表
        String[] orderIds = orderList.split(",");//获取订单号数组

        for(String orderId:orderIds){
            Order order = orderDao.selectByPrimaryKey( Long.parseLong(orderId) );
            if(order!=null){
                order.setStatus("2");//已付款
                orderDao.updateByPrimaryKey(order);
            }
        }
        //清除redis缓存数据
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());

    }

}
