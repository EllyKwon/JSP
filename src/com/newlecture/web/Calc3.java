package com.newlecture.web;

import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/calc3")
public class Calc3 extends HttpServlet {

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ServletContext application = request.getServletContext();
		HttpSession session = request.getSession();
		Cookie[] cookies = request.getCookies();		//사용자가 전달한걸 쿠키에저장해서 읽어옴
		
		String value = request.getParameter("value");	// 셋중에 하나 누르면 나머지 2개는 null값됨
		String operator = request.getParameter("operator");
		String dot = request.getParameter("dot");

		String exp = "";
		if(cookies != null)
			for(Cookie c : cookies) 
				if(c.getName().equals("exp")) {
					exp = c.getValue();
					break;
				}
		
		if(operator != null && operator.equals("=")) {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn"); //자바스크립트코드씀
			try {
				exp = String.valueOf(engine.eval(exp));
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(operator != null && operator.equals("C")) {
				exp="";		// 쿠키제거
			
		}else {
			
			exp += (value==null)?"":value;
			exp += (operator==null)?"":operator;
			exp += (dot==null)?"":dot;
			
		}
		
		
		Cookie expCookie = new Cookie("exp", exp); 	// 누적된 상태에서 쿠키남김
		if(operator != null && operator.equals("C"))  expCookie.setMaxAge(0);	// expCookie.setMaxAge(0); 이걸설정해야 쿠키가 브라우저로 가서 바로소멸됨
		expCookie.setPath("/");	
		response.addCookie(expCookie);
		response.sendRedirect("calcpage");	// 여기로 리다이렉트함
		
	}
}
