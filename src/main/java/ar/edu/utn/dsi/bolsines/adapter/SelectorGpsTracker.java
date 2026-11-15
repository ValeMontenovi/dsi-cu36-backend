package ar.edu.utn.dsi.bolsines.adapter;
import org.springframework.stereotype.Component;
import ar.edu.utn.dsi.bolsines.domain.ModeloGps;
@Component public class SelectorGpsTracker {
 private final XtrAdapter xtr;private final NavTrackAdapter nav;private final GeoPulseAdapter geo;
 public SelectorGpsTracker(XtrAdapter xtr,NavTrackAdapter nav,GeoPulseAdapter geo){this.xtr=xtr;this.nav=nav;this.geo=geo;}
 public IGpsTracker seleccionar(ModeloGps modelo){return switch(modelo){case XTR_4500L->xtr;case NAVTRACK_QX_7A->nav;case GEOPULSE_MTR_900->geo;};}
}
