package Servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yizhou on 14-6-8.
 */
public class TrainServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        try {
            String[] option = weka.core.Utils.splitOptions("weka.classifiers.functions.LibLINEAR -S 1 -C 1.0 -E 0.01 -B 1.0");
//            DataPreProcess.Train("D:\\股吧\\", "train", "liblinear", Integer.parseInt(request.getParameter("savenum")));
            response.getWriter().println("<script> alert(\"train success!\"); </script>");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script> alert(\"train failed!\"); </script>");
        }
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
