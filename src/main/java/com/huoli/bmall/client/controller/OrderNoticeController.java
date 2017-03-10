package com.huoli.bmall.client.controller;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.huoli.bmall.adapter.service.IGaotieService;
import com.huoli.bmall.adapter.service.IHbService;
import com.huoli.bmall.adapter.service.IWeiXinUserService;
import com.huoli.bmall.adapter.tools.AdapterTools;
import com.huoli.bmall.exception.MyException;
import com.huoli.bmall.points.dao.OrderDao;
import com.huoli.bmall.points.model.OrderQuery;
import com.huoli.bmall.points.model.Product;
import com.huoli.bmall.points.model.ZOrder;
import com.huoli.bmall.points.model.coupon.CouponCheck;
import com.huoli.bmall.points.model.pay.PayBeforeResp;
import com.huoli.bmall.points.model.pay.WeiXinPayviewResp;
import com.huoli.bmall.points.model.pay.WeiXinTokenCheckResp;
import com.huoli.bmall.points.service.ICouponService;
import com.huoli.bmall.points.service.IOrderService;
import com.huoli.bmall.points.service.IProductService;
import com.huoli.bmall.points.service.IUserPayService;
import com.huoli.bmall.points.service.impl.OrderServiceImpl;
import com.huoli.bmall.util.Tools;

@Controller
public class OrderNoticeController {
	@Resource
	private IOrderService orderService;
	@Resource
	private ICouponService couponService;
	@Resource
	private IWeiXinUserService weiXinUserService;
	@Resource
	private IUserPayService userPayService;
	@Resource
	private OrderDao orderDao;
	@Resource
	private IProductService productService;
	@Resource
	IGaotieService gaotieService;
	@Resource
	IHbService hbService;
	private static Logger log = Logger.getLogger("paylog");

	@RequestMapping("/rest/payBack")
	public @ResponseBody
	String payBack(@RequestParam("payInfo") String payinfo) {
		log.info("payback:" + payinfo);
		// orderService.payNotice(payinfo, 1);
		// orderService.payNewNotice(payinfo);
		return "success";
	}

	@RequestMapping("/rest/hbpayBack")
	public @ResponseBody
	String hbPayNotice(@RequestParam("payInfo") String payinfo) {
		log.info("hbpayback:" + payinfo);
		// orderService.payNotice(payinfo,2);
		return "success";
	}

	@RequestMapping(value = "/rest/payNotice", method = RequestMethod.POST)
	public @ResponseBody
	String payNotice(@RequestBody String para) {
		try {
			para = URLDecoder.decode(para, "UTF-8");
		} catch (Exception e) {
		}
		log.info("payNotice:" + para);
		if (para.endsWith("="))
			para = para.substring(0,para.length()-1);
		orderService.payNewNotice(para);
		return "success";
	}

	@RequestMapping("/rest/closeOrder")
	public @ResponseBody
	String closeOrder(@RequestParam("payInfo") String orderid) {
		try {
			log.info("callback:" + orderid);
			orderService.closeOrder(orderid, 1);
		} catch (MyException e) {
			e.printStackTrace();
		}
		return "success";
	}

	@RequestMapping("/rest/orderRefound")
	public @ResponseBody
	void closeOrder(@RequestParam("orderid") String orderid,
			@RequestParam("amount") Double amount) {
		ZOrder order = null;
		OrderQuery query = OrderServiceImpl.genOrderQuery(orderid, -1);
		order = orderDao.getZOrderByID(query);
		
		log.info("orderRefound回调："+order.getOrderid());

//		UserInfo user = new UserInfo();
//		user.setUserid(order.getUserid());
//		orderService.changeOrderStat(user, order, 4, null, 0, app);
	}

	@RequestMapping("/payview.do")
	public @ResponseBody
	ModelAndView payView(@RequestParam("orderid") String orderid) {
		ModelAndView mv = new ModelAndView();
		OrderQuery query = OrderServiceImpl.genOrderQuery(orderid, -1);
		ZOrder order = orderDao.getZOrderByID(query);
		mv.addObject("img", order.getImg());
		
		Product product = new Product();
		try {
			product = productService.forceGetProduct(order.getProductid(),order.getApp());
		} catch (MyException e) {
			log.error(e);
			e.printStackTrace();
		}
		if (StringUtils.isNotBlank(product.getShortdesc())) {
			mv.addObject("desc", product.getShortdesc());
		} else {
			mv.addObject("desc", "");
		}
		mv.addObject("title", product.getTitle());
		int count = order.getAmount();
		if (count == 0)
			count = 1;
		mv.addObject("pprice", order.getPprice());
		mv.addObject("price", order.getMprice());
		mv.addObject("count", count);
		if (StringUtils.isNotBlank(order.getSpec()))
			mv.addObject("spec", order.getSpec());

		String period = getRemainMin(order.getCreatetime(), product);
		mv.addObject("period", period);
		mv.setViewName("payview");
		return mv;
	}

	private String getRemainMin(String createTime, Product product) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(Tools.dateFormat.parse(createTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
		long waitTime = System.currentTimeMillis() - c.getTimeInMillis();
		double expireTime = (double) (waitTime % (1000 * 60 * 60))
				/ (1000 * 60);

		switch (product.getCodesource()) {
		case 6:
			int crowdRemain = (int) Math.ceil(5 - expireTime);
			crowdRemain = crowdRemain < 0 ? 1 : crowdRemain;
			return crowdRemain + "";
		case 10:
			int carRemain = (int) Math.ceil(10 - expireTime);
			carRemain = carRemain < 0 ? 1 : carRemain;
			return carRemain + "";
		default:
			int defaultRemain = (int) Math.ceil(60 - expireTime);
			defaultRemain = defaultRemain < 0 ? 1 : defaultRemain;
			return defaultRemain + "";
		}
	}

	@RequestMapping("/hbpayview.do")
	public @ResponseBody
	ModelAndView hbpayView(@RequestParam("orderid") String orderid) {
		ModelAndView mv = new ModelAndView();
		OrderQuery query = OrderServiceImpl.genOrderQuery(orderid, -1);
		ZOrder order = orderDao.getZOrderByID(query);
		mv.addObject("img", order.getImg());
		Product product = new Product();
		try {
			product = productService.forceGetProduct(order.getProductid(),order.getApp());
		} catch (MyException e) {
			log.error(e);
			e.printStackTrace();
		}
		if (StringUtils.isNotBlank(product.getShortdesc())) {
			mv.addObject("desc", product.getShortdesc());
		} else {
			mv.addObject("desc", "");
		}
		mv.addObject("title", product.getTitle());
		int count = order.getAmount();
		if (count == 0)
			count = 1;
		mv.addObject("pprice", order.getPprice());
		mv.addObject("price", order.getMprice());
		mv.addObject("count", count);
		if (StringUtils.isNotBlank(order.getSpec()))
			mv.addObject("spec", order.getSpec());
		String period = getRemainMin(order.getCreatetime(), product);
		mv.addObject("period", period);
		mv.setViewName("payview");
		return mv;
	}

	@RequestMapping(value = "/rest/payBefore")
	public @ResponseBody
	Map<String, Object> getBeforePay(@RequestParam("orderid") String orderid,
			@RequestParam("productid") String productid) {
		PayBeforeResp payBeforeResp = new PayBeforeResp();
		int app = Integer.parseInt(productid);
		log.info("payBeforeLog:" + orderid);
		if (app == OrderServiceImpl.GIFT_ORDER_PRODUCTID_TRAIN_MALL) {
			app = 1;
		} else {
			app = 2;
		}
		if(AdapterTools.getDirectory("server.name").equals("test")){
			app=-1;
		}
		payBeforeResp = userPayService.getBeforePayMsg(orderid, app);
		return Tools.beanToMap(payBeforeResp);
	}

	@RequestMapping(value = "/rest/checkCoupon")
	public @ResponseBody
	Map<String, Object> checkCoupon(@RequestParam("orderid") String orderid,
			@RequestParam("rule") String rule, @RequestParam("cid") String cid,
			@RequestParam("outorderid") String outorderid) {
		CouponCheck couponCheck = new CouponCheck();
		try {
			log.info("checkCoupon:" + orderid + "券cid:" + cid + ",rule:"
					+ URLDecoder.decode(rule, "UTF-8"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		couponCheck = couponService.checkOrderCoupon(orderid, outorderid, cid,
				rule);
		return Tools.beanToMap(couponCheck);
	}

	@RequestMapping(value = "/rest/checkToken")
	public @ResponseBody
	Map<String, Object> checkWeiXinToken(
			@RequestParam("orderid") String orderid,
			@RequestParam("token") String token) {
		WeiXinTokenCheckResp resp = new WeiXinTokenCheckResp();
		log.info("checkWeiXinToken，payorderid:" + orderid + "token:" + token);
		resp = weiXinUserService.checkWeiXinToken(orderid, token);
		return Tools.beanToMap(resp);
	}

	@RequestMapping(value = "/rest/wpayview")
	public @ResponseBody
	Map<String, Object> weixinPayview(@RequestParam("orderid") String orderid) {
		WeiXinPayviewResp resp = new WeiXinPayviewResp();
		log.info("wpayview，orderid:" + orderid);
		resp = weiXinUserService.wpayview(orderid);
		return Tools.beanToMap(resp);
	}

	@RequestMapping("/rest/pushMsg")
	@ResponseBody
	public String pushMsg(@RequestParam("userid") String userid,
			HttpServletRequest request, @RequestParam("app") int app) {
		String desc = request.getParameter("desc");
		String clickDesc = request.getParameter("clickDesc") == null ? ""
				: request.getParameter("clickDesc");
		String clickLink = request.getParameter("clickLink") == null ? ""
				: request.getParameter("clickLink");
		boolean flag=false;
		if (app == 1)
			flag= gaotieService.pushMsg(userid, desc, clickDesc, clickLink,1);
		else
			flag= hbService.pushMsg(userid, desc, clickDesc, clickLink,1);
		if(flag)
			return "true";
		else
			return "false";
	}

}
