package cn.cjh.core.service;

import cn.cjh.core.dao.log.PayLogDao;
import cn.cjh.core.dao.order.OrderDao;
import cn.cjh.core.dao.order.OrderItemDao;
import cn.cjh.core.entity.BuyerCart;
import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.log.PayLog;
import cn.cjh.core.pojo.order.Order;
import cn.cjh.core.pojo.order.OrderItem;
import cn.cjh.core.util.IdWorker;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private OrderDao orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderItemDao orderItemMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private PayLogDao payLogDao;

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        return null;
    }

    @Override
    public void add(Order order) {
        //得到购物车数据
        List<BuyerCart> cartList = (List<BuyerCart>)redisTemplate.boundHashOps("cartList").get(order.getUserId());

        List<String> orderIdList=new ArrayList();//订单ID列表
        double total_money=0;//总金额 （元）

        for(BuyerCart cart:cartList){
            long orderId = idWorker.nextId();
            System.out.println("sellerId:"+cart.getSellerId());
            Order tborder=new Order();//新创建订单对象
            tborder.setOrderId(orderId);//订单ID
            tborder.setUserId(order.getUserId());//用户名
            tborder.setPaymentType(order.getPaymentType());//支付类型
            tborder.setStatus("1");//状态：未付款
            tborder.setCreateTime(new Date());//订单创建日期
            tborder.setUpdateTime(new Date());//订单更新日期
            tborder.setReceiverAreaName(order.getReceiverAreaName());//地址
            tborder.setReceiverMobile(order.getReceiverMobile());//手机号
            tborder.setReceiver(order.getReceiver());//收货人
            tborder.setSourceType(order.getSourceType());//订单来源
            tborder.setSellerId(cart.getSellerId());//商家ID
            //循环购物车明细
            double money=0;
            for(OrderItem orderItem :cart.getOrderItemList()){
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId( orderId  );//订单ID
                orderItem.setSellerId(cart.getSellerId());
                money+=orderItem.getTotalFee().doubleValue();//金额累加
                orderItemMapper.insert(orderItem);
            }
            tborder.setPayment(new BigDecimal(money));
            orderMapper.insert(tborder);

            orderIdList.add(orderId+"");//添加到订单列表
            total_money+=money;//累加到总金额
        }
        if("1".equals(order.getPaymentType())){//如果是微信支付
            PayLog payLog=new PayLog();
            String outTradeNo=  idWorker.nextId()+"";//支付订单号
            payLog.setOutTradeNo(outTradeNo);//支付订单号
            payLog.setCreateTime(new Date());//创建时间
            //订单号列表，逗号分隔
            String ids=orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
            payLog.setOrderList(ids);//订单号列表，逗号分隔
            payLog.setPayType("1");//支付类型
            payLog.setTotalFee( (long)(total_money*100 ) );//总金额(分)
            payLog.setTradeState("0");//支付状态
            payLog.setUserId(order.getUserId());//用户ID
            payLogDao.insert(payLog);//插入到支付日志表
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//放入缓存
        }

        redisTemplate.boundHashOps("cartList").delete(order.getUserId());

    }

    @Override
    public void update(Order order) {

    }

    @Override
    public Order findOne(Long orderId) {
        return null;
    }

    @Override
    public void delete(Long[] orderIds) {

    }

    @Override
    public PageResult findPage(Order order, int pageNum, int pageSize) {
        return null;
    }

    @Override
    public void updateOrderStauts(String out_trade_no, String transaction_id) {

    }
}
