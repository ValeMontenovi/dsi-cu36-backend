// =====================================================================
// VISTA: SeleccionBolsin
// ---------------------------------------------------------------------
// Parte de solicitarSelBolsinTipo(): barra que acompaña a la lista
// mientras el actor elige un bolsín. Con "Continuar" se dispara
// tomarSeleccionBolsin() en la pantalla.
// Cuando termina el caso de uso (FinCU) muestra que el mail se envió.
// Solo dibuja: no tiene lógica del caso de uso.
// =====================================================================

export default function SeleccionBolsin({ bolsines, numeroElegido, errorNotificacion, notificacionEnviada, onContinuar }) {
  // Fin del caso de uso: el backend confirmó el envío (204)
  if (notificacionEnviada) {
    return (
      <section className="panel-seleccion panel-enviado" role="status">
        <svg className="panel-icono" viewBox="0 0 24 24" aria-hidden="true">
          <circle cx="12" cy="12" r="12" fill="currentColor" opacity="0.15" />
          <path d="M7 12.5l3.2 3.2L17 9" fill="none" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
        <div className="panel-texto">
          <strong>Ubicación enviada</strong>
          <p>
            Se notificó la ubicación del bolsín {notificacionEnviada.numeroBolsin} al gerente de la CM{' '}
            {notificacionEnviada.cmDestino}.
          </p>
        </div>
      </section>
    )
  }

  const elegido = bolsines.find((bolsin) => bolsin.numeroBolsin === numeroElegido)

  return (
    <section className="panel-seleccion" aria-label="Selección del bolsín">
      <div className="panel-texto">
        <strong>
          {elegido
            ? `Bolsín ${elegido.numeroBolsin} · ${elegido.cmOrigen} → ${elegido.cmDestino}`
            : 'Elegí un bolsín de la lista'}
        </strong>
        <p>
          {elegido
            ? 'Continuá para notificar su ubicación por mail al gerente de la CM destino.'
            : 'Marcá el bolsín cuya ubicación querés notificar por mail al gerente de la CM destino.'}
        </p>

        {/* Flujo alternativo: el backend no pudo enviar el mail */}
        {errorNotificacion && (
          <p className="panel-error" role="alert">
            <strong>No se pudo enviar el mail.</strong> {errorNotificacion}
          </p>
        )}
      </div>

      <button className="boton-principal" onClick={onContinuar} disabled={!elegido}>
        Continuar
      </button>
    </section>
  )
}
