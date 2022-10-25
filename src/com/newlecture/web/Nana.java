package com.newlecture.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/hi")
public class Nana extends HttpServlet {
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setCharacterEncoding("UTF-8"); // UTF-8로 써서 브라우저로 보내갰다
		resp.setContentType("text/html; charset=UTF-8"); // 브라우저야 너는 자의적으로 해석하지말고 반드시 UTF-8로 읽어야해

		PrintWriter out = resp.getWriter();
//		out.println("Hello ~~~ asdf");

		String cnt_ = req.getParameter("cnt");

		int cnt = 10;		//기본값을 10으로 설정해서 오타쳐도 10뜸
		if (cnt_ != null && !cnt_.equals(""))
			cnt = Integer.parseInt(cnt_);

		for (int i = 0; i < cnt; i++)
			out.println((i + 1) + ":안녕 Servlet!!<br >");

	}
}
