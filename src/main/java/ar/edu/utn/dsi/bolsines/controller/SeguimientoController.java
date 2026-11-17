package ar.edu.utn.dsi.bolsines.controller;

import ar.edu.utn.dsi.bolsines.domain.Sesion;
import ar.edu.utn.dsi.bolsines.dto.SeguimientoBolsin;
import ar.edu.utn.dsi.bolsines.service.GestorSeguimientoBolsin;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seguimiento")
@CrossOrigin(origins = "http://localhost:5173")
public class SeguimientoController {

    private final GestorSeguimientoBolsin gestor;

    public SeguimientoController(GestorSeguimientoBolsin gestor) {
        this.gestor = gestor;
    }

    // DEMO: usuario por parámetro, sustituir por sesión/autenticación antes de producción.
    @GetMapping
    public List<SeguimientoBolsin> consultar(
        @RequestParam String usuario,
        @RequestParam(required = false) String precinto,
        @RequestParam(required = false) String destino
    ) {
        return gestor.consultar(new Sesion(usuario), precinto, destino);
    }

    @PostMapping("/{numero}/notificar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void notificar(@PathVariable Long numero, @RequestParam String usuario) {
        gestor.notificar(new Sesion(usuario), numero);
    }

    @ExceptionHandler(ar.edu.utn.dsi.bolsines.domain.InconsistenciaEstadoBolsinException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String inconsistenciaEstado(Exception e) {
        return e.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequest(Exception e) {
        return e.getMessage();
    }

    @ExceptionHandler(ar.edu.utn.dsi.bolsines.adapter.GpsException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String gpsUnavailable(Exception e) {
        return e.getMessage();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public String missingConfig(Exception e) {
        return e.getMessage();
    }
}
