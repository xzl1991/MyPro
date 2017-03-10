package com.huoli.bmall.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLHelper
{
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> Dom2Map(Document doc)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		if (doc == null)
			return map;
		Element root = doc.getRootElement();
		for (Iterator iterator = root.elementIterator(); iterator.hasNext();)
		{
			Element e = (Element) iterator.next();
			List list = e.elements();
			if (list.size() > 0)
			{
				map.put(e.getName(), Dom2Map(e));
			} else
				map.put(e.getName(), e.getText());
		}
		return map;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map Dom2Map(Element e)
	{
		Map map = new HashMap();
		List list = e.elements();
		if (list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				Element iter = (Element) list.get(i);
				List mapList = new ArrayList();

				if (iter.elements().size() > 0)
				{
					Map m = Dom2Map(iter);
					if (map.get(iter.getName()) != null)
					{
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName()
								.equals("java.util.ArrayList"))
						{
							mapList = new ArrayList();
							mapList.add(obj);
							mapList.add(m);
						}
						if (obj.getClass().getName()
								.equals("java.util.ArrayList"))
						{
							mapList = (List) obj;
							mapList.add(m);
						}
						map.put(iter.getName(), mapList);
					} else
						map.put(iter.getName(), m);
				} else
				{
					if (map.get(iter.getName()) != null)
					{
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName()
								.equals("java.util.ArrayList"))
						{
							mapList = new ArrayList();
							mapList.add(obj);
							mapList.add(iter.getText());
						}
						if (obj.getClass().getName()
								.equals("java.util.ArrayList"))
						{
							mapList = (List) obj;
							mapList.add(iter.getText());
						}
						map.put(iter.getName(), mapList);
					} else
						map.put(iter.getName(), iter.getText());
				}
			}
		} else
			map.put(e.getName(), e.getText());
		return map;
	}

	public static void main(String[] args) throws IOException,
			DocumentException
	{

		Document doc = DocumentHelper
				.parseText("<XML><A>123</A><B>whl123</B><C>亮亮</C><D>1</D><E>1</E><F>165074</F><G>贫穷</G><H>1698.0</H><I>初级士官</I><J>湖南</J><K>常德</K><L>1</L></XML>");

		System.out.println(doc.asXML());
		long beginTime = System.currentTimeMillis();

		Map<String, Object> map = XMLHelper.Dom2Map(doc);
		System.out.println(map.toString());

		System.out.println("Use time:"
				+ (System.currentTimeMillis() - beginTime));
	}
}
