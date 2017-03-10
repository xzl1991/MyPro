package com.huoli.bmall.client.controller;

import java.net.URLDecoder;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huoli.bmall.adapter.service.IExpressService;
import com.huoli.bmall.adapter.service.IGaotieService;
import com.huoli.bmall.adapter.service.IRedisService;
import com.huoli.bmall.adapter.service.IThirdPartCodeService;
import com.huoli.bmall.adapter.service.IUrlMapService;
import com.huoli.bmall.adapter.service.IVirtualClickService;
import com.huoli.bmall.adapter.service.IWeiXinUserService;
import com.huoli.bmall.adapter.tools.AdapterTools;
import com.huoli.bmall.exception.MyException;
import com.huoli.bmall.model.JsonrpcResponse;
import com.huoli.bmall.points.model.UserInfo;
import com.huoli.bmall.points.model.ZOrder;
import com.huoli.bmall.points.service.IActiveService;
import com.huoli.bmall.points.service.IAddressService;
import com.huoli.bmall.points.service.IAdvertisingService;
import com.huoli.bmall.points.service.ICouponService;
import com.huoli.bmall.points.service.ICrowdService;
import com.huoli.bmall.points.service.IGiftService;
import com.huoli.bmall.points.service.ILotteryService;
import com.huoli.bmall.points.service.INotifyMsgService;
import com.huoli.bmall.points.service.IOrderService;
import com.huoli.bmall.points.service.IPopUpService;
import com.huoli.bmall.points.service.IProductService;
import com.huoli.bmall.points.service.IProductStockService;
import com.huoli.bmall.points.service.IShowMsgService;
import com.huoli.bmall.points.service.IUserGoodsService;
import com.huoli.bmall.points.service.IUserPayService;
import com.huoli.bmall.points.service.IUserService;
import com.huoli.bmall.util.Tools;

@Controller
public class BMallController
{
	@Resource
	IGaotieService gaotieService;
	
	@Resource
	private IUserService userService;

	@Resource
	private IAdvertisingService advertisingService;

	@Resource
	private IOrderService pointsOrderService;
	
	@Resource
	private IUserPayService userPayService;
	
	@Resource
	private IGiftService giftService;


	@Resource
	private IProductService productService;
	
	@Resource
	private IUserGoodsService userGoodsService;


	@Resource
	private IExpressService expressService;

	@Resource
	private ILotteryService lotteryService;

	@Resource
	private IAddressService addressService;

	@Resource
	private ICouponService couponService;

	@Resource
	private IActiveService activeService;
	@Resource
	private IPopUpService popUpService;

	@Resource
	private IThirdPartCodeService thirdPartCodeService;

	@Resource
	private IVirtualClickService virtualClickService;

	@Resource
	private ICrowdService crowdService;

	@Resource
	private IRedisService redisService;
	
	@Resource
	private IUrlMapService urlMapService;
	@Resource
	private IShowMsgService showMsgService;
	@Resource
	private INotifyMsgService notifyMsgService;
	@Resource
	private IProductStockService stockService;
	
	@Resource
	private IWeiXinUserService weiXinUserService;

	private static Logger log = Logger.getLogger(BMallController.class);

	@RequestMapping("/rest/refresh")
	public @ResponseBody
	String reloadProduct(HttpServletRequest request,
			HttpServletResponse response)
	{
		String from = request.getHeader("x-forwarded-for");
		String auth = request.getParameter("authcode");
		if (!auth.equals("1431316329231"))
			return "ok";
		if (!(from.equals("111.200.243.114")||from.equals("43.241.216.246")))
		{
			if(!AdapterTools.getDirectory("server.name").equals("test")){
				log.error("error refresh ip:" + from);
				return "ok!";
			}
		}
//		int part = Integer.parseInt(request.getParameter("part"));
		productService.loadProducts(true);
	
		thirdPartCodeService.reset();
		addressService.reset();
		advertisingService.reset();
		lotteryService.reset(3);
		virtualClickService.reset();
		crowdService.reset();
	    urlMapService.reset();
	    activeService.reset();
	    showMsgService.reset();
	    notifyMsgService.reset();
	    couponService.reset();
	    popUpService.reset();
	    stockService.reset();
	    userGoodsService.reset();
		return "done";
	}
	@RequestMapping("/rest/synorder")
	public @ResponseBody
	String synOrder(HttpServletRequest request,
			HttpServletResponse response)
	{
		String from = request.getHeader("x-forwarded-for");
		if (!(from.equals("111.200.243.114")||from.equals("43.241.216.246")))
		{
			log.error("error refresh ip:" + from);
			return "ok";
		}
		int app = Integer.parseInt(request.getParameter("app"));
		String orderid = request.getParameter("orderid");
		return pointsOrderService.synOrder(orderid, app);
	}

	@RequestMapping("/rest/refound")
	public @ResponseBody
	String refound(HttpServletRequest request, HttpServletResponse response)
	{
		String from = request.getHeader("x-forwarded-for");
		String auth = request.getParameter("authcode");
		if (!auth.equals("1431316329231"))
			return "ok";
		if (!(from.equals("111.200.243.114")||from.equals("43.241.216.246")||from.equals("221.235.53.164")
				|| from.equals("58.83.130.76") || from.equals("103.37.146.143")))
		{
			log.error("error refresh ip:" + from);
			return "error ip";
		}
		String orderid = request.getParameter("orderid");
		String refoundMoney=request.getParameter("refoundMoney");
		String refoundPoints=request.getParameter("refoundPoints");
		ZOrder order=pointsOrderService.getOrderById(orderid, null, -1);
		order.setRefoundMoney(refoundMoney);
		order.setRefoundPoints(refoundPoints);
		String sid = request.getParameter("sid");
		String ret;
		int ishcar=0;
		String messagecontent ="";
		int refundamounttype=0;
		int refundcoupon=0;
		if(StringUtils.isNotBlank(request.getParameter("messagecontent")))
			messagecontent=request.getParameter("messagecontent");
		if(StringUtils.isNotBlank(request.getParameter("refundamounttype")))
			refundamounttype=Integer.parseInt(request.getParameter("refundamounttype"));
		if(StringUtils.isNotBlank(request.getParameter("refundcoupon")))
			refundcoupon=Integer.parseInt(request.getParameter("refundcoupon"));
		if(StringUtils.isNotBlank(request.getParameter("hcar")))
			ishcar=1;
		ret = userPayService.refound(order, sid,ishcar,messagecontent,refundamounttype,refundcoupon);
		return ret;
	}

	@RequestMapping(value = "/rest/insure", method = RequestMethod.POST)
	public @ResponseBody
	String insure(@RequestBody String para,HttpServletRequest request)
	{
		try
		{
			if (para.charAt(para.length() - 1) == '=')
			{
				para = para.substring(0, para.length() - 1);
			}
			log.info(para);
			para = URLDecoder.decode(para, "utf-8");
			log.info(para);
			JSONObject req = JSON.parseObject(URLDecoder.decode(para, "utf-8"));
			UserInfo user = new UserInfo();
			try
			{
				user.setUserid(req.getString("userid"));
			} catch (Exception e)
			{
				user.setUserid("2");
			}
			user.setAuthcode("11111111111111111111111111111");
			JSONObject param = new JSONObject();
			param.put("input", req);
			String ip=request.getHeader("x-forwarded-for");
			user.setIp(ip);
			pointsOrderService.CreateOrder(10000, user, param,1);
			String from =req.getString("hlfrom");
			if(StringUtils.isBlank(from)){
				from="defaultFrom";
			}else{
				user.setFrom(from);
			}
			redisService.setStatRedis("insure", "" + productService.getInsureId() , 1,from);
			// return "ok";
		} catch (Exception e)
		{
		}
		return "ok";
	}

	@RequestMapping("/rest/reloadProduct")
	public @ResponseBody
	String reloadProduct(@RequestParam("authcode") String ua,
			@RequestParam("uid") String uid,
			@RequestParam("userid") String userid) throws Exception
	{
		if (ua.equals("1431316329231"))
		{
			productService.loadProducts(true);
			addressService.reset();
			couponService.reset();
			thirdPartCodeService.reset();
			advertisingService.reset();
			crowdService.reset();
			log.info("reload products");
		} else
		{
			log.error("reload ok");
		}
		return "ok";
	}
	@RequestMapping("/rest/redistest")
	public @ResponseBody
	String redistest(@RequestParam("authcode") String ua,
			@RequestParam("uid") String uid,
			@RequestParam("userid") String userid) throws Exception
	{
		UserInfo user = new UserInfo();
		user.setAuthcode(ua);
		user.setUid(uid);
		user.setUserid(userid);
		String result = gaotieService.getUserInfoFromRedis(user);
		return result;
	}

	@RequestMapping(value = "/rest/hb", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> hbclientRequest(@RequestBody String para,HttpServletRequest request)
	{
		try
		{
			if (para.charAt(para.length() - 1) == '=')
			{
				para = para.substring(0, para.length() - 1);
			}
			para = URLDecoder.decode(para, "utf-8");
			log.info(para+".....流程测试...");
			JSONObject req = JSON.parseObject(URLDecoder.decode(para, "utf-8"));
			String method = req.getString("method");
			JSONObject param= null;
			try
			{
				param = req.getJSONObject("params");
			}catch(Exception e)
			{}
			String ip=request.getHeader("x-forwarded-for");
			return processRequest(method, param, ip,request,2);
		}catch(MyException e)
		{
			JsonrpcResponse resp = new JsonrpcResponse();
			resp.setError(e);
			return resp.GenMapResp(null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error("非法调用:" + para);
			JsonrpcResponse resp = new JsonrpcResponse();
			resp.setError("hbclientRequest调用错误"+para,-3);
			return resp.GenMapResp(null);
		}
	}

	@RequestMapping(value = "/express", method = RequestMethod.GET, produces = { "application/json;charset=UTF-8" })
	public @ResponseBody
	String express(@RequestParam String companyid, @RequestParam String tracking)
			throws Exception
	{
//		System.out.println("这里的参数："+companyid+",tracking:"+tracking);
		String result = expressService.getExpress(companyid, tracking).toJSONString();  
		if(AdapterTools.getDirectory("server.name").equals("test"))
			System.out.println("express的返回包："+result);
		return result;
	}
	
	private void handleUserFrom(UserInfo user){
		try {
			if(user.isFromWeiXin()){
				String userFrom=user.getFrom();
				String openid=user.getOpenid();
				if(userFrom.equals("defaultFrom")){
					String tempStr="";
					if(StringUtils.isNotBlank(openid)){
						tempStr="weixin";
					}else{
						tempStr="web";
					}
					user.setFrom(tempStr);
				}
				if(StringUtils.isNotBlank(openid)){
					user.setPlatform("weixin");
				}else{
					user.setPlatform("web");
				}
			}else{
				user.setPlatform("app");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param method
	 * @param param
	 * @param ip
	 * @param request
	 * @param app
	 * @return
	 * @throws MyException
	 */
	private Map<String, Object> processRequest(String method,JSONObject param,String ip,HttpServletRequest request, int app) throws MyException
	{
		String stat = "";
		String token=null;
		String from ="defaultFrom";
		try
		{
			
			if (method.equals("checkTokenValid"))
			{
				JsonrpcResponse resp = new JsonrpcResponse();
				String whiteToken=param.getString("token");
				resp=weiXinUserService.checkTokenValid(whiteToken,app);
				return resp.GenMapResp(token);
			}
			if (method.equals("checkWhiteList"))
			{
				JsonrpcResponse resp = new JsonrpcResponse();
				String whiteToken=param.getString("token");
				resp=weiXinUserService.checkWhiteList(whiteToken,app);
				return resp.GenMapResp(token);
			}
			if (method.equals("closeCrowd"))
			{
				JsonrpcResponse resp = new JsonrpcResponse();
				resp.setResult("ok");
				productService.closeCrowd();
				return resp.GenMapResp(token);
			}
			if (method.equals("crowdWinList"))
			{
				JsonrpcResponse resp = crowdService.crowdWinList(app);
				return resp.GenMapResp(token);
			}
			
//			if (method.equals("getBanners"))
//			{
//				JsonrpcResponse resp = productService.getBanners(app);
//				return resp.GenMapResp(token);
//			}
//			if (method.equals("getActive"))
//			{
//				int activeid = param.getIntValue("activeid");
//				JsonrpcResponse resp = activeService.getActive(activeid);
//				return resp.GenMapResp(token);
//			}
			
			UserInfo user = new UserInfo();
			try
			{
				user.setImei(param.getString("imei"));
				user.setIp(ip);
				user.setFrom(from);
				if(param!=null&&StringUtils.isNotBlank(param.getString("hlfrom"))){
					from=param.getString("hlfrom");
					user.setFrom(from);
				}else if(param!=null&&StringUtils.isNotBlank(param.getString("from"))){
						from=param.getString("from");
						user.setFrom(from);
					}
				
			} catch (Exception e)
			{
			}
			try
			{
				user.setP(param.getString("p"));
			} catch (Exception e)
			{
			}
			if (method.equals("mainProductList"))
			{
				JsonrpcResponse resp = productService.getProductList(user, app);
				return resp.GenMapResp(token);
			}
			if (method.equals("getActive"))
			{
				int activeid = param.getIntValue("activeid");
				JsonrpcResponse resp = activeService.getActive(user,activeid,app);
				return resp.GenMapResp(token);
			}
			if (method.equals("getExpressByOrder"))
			{
				String orderid = param.getString("orderid");
				JsonrpcResponse resp = expressService.getExpressByOrderId(orderid);  
				return resp.GenMapResp(token);
			}
			if(method.equals("indexData")){
				String appInfo=Tools.getCookieByHeader("appInfo", request);
				user.setAppInfo(appInfo);
				float version=param.getFloatValue("v");
				JsonrpcResponse resp = productService.indexData(user,version,app);
				return resp.GenMapResp(token);
			}
			if(method.equals("getClassifyList")){
				int hasGoods=param.getIntValue("hasGoods");
				JsonrpcResponse resp = productService.getClassifyList(hasGoods,user,app);
				return resp.GenMapResp(token);
			}
			if (method.equals("classifyGoods"))
			{
				String groupId = param.getString("groupId");
				stat = groupId;
				JsonrpcResponse resp = productService.getGoodsByClassify(Integer.parseInt(groupId), app);
				return resp.GenMapResp(token);
			}

			if (method.equals("tplProduct"))
			{
				int productid = param.getIntValue("productid");
				stat = ""+productid;
				JsonrpcResponse response = productService.getFProd(user,
						productid, app);
				return response.GenMapResp(token);
			}

			if (method.equals("crowdResult"))
			{
				int productid = param.getIntValue("productid");
				stat = ""+productid;
				JsonrpcResponse response = crowdService.getResult(productid);
				return response.GenMapResp(token);
			}

			if (method.equals("getRegion"))
			{
				String id = param.getString("id");
				JsonrpcResponse resp = addressService.getRegion(id);
				return resp.GenMapResp(token);
			}

			if (method.equals("getClassify"))
			{
				String classify = param.getString("classify");
				stat = classify;
				JsonrpcResponse resp = productService.getClassify(classify, app);
				return resp.GenMapResp(token);
			}
			if (method.equals("checkPhone"))
			{
				String phone = param.getString("cphone");
				int productid = param.getIntValue("productid");
				JsonrpcResponse resp = userService.checkGtPhone(user, phone,productid);
				return resp.GenMapResp(token);
			}
			
			//微信登录begin
			if (method.equals("webLogin"))
			{
				String wuid=param.getString("wuid");
				String wechatKey=param.getString("wechatKey");
				String phone = param.getString("phone");
				String code=param.getString("code");
				String cookieToken=param.getString("tempkey");
				if(StringUtils.isBlank(wuid)) wuid="";
				log.info("webLogin调用tempkey:"+cookieToken);
//				String cookieToken=Tools.getCookieValue("token", request);
				JsonrpcResponse resp = weiXinUserService.webLogin( wuid, phone, wechatKey,cookieToken, code, app);
				return resp.GenMapResp(token);
			}
			
			if (method.equals("sendLoginCode"))
			{
				String wuid=param.getString("wuid");
				String phone=param.getString("phone");
				if(StringUtils.isBlank(wuid)) wuid="";
				JsonrpcResponse resp = weiXinUserService.sendLoginCode(wuid, phone, app);
				return resp.GenMapResp(token);
			}
			
			if (method.equals("unbindWeChat"))
			{
				String wuid=param.getString("wuid");
				String wechatKey=param.getString("wechatKey");
				if(StringUtils.isBlank(wuid)) wuid="";
				JsonrpcResponse resp = weiXinUserService.unbindWeChat(wuid, wechatKey, app);
				return resp.GenMapResp(token);
			}
			
			if (method.equals("getTokenByWeChatKey"))
			{
				String wechatKey=param.getString("wechatKey");
				JsonrpcResponse resp = weiXinUserService.getTokenByWeChatKey(wechatKey, app);
				return resp.GenMapResp(token);
			}
			
			//微信登录end
				user.setAuthcode(param.getString("authcode"));
				user.setUid(param.getString("uid"));
				String userId=param.getString("userid");
				userId=Tools.filterAttackStr(userId);
				user.setUserid(userId);
				try
				{
					user.setPhone(param.getString("phone"));
				} catch (Exception e)
				{
					user.setPhone("");
				}
				
				if (app == 1)
				{
					try
					{
						user.setHbauthcode(param.getString("hbauthcode"));
					} catch (Exception e)
					{
					}
					try
					{
						user.setHbuserid(param.getString("hbuserid"));
					} catch (Exception e)
					{
					}
				}else
				{
					user.setHbauthcode(user.getAuthcode());
					user.setHbuserid(user.getUserid());
				}
				
				//处理微信端token
				if(StringUtils.isBlank(user.getAuthcode())){
					String cookieToken=Tools.getCookieValue("token", request);
//					System.out.println("header中cookie："+request.getHeader("Cookie"));
					if(StringUtils.isNotBlank(cookieToken)){
//						token=cookieToken;
						log.info("cookie中token："+cookieToken+","+method);
						user=weiXinUserService.getUserByToken(cookieToken,app);
						user.setIp(ip);
						user.setFrom(from);
					}
					handleUserFrom(user);
				}
				
			if (method.equals("getPopUp")) {
				int position = param.getIntValue("position");
				JsonrpcResponse resp = popUpService.getPopUp(position, user,app);
				return resp.GenMapResp(token);
			}
			
			if (method.equals("showGift")) {
				String orderid=param.getString("orderid");
				JsonrpcResponse resp = giftService.showGift(orderid,user);
				return resp.GenMapResp(token);
			}
			
			if (method.equals("giftDetail")) {
				String orderid=param.getString("orderid");
				JsonrpcResponse resp = giftService.giftDetail(user,orderid);
				return resp.GenMapResp(token);
			}
			
			if (method.equals("createGiftOrder")) {
				String orderid=param.getString("orderid");
				JsonrpcResponse resp = giftService.createGiftOrder(user,orderid, param);
				return resp.GenMapResp(token);
			}
			
			if (method.equals("goodsDetail"))
			{
				int productid = param.getIntValue("productid");
				stat = ""+productid;
				JsonrpcResponse resp = productService.getGoodsDetail(productid, user, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("crowdColumn"))
			{
				JsonrpcResponse resp = crowdService.getCrowdChannel(user,app);
				return resp.GenMapResp(token,user);
			}
			
			if (method.equals("getUrl"))
			{
				int productid = param.getIntValue("productid");
				stat = ""+productid;
				JsonrpcResponse resp = productService.getUrl(productid, user,param, app);
				return resp.GenMapResp(token,user);
			}
			
			if (method.equals("getUserInfo"))
			{
				JsonrpcResponse resp = userService.getUserInfo(user, app);
				return resp.GenMapResp(token,user);
			}
			
			if(StringUtils.isNotBlank(token)){
				switch (user.getState()) {
				case 1:
					throw new MyException("哎呀，管家开小差了.请您再试一次!："+token,-3331);//token不为空但失效
                case 2:
                	throw new MyException("哎呀，管家开小差了.请您再试一次!："+token,-3331);//token不为空但找不到用户
				}
			}
				
			if(StringUtils.isBlank(user.getUserid()))
			{
				JsonrpcResponse resp = new JsonrpcResponse();
				resp.setError("processRequest错误：用户id为空"+method,-3330);
				log.info("userblank:"+method);
				return resp.GenMapResp(token,user);
			}
			
//			userService.checkUserInfo(user, app);//暂时不用
			
			//下面的协议是需要用户信息的
			if (method.equals("productDetail"))
			{
				int productid = param.getIntValue("productid");
				stat = ""+productid;
				JsonrpcResponse resp = productService.getProductDetail(productid, user, app);
				return resp.GenMapResp(token,user);
			}

			if (method.equals("setDefAddr"))
			{
				 JsonrpcResponse resp = addressService.updateAddress(user,param);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("createLottery"))
			{
				int productid = param.getIntValue("productid");
				JsonrpcResponse resp = pointsOrderService.CreateOrder(productid,
						user, param, app);
				return resp.GenMapResp(token,user);
			}

			if (method.equals("crowdDetail"))
			{
				int productid = param.getIntValue("productid");
				stat = ""+productid;
				JsonrpcResponse resp = productService.crowdDetail(productid,
						user, app);
				return resp.GenMapResp(token,user);
			}

//			if (method.equals("checkin"))
//			{
//				JsonrpcResponse resp = userService.checkInGt(user);
//				return resp.GenMapResp(token,user);
//			}
			if (method.equals("createOrder"))
			{
				int productid = param.getIntValue("productid");
				stat = ""+productid;
				JsonrpcResponse resp = pointsOrderService.CreateOrder(productid,user, param, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("getCoupon"))
			{
				JsonrpcResponse resp = couponService.getCoupon(user,param,app);
				return resp.GenMapResp(token,user);
			}
			
			if (method.equals("getUserCouponStat"))
			{
				JsonrpcResponse resp = couponService.getUserCouponStat(user,param,app);
				return resp.GenMapResp(token,user);
			}
			
			if (method.equals("getUserCouponStat"))
			{
				JsonrpcResponse resp = couponService.getUserCouponStat(user,param,app);
				return resp.GenMapResp(token,user);
			}

			if (method.equals("orderNewDetail"))
			{
				String orderid = param.getString("orderid");
				JsonrpcResponse resp = pointsOrderService.OrderNewDetail(user,orderid, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("cancelOrder"))
			{
				String orderid = param.getString("orderid");
				JsonrpcResponse resp = pointsOrderService.cancelOrder(user,orderid, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("removeOrder"))
			{
				String orderid = param.getString("orderid");
				JsonrpcResponse resp = pointsOrderService.removeOrder(user, orderid, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("cancelCollect"))
			{
				int productid = param.getIntValue("productid");
				stat = ""+productid;
				JsonrpcResponse resp = userGoodsService.cancelCollect(productid,user, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("removeCollect"))
			{
				String productidStr = param.getString("productid");
				stat = productidStr;
				JsonrpcResponse resp = userGoodsService.removeCollect(productidStr, user,param, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("collectList"))
			{
				JsonrpcResponse resp = userGoodsService.collectList(user, param, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("collectGoods"))
			{
				int productid = param.getIntValue("productid");
				stat = ""+productid;
				JsonrpcResponse resp = userGoodsService.collectGoods(productid,user, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("refoundApply"))
			{
				String orderid = param.getString("orderid");
				JsonrpcResponse resp = pointsOrderService.applyRefound(user,orderid,param, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("refoundDetail"))
			{
				String orderid = param.getString("orderid");
				JsonrpcResponse resp = pointsOrderService.refoundDetail(user,orderid, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("orderList"))
			{
				JsonrpcResponse resp = pointsOrderService.OrderList(user, param, app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("couponList"))
			{
				JsonrpcResponse resp = couponService.getUserCoupnList(user,app);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("getUserMallInfo"))
			{
				JsonrpcResponse resp = userService.getUserMallInfo(user,app);
				return resp.GenMapResp(token,user);
			}

			if (method.equals("userInvolvedCrowd"))
			{
				JsonrpcResponse resp = crowdService.getUserCrowdList(user,param,app);
				return resp.GenMapResp(token,user);
			}
			
			if (method.equals("getAddress"))
			{
				JsonrpcResponse resp = addressService.getAddresses(user);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("updateAddress"))
			{
				JsonrpcResponse resp = addressService
						.updateAddress(user, param);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("newAddress"))
			{
				JsonrpcResponse resp = addressService.addAddress(user, param);
				return resp.GenMapResp(token,user);
			}
			if (method.equals("delAddress"))
			{
				int addressid = param.getIntValue("addressid");
				JsonrpcResponse resp = addressService.delAddress(user,
						addressid);
				return resp.GenMapResp(token,user);
			}

			if (method.equals("addOrderAddr"))
			{
				String orderid = param.getString("orderid");
				JsonrpcResponse resp = pointsOrderService.addOrderAddr(user, orderid,param, app);
				return resp.GenMapResp(token,user);
			}
			
		}
	    catch (MyException e){
	    	throw e;
	    }
		catch (Exception e)
		{
			e.printStackTrace();
			log.error(e);
		}finally
		{
			redisService.setStatRedis(method, stat,app,from);
		}
//		log.error("非法调用:" + para);
//		JsonrpcResponse resp = new JsonrpcResponse();
//		resp.setError(-3);
//		return resp.GenMapResp("gtgj");
		String detail="processRequest找不以相关协议了："+method;
		throw new MyException(detail,-3);
	}

	@RequestMapping(value = "/rest", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> clientsRequest(@RequestBody String para,HttpServletRequest request)
	{
		try
		{
			if (para.charAt(para.length() - 1) == '=')
			{
				para = para.substring(0, para.length() - 1);
			}
			para = URLDecoder.decode(para, "utf-8");
//			log.info(para);
			log.info(para+"....非航班.流程测试...");
			JSONObject req = JSON.parseObject(URLDecoder.decode(para, "utf-8"));
			String method = req.getString("method");
			JSONObject param= null;
			try
			{
				param = req.getJSONObject("params");
			}catch(Exception e)
			{}
			
			String ip=request.getHeader("x-forwarded-for");
			return processRequest(method, param,ip, request,1);
		}catch(MyException e)
		{
			JsonrpcResponse resp = new JsonrpcResponse();
			resp.setError(e);
			return resp.GenMapResp(null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error("非法调用:" + para);
			JsonrpcResponse resp = new JsonrpcResponse();
			resp.setError("clientsRequest调用错误"+para,-3);
			return resp.GenMapResp(null);
		}
	}
	
}
