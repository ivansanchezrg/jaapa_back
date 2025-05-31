package com.jaapa_back.service;

import com.jaapa_back.exception.custom.EmailServiceException;
import com.jaapa_back.exception.custom.ValorActualIncorrectoException;
import com.jaapa_back.model.Correo;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLHandshakeException;
import java.io.EOFException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Base64;

@Service
@Slf4j
public class EmailService {
    @Value("${spring.mail.username}")
    private String emailFrom;

    private final JavaMailSender mailSender;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 2000; // 2 segundos

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(Correo correo) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < MAX_RETRIES) {
            try {
                attemptSendEmail(correo);
                log.info("Email enviado exitosamente a: {}", correo.getUsuario().getCorreo());
                return;
            } catch (Exception e) {
                lastException = e;
                attempts++;

                Throwable rootCause = getRootCause(e);
                String errorDetails = getErrorDetails(rootCause, e);

                log.error("Intento {}/{} fallido: {}", attempts, MAX_RETRIES, errorDetails);

                if (isRetryableError(rootCause)) {
                    if (attempts < MAX_RETRIES) {
                        try {
                            Thread.sleep(RETRY_DELAY);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new EmailServiceException("Proceso interrumpido durante reintento", ie);
                        }
                    }
                } else {
                    // Si no es un error recuperable, fallamos inmediatamente
                    throw new EmailServiceException(errorDetails, e);
                }
            }
        }

        // Si llegamos aquí, todos los intentos fallaron
        Throwable rootCause = getRootCause(lastException);
        throw new EmailServiceException(
                String.format("No se pudo enviar el email después de %d intentos: %s",
                        MAX_RETRIES,
                        getErrorDetails(rootCause, lastException)),
                lastException
        );
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }


    private String getErrorDetails(Throwable rootCause, Exception originalException) {
        if (rootCause instanceof EOFException && rootCause.getMessage().contains("SSL peer shut down incorrectly")) {
            return "Error de seguridad SSL: La conexión segura con el servidor de correo fue interrumpida. " +
                    "Verifique su configuración SSL/TLS y asegúrese de que no hay problemas de red.";
        }

        if (originalException instanceof MailSendException) {
            Throwable cause = originalException.getCause();
            if (cause instanceof MessagingException &&
                    cause.getMessage().contains("Could not convert socket to TLS")) {
                return "Error de configuración SSL/TLS: No se pudo establecer una conexión segura con el servidor. " +
                        "Verifique su configuración de correo y asegúrese de que TLS está habilitado correctamente.";
            }
        }

        if (rootCause instanceof SSLHandshakeException) {
            return "Error de handshake SSL: No se pudo establecer una conexión segura con el servidor. " +
                    "Esto puede deberse a problemas de certificados o configuración de seguridad.";
        }

        if (rootCause instanceof UnknownHostException) {
            return "No hay conexión a Internet o no se puede resolver el servidor SMTP (smtp.gmail.com)";
        } else if (rootCause instanceof ConnectException) {
            return "No se puede establecer conexión con el servidor SMTP. Puerto 587 bloqueado o servidor no disponible";
        } else if (rootCause instanceof SocketTimeoutException) {
            return "Tiempo de espera agotado al conectar con el servidor SMTP";
        } else if (rootCause instanceof AuthenticationFailedException) {
            return "Credenciales de correo incorrectas o no autorizadas";
        }

        return "Error en el servicio de correo: " + rootCause.getMessage();
    }

    private void attemptSendEmail(Correo correo) throws MessagingException, UnsupportedEncodingException {
        String emailDestino = correo.getUsuario().getCorreo().trim();
        if (!isValidEmail(emailDestino)) {
            throw new ValorActualIncorrectoException("Dirección de correo inválida: " + emailDestino);
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(new InternetAddress(emailFrom, "JAAPA"));
        helper.setTo(emailDestino);
        helper.setSubject("Solicitud " + correo.getTipoSolicitud());

        String content = String.format("Estimado %s %s, adjuntamos la solicitud del trámite realizado.",
                correo.getUsuario().getNombre(),
                correo.getUsuario().getApellido());
        helper.setText(content, true);

        byte[] pdfBytes = Base64.getDecoder().decode(correo.getPdf());
        String fileName = String.format("solicitud_%s.pdf", correo.getNumeroSolicitud());
        helper.addAttachment(fileName, new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }

    private boolean isRetryableError(Throwable throwable) {
        return throwable instanceof UnknownHostException ||
                throwable instanceof ConnectException ||
                throwable instanceof SocketTimeoutException ||
                (throwable instanceof EOFException &&
                        throwable.getMessage().contains("SSL peer shut down incorrectly"));
    }


    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
