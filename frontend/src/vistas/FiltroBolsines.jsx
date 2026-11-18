// =====================================================================
// VISTA: FiltroBolsines
// ---------------------------------------------------------------------
// Paso 6 de la ERS: además de elegir del mapa, el actor puede filtrar
// por número de precinto o por CM destino. Si el precinto no coincide
// con ningún bolsín, la pantalla muestra el flujo alternativo A2.
// Solo dibuja: no tiene lógica del caso de uso.
// =====================================================================

export default function FiltroBolsines({
  filtroPrecinto,
  filtroDestino,
  destinos,
  cantidadVisible,
  cantidadTotal,
  onCambiarPrecinto,
  onCambiarDestino,
  onLimpiar,
}) {
  const hayFiltro = Boolean(filtroPrecinto.trim() || filtroDestino)

  return (
    <div className="filtros" role="search">
      <label className="filtro">
        <span>Número de precinto</span>
        <input
          type="search"
          placeholder="Ej.: P-101"
          value={filtroPrecinto}
          onChange={(evento) => onCambiarPrecinto(evento.target.value)}
          aria-invalid={hayFiltro && cantidadVisible === 0}
        />
      </label>

      <label className="filtro">
        <span>CM destino</span>
        <select value={filtroDestino} onChange={(evento) => onCambiarDestino(evento.target.value)}>
          <option value="">Todas</option>
          {destinos.map((destino) => (
            <option key={destino} value={destino}>
              {destino}
            </option>
          ))}
        </select>
      </label>

      {hayFiltro && (
        <p className="filtro-resultado">
          Mostrando {cantidadVisible} de {cantidadTotal}
          <button className="boton-enlace" onClick={onLimpiar}>
            Limpiar
          </button>
        </p>
      )}
    </div>
  )
}
