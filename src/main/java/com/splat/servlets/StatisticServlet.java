package com.splat.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Сервис получения общей статистики по запросу.
 */
@WebServlet(name = "StatisticServlet", urlPatterns = "/statistic")
public class StatisticServlet extends HttpServlet
{

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("Total count for getAmount = " + AmountServlet.totalReadStatistic + "</br>");
        out.println("Total count for addAmount = " + AmountServlet.totalWriteStatistic + "</br>");
        out.println("For reset statistic use <a href='/reset'>/reset</a> url.");
        out.flush();
        out.close();
    }
}
