package iwb.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import iwb.engine.FrameworkEngine;
import iwb.cache.FrameworkCache;
import iwb.util.GenericUtil;
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

		String code = request.getParameter("invitation_code");
		if(!GenericUtil.isEmpty(code)) {
			String projectUuid = code.substring(0,36);
			if(FrameworkCache.wProjects.containsKey(projectUuid)) {
				String email= request.getParameter("email");
				Map scd = engine.userExists(email);
				if(scd == null){
					response.sendRedirect("/index.html");
				} else {
					int userId = GenericUtil.uInt(scd.get("user_id"));
					engine.addToProject(userId, projectUuid);
				}
			}
		}
	    
	   
	    //boolean b = engine.changeActiveProject(scd, uuid);
		//response.getWriter().write("{\"success\"");
		//response.getWriter().close();		
	}

}
