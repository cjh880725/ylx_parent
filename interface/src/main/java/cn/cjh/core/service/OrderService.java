package cn.cjh.core.service;


import cn.cjh.core.entity.PageResult;
import cn.cjh.core.pojo.order.Order;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<Order> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Order order);
	
	
	/**
	 * 修改
	 */
	public void update(Order order);
	

	/**
	 * 根据ID获取实体
	 * @param
	 * @return
	 */
	public Order findOne(Long orderId);
	
	
	/**
	 * 批量删除
	 * @param
	 */
	public void delete(Long[] orderIds);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(Order order, int pageNum, int pageSize);

	/**
	 * 当支付成功以后
	 * 1、修改订单状态
	 * 2、修改支付日志的状态，并将支付日志的redis清空
	 */
	public  void updateOrderStauts(String out_trade_no, String transaction_id);
	
}
