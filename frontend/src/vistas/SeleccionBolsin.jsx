// =====================================================================
// VISTA: SeleccionBolsin
// ---------------------------------------------------------------------
// Parte de solicitarSelBolsinTipo(): barra que acompaña a la lista
// mientras el actor elige un bolsín.
//   "Continuar" → tomarSeleccionBolsin() en la pantalla
//   "Cancelar"  → flujo alternativo A4: termina el caso de uso
// Cuando termina el caso de uso (FinCU) muestra cómo terminó: con el
// mail enviado o sin enviarlo (A5).
// Solo dibuja: no tiene lógica del caso de uso.
// =====================================================================

function IconoListo() {
  return (
    <svg className="panel-icono" viewBox="0 0 24 24" aria-hidden="true">
      <circle cx="12" cy="12" r="12" fill="currentColor" opacity="0.15" />
      <path
        d="M7 12.5l3.2 3.2L17 9"
        fill="none"
        stroke="currentColor"
        strokeWidth="2.4"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}

export default function SeleccionBolsin({
  bolsines,
  numeroElegido,
  errorNotificacion,
  finCasoDeUso,
  onContinuar,
  onCancelar,
  onVolverAlMenu,
}) {
  // Fin del caso de uso (FinCU): con mail enviado o sin enviarlo (A5)
  if (finCasoDeUso) {
    const { bolsin, mailEnviado } = finCasoDeUso
    return (
      <section className={mailEnviado ? 'panel-seleccion panel-enviado' : 'panel-seleccion panel-sin-envio'} role="status">
        <IconoListo />
        <div className="panel-texto">
          <strong>{mailEnviado ? 'Ubicación enviada' : 'Consulta terminada sin enviar el mail'}</strong>
          <p>
            {mailEnviado
              ? `Se le envió un mail al gerente de la CM ${bolsin.cmDestino} con la ubicación del bolsín ${bolsin.numeroBolsin}.`
              : `Elegiste no notificar la ubicación del bolsín ${bolsin.numeroBolsin}. No se envió ningún mail.`}
          </p>
        </div>
        <button className="boton-secundario" onClick={onVolverAlMenu}>
          Volver al menú principal
        </button>
      </section>
    )
  }

  const elegido = bolsines.find((bolsin) => bolsin.numeroBolsin === numeroElegido)

  return (
    <section className="panel-seleccion" aria-label="Selección del bolsín">
      <div className="panel-texto">
        <strong>
          {elegido
            ? `Bolsín ${elegido.numeroBolsin}: ${elegido.cmOrigen} → ${elegido.cmDestino}`
            : 'Elegí un bolsín de la lista o del mapa'}
        </strong>
        <p>
          {elegido
            ? 'Continuá para decidir si notificás su ubicación por mail al gerente de la CM destino.'
            : 'Solo se pueden elegir los bolsines que tienen ubicación del GPS.'}
        </p>

        {/* Flujo alternativo: el backend no pudo enviar el mail */}
        {errorNotificacion && (
          <p className="panel-error" role="alert">
            <strong>No se pudo enviar el mail.</strong> {errorNotificacion}
          </p>
        )}
      </div>

      <div className="panel-botones">
        <button className="boton-secundario" onClick={onCancelar}>
          Cancelar
        </button>
        <button className="boton-principal" onClick={onContinuar} disabled={!elegido}>
          Continuar
        </button>
      </div>
    </section>
  )
}
