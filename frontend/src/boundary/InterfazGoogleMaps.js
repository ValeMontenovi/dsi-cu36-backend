// =====================================================================
// BOUNDARY: InterfazGoogleMaps
// ---------------------------------------------------------------------
// Es la frontera entre nuestro sistema y el servicio externo de mapas
// (en el diagrama de secuencia, el actor "Google Maps").
//
// ACLARACIÓN: para no necesitar una clave paga de Google, el mapa se
// dibuja con OpenStreetMap (gratis) usando la librería Leaflet.
// El nombre de la clase y de sus métodos se mantiene igual al diagrama.
// =====================================================================

// Servidor de donde se bajan las imágenes del mapa
const SERVIDOR_DE_MAPAS = 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
// Créditos que exige la licencia de OpenStreetMap
const CREDITOS_DEL_MAPA = '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
// Centro aproximado de Argentina: [latitud, longitud]
const CENTRO_ARGENTINA = [-34.6, -64.0]
// Zoom 5 = se ve todo el país (0 = el mundo entero, 18 = nivel calle)
const ZOOM_PAIS = 5

export default class InterfazGoogleMaps {
  // Diagrama: GestorSeguimientoBolsin → InterfazGoogleMaps : recibirUbicacionBolsin()
  // Recibe los bolsines y se queda SOLO con los que el GPS pudo ubicar
  // (nunca se inventan coordenadas).
  recibirUbicacionBolsin(bolsines) {
    const marcadores = bolsines
      .filter((bolsin) => bolsin.ubicacion) // descarta los que vinieron sin ubicación
      .map((bolsin) => ({
        numeroBolsin: bolsin.numeroBolsin,
        posicion: [bolsin.ubicacion.latitud, bolsin.ubicacion.longitud],
        recorrido: `${bolsin.cmOrigen} → ${bolsin.cmDestino}`,
        fechaHoraActualizacion: bolsin.ubicacion.fechaHoraActualizacion,
      }))

    return this.mostrarMaps(marcadores)
  }

  // Diagrama: InterfazGoogleMaps → Google Maps : mostrarMaps()
  // Arma todo lo que el servicio de mapas necesita para dibujar:
  // de qué servidor bajar las imágenes, dónde centrar y qué marcadores poner.
  mostrarMaps(marcadores) {
    return {
      servidor: SERVIDOR_DE_MAPAS,
      creditos: CREDITOS_DEL_MAPA,
      centro: CENTRO_ARGENTINA,
      zoom: ZOOM_PAIS,
      marcadores,
    }
  }
}
