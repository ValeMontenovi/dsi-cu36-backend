// =====================================================================
// VISTA: IconosMenu
// ---------------------------------------------------------------------
// Un dibujo (SVG) para cada opción del menú principal. El nombre del
// ícono sale de opcionesDelSistema.js. Solo decoración.
// =====================================================================

const TRAZOS = {
  // bolsín con un "+"
  generar: (
    <>
      <path d="M8 8.5c0-2.2 1.8-4 4-4s4 1.8 4 4" />
      <path d="M5 8.5h14l-1.4 11a1.6 1.6 0 0 1-1.6 1.4H8a1.6 1.6 0 0 1-1.6-1.4z" />
      <path d="M12 12v5M9.5 14.5h5" />
    </>
  ),
  // lápiz
  modificar: (
    <>
      <path d="M4 20h4L19 9l-4-4L4 16z" />
      <path d="M13.5 6.5l4 4" />
    </>
  ),
  // tacho
  eliminar: (
    <>
      <path d="M4 7h16M9.5 7V4.5h5V7" />
      <path d="M6 7l1 13h10l1-13" />
      <path d="M10 11v5M14 11v5" />
    </>
  ),
  // lupa
  consultar: (
    <>
      <circle cx="10.5" cy="10.5" r="6" />
      <path d="M15 15l5 5" />
    </>
  ),
  // precinto (candado numerado)
  precinto: (
    <>
      <rect x="5" y="10.5" width="14" height="10" rx="2" />
      <path d="M8 10.5V8a4 4 0 0 1 8 0v2.5" />
      <path d="M12 14.5v2.5" />
    </>
  ),
  // candado abierto
  reabrir: (
    <>
      <rect x="5" y="10.5" width="14" height="10" rx="2" />
      <path d="M8 10.5V8a4 4 0 0 1 7.6-1.8" />
      <path d="M12 14.5v2.5" />
    </>
  ),
  // camión del correo
  camion: (
    <>
      <path d="M3 6.5h11v10H3zM14 10h4l3 3.5v3h-7" />
      <circle cx="7" cy="18" r="1.8" />
      <circle cx="17.5" cy="18" r="1.8" />
    </>
  ),
  // bandeja de entrada
  recepcion: (
    <>
      <path d="M4 13.5l2.5-8h11l2.5 8v6H4z" />
      <path d="M4 13.5h4.5l1 2h5l1-2H20" />
    </>
  ),
  // remito (hoja con renglones)
  remito: (
    <>
      <path d="M6 3.5h9l3 3v14H6z" />
      <path d="M9 10h6M9 13.5h6M9 17h4" />
    </>
  ),
  // reporte (hoja con gráfico)
  reporte: (
    <>
      <path d="M5 3.5h14v17H5z" />
      <path d="M9 16.5v-4M12 16.5v-7M15 16.5v-2.5" />
    </>
  ),
  // estadística (torta)
  estadistica: (
    <>
      <path d="M12 4a8 8 0 1 0 8 8h-8z" />
      <path d="M14.5 2.5v7h7a7 7 0 0 0-7-7z" />
    </>
  ),
  // persona
  usuario: (
    <>
      <circle cx="12" cy="8" r="4" />
      <path d="M4.5 20.5c1-3.8 4-5.5 7.5-5.5s6.5 1.7 7.5 5.5" />
    </>
  ),
  // puerta con flecha
  salir: (
    <>
      <path d="M10 4H5v16h5" />
      <path d="M14 8l4 4-4 4M18 12H9" />
    </>
  ),
  // mapa con pin
  mapa: (
    <>
      <path d="M3.5 6.5l5-2 7 2 5-2v13l-5 2-7-2-5 2z" />
      <path d="M8.5 4.5v13M15.5 6.5v13" />
    </>
  ),
}

export default function IconoOpcion({ nombre }) {
  return (
    <svg className="icono-opcion" viewBox="0 0 24 24" aria-hidden="true">
      <g fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        {TRAZOS[nombre] ?? TRAZOS.consultar}
      </g>
    </svg>
  )
}
