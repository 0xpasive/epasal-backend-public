package epasal.com.mailserver.listener;

import epasal.com.mailserver.config.EmailService;
import epasal.com.mailserver.config.RabbitConfig;
import epasal.com.mailserver.event.UserCodeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class UserCodeEventListener {
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    public String verification = "VERIFICATION CODE";
    public String password_reset = "PASSWORD RESET";

    @RabbitListener(queues = RabbitConfig.USER_CODE_QUEUE)
    public void handleUserCodeEvent(UserCodeEvent userCodeEvent) {
        if (userCodeEvent.getType() == UserCodeEvent.Type.VERIFICATION) {
            Context context = new Context();
            context.setVariable("code", userCodeEvent.getCode());
            String htmlContent = templateEngine.process("verification", context);

            emailService.sendEmail(userCodeEvent.getEmail(), verification, htmlContent);
        } else if (userCodeEvent.getType() == UserCodeEvent.Type.PASSWORD_RESET) {
            Context context = new Context();
            context.setVariable("code", userCodeEvent.getCode());
            String htmlContent = templateEngine.process("password-reset", context);

            emailService.sendEmail(userCodeEvent.getEmail(), password_reset, htmlContent);
        }

    }

}
