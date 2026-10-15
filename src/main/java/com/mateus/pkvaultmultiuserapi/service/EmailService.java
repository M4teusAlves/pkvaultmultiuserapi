package com.mateus.pkvaultmultiuserapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.url}")
    private String appUrl;

    public void sendActivationEmail(String destinatario, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Ative sua conta - PkVault");
        message.setText(
            "Olá!\n\n" +
            "Clique no link abaixo para ativar sua conta:\n\n" +
            appUrl + "/user/activate/" + token + "\n\n" +
            "O link expira em 24 horas.\n\n" +
            "Se não foi você, ignore este email."
        );
        mailSender.send(message);
    }
}
