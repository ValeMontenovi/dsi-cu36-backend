package ar.edu.utn.dsi.bolsines.service;

import ar.edu.utn.dsi.bolsines.adapter.*;
import ar.edu.utn.dsi.bolsines.domain.*;
import ar.edu.utn.dsi.bolsines.dto.*;
import ar.edu.utn.dsi.bolsines.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GestorSeguimientoBolsin {

    private final BolsinRepository bolsines;
    private final EmpleadoRepository empleados;
    private final InterfazGPSTracker interfazGps;
    private final NotificadorUbicacion notificador;

    public GestorSeguimientoBolsin(
        BolsinRepository b,
        EmpleadoRepository e,
        InterfazGPSTracker s,
        NotificadorUbicacion n
    ) {
        bolsines = b;
        empleados = e;
        interfazGps = s;
        notificador = n;
    }

    private String buscarCMUsuario(Sesion sesion) {
        String usuario = sesion.getUsuario();
        for (Empleado empleado : empleados.findAll()) {
            if (empleado.esTuUsuario(usuario)) return empleado.obtenerCM();
        }
        throw new IllegalArgumentException("Usuario no encontrado: " + usuario);
    }

    private List<Bolsin> buscarBolsinesEnviadoCM(String codigoCM) {
        return bolsines
            .findAll()
            .stream()
            .filter(b -> b.esTuCMOrigen(codigoCM))
            .filter(Bolsin::sosEnviado)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<SeguimientoBolsin> consultar(Sesion sesion, String precinto, String destino) {
        String cm = buscarCMUsuario(sesion);
        List<Bolsin> encontrados = buscarBolsinesEnviadoCM(cm);
        List<Bolsin> filtrados = encontrados
            .stream()
            .filter(
                b -> precinto == null || precinto.isBlank() || precinto.equals(b.getNroPrecinto())
            )
            .filter(
                b ->
                    destino == null ||
                    destino.isBlank() ||
                    destino.equals(b.obtenerCMDestino().getCodigo())
            )
            .toList();
        return localizarBolsines(filtrados, cm);
    }

    private List<SeguimientoBolsin> localizarBolsines(
        List<Bolsin> encontrados,
        String codigoCMOrigen
    ) {
        if (encontrados.isEmpty()) return List.of();
        int[] numeros = new int[encontrados.size()];
        String[] modelos = new String[numeros.length];
        String[] destinos = new String[numeros.length];
        for (int i = 0; i < numeros.length; i++) {
            Bolsin b = encontrados.get(i);
            try {
                numeros[i] = Math.toIntExact(b.getNroBolsin());
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException(
                    "Numero de bolsin fuera del rango int: " + b.getNroBolsin(),
                    e
                );
            }
            modelos[i] = b.getModeloGps().name();
            destinos[i] = b.obtenerCMDestino().getCodigo();
        }
        String[][] filas = interfazGps.obtenerLocalizacion(
            numeros,
            modelos,
            codigoCMOrigen,
            destinos
        );
        var resultados = new java.util.ArrayList<SeguimientoBolsin>();
        for (int i = 0; i < numeros.length; i++) {
            Bolsin b = encontrados.get(i);
            String[] fila = filas[i];
            UbicacionBolsin ubicacion =
                fila[4] == null
                    ? new UbicacionBolsin(
                          Long.valueOf(fila[0]),
                          Double.parseDouble(fila[1]),
                          Double.parseDouble(fila[2]),
                          java.time.OffsetDateTime.parse(fila[3])
                      )
                    : null;
            resultados.add(
                new SeguimientoBolsin(
                    b.getNroBolsin(),
                    b.getNroPrecinto(),
                    b.getOrigen().getNombre(),
                    b.obtenerCMDestino().getNombre(),
                    b.getModeloGps().name(),
                    ubicacion,
                    fila[4]
                )
            );
        }
        return resultados;
    }

    @Transactional(readOnly = true)
    public void notificar(Sesion sesion, Long numero) {
        Bolsin b = bolsines
            .findById(numero)
            .orElseThrow(() -> new IllegalArgumentException("Bolsín inexistente"));
        String cm = buscarCMUsuario(sesion);
        if (!b.esTuCMOrigen(cm) || !b.sosEnviado()) throw new IllegalArgumentException(
            "Bolsín no disponible para este usuario"
        );
        SeguimientoBolsin s = localizarBolsines(List.of(b), cm).get(0);
        if (s.ubicacion() == null) throw new GpsException(
            "No hay ubicación disponible para notificar"
        );
        if (
            b.obtenerCMDestino().getEmailGerente() == null ||
            b.obtenerCMDestino().getEmailGerente().isBlank()
        ) throw new IllegalArgumentException("CM destino sin email de gerente");
        notificador.notificar(b.obtenerCMDestino().getEmailGerente(), s);
    }
}
