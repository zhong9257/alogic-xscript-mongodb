package com.alogic.xscript.mongodb.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bson.conversions.Bson;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alogic.xscript.LogicletContext;
import com.anysoft.util.Configurable;
import com.anysoft.util.Factory;
import com.anysoft.util.Properties;
import com.anysoft.util.XMLConfigurable;
import com.anysoft.util.XmlElementProperties;
import com.anysoft.util.XmlTools;
/**
 * Filter Builder
 * 
 * @author zhongyi
 *
 */
public interface FilterBuilder extends XMLConfigurable,Configurable{
	
	
	/**
	 * 获取Filter实例
	 * 
	 * @return Filter实例
	 */
	public Bson getFilter(Properties p,LogicletContext ctx);
	
	
	
	/**
	 * 虚基类
	 * 
	 * @author zhongyi
	 *
	 */
	public abstract static class Abstract implements FilterBuilder{
		/**
		 * a logger of log4j
		 */
		protected static final Logger LOG = LogManager.getLogger(FilterBuilder.class);
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
            configure(props);
		}
	}
	
	/**
	 * Filter列表实现
	 * 
	 * @author zhongyi
	 *
	 */
	public abstract static class Multi extends Abstract{
		/**
		 * 子FilterBuidler
		 */
		protected List<FilterBuilder> children = new ArrayList<FilterBuilder>();
		
		protected abstract Bson getFilter(Bson[] bsons);
		
		@Override
		public Bson getFilter(Properties p,LogicletContext ctx) {
			
			Bson[] bsons = new Bson[children.size()];
			int idx=0;
			for (FilterBuilder fb:children){			
				Bson filter = fb.getFilter(p,ctx);
				bsons[idx++]=filter;	
			}
			
			
			return getFilter(bsons);
		}

		@Override
		public void configure(Properties p) {

		}
		
		@Override
		public void configure(Element e, Properties p) {
			Properties props = new XmlElementProperties(e,p);
			
			NodeList nodeList = XmlTools.getNodeListByPath(e, "filter");
			
			TheFactory factory = new TheFactory();
			
			for (int i = 0 ;i < nodeList.getLength() ; i ++){
				Node n = nodeList.item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE){
					continue;
				}
				
				Element elem = (Element)n;
				
				try {
					FilterBuilder fb = factory.newInstance(elem, p, "module");
					if (fb != null){
						children.add(fb);
					}
				}catch (Exception ex){
					LOG.error("Can not create instance of FilterBuilder,xml:" + XmlTools.node2String(elem));
				}
			}
			
			configure(props);
		}		
	}
	
	/**
	 * 工厂类
	 * 
	 * @author zhongyi
	 *
	 */
	public static class TheFactory extends Factory<FilterBuilder>{
		@Override
        public String getClassName(String module){
			if (module.indexOf(".") < 0){
				return "com.alogic.xscript.mongodb.util.filter." + module;
			}
			return module;
		}		
	}
}