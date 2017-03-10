package com.huoli.bmall.client.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSONObject;
import com.huoli.bmall.adapter.service.IRedisService;
import com.huoli.bmall.adapter.service.IUrlMapService;
import com.huoli.bmall.adapter.service.IVirtualClickService;
import com.huoli.bmall.adapter.service.impl.RedisServiceImpl;
import com.huoli.bmall.exception.MyException;
import com.huoli.bmall.model.jsonresp.CodeMsgResp;
import com.huoli.bmall.model.jsonresp.StatRedisResp;
import com.huoli.bmall.model.jsonresp.ThridUserResp;
import com.huoli.bmall.points.service.IAdvertisingService;
import com.huoli.bmall.points.service.ICouponService;
import com.huoli.bmall.points.service.IProductService;
import com.huoli.bmall.points.service.IUserService;
import com.huoli.bmall.points.service.impl.ProductServiceImpl;
import com.huoli.bmall.util.Tools;

@Controller
public class ActiveController {
	private static Logger virtuallog = Logger.getLogger("virtuallog");
	private static Logger log = Logger.getLogger(ActiveController.class);

	@Resource
	private IVirtualClickService vcService;

	@Resource
	private IAdvertisingService advertisingService;
	
	@Resource
	private IProductService productService;
	
	@Resource
	private ICouponService couponService;
	
	@Resource
	private IRedisService redisService;
	
	@Resource
	private IUrlMapService urlMapService;
	
	@Resource
	private IUserService userService;

	@RequestMapping(value = "/rest/rd")
	public String redirect(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String p = "ios,gtgj,3.8,";
			String imei = request.getParameter("imei");
			String url = vcService.doClick(2, p, imei);
			if (url != null) {
				url = "redirect:" + url;
				virtuallog.info(url);
				return url;
			}
			return "false";
		} catch (Exception e) {
			return "false";
		}
	}

	@RequestMapping(value = "/rest/scene")
	public @ResponseBody
	Map<String, Object> getAdv(@RequestParam("data") String data, @RequestParam("pos") int pos,HttpServletRequest request )
	{
		String from ="defaultFrom";
		try	
		{
			log.info("rest/scene参数："+data+","+pos);
			if(StringUtils.isNotBlank(request.getParameter("hlfrom"))){
				from=request.getParameter("hlfrom");
			}else if(StringUtils.isNotBlank(request.getParameter("from"))){
					from=request.getParameter("from");
				}
			String urlde=URLDecoder.decode(data,"UTF-8");
			BASE64Decoder decoder = new BASE64Decoder();
			String decodeStr=new String(decoder.decodeBuffer(urlde),"UTF-8");
			JSONObject jsonObject = JSONObject.parseObject(decodeStr);
			log.info("调用地址链接口:"+jsonObject.toJSONString()+","+pos);
			Map<String,Object> resp =advertisingService.getAdvertisingUrl(pos, jsonObject,from);
			return resp;
				
		} catch (Exception e)
		{
			e.printStackTrace();
			log.error("获取某位置的广告报错:" +pos);
			return null;
		}
		
	}
	
	@RequestMapping(value = "/rest/testStock")
	@ResponseBody
	public String redirect(@RequestParam int productId,@RequestParam int app,HttpServletRequest request ) throws Exception {
		
		JSONObject rtnJson = new JSONObject();
		try {
			int goodspecid=0;
			if(StringUtils.isNotBlank(request.getParameter("goodspecid"))){
				goodspecid=Integer.parseInt(request.getParameter("goodspecid"));
				System.out.println("testStock方法的规格id为："+goodspecid);
			}
			rtnJson =productService.getComparedStock(productId, goodspecid,app);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return rtnJson.toJSONString();
	}
	
	@RequestMapping(value = "/rest/testAll")
	@ResponseBody
	public String testALl(@RequestParam int app ) throws Exception {
		
		JSONObject rtnJson = new JSONObject();
		try {
			rtnJson =productService.getALLProdctRedis( app);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return rtnJson.toJSONString();
	}
	
	@RequestMapping(value = "/rest/getCouponRedis")
	@ResponseBody
	public String getCouponRedis(@RequestParam int ruleId,@RequestParam int app ) throws Exception {
		
		JSONObject rtnJson = new JSONObject();
		try {
			rtnJson =couponService.getComparedStock(ruleId, app);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return rtnJson.toJSONString();
	}
	
	@RequestMapping(value = "/rest/testCouponAll")
	@ResponseBody
	public String tesCouponAll(@RequestParam int app ) throws Exception {
		
		JSONObject rtnJson = new JSONObject();
		try {
			rtnJson =couponService.getALLCouponRedis(app);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return rtnJson.toJSONString();
	}
	
	@RequestMapping(value = "/rest/testCrowdList")
	@ResponseBody
	public String testCrowdList(@RequestParam int app ) throws Exception {
		
		JSONObject rtnJson = new JSONObject();
		try {
			rtnJson =productService.getALLCrowdRedis( app);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return rtnJson.toJSONString();
	}
	
	@RequestMapping("/rest/resetRedis")
	public @ResponseBody
	String reloadProduct(HttpServletRequest request,@RequestParam int productId,@RequestParam int app )
	{
		String from = request.getHeader("x-forwarded-for");
		String auth = request.getParameter("authcode");
		if (!auth.equals("1431316329231"))
			return "ok";
		if (!(from.equals("111.200.243.114")||from.equals("43.241.216.246")))
		{
			log.error("error refresh ip:" + from);
			return "thanks";
		}
		int part = Integer.parseInt(request.getParameter("part"));
		try {
			int goodspecid=0;
			if(StringUtils.isNotBlank(request.getParameter("goodspecid"))){
				goodspecid=Integer.parseInt(request.getParameter("goodspecid"));
				System.out.println("resetRedis方法的规格id为："+goodspecid);
			}
			if (part % 2 == 1)
				productService.reSetStock(productId,goodspecid,app);
			part = part / 2;
			if (part % 2 == 1)
				productService.reSetStock(productId,goodspecid,app);
			
		} catch (MyException e) {
			return "false"+e.toString();
			
		}
		
		return "done";
	}
	
	@RequestMapping("/rest/reSetCoupon")
	public @ResponseBody
	String reSetCoupon(HttpServletRequest request,@RequestParam int ruleId,@RequestParam int app )
	{
		String from = request.getHeader("x-forwarded-for");
		String auth = request.getParameter("authcode");
		if (!auth.equals("1431316329231"))
			return "ok";
		if (!(from.equals("111.200.243.114")||from.equals("43.241.216.246")))
		{
			log.error("error refresh ip:" + from);
			return "thanks";
		}
		int part = Integer.parseInt(request.getParameter("part"));
		try {
			
			if (part % 2 == 1)
				couponService.reSetCoupon(ruleId, app);
			part = part / 2;
			if (part % 2 == 1)
				couponService.reSetCoupon(ruleId, app);
			
		} catch (MyException e) {
			return "false"+e.toString();
			
		}
		
		return "done";
	}
	
	@RequestMapping("/rest/getStatRedis")
	public @ResponseBody
	List<StatRedisResp> redisKeyValue(HttpServletRequest request, @RequestParam String  date,@RequestParam int app,@RequestParam String  method)
	{
		String from = request.getHeader("x-forwarded-for");
		
		List<StatRedisResp> statList=new ArrayList<StatRedisResp>();
		if (!(from.equals("111.200.243.114")||from.equals("43.241.216.246")))
		{
			log.error("error refresh ip:" + from);
			StatRedisResp statResp=new StatRedisResp();
			statResp.setMsg("错误ip");
			statList.add(statResp);
			return statList;
		}
		try {
			statList=redisService.getStatRedis(date, app,method);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statList;
	}
	
	@RequestMapping("/rest/getMallStat")
	public @ResponseBody
	List<StatRedisResp> getMallStat(HttpServletRequest request, @RequestParam String  date,
			@RequestParam int app,@RequestParam String  method,@RequestParam String  productid,@RequestParam String statfrom)
	{
		String ip = request.getHeader("x-forwarded-for");
		
		List<StatRedisResp> statList=new ArrayList<StatRedisResp>();
		if (!(ip.equals("111.200.243.114")||ip.equals("43.241.216.246")||ip.equals("221.235.53.164")
				|| ip.equals("58.83.130.76") || ip.equals("103.37.146.143")))
		{
			log.error("error refresh ip:" + ip);
			StatRedisResp statResp=new StatRedisResp();
			statResp.setMsg("错误ip");
			statList.add(statResp);
			return statList;
		}
		try {
			if(StringUtils.isEmpty(method)) method="*";
			if(StringUtils.isEmpty(productid)) productid="*";
			if(StringUtils.isEmpty(statfrom)) statfrom="*";
			statList=redisService.getMallStat(date, app,method,productid,statfrom);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statList;
	}

	@RequestMapping(value = "/rest/redirectUrl")
	public String redirectUrl(HttpServletRequest request,@RequestParam int urlId,@RequestParam int app) {
		try {
			String url = urlMapService.getUrlById(urlId,app);
			if (url != null) {
				url = "redirect:" + url;
				String stat=""+urlId;
				String from =request.getParameter("from");
				if(StringUtils.isBlank(from)){
					from="defaultFrom";
				}
				redisService.setStatRedis(RedisServiceImpl.urlMapMethod,stat, app,from);
				return url;
			}
			return "false";
		} catch (Exception e) {
			return "false";
		}
	}
	
	@RequestMapping("/rest/refreshCoupon")
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
			log.error("error refresh ip:" + from);
			return "ok";
		}
		couponService.reset();
		return "done";
	}
	
	
	@RequestMapping("/rest/setIosVersion")
	public @ResponseBody
	String setIosVersion(HttpServletRequest request,@RequestParam float version,@RequestParam String method)
	{
		String from = request.getHeader("x-forwarded-for");
		String auth = request.getParameter("authcode");
		if (!auth.equals("1431316329231"))
			return "ok";
		if (!(from.equals("111.200.243.114")||from.equals("43.241.216.246")))
		{
			log.error("error refresh ip:" + from);
			return "thanks";
		}
		int app = Integer.parseInt(request.getParameter("app"));
		try {
			
			if (app == 1){
				if(method.equals("get")){
					return ProductServiceImpl.getIosLimitVersion(app)+"";
				}else{
					ProductServiceImpl.setIosLimitVersion(version, app);
					return "done";
				}
			}else{
				if(method.equals("get")){
					return ProductServiceImpl.getIosLimitVersion(app)+"";
				}else{
					ProductServiceImpl.setIosLimitVersion(version, app);
					return "done";
				}
			}
			
		} catch (Exception e) {
			return "false"+e.toString();
			
		}
	}
	
	@RequestMapping("/rest/refreshStaff")
	public @ResponseBody
	String refreshStaff(HttpServletRequest request)
	{
		String from = request.getHeader("x-forwarded-for");
		String auth = request.getParameter("authcode");
		if (!auth.equals("1431316329231"))
			return "ok";
		if (!(from.equals("111.200.243.114")||from.equals("43.241.216.246")))
		{
			log.error("error refresh ip:" + from);
			return "thanks";
		}
		try {
			userService.initStaff();
			
		} catch (Exception e) {
			return "false";
		}
		
		return "done";
	}
	
	@RequestMapping("/rest/getInfoByHmac")
	public @ResponseBody
	Map<String, Object> getInfoByHmac(@RequestParam("hmac") String hmac)
	{
		ThridUserResp resp = new ThridUserResp();
		log.info("getInfoByHmac的参数hmac为:"+hmac);
		resp = userService.getInfoByHmac(hmac);
		return Tools.beanToMap(resp);
	}
	
	@RequestMapping(value="/rest/giveBonus")
	public @ResponseBody
	Map<String, Object> giveBonus(HttpServletRequest request
			,@RequestParam(value="phone") String phone)
	{
		CodeMsgResp resp = new CodeMsgResp();
		log.info("giveBonus:"+phone);
		resp = userService.giveBonus(phone,request);
		return Tools.beanToMap(resp);
	}
	
	@RequestMapping(value="/rest/genHmac")
	public @ResponseBody
	String genHmac(@RequestParam(value="phone") String phone)
	{
		String hmac=ProductServiceImpl.genHmac(phone);
		return hmac;
	}
}
