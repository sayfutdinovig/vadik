package com.splat.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
Основной сервлет, с которого можно получить общую статистику запросов, а также обнулить ее.
 */
@WebServlet(name = "MainServlet", urlPatterns = "/")
public class MainServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("GET /server?id={id} - for method getAmount(Integer id).</br>");
        out.println("POST /server?id={id}&value={value} - for method addAmount(Integer id, Long value).</br>");
        out.println("GET <a href='/statistic'>/statistic</a> - for get statistics.</br>");
        out.println("GET <a href='/reset'>/reset</a> - for reset statistics.</br>");
        out.flush();
        out.close();
    }
}
