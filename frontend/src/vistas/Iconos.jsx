// =====================================================================
// VISTA: Iconos
// ---------------------------------------------------------------------
// Dibujos en SVG que usa la pantalla. Son solo decoración:
// no tienen lógica del caso de uso.
// =====================================================================

// Logo: un bolsín azul cerrado con su precinto amarillo
export function IconoBolsin() {
  return (
    <svg className="icono-bolsin" viewBox="0 0 40 40" aria-hidden="true">
      <rect x="1" y="1" width="38" height="38" rx="9" fill="#f2b705" />
      <path d="M15 15c0-3 2.2-5 5-5s5 2 5 5" fill="none" stroke="#163a70" strokeWidth="2.4" strokeLinecap="round" />
      <path d="M11 15h18l-2 15.5a2 2 0 0 1-2 1.8H15a2 2 0 0 1-2-1.8z" fill="#163a70" />
      <circle cx="20" cy="22" r="2.4" fill="#f2b705" />
    </svg>
  )
}

