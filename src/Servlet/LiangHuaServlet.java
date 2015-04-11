package Servlet;

import product.DataPreProcess;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yizhou on 14-6-8.
 */
public class LiangHuaServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String gupiao = request.getParameter("gupiao");
        String start = request.getParameter("starttime");
        String end = request.getParameter("endtime");
        System.out.println(start+end+"woqu");
        DataPreProcess.QingXulianghua("D:\\股吧\\",gupiao,start,end);
        RequestDispatcher view = request.getRequestDispatcher("qxlh.jsp");
        view.forward(request, response);
    }
}
