package Servlet;

import product.WebSpider;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yizhou on 14-6-7.
 */
public class PaquServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String gupiao = request.getParameter("gupiao");
        String start = request.getParameter("starttime");
        String end = request.getParameter("endtime");
        Integer first = Integer.parseInt(request.getParameter("first"));
        Integer last = Integer.parseInt(request.getParameter("last"));
        System.out.println(gupiao + start + end + first + last);
        WebSpider.SpiderRunAll(gupiao,start,end,first,last);
        request.setAttribute("gupiao",gupiao);
        request.setAttribute("starttime",start);
        request.setAttribute("endtime",end);
        RequestDispatcher view = request.getRequestDispatcher("qxlh.jsp");
        view.forward(request, response);
    }
    }
