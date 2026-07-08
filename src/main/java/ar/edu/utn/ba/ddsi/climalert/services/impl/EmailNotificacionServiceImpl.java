package ar.edu.utn.ba.ddsi.climalert.services.impl;

import ar.edu.utn.ba.ddsi.climalert.models.entities.alerta.AlertaClimatica;
import ar.edu.utn.ba.ddsi.climalert.services.NotificacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailNotificacionServiceImpl implements NotificacionService {

    private final JavaMailSender mailSender;

    public EmailNotificacionServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarAlerta(AlertaClimatica alerta) {
        log.info("""
                
                ========================================================================
                📧 SIMULACIÓN / ENVÍO DE MAIL:
                Para: %s
                Asunto: 🚨 Climalert: Alerta meteorológica detectada 🚨
                Cuerpo:
                %s========================================================================
                """.formatted(String.join(", ", alerta.getDestinatarios()), alerta.getCuerpoMensaje()));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(alerta.getDestinatarios().toArray(new String[0]));
        message.setSubject("🚨 Climalert: Alerta meteorológica detectada 🚨");
        message.setText(alerta.getCuerpoMensaje());
        mailSender.send(message);
    }
}

