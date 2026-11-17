// =====================================================================
// VISTA: CicloDeVidaBolsin
// ---------------------------------------------------------------------
// Gráfico chico del menú principal con los estados por los que pasa
// un bolsín (reglas de negocio de la ERS). Resalta "Enviado", que es
// el estado de los bolsines que muestra el seguimiento. Solo dibuja.
// =====================================================================

const ESTADOS = ['Creado', 'Cerrado', 'Enviado', 'Recibido']
const ESTADO_DEL_SEGUIMIENTO = 'Enviado'

export default function CicloDeVidaBolsin() {
  return (
    <section className="ciclo" aria-labelledby="titulo-ciclo">
      <h2 id="titulo-ciclo" className="ciclo-titulo">
        Estados de un bolsín
      </h2>

      <ol className="ciclo-estados">
        {ESTADOS.map((estado) => (
          <li key={estado} className={estado === ESTADO_DEL_SEGUIMIENTO ? 'ciclo-estado ciclo-seguimiento' : 'ciclo-estado'}>
            {estado}
          </li>
        ))}
      </ol>
    </section>
  )
}
