package in.mariasorganics.inventorytracker.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import in.mariasorganics.inventorytracker.service.IEmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("noreply@mariasmushroomfarm.in");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
