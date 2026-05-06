package epasal.com.mailserver.listener;

import epasal.com.mailserver.config.EmailService;
import epasal.com.mailserver.config.RabbitConfig;
import epasal.com.mailserver.dto.ApiResponse;
import epasal.com.mailserver.dto.OrderResponse;
import epasal.com.mailserver.dto.PaymentResponse;
import epasal.com.mailserver.dto.UserEmailResponse;
import epasal.com.mailserver.event.OrderEvent;
import epasal.com.mailserver.service.OrderServiceClient;
import epasal.com.mailserver.service.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventListener {
    private final EmailService emailService;
    private final OrderServiceClient orderServiceClient;
    private final TemplateEngine templateEngine;
    private final UserServiceClient userServiceClient;

    public final String SUBJECT = "ORDER CONFIRMATION!";

    @RabbitListener(queues = RabbitConfig.ORDER_EVENT_QUEUE)
    public void handleOrderEvent(OrderEvent orderEvent) {

        ApiResponse<OrderResponse> orderResponse = orderServiceClient.getOrderById(orderEvent.getOrderId());
        OrderResponse order = orderResponse.getData();

        ApiResponse<PaymentResponse> paymentResponse = orderServiceClient.getPaymentByIdAdmin(order.getTransactionId());
        PaymentResponse payment = paymentResponse.getData();

        ApiResponse<UserEmailResponse> userResponse = userServiceClient.getUserFullName(order.getUserId());
        String email = userResponse.getData().getEmail();

        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("payment", payment);


        if (orderEvent.getStatus().equals("CONFIRMED")) {
            String htmlContent = templateEngine.process("order", context);
            emailService.sendEmail(email, SUBJECT, htmlContent);
        } else {
            String htmlContent = templateEngine.process("order-dispute", context);
            emailService.sendEmail(email, SUBJECT, htmlContent);
        }

    }
}
