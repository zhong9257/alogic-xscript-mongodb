import java.util.HashMap;
import java.util.Map;

import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.Script;
import com.anysoft.util.CommandLine;
import com.anysoft.util.Properties;
import com.anysoft.util.Settings;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.JsonProviderFactory;

public class DemoFind {
	public static void run(String src,Properties p){
		Script script = Script.create(src, p);
		if (script == null){
			System.out.println("Fail to compile the script");
			return;
		}
		long start = System.currentTimeMillis();
		Map<String,Object> root = new HashMap<String,Object>();
		LogicletContext ctx = new LogicletContext(p);
		script.execute(root, root, ctx, new ExecuteWatcher.Quiet());
		
		System.out.println("Script:" + src);
		System.out.println("Duration:" + (System.currentTimeMillis() - start) + "ms");
		
		JsonProvider provider = JsonProviderFactory.createProvider();
		System.out.println("#########################################################");
		System.out.println(provider.toJson(root));				
		System.out.println("#########################################################");
	}
	
	public static void main(String[] args) {
		Settings settings = Settings.get();		
		settings.addSettings(new CommandLine(args));		
		settings.addSettings("java:///conf/settings.xml#DemoFind", null, Settings.getResourceFactory());

		//run("java:///xscript/MongoDelete.xml#Demo",settings);
		//run("java:///xscript/MgQuery.xml#DemoFind",settings);
		//run("java:///xscript/MgTextSearch.xml#DemoFind",settings);
		//run("java:///xscript/MgInsert.xml#DemoFind",settings);
		//run("java:///xscript/MgCount.xml#DemoFind",settings);
		//run("java:///xscript/Mg2dsphereQuery.xml#DemoFind",settings);
		//run("java:///xscript/MgTableNew.xml#DemoFind",settings);
		//run("java:///xscript/MgAggregate.xml#DemoFind",settings);
		//run("java:///xscript/MgQueryOneAndDelete.xml#DemoFind",settings);
		//run("java:///xscript/MgReplaceOne.xml#DemoFind",settings);
		//run("java:///xscript/MgQueryOneAndReplace.xml#DemoFind",settings);
		//run("java:///xscript/MgQueryOneAndUpdate.xml#DemoFind",settings);
		run("java:///xscript/MgDistinct.xml#DemoFind",settings);
	}

}
