package iwb.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import iwb.engine.FrameworkEngine;
import iwb.util.UserUtil;

@Controller
@RequestMapping("/invitation")
public class InvitationServlet {
	
	@Autowired
	private FrameworkEngine engine;
	
	@RequestMapping("/accept")
	public void hndAccept(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		//logger.info("hndAjaxChangeActiveProject"); 
	    Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
	    String uuid= request.getParameter("_uuid");
	    Object ob = scd;
	    //boolean b = engine.changeActiveProject(scd, uuid);
		//response.getWriter().write("{\"success\"");
		//response.getWriter().close();		
	}

}
