package com.newlecture.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/calculator")
public class Calculator extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Cookie[] cookies = request.getCookies();

		String exp = "0"; // 연산식 그대로 읽어서 출력하는방식
		if (cookies != null)
			for (Cookie c : cookies)
				if (c.getName().equals("exp")) {
					exp = c.getValue();
					break;
				}

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		out.write("<!DOCTYPE html>");
		out.write("<html>");
		out.write("<head>");
		out.write("<meta charset=\"UTF-8\">");
		out.write("<title>Insert title here</title>");
		out.write("<style>");
		out.write("input {");
		out.write("	width : 50px;");
		out.write("	height : 50px;");
		out.write("}");
		out.write(".output{");
		out.write("	height:50px;");
		out.write("	background: gray;");
		out.write("	font-size: 24px;");
		out.write("	font-weight: bold;");
		out.write("	text-align: right;");
		out.write("	padding: 0px 5px;");
		out.write("}");
		out.write("</style>");
		out.write("</head>");
		out.write("<body>");
		out.write("	<form action=\"calc3\" method=\"post\">");
		out.write("		<table>");
		out.write("			<tr>");
		out.printf("				<td class=\"output\" colspan=\"4\">%s</td>", exp);
		out.write("			</tr>");
		out.write("			<tr>");
		out.write("				<td><input type=\"submit\" name=\"operator\" value=\"CE\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"operator\" value=\"C\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"operator\" value=\"BS\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"operator\" value=\"/\" /></td>");
		out.write("			</tr>");
		out.write("			<tr>");
		out.write("				<td><input type=\"submit\" name=\"value\" value=\"7\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"value\" value=\"8\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"value\" value=\"9\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"operator\" value=\"*\" /></td>");
		out.write("			</tr>");
		out.write("			<tr>");
		out.write("				<td><input type=\"submit\" name=\"value\" value=\"4\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"value\" value=\"5\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"value\" value=\"6\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"operator\" value=\"-\" /></td>");
		out.write("			</tr>");
		out.write("			<tr>");
		out.write("				<td><input type=\"submit\" name=\"value\" value=\"1\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"value\" value=\"2\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"value\" value=\"3\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"operator\" value=\"+\" /></td>");
		out.write("			</tr>");
		out.write("			<tr>");
		out.write("				<td></td>");
		out.write("				<td><input type=\"submit\" name=\"value\" value=\"0\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"dot\" value=\".\" /></td>");
		out.write("				<td><input type=\"submit\" name=\"operator\" value=\"=\" /></td>");
		out.write("			</tr>");
		out.write("		</table>");

		out.write("	</form>");
		out.write("</body>");
		out.write("</html>");

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Cookie[] cookies = request.getCookies(); // 사용자가 전달한걸 쿠키에저장해서 읽어옴

		String value = request.getParameter("value"); // 셋중에 하나 누르면 나머지 2개는 null값됨
		String operator = request.getParameter("operator");
		String dot = request.getParameter("dot");

		String exp = "";
		if (cookies != null)
			for (Cookie c : cookies)
				if (c.getName().equals("exp")) {
					exp = c.getValue();
					break;
				}

		if (operator != null && operator.equals("=")) {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn"); // 자바스크립트코드씀
			try {
				exp = String.valueOf(engine.eval(exp));
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (operator != null && operator.equals("C")) {
			exp = ""; // 쿠키제거

		} else {

			exp += (value == null) ? "" : value;
			exp += (operator == null) ? "" : operator;
			exp += (dot == null) ? "" : dot;

		}

		Cookie expCookie = new Cookie("exp", exp); // 누적된 상태에서 쿠키남김
		if (operator != null && operator.equals("C"))
			expCookie.setMaxAge(0); // expCookie.setMaxAge(0); 이걸설정해야 쿠키가 브라우저로 가서 바로소멸됨
		expCookie.setPath("/calculator");
		response.addCookie(expCookie);
		response.sendRedirect("calculator"); // 여기로 리다이렉트함

	}
}
