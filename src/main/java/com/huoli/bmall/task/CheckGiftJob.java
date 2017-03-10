package com.huoli.bmall.task;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.huoli.bmall.points.dao.NotifyDao;
import com.huoli.bmall.points.dao.OrderDao;
import com.huoli.bmall.points.model.Notify;
import com.huoli.bmall.points.model.OrderQuery;
import com.huoli.bmall.points.model.ZOrder;
import com.huoli.bmall.points.service.IGiftService;
import com.huoli.bmall.points.service.INotifyMsgService;
import com.huoli.bmall.points.service.impl.OrderServiceImpl;

/**
 * @author :zhouwenbin
 * @time :2016-9-6
 * @comment:
 **/
public class CheckGiftJob {
	@Resource
	public OrderDao orderDao;
	@Resource
	NotifyDao notifyDao;
	@Resource
	IGiftService giftService;
	@Resource
	private INotifyMsgService notifyMsgService;

	private static Logger log = Logger.getLogger(CheckGiftJob.class);
                                //  0   1   2   3   4   5   6  7   8  9   10  11

	public void checkGift() {

		try {
			List<ZOrder> giftOrders = new ArrayList<ZOrder>();
			for (int i = 0; i <= OrderServiceImpl.tableNoLimit; i++) {
				String table = "zorders_" + i;
				OrderQuery query = new OrderQuery();
				query.setTable(table);
				List<ZOrder> tempOrder = orderDao.getGiftOffOrder(query);
				if (CollectionUtils.isNotEmpty(tempOrder))
					giftOrders.addAll(tempOrder);
			}
			if (CollectionUtils.isEmpty(giftOrders))
				return;
			for (int i = giftOrders.size() - 1; i >= 0; i--) {
				ZOrder order = giftOrders.get(i);
				log.info("CheckGiftJob开始：" + order.getOrderid());
				giftService.closeGiftOrder(order);
			}
			
			// 处理通知订单
			List<Notify> notifyOrders = new ArrayList<Notify>();
			notifyOrders = notifyDao.getNeedNotifys();
			for (int i = notifyOrders.size() - 1; i >= 0; i--) {
				Notify notify = notifyOrders.get(i);
				System.out.println("notifyOrder start:" + notify.getOrderid());
				notifyMsgService.notifyOrder(notify);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
