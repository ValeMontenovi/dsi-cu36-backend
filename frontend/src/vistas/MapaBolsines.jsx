import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet'
import 'leaflet/dist/leaflet.css'
import L from 'leaflet'
import { formatearFechaHora } from './formato'

// Ícono del marcador (el "pin"): un círculo amarillo, del color del precinto,
// con un anillo que late para mostrar que el bolsín está en camino.
// Se dibuja con HTML y CSS (clases .pin-* en estilos/index.css).
const ICONO_BOLSIN = L.divIcon({
  className: 'pin-bolsin',
  html: '<span class="pin-pulso"></span><span class="pin-punto"></span>',
  iconSize: [28, 28], // ancho y alto en píxeles
  iconAnchor: [14, 14], // el centro del círculo apoya sobre la coordenada
  popupAnchor: [0, -16], // el cartelito aparece arriba del círculo
})

// Pin del bolsín elegido en la lista: más grande y con los colores invertidos
const ICONO_BOLSIN_ELEGIDO = L.divIcon({
  className: 'pin-bolsin pin-elegido',
  html: '<span class="pin-pulso"></span><span class="pin-punto"></span>',
  iconSize: [36, 36],
  iconAnchor: [18, 18],
  popupAnchor: [0, -20],
})

// =====================================================================
// VISTA: MapaBolsines
// ---------------------------------------------------------------------
// Parte de mostrarMapaBolsin(): dibuja el mapa que armó
// InterfazGoogleMaps.mostrarMaps() → un marcador por bolsín ubicado.
// Si hay un bolsín elegido (numeroElegido), su pin se resalta.
// Solo dibuja: no tiene lógica del caso de uso.
// =====================================================================

export default function MapaBolsines({ mapa, numeroElegido = null }) {
  return (
    <section className="mapa-tarjeta">
      <h2 className="titulo-seccion">Última ubicación informada por el GPS</h2>
      <p className="mapa-nota">Tocá un pin para ver el recorrido y la hora del dato.</p>

      {/* IMPORTANTE: .mapa tiene un alto fijo en el CSS; sin alto, el mapa no se ve */}
      <MapContainer center={mapa.centro} zoom={mapa.zoom} className="mapa">
        <TileLayer url={mapa.servidor} attribution={mapa.creditos} />

        {mapa.marcadores.map((marcador) => (
          <Marker
            key={marcador.numeroBolsin}
            position={marcador.posicion}
            icon={marcador.numeroBolsin === numeroElegido ? ICONO_BOLSIN_ELEGIDO : ICONO_BOLSIN}
            zIndexOffset={marcador.numeroBolsin === numeroElegido ? 1000 : 0}
          >
            <Popup>
              <div className="popup-bolsin">
                <strong>Bolsín {marcador.numeroBolsin}</strong>
                <span>{marcador.recorrido}</span>
                <small>{formatearFechaHora(marcador.fechaHoraActualizacion)}</small>
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </section>
  )
}
