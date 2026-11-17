import { useEffect } from 'react'
import { formatearCoordenada, formatearFechaHora } from './formato'

// =====================================================================
// VISTA: ConfirmacionEnvioMail
// ---------------------------------------------------------------------
// Parte de solicitarEnvioMail(): ventana que le pregunta al actor si
// quiere enviar por mail la ubicación del bolsín seleccionado.
//   "Confirmar envío" → tomarConfirmacion()
//   "Cancelar" o Escape → vuelve a la selección (el actor no confirma)
// Solo dibuja: no tiene lógica del caso de uso.
// =====================================================================

export default function ConfirmacionEnvioMail({ bolsin, enviando, onConfirmar, onCancelar }) {
  // La tecla Escape cierra la ventana (salvo mientras se está enviando)
  useEffect(() => {
    const alApretarTecla = (evento) => {
      if (evento.key === 'Escape' && !enviando) onCancelar()
    }
    window.addEventListener('keydown', alApretarTecla)
    return () => window.removeEventListener('keydown', alApretarTecla)
  }, [enviando, onCancelar])

  return (
    <div className="velo">
      <section className="dialogo" role="dialog" aria-modal="true" aria-labelledby="titulo-confirmacion">
        <h2 id="titulo-confirmacion" className="dialogo-titulo">
          ¿Enviar la ubicación por mail?
        </h2>
        <p className="dialogo-texto">
          Se le va a enviar un mail al gerente de la comisión médica destino con la última ubicación que informó el
          GPS.
        </p>

        <dl className="dialogo-datos">
          <div>
            <dt>Bolsín</dt>
            <dd>
              {bolsin.numeroBolsin} · precinto {bolsin.numeroPrecinto}
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
          <button className="boton-secundario" onClick={onCancelar} disabled={enviando}>
            Cancelar
          </button>
          <button className="boton-principal" onClick={onConfirmar} disabled={enviando} autoFocus>
            {enviando ? (
              <>
                <span className="spinner" aria-hidden="true" />
                Enviando…
              </>
            ) : (
              'Confirmar envío'
            )}
          </button>
        </div>
      </section>
    </div>
  )
}
