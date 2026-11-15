package ar.edu.utn.dsi.bolsines.service;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.utn.dsi.bolsines.adapter.*;
import ar.edu.utn.dsi.bolsines.domain.*;
import ar.edu.utn.dsi.bolsines.dto.*;
import ar.edu.utn.dsi.bolsines.repository.*;
@Service public class GestorSeguimientoBolsin {
 private final BolsinRepository bolsines;private final EmpleadoRepository empleados;private final SelectorGpsTracker selector;private final NotificadorUbicacion notificador;
 public GestorSeguimientoBolsin(BolsinRepository b,EmpleadoRepository e,SelectorGpsTracker s,NotificadorUbicacion n){bolsines=b;empleados=e;selector=s;notificador=n;}
 private Empleado empleado(String usuario){return empleados.findByUsuario(usuario).orElseThrow(()->new IllegalArgumentException("Usuario no encontrado: "+usuario));}
 @Transactional(readOnly=true) public List<SeguimientoBolsin> consultar(String usuario,String precinto,String destino){String cm=empleado(usuario).getComisionMedica().getCodigo();List<Bolsin> encontrados=bolsines.findByOrigenCodigoAndEstado(cm,EstadoBolsin.ENVIADO);
 return encontrados.stream().filter(b->precinto==null||precinto.isBlank()||precinto.equals(b.getNumeroPrecinto())).filter(b->destino==null||destino.isBlank()||destino.equals(b.getDestino().getCodigo())).map(this::localizar).toList();}
 private SeguimientoBolsin localizar(Bolsin b){UbicacionBolsin ubicacion=null;String error=null;try{ubicacion=selector.seleccionar(b.getModeloGps()).obtenerUbicacion(b);if(!b.getNumero().equals(ubicacion.numeroBolsin()))throw new GpsException("Respuesta GPS corresponde a otro bolsín");}catch(GpsException e){error=e.getMessage();}return new SeguimientoBolsin(b.getNumero(),b.getNumeroPrecinto(),b.getOrigen().getNombre(),b.getDestino().getNombre(),b.getModeloGps().name(),ubicacion,error);}
 @Transactional(readOnly=true) public void notificar(String usuario,Long numero){Bolsin b=bolsines.findById(numero).orElseThrow(()->new IllegalArgumentException("Bolsín inexistente"));String cm=empleado(usuario).getComisionMedica().getCodigo();if(!b.getOrigen().getCodigo().equals(cm)||b.getEstado()!=EstadoBolsin.ENVIADO)throw new IllegalArgumentException("Bolsín no disponible para este usuario");SeguimientoBolsin s=localizar(b);if(s.ubicacion()==null)throw new GpsException("No hay ubicación disponible para notificar");if(b.getDestino().getEmailGerente()==null||b.getDestino().getEmailGerente().isBlank())throw new IllegalArgumentException("CM destino sin email de gerente");notificador.notificar(b.getDestino().getEmailGerente(),s);}
}
