package br.com.ecommerce;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.Source;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GenerateAllReportsServlet extends HttpServlet {
        private final KafkaDispather<String> dispatcher =new KafkaDispather<>();


    @Override
    public void destroy() {
        super.destroy();
        dispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            dispatcher.send("SEND_MESSAGE_TO_ALL_USERS","USER_GENERATE_READING_REPORT","USER_GENERATE_READING_REPORT");

            System.out.println("Sent generate reports to all users");

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Report requests generated");

        } catch (ExecutionException e) {
            throw  new ServletException(e);
        } catch (InterruptedException e) {
            throw  new ServletException(e);
        }
    }
}
