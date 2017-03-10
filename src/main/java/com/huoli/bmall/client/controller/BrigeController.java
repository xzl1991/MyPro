package com.huoli.bmall.client.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huoli.bmall.adapter.model.BrigeRespInfo;
import com.huoli.bmall.adapter.service.IMallService;
import com.huoli.bmall.adapter.tools.AdapterTools;
import com.huoli.bmall.util.Tools;

@Controller
public class BrigeController {
	private static Logger log = Logger.getLogger(BrigeController.class);

	@Resource
	private IMallService mallService;


	@RequestMapping(value = "/rest/checkUserInfo")
	public @ResponseBody
	Map<String, Object> checkUserInfo(@RequestParam("phoneid") String phoneid,HttpServletRequest request )
	{
		BrigeRespInfo checkInfo = new BrigeRespInfo();
		log.info("checkMallUserInfo begin:"+phoneid);
		checkInfo = mallService.checkMallUser(phoneid);
		return Tools.beanToMap(checkInfo);
	}
	
	@RequestMapping(value = "/rest/mergeUserInfo")
	public @ResponseBody
	Map<String, Object> mergeUserInfo(@RequestParam("primaryphoneid") String primaryphoneid,
			@RequestParam("deputyphoneid") String deputyphoneid,HttpServletRequest request)
	{
		BrigeRespInfo checkInfo = null;
		String from = request.getHeader("x-forwarded-for");
		try {
			if(AdapterTools.getDirectory("server.name").equals("test"))
				checkInfo = mallService.mergeUserInfo(primaryphoneid, deputyphoneid);
			else if(AdapterTools.getDirectory("server.name").equals("mall")){
				if (!(from.equals("43.241.208.236") || from.equals("111.200.243.114") || from.equals("182.92.159.18")
						|| from.equals("58.83.130.69") || from.equals("58.83.130.79")|| from.equals("58.83.130.115")))
				{
					log.error("error refresh ip:" + from);
					checkInfo=new BrigeRespInfo();
					checkInfo.setSuccess("false");
					checkInfo.setCode(0);
					checkInfo.setMsg("ip错误，请联系商城开发人员！");
				}else{
					log.info("checkMallUserInfo begin:"+primaryphoneid+","+deputyphoneid);
					checkInfo = mallService.mergeUserInfo(primaryphoneid, deputyphoneid);
				}
					
			}
				
		} catch (Exception e) {
			checkInfo=new BrigeRespInfo();
			log.error("调用mergeUserInfo接口报错,错误的primaryphoneid为：" +primaryphoneid+","+deputyphoneid);
			checkInfo.setSuccess("false");
			checkInfo.setCode(0);
			checkInfo.setMsg("合并错误，请联系我们线下合并！");
		}
		
		return Tools.beanToMap(checkInfo);
	}
	
	@RequestMapping(value = "/rest/queryMergedInfo")
	public @ResponseBody
	Map<String, Object> queryMergedInfo(@RequestParam("phoneid1") String phoneid1,@RequestParam("phoneid2") String phoneid2 )
	{
		BrigeRespInfo queryResp = new BrigeRespInfo();
		log.info("queryMergedInfo begin:"+phoneid1+","+phoneid2);
		queryResp = mallService.queryMergedInfo(phoneid1, phoneid2);
		return Tools.beanToMap(queryResp);
	}
	
	@RequestMapping(value = "/rest/queryWarningProds")
	public @ResponseBody
	Map<String, Object> queryWarningProds(@RequestParam("app") int app)
	{
		BrigeRespInfo queryResp = new BrigeRespInfo();
		queryResp=mallService.queryWarningProds(app);
		return Tools.beanToMap(queryResp);
	}
	
	@RequestMapping(value = "/rest/queryMallOrder")
	public @ResponseBody
	Map<String, Object> queryMallOrder(@RequestParam("phoneid") String phoneid,@RequestParam("type") int type,HttpServletRequest request )
	{
		BrigeRespInfo checkInfo = new BrigeRespInfo();
		log.info("queryMallOrder begin:"+phoneid);
		String from = request.getHeader("x-forwarded-for");
		if(AdapterTools.getDirectory("server.name").equals("test"))
			checkInfo = mallService.queryMallOrder(phoneid,type);
		else if(AdapterTools.getDirectory("server.name").equals("mall")){
			if (!(from.equals("43.241.208.236")))
			{
				log.error("error refresh ip:" + from);
				checkInfo=new BrigeRespInfo();
				checkInfo.setSuccess("false");
				checkInfo.setCode(0);
				checkInfo.setMsg("ip错误，请联系商城开发人员！");
			}else{
				checkInfo = mallService.queryMallOrder(phoneid,type);
			}
				
		}
		return Tools.beanToMap(checkInfo);
	}
	
	@RequestMapping(value = "/rest/deleteTestOrder")
	public @ResponseBody
	Map<String, Object> queryWarningProds(@RequestParam("userid") String userid,@RequestParam("productid") int productid)
	{
		BrigeRespInfo queryResp = new BrigeRespInfo();
		if(AdapterTools.getDirectory("server.name").equals("test"))
		    queryResp=mallService.deleteTestOrder(userid, productid);
		return Tools.beanToMap(queryResp);
	}
	
	
}
