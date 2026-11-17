// Punto de arranque del frontend.
// Monta la Aplicacion dentro del <div id="root"> de index.html.
// La Aplicacion arranca en la pantalla de inicio de sesión.
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
// Letra Barlow (descargada con npm, así funciona aunque no haya internet)
import '@fontsource/barlow/400.css'
import '@fontsource/barlow/500.css'
import '@fontsource/barlow/600.css'
import '@fontsource/barlow-condensed/600.css'
import '@fontsource/barlow-condensed/700.css'
import './estilos/index.css'
import Aplicacion from './boundary/Aplicacion.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Aplicacion />
  </StrictMode>,
)
