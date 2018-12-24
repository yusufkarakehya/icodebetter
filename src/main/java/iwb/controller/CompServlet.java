package iwb.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import iwb.cache.FrameworkCache;
import iwb.engine.FrameworkEngine;
import iwb.util.GenericUtil;
import iwb.util.UserUtil;

@Controller
@RequestMapping("/comp")
public class CompServlet {
	private static Logger logger = Logger.getLogger(CompServlet.class);

	@Autowired
	private FrameworkEngine engine;
	
	@RequestMapping("/*")
	public void hndJavascripts(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException{
		logger.info("hndJasperReport"); 
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	String uri = request.getRequestURI();
    	if(uri.endsWith(".css")){
    		uri = uri.substring(uri.lastIndexOf('/')+1);
    		uri = uri.substring(0, uri.length()-4);
        	String css = FrameworkCache.getComponentCss(scd, GenericUtil.uInt(uri));
    		response.setContentType("text/css; charset=UTF-8");
        	if(css!=null){
        		response.getWriter().write(css);
        	} else {
        		
        	}
    	} else if(uri.endsWith(".js")){
    		uri = uri.substring(uri.lastIndexOf('/')+1);
    		uri = uri.substring(0, uri.length()-3);
        	String js = FrameworkCache.getComponentJs(scd, GenericUtil.uInt(uri));
    		response.setContentType("text/javascript; charset=UTF-8");
        	if(js!=null){
        		response.getWriter().write(js);
        	} else {
        		
        	}
    	}

		response.getWriter().close();
	}	
}
