package br.com.ecommerce;

import br.com.ecommerce.dispatcher.KafkaDispather;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet{
    private final KafkaDispather<Order> orderKafkaDispather =new KafkaDispather<>();

    @Override
    public void destroy() {
        super.destroy();
        orderKafkaDispather.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            //we are not caring about any security issues, we are only
            // showing how to use ttp as a starting point
            var email = req.getParameter("email");
            var orderId = UUID.randomUUID().toString();
            var amout = new BigDecimal(req.getParameter("amout"));

            var order = new Order(orderId,amout,email);
            orderKafkaDispather.send("ECOMMERCE_NEW_ORDER" ,email,new CorrelationId(NewOrderServlet.class.getSimpleName()), order);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("new order sent successfully");

        } catch (ExecutionException e) {
            throw  new ServletException(e);
        } catch (InterruptedException e) {
            throw  new ServletException(e);
        }
    }
}
