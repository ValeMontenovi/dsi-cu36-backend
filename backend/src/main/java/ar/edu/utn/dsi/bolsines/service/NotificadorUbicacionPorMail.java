package ar.edu.utn.dsi.bolsines.service;

import ar.edu.utn.dsi.bolsines.dto.SeguimientoBolsin;
import ar.edu.utn.dsi.bolsines.dto.UbicacionBolsin;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * CU 31 "Notificar ubicación de bolsín": arma el mail con el número de bolsín, la fecha y hora
 * de la última actualización y las coordenadas, y lo envía al gerente de la CM destino.
 *
 * <ul>
 *   <li>Si hay un servidor de correo configurado ({@code spring.mail.host}), lo envía por SMTP.
 *   <li>Si no, lo guarda como archivo {@code .eml} en {@code notificaciones.carpeta} (por defecto
 *       {@code data/mails}). Ese archivo se abre con Outlook o Thunderbird y muestra el mail tal
 *       cual se enviaría. Sirve para la demo sin compartir contraseñas de correo.
 * </ul>
 */
@Component
public class NotificadorUbicacionPorMail implements NotificadorUbicacion {

    private static final Logger log = LoggerFactory.getLogger(NotificadorUbicacionPorMail.class);
    private static final ZoneId ARGENTINA = ZoneId.of("America/Argentina/Buenos_Aires");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern(
        "dd/MM/yyyy HH:mm",
        Locale.forLanguageTag("es-AR")
    );
    private static final DateTimeFormatter NOMBRE_ARCHIVO = DateTimeFormatter.ofPattern(
        "yyyyMMdd-HHmmss"
    );

    private final ObjectProvider<JavaMailSender> servidorCorreo;
    private final String remitente;
    private final Path carpeta;

    public NotificadorUbicacionPorMail(
        ObjectProvider<JavaMailSender> servidorCorreo,
        @Value("${notificaciones.remitente:seguimiento-bolsines@organismo.test}") String remitente,
        @Value("${notificaciones.carpeta:./data/mails}") String carpeta
    ) {
        this.servidorCorreo = servidorCorreo;
        this.remitente = remitente;
        this.carpeta = Path.of(carpeta);
    }

    @Override
    public void notificar(String email, SeguimientoBolsin seguimiento) {
        JavaMailSender servidor = servidorCorreo.getIfAvailable();
        try {
            MimeMessage mail = armarMail(email, seguimiento);
            if (servidor != null) {
                servidor.send(mail);
                log.info(
                    "Mail de ubicación del bolsín {} enviado a {}",
                    seguimiento.numeroBolsin(),
                    email
                );
            } else {
                Path archivo = guardarMail(mail, seguimiento.numeroBolsin());
                log.info(
                    "Mail de ubicación del bolsín {} para {} guardado en {}",
                    seguimiento.numeroBolsin(),
                    email,
                    archivo.toAbsolutePath()
                );
            }
        } catch (MessagingException | IOException | MailException e) {
            throw new NotificacionException("No se pudo enviar el mail: " + e.getMessage(), e);
        }
    }

    MimeMessage armarMail(String email, SeguimientoBolsin seguimiento) throws MessagingException {
        MimeMessage mail = new MimeMessage(Session.getInstance(new Properties()));
        MimeMessageHelper ayudante = new MimeMessageHelper(mail, false, "UTF-8");
        ayudante.setFrom(remitente);
        ayudante.setTo(email);
        ayudante.setSubject(
            "Ubicación del bolsín " +
                seguimiento.numeroBolsin() +
                " (precinto " +
                seguimiento.numeroPrecinto() +
                ")"
        );
        ayudante.setText(armarTexto(seguimiento), false);
        return mail;
    }

    String armarTexto(SeguimientoBolsin seguimiento) {
        UbicacionBolsin ubicacion = seguimiento.ubicacion();
        String latitud = String.format(Locale.ROOT, "%.4f", ubicacion.latitud());
        String longitud = String.format(Locale.ROOT, "%.4f", ubicacion.longitud());
        String fechaHora = ubicacion
            .fechaHoraActualizacion()
            .atZoneSameInstant(ARGENTINA)
            .format(FECHA_HORA);
        return String.join(
            "\n",
            "Hola,",
            "",
            "Le informamos la última ubicación del bolsín " +
                seguimiento.numeroBolsin() +
                ", enviado desde la Comisión Médica " +
                seguimiento.cmOrigen() +
                " hacia la Comisión Médica " +
                seguimiento.cmDestino() +
                ".",
            "",
            "Número de bolsín: " + seguimiento.numeroBolsin(),
            "Número de precinto: " + seguimiento.numeroPrecinto(),
            "Última actualización: " + fechaHora + " (hora de Argentina)",
            "Coordenadas: " + latitud + ", " + longitud,
            "Ver en el mapa: https://www.openstreetmap.org/?mlat=" +
                latitud +
                "&mlon=" +
                longitud +
                "#map=12/" +
                latitud +
                "/" +
                longitud,
            "",
            "Este mail se generó automáticamente desde el sistema de seguimiento de bolsines."
        );
    }

    private Path guardarMail(MimeMessage mail, Long numeroBolsin)
        throws MessagingException, IOException
    {
        Files.createDirectories(carpeta);
        Path archivo = carpeta.resolve(
            LocalDateTime.now(ARGENTINA).format(NOMBRE_ARCHIVO) + "-bolsin-" + numeroBolsin + ".eml"
        );
        mail.saveChanges();
        try (OutputStream salida = Files.newOutputStream(archivo)) {
            mail.writeTo(salida);
        }
        return archivo;
    }
}
