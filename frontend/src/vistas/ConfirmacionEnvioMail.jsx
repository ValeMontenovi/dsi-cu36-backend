import { useEffect } from 'react'
import { formatearCoordenada, formatearFechaHora } from './formato'

// =====================================================================
// VISTA: ConfirmacionEnvioMail
// ---------------------------------------------------------------------
// Parte de solicitarEnvioMail(): ventana que le pregunta al actor si
// quiere enviar por mail la ubicación del bolsín seleccionado.
//   "Enviar mail"  → tomarConfirmacion(true)
//   "No enviar"    → tomarConfirmacion(false): flujo alternativo A5
//   "Elegir otro" o Escape → vuelve a la selección del bolsín
// Solo dibuja: no tiene lógica del caso de uso.
// =====================================================================

export default function ConfirmacionEnvioMail({ bolsin, enviando, onEnviar, onNoEnviar, onVolver }) {
  // La tecla Escape cierra la ventana (salvo mientras se está enviando)
  useEffect(() => {
    const alApretarTecla = (evento) => {
      if (evento.key === 'Escape' && !enviando) onVolver()
    }
    window.addEventListener('keydown', alApretarTecla)
    return () => window.removeEventListener('keydown', alApretarTecla)
  }, [enviando, onVolver])

  return (
    <div className="velo">
      <section className="dialogo" role="dialog" aria-modal="true" aria-labelledby="titulo-confirmacion">
        <h2 id="titulo-confirmacion" className="dialogo-titulo">
          ¿Enviar la ubicación por mail?
        </h2>
        <p className="dialogo-texto">
          Podés avisarle al gerente de la comisión médica destino dónde está el bolsín. No es obligatorio: si no lo
          enviás, la consulta termina igual.
        </p>

        <dl className="dialogo-datos">
          <div>
            <dt>Bolsín</dt>
            <dd>
              {bolsin.numeroBolsin}, precinto {bolsin.numeroPrecinto}
            </dd>
          </div>
          <div>
            <dt>Recorrido</dt>
            <dd>
              {bolsin.cmOrigen} → {bolsin.cmDestino}
            </dd>
          </div>
          <div>
            <dt>Última ubicación</dt>
            <dd>
              {formatearCoordenada(bolsin.ubicacion.latitud)}, {formatearCoordenada(bolsin.ubicacion.longitud)}
              <small>Actualizado el {formatearFechaHora(bolsin.ubicacion.fechaHoraActualizacion)}</small>
            </dd>
          </div>
          <div>
            <dt>Destinatario</dt>
            <dd>Gerente de la CM {bolsin.cmDestino}</dd>
          </div>
        </dl>

        <div className="dialogo-botones">
          <button className="boton-enlace" onClick={onVolver} disabled={enviando}>
            Elegir otro bolsín
          </button>
          <button className="boton-secundario" onClick={onNoEnviar} disabled={enviando}>
            No enviar
          </button>
          <button className="boton-principal" onClick={onEnviar} disabled={enviando} autoFocus>
            {enviando ? (
              <>
                <span className="spinner" aria-hidden="true" />
                Enviando…
              </>
            ) : (
              'Enviar mail'
            )}
          </button>
        </div>
      </section>
    </div>
  )
}
