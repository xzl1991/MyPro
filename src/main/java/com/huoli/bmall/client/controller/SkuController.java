package com.huoli.bmall.client.controller;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huoli.bmall.exception.MyException;
import com.huoli.bmall.model.JsonrpcResponse;
import com.huoli.bmall.points.model.UserInfo;
import com.huoli.bmall.points.model.sku.Sku;
import com.huoli.bmall.points.model.sku.SkuMap;
import com.huoli.bmall.points.service.SkuService;
import com.huoli.bmall.util.HttpTookit;

@Controller
/**
 * @category sk同步 erp 数据
 * 
 * */
public class SkuController
{
	@Resource
	SkuService skuService;
	private static Logger log = Logger.getLogger("skuSync.log");
	/**
	 * @author xzl
	 * @category 主动同步 erp 数据方法
	 * @param    物料主键  FMATERIALID
 	   @param    仓库主键  FSTOCKID
	 * 
	 * */
	@RequestMapping(value = "/rest/skuSync", method = RequestMethod.POST)
	public @ResponseBody //主动同步 
	Map<String, Object> skuRequest(@RequestParam("skuid") int skuid, HttpServletRequest request)
	{
		String para =  null;
		try
		{
			long start = System.currentTimeMillis();
//			List<Sku> skuList =  skuService.findSku();
			Sku sku = new Sku();
			sku.setErpId(para);
			sku.setStoreCode(para);
			sku.setSecondAttribute(para);
			
			sku = skuService.findSkuByErp(sku);//获取指定的sku
			/*
			 * 接口参数： sku.getFMATERIALID
			 * 		   sku.FMATERIALID
			 * */
			sku.getErpId();
			
			log.info("访问erp接口...业务处理---InstantInventoryInterfac（string ctx,string params）");
			Map<String, String> paramMap =  null;
			HttpTookit.sendPostRequest(null, paramMap);//获取接口数据
			para = URLDecoder.decode(para, "utf-8");
			log.info(para+".....流程测试...");
			
			log.info("通过获取到的");
			JSONObject req = JSON.parseObject(URLDecoder.decode(para, "utf-8"));
			String method = req.getString("method");
			JSONObject param= null;
			try
			{
				param = req.getJSONObject("params");
			}catch(Exception e){
				
			}
			String ip=request.getHeader("x-forwarded-for");
			return processSku(method, param, ip,sku,false);
		}catch(MyException e)
		{
			JsonrpcResponse resp = new JsonrpcResponse();
			resp.setError(e);
			return resp.GenMapResp(null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error("更新失败:" + para);
			JsonrpcResponse resp = new JsonrpcResponse();
			resp.setError("更新失败:"+para,-3);
			return resp.GenMapResp(null);
		}
	}
	/**
	 * @author xzl
	 * @category 对ERP暴漏的 接口 ， 接收rep的请求，更新数据， 可用量增减
	 * 
	 * */
	@RequestMapping(value = "/rest/postInventoryInterfac", method = RequestMethod.POST)
	public @ResponseBody //接受ERP调用，更新ERP停供的数据
	Map<String, Object> skuErpRequest(@RequestBody  String para, HttpServletRequest request)
	{ 
		long statTime = System.currentTimeMillis();
		try
		{
			if (para.charAt(para.length() - 1) == '=')
			{
				para = para.substring(0, para.length() - 1);
			}
			para = URLDecoder.decode(para, "utf-8");
			log.info(para+"....erp调用接口...");
			JSONObject req = JSON.parseObject(URLDecoder.decode(para, "utf-8"));
			String method = req.getString("method");
			JSONObject param= null;
			try
			{
				param = req.getJSONObject("params");
			}catch(Exception e){
				
			}
			
//			List<Sku> skuList =  skuService.findSku();
			log.info("获取批量更新的数据:erpid，增减量等数据");
			int skuid = 0;
			int quantity = param.getIntValue("quantity");//库存减10个
			Sku sku = new Sku();
			sku.setErpId(para);
			sku.setSecondAttribute(method);
			sku.setStoreCode(method);
			sku = skuService.findSkuByErp(sku);//获取要更新的 sku
			String ip=request.getHeader("x-forwarded-for");
			return processSku(method, param, ip,sku,true);
			
//			List<Sku> sku = skuService.findSku();//获取指定的sku
//			String ip=request.getHeader("x-forwarded-for");
//			StringBuffer sb = new StringBuffer(20);
//			JsonrpcResponse resp = new JsonrpcResponse();
//			resp.setResult(sku);
//			return resp.GenMapResp(null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error("更新失败:" + para);
			JsonrpcResponse resp = new JsonrpcResponse();
			resp.setError("sku更新失败:"+para+(new Date()),-3);
			return resp.GenMapResp(null);
		}finally{
			log.info("接口总时间:"+(System.currentTimeMillis()-statTime));
		}
	}
	/**
	 * @author xzl
	 * @param method
	 * @param param  ERP数据
	 * @param List<Sku>  数据库里的 sku 数据
	 * @param boolean isErp false 表示主动更新，true:ERP调用更新，此时需要
	 * @return
	 * @throws MyException
	 */
	private Map<String, Object> processSku(String method,JSONObject param,String ip, Sku sku,boolean isErp) throws MyException
	{
		try
		{
			int fqty = param.getIntValue("fqty");  //获取的可用库存数量
			int quantity = sku.getQuantity();
			int usedQuantity = sku.getUsequantity();
			if(isErp){//erp调用接口增减库存
				if(fqty>=0){
					// 重新 设置可用数量
					sku.setQuantity(quantity+fqty);
					skuService.updateSku(sku);
				}else{//减库存
					int needQuanty = sku.getQuantity()+fqty;//减库存后的数量
					if((needQuanty-sku.getUsequantity())>=0){
						sku.setQuantity(needQuanty);
						skuService.updateSku(sku);//将可用量调整
					}else{///解锁lock
						int freeLock = sku.getLocks() - sku.getUselock()-needQuanty;//可以释放的lock量
						processSkuMap(method, param, ip, freeLock, sku);//释放skumap lock
						// 重新 设置可用数量
						sku.setQuantity(quantity+fqty+usedQuantity);//原 quantity - 减库存= 可用      可用+usedquantity= 新quantity
						skuService.updateSku(sku);
					}
					
				}
			}else{//同步可用库存
				int freeQuantity = quantity-usedQuantity+sku.getLocks()-sku.getUselock();//可用的库存
				int skuId = sku.getSkuid();
				if(freeQuantity<fqty){//可用库存 < fqty
					String detail=skuId+",库存不足!"+method;
					log.error(detail);
					// 这里是要通知 ?
					throw new MyException(detail,-3);
				}
				int needQuanty = fqty-quantity+usedQuantity ;
				if(needQuanty<=0){//减库存后的数量
					sku.setQuantity(needQuanty);
					skuService.updateSku(sku);//将可用量调整
				}else{///解锁lock
					processSkuMap(method, param, ip, needQuanty, sku);//释放skumap lock
					//调整 sku quantity = fqty + usedquantity
				}
			}
		}
	    catch (Exception e){
	    	String detail="更新失败："+method;
	    	log.info("失败:"+detail);
			throw new MyException(detail,-3);
	    }
		JsonrpcResponse resp = new JsonrpcResponse();
		log.info("更新成功:"+method);
		return resp.GenMapResp(null);
	}
	/**
	 * @author xzl
	 * @category 同步erp 可用库存量
	 * @param method
	 * @param param  ERP数据
	 * @param List<Sku>  数据库里的 sku 数据
	 * @return
	 * @throws MyException
	 */
	private void processSkuMap(String method,JSONObject param,String ip,int freeLock, Sku sku){
		String from ="defaultFrom";
		try
		{	
			int skuId = sku.getSkuid();
			if(freeLock<0){//可用lock < 0
				String detail=skuId+",库存不足!"+method;
				log.error(detail);
				throw new MyException(detail,-3);
			}
			int fqty = param.getIntValue("fqty");  //获取的可用库存数量
			//变更 skumap lock 
			List<SkuMap> skuMapList = sku.getSkuMapList();//skuService.findSkuMap(skuId);
			int size = skuMapList.size();
			List<SkuMap> skuMapUpList = new ArrayList<>();
			int skuFreeLock = 0;
			SkuMap skuMap = null;
			for(int i=0;i<size;i++){
				skuMap = skuMapList.get(i);
				skuFreeLock = skuMap.getLock() - skuMap.getUselock();//smumap里面可用lock 数量
				if(skuFreeLock>0){
					fqty = fqty - skuFreeLock;//剩余的需要解锁的数量
					if(fqty<=0){
						skuMap.setLock(skuFreeLock-fqty);//剩余的freelock
						skuMapUpList.add(skuMap);
						break;
					}
					skuMap.setLock(skuMap.getLock());//lock 和 uselock 相等
					// 保存做批量更新
					skuMapUpList.add(skuMap);
				}
			}
			//批量更新
			log.info("...即将批量更新...");
			skuService.updateSkuMapBatch(skuMapUpList);
		}
	    catch (Exception e){
	    	String detail="更新失败："+method+","+e.getMessage();
	    	log.error(detail);
	    	e.printStackTrace();
	    }
//		JsonrpcResponse resp = new JsonrpcResponse();
//		log.info("更新成功:"+method);
//		return resp.GenMapResp(null);
	}
}
