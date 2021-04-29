package br.com.ecommerce;

import br.com.ecommerce.dispatcher.KafkaDispather;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
            dispatcher.send("ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS","ECOMMERCE_USER_GENERATE_READING_REPORT",
                    new CorrelationId(GenerateAllReportsServlet.class.getSimpleName()),"ECOMMERCE_USER_GENERATE_READING_REPORT");

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
