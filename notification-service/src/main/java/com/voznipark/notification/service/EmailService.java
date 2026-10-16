package com.voznipark.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends plain-text emails via JavaMailSender.
 * Failures are caught and logged — never rethrown, to avoid Kafka retries.
 * Returns true if the email was handed off to the SMTP server, false otherwise.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.email.from}")
    private String fromAddress;

    @Value("${notification.email.admin}")
    private String adminAddress;

    public boolean sendAdminNotification(String subject, String body) {
        return sendEmail(adminAddress, subject, body);
    }

    public boolean sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent → to={} | subject={}", to, subject);
            return true;
        } catch (MailException e) {
            log.error("Failed to send email → to={} | subject={} | error={}",
                    to, subject, e.getMessage(), e);
            return false;
        }
    }
}