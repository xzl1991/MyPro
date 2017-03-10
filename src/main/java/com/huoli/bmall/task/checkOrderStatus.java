package com.huoli.bmall.task;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.huoli.bmall.adapter.model.PayOrderInfo;
import com.huoli.bmall.adapter.service.IPayService;
import com.huoli.bmall.adapter.tools.PayOrderStatus;
import com.huoli.bmall.exception.MyException;
import com.huoli.bmall.points.dao.OrderDao;
import com.huoli.bmall.points.model.OrderQuery;
import com.huoli.bmall.points.model.UserInfo;
import com.huoli.bmall.points.model.ZOrder;
import com.huoli.bmall.points.service.IOrderService;
import com.huoli.bmall.points.service.IProductService;
import com.huoli.bmall.points.service.impl.OrderServiceImpl;

public class checkOrderStatus {
	@Resource
	OrderDao orderDao;
	@Resource
	IOrderService orderService;
	@Resource
	IPayService payService;
	@Resource
	IProductService productService;
	

	private static Logger log = Logger.getLogger("paylog");

	public void checkOrder() throws MyException {
		try {

//			System.out.println("定时任务startJob");
			List<ZOrder> orders = new ArrayList<ZOrder>();

			for (int i = 0; i <= OrderServiceImpl.tableNoLimit; i++) {
				String table = "zorders_" + i;
				OrderQuery query = new OrderQuery();
				query.setTable(table);
				List<ZOrder> tempList = orderDao.getNotPayZOrder(query);
				if (CollectionUtils.isNotEmpty(tempList))
					orders.addAll(tempList);
			}
			OrderQuery crowdQuery = new OrderQuery();
			crowdQuery.setTable("zorders_crowd");
			List<ZOrder> crowdList = orderDao.getNotPayZOrder(crowdQuery);
			if (CollectionUtils.isNotEmpty(crowdList))
				orders.addAll(crowdList);

//			System.out.println("notify begin:");
			for (int j = orders.size() - 1; j >= 0; j--) {
				ZOrder order = orders.get(j);

				String regex = " ";
				String[] info = order.getPayuserid().split(regex);
				PayOrderInfo payOrderInfo = payService.getOrderDetail(info[0],order.getPayorderid());
				int newstat = PayOrderStatus.getStatus(payOrderInfo.getStatus());
				if (newstat != order.getStatus()) {
					UserInfo user = new UserInfo();
					user.setUserid(order.getUserid());
					user.setHbuserid(info[0]);
					if (info.length > 1) {
						user.setHbauthcode(info[1]);
						user.setAuthcode(info[1]);
						user.setUid(info[2]);
					}
					order.setPaytime(payOrderInfo.getPaytime());
					log.info("change order " + order.getOrderid() + " from "+ order.getStatus() + " to " + newstat);
					orderService.changeOrderStat(user, order, newstat,order.getApp());
				}
			}

//			System.out.println("notify come:");
			List<ZOrder> thirdOrders = new ArrayList<ZOrder>();
			for (int i = 0; i <= OrderServiceImpl.tableNoLimit; i++) {
				String table = "zorders_" + i;
				OrderQuery query = new OrderQuery();
				query.setTable(table);
				List<ZOrder> tempOrder = orderDao.getThirdPartSynZOrder(query);
				if (CollectionUtils.isNotEmpty(tempOrder))
					thirdOrders.addAll(tempOrder);
			}
			for (int i = thirdOrders.size() - 1; i >= 0; i--) {
				ZOrder order = thirdOrders.get(i);
				// System.out.println("testTHOrder:"+order.getOrderid());
				orderService.synThirdpartOrder(null, order, order.getApp());
			}

			productService.closeCrowd();
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}
}
