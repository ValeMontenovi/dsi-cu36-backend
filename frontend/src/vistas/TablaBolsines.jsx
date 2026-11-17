import { formatearCoordenada, formatearFechaHora } from './formato'

// Qué adaptador del backend atiende a cada modelo de GPS (patrón Adapter).
// Se usa SOLO para mostrarlo en pantalla durante la defensa.
const ADAPTADOR_POR_MODELO = {
  XTR_4500L: 'AdaptadorXTR4500L',
  NAVTRACK_QX_7A: 'AdaptadorNavTrackQX7A',
  GEOPULSE_MTR_900: 'AdaptadorGeoPulseMTR900',
}

// =====================================================================
// VISTA: TablaBolsines
// ---------------------------------------------------------------------
// Parte de mostrarMapaBolsin(): lista los bolsines enviados con la
// ubicación que devolvió el GPS (o el error si el GPS no respondió).
// Cada bolsín se dibuja como una etiqueta de envío con su talón de precinto.
//
// Después de solicitarSelBolsinTipo() (seleccionHabilitada = true) cada
// etiqueta se puede elegir con un botón de opción. Solo se pueden elegir
// los bolsines con ubicación: sin ubicación no hay nada para notificar.
// Solo dibuja: no tiene lógica del caso de uso.
// =====================================================================

export default function TablaBolsines({ bolsines, seleccionHabilitada = false, numeroElegido = null, onElegir }) {
  return (
    <section className="lista">
      <h2 className="titulo-seccion">Bolsines enviados</h2>

      <ul
        className="etiquetas"
        role={seleccionHabilitada ? 'radiogroup' : undefined}
        aria-label={seleccionHabilitada ? 'Elegí un bolsín' : undefined}
      >
        {bolsines.map((bolsin, orden) => {
          const elegible = seleccionHabilitada && Boolean(bolsin.ubicacion)
          const clases = [
            'etiqueta',
            elegible && 'seleccionable',
            seleccionHabilitada && !elegible && 'no-elegible',
            seleccionHabilitada && numeroElegido === bolsin.numeroBolsin && 'elegida',
          ]
            .filter(Boolean)
            .join(' ')

          return (
            // --orden sirve para que las etiquetas aparezcan una después de la otra
            <li
              key={bolsin.numeroBolsin}
              className={clases}
              style={{ '--orden': orden }}
              onClick={elegible ? () => onElegir(bolsin.numeroBolsin) : undefined}
            >
              {/* Talón amarillo: el precinto con el que se cerró el bolsín */}
              <div className="etiqueta-talon">
                <span className="talon-texto">Precinto</span>
                <span className="talon-numero">{bolsin.numeroPrecinto}</span>
              </div>

              <div className="etiqueta-cuerpo">
                <div className="etiqueta-encabezado">
                  <h3 className="etiqueta-titulo">Bolsín {bolsin.numeroBolsin}</h3>
                  {/* Botón de opción: aparece solo mientras se pide elegir un bolsín */}
                  {seleccionHabilitada && (
                    <input
                      type="radio"
                      name="bolsin-seleccionado"
                      className="opcion-bolsin"
                      checked={numeroElegido === bolsin.numeroBolsin}
                      disabled={!elegible}
                      onChange={() => onElegir(bolsin.numeroBolsin)}
                      aria-label={`Elegir el bolsín ${bolsin.numeroBolsin}`}
                    />
                  )}
                </div>

                {/* Recorrido: CM origen ---- CM destino */}
                <p className="ruta">
                  <span>{bolsin.cmOrigen}</span>
                  <span className="ruta-linea" aria-hidden="true" />
                  <span className="solo-lector">hacia</span>
                  <span>{bolsin.cmDestino}</span>
                </p>

                {/* Modelo de GPS y adaptador del backend que lo atendió */}
                <p className="gps">
                  <span className="chip-modelo" data-modelo={bolsin.modeloGps}>
                    {bolsin.modeloGps}
                  </span>
                  <span className="adaptador">atendido por {ADAPTADOR_POR_MODELO[bolsin.modeloGps]}</span>
                </p>

                {bolsin.ubicacion ? (
                  <p className="estado estado-ubicado">
                    <span className="estado-punto" aria-hidden="true" />
                    <span>
                      <span className="estado-texto">
                        {formatearCoordenada(bolsin.ubicacion.latitud)}, {formatearCoordenada(bolsin.ubicacion.longitud)}
                      </span>
                      <small>Actualizado el {formatearFechaHora(bolsin.ubicacion.fechaHoraActualizacion)}</small>
                    </span>
                  </p>
                ) : (
                  // Flujo alternativo: el GPS de este bolsín no respondió
                  <p className="estado estado-sin-senal">
                    <span className="estado-punto" aria-hidden="true" />
                    <span>
                      <span className="estado-texto">Sin señal del GPS</span>
                      <small>{bolsin.errorGps ?? 'Sin ubicación'}</small>
                      {seleccionHabilitada && <small>No se puede notificar un bolsín sin ubicación.</small>}
                    </span>
                  </p>
                )}
              </div>
            </li>
          )
        })}
      </ul>
    </section>
  )
}
