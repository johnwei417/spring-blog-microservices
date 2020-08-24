package com.honglin.service;

import com.honglin.config.EmailConfig;
import com.honglin.vo.EmailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
public class EmailService {
    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(emailConfig.getHost());
        mailSenderImpl.setPort(emailConfig.getPort());
        mailSenderImpl.setUsername(emailConfig.getUsername());
        mailSenderImpl.setPassword(emailConfig.getPassword());

        return mailSenderImpl;
    }

    public void sendEmail(EmailDto emailDto) throws MessagingException, IOException {
        MimeMessage message = getJavaMailSender().createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(emailDto.getProps());

        String html = templateEngine.process("email-template.html", context);

        helper.setTo(emailDto.getMailTo());
        helper.setText(html, true);
        helper.setSubject(emailDto.getSubject());
        helper.setFrom(emailDto.getFrom());

        getJavaMailSender().send(message);
    }
}
