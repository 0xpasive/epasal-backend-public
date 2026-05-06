package epasal.com.mailserver.listener;

import epasal.com.mailserver.config.EmailService;
import epasal.com.mailserver.config.RabbitConfig;
import epasal.com.mailserver.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class UserEventListener {
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    @RabbitListener(queues = RabbitConfig.USER_EVENT_QUEUE)
    public void handleUserRegistered(UserRegisteredEvent event) {

        Context context = new Context();
        context.setVariable("name", event.getFullName());

        String htmlContent = templateEngine.process("welcome", context);
        emailService.sendEmail(event.getEmail(), "Welcome to ePasal!", htmlContent);
    }
}
