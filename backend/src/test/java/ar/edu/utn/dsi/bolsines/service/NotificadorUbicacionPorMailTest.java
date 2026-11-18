package ar.edu.utn.dsi.bolsines.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ar.edu.utn.dsi.bolsines.dto.SeguimientoBolsin;
import ar.edu.utn.dsi.bolsines.dto.UbicacionBolsin;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;

class NotificadorUbicacionPorMailTest {

    private final SeguimientoBolsin seguimiento = new SeguimientoBolsin(
        101L,
        "P-101",
        "Córdoba",
        "Rosario",
        "XTR_4500L",
        new UbicacionBolsin(101L, -31.4201, -64.1888, OffsetDateTime.parse("2026-03-07T12:30:00Z")),
        null
    );

    @SuppressWarnings("unchecked")
    private ObjectProvider<JavaMailSender> proveedor(JavaMailSender servidor) {
        ObjectProvider<JavaMailSender> proveedor = mock(ObjectProvider.class);
        when(proveedor.getIfAvailable()).thenReturn(servidor);
        return proveedor;
    }

    @Test
    void sinServidorDeCorreoGuardaElMailComoEml(@TempDir Path carpeta) throws Exception {
        var notificador = new NotificadorUbicacionPorMail(
            proveedor(null),
            "remitente@organismo.test",
            carpeta.toString()
        );

        notificador.notificar("gerente-ros@example.test", seguimiento);

        List<Path> archivos;
        try (var lista = Files.list(carpeta)) {
            archivos = lista.toList();
        }
        assertEquals(1, archivos.size());
        assertTrue(archivos.get(0).getFileName().toString().endsWith("-bolsin-101.eml"));

        MimeMessage mail;
        try (InputStream entrada = Files.newInputStream(archivos.get(0))) {
            mail = new MimeMessage(Session.getInstance(new Properties()), entrada);
        }
        assertEquals("Ubicación del bolsín 101 (precinto P-101)", mail.getSubject());
        assertEquals(
            "gerente-ros@example.test",
            ((InternetAddress) mail.getAllRecipients()[0]).getAddress()
        );
        String texto = (String) mail.getContent();
        assertTrue(texto.contains("Número de bolsín: 101"));
        assertTrue(texto.contains("Coordenadas: -31.4201, -64.1888"));
        // 12:30 UTC son las 09:30 en Argentina
        assertTrue(texto.contains("Última actualización: 07/03/2026 09:30"));
    }

    @Test
    void conServidorDeCorreoLoEnviaPorSmtp(@TempDir Path carpeta) throws Exception {
        JavaMailSender servidor = mock(JavaMailSender.class);
        var notificador = new NotificadorUbicacionPorMail(
            proveedor(servidor),
            "remitente@organismo.test",
            carpeta.toString()
        );

        notificador.notificar("gerente-ros@example.test", seguimiento);

        verify(servidor).send(any(MimeMessage.class));
        try (var lista = Files.list(carpeta)) {
            assertEquals(0, lista.count());
        }
    }

    @Test
    void siElServidorFallaInformaQueNoSeEnvio(@TempDir Path carpeta) {
        JavaMailSender servidor = mock(JavaMailSender.class);
        doThrow(new org.springframework.mail.MailSendException("conexión rechazada"))
            .when(servidor)
            .send(any(MimeMessage.class));
        var notificador = new NotificadorUbicacionPorMail(
            proveedor(servidor),
            "remitente@organismo.test",
            carpeta.toString()
        );

        var error = assertThrows(NotificacionException.class, () ->
            notificador.notificar("gerente-ros@example.test", seguimiento)
        );
        assertTrue(error.getMessage().contains("conexión rechazada"));
    }
}
