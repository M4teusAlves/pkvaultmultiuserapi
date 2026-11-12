package com.mateus.pkvaultmultiuserapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String appFrontendUrl;

    public void sendActivationEmail(String destinatario, String token) {
        String link = appFrontendUrl + "/activate/" + token;

        String texto = "Olá!\n\n"
                + "Clique no link abaixo para ativar sua conta:\n\n"
                + link + "\n\n"
                + "O link expira em 24 horas.\n\n"
                + "Se não foi você, ignore este email.";

        String html = """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <body style="margin:0;padding:0;background-color:#f4f5f7;font-family:Arial,Helvetica,sans-serif;">
              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f5f7;padding:24px 0;">
                <tr>
                  <td align="center">
                    <table role="presentation" width="480" cellpadding="0" cellspacing="0" style="background-color:#ffffff;border-radius:8px;padding:32px;">
                      <tr>
                        <td style="font-size:22px;font-weight:bold;color:#222222;padding-bottom:16px;">
                          Olá! Bem-vindo ao PkVault
                        </td>
                      </tr>
                      <tr>
                        <td style="font-size:15px;line-height:22px;color:#444444;padding-bottom:24px;">
                          Clique no botão abaixo para ativar sua conta.
                        </td>
                      </tr>
                      <tr>
                        <td align="center" style="padding-bottom:24px;">
                          <a href="%s" target="_blank"
                             style="background-color:#ff6900;color:#ffffff;text-decoration:none;padding:14px 32px;border-radius:6px;font-size:16px;font-weight:bold;display:inline-block;">
                            Ativar minha conta
                          </a>
                        </td>
                      </tr>
                      <tr>
                        <td style="font-size:13px;color:#777777;padding-bottom:16px;">
                          O link expira em 24 horas.
                        </td>
                      </tr>
                      <tr>
                        <td style="font-size:12px;line-height:18px;color:#999999;border-top:1px solid #eeeeee;padding-top:16px;">
                          Se o botão não funcionar, copie e cole este endereço no navegador:<br>
                          <a href="%s" style="color:#2563eb;word-break:break-all;">%s</a><br><br>
                          Se não foi você, ignore este e-mail.
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(link, link, link);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(destinatario);
            helper.setSubject("Ative sua conta - PkVault");
            helper.setText(texto, html);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Falha ao enviar e-mail de ativação", e);
        }
    }
}