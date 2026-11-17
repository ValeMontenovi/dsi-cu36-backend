// =====================================================================
// VISTA: DibujoRedComisiones
// ---------------------------------------------------------------------
// Dibujo de la pantalla de inicio de sesión: las comisiones médicas
// del perfil demo, ubicadas más o menos como en el mapa (oeste a la
// izquierda, norte arriba), con los recorridos de los bolsines de prueba
// y un bolsín que viaja de Córdoba a Rosario. Solo decoración.
// =====================================================================

// Posiciones calculadas a partir de la longitud y latitud de cada ciudad
const CIUDADES = [
  { nombre: 'Mendoza', x: 46, y: 132, alineacion: 'start', dx: -10, dy: -20 },
  { nombre: 'Córdoba', x: 196, y: 54, alineacion: 'middle', dx: 0, dy: -18 },
  { nombre: 'Rosario', x: 302, y: 124, alineacion: 'start', dx: 18, dy: 5 },
  { nombre: 'Buenos Aires', x: 362, y: 196, alineacion: 'end', dx: 8, dy: 40 },
]

// Recorridos de los bolsines de prueba (101 a 104).
// La misma ruta Córdoba → Rosario está copiada en index.css (.bolsin-viajero).
const RUTA_CORDOBA_ROSARIO = 'M196 54 C 240 58, 280 84, 302 124'
const RUTAS = [
  RUTA_CORDOBA_ROSARIO,
  'M302 124 C 230 170, 110 176, 46 132', // Rosario → Mendoza
  'M46 132 C 120 230, 290 238, 362 196', // Mendoza → Buenos Aires
  'M362 196 C 330 120, 270 62, 196 54', // Buenos Aires → Córdoba
]

export default function DibujoRedComisiones() {
  return (
    <svg className="dibujo-red" viewBox="0 0 400 250" aria-hidden="true">
      {/* Cuadrícula muy suave, como las líneas de un mapa */}
      <g stroke="rgb(255 255 255 / 0.08)" strokeWidth="1">
        {[50, 100, 150, 200].map((y) => (
          <line key={`h${y}`} x1="0" x2="400" y1={y} y2={y} />
        ))}
        {[80, 160, 240, 320].map((x) => (
          <line key={`v${x}`} x1={x} x2={x} y1="0" y2="250" />
        ))}
      </g>

      {/* Recorridos de los bolsines */}
      <g fill="none" stroke="rgb(255 255 255 / 0.45)" strokeWidth="2" strokeDasharray="5 7" strokeLinecap="round">
        {RUTAS.map((ruta) => (
          <path key={ruta} d={ruta} />
        ))}
      </g>
      {/* El recorrido que está haciendo el bolsín, resaltado en amarillo */}
      <path className="ruta-activa" d={RUTA_CORDOBA_ROSARIO} fill="none" stroke="#f2b705" strokeWidth="3" strokeLinecap="round" />

      {/* Comisiones médicas */}
      {CIUDADES.map((ciudad) => (
        <g key={ciudad.nombre}>
          <circle cx={ciudad.x} cy={ciudad.y} r="13" fill="rgb(242 183 5 / 0.18)" />
          <circle cx={ciudad.x} cy={ciudad.y} r="6.5" fill="#ffffff" stroke="#f2b705" strokeWidth="3" />
          <text
            x={ciudad.x + ciudad.dx}
            y={ciudad.y + ciudad.dy}
            textAnchor={ciudad.alineacion}
            fill="#ffffff"
            fontSize="14"
            fontWeight="600"
          >
            CM {ciudad.nombre}
          </text>
        </g>
      ))}

      {/* Bolsín viajando (se mueve con CSS por la ruta Córdoba → Rosario) */}
      <g className="bolsin-viajero">
        <rect x="-11" y="-11" width="22" height="22" rx="6" fill="#f2b705" stroke="#0e2850" strokeWidth="2" />
        <path d="M-4 -3c0-2.4 1.8-4 4-4s4 1.6 4 4" fill="none" stroke="#0e2850" strokeWidth="1.8" strokeLinecap="round" />
        <circle cx="0" cy="3" r="2" fill="#0e2850" />
      </g>
    </svg>
  )
}
