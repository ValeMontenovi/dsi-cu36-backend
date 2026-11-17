package ar.edu.utn.dsi.bolsines.adapter;

public interface IAdaptadorGPSTracker {
    String[][] recibirDatosLocalizacion(
        int[] nroBolsines,
        String codigoCMOrigen,
        String[] codigosCMDestino
    );
}
