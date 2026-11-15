package ar.edu.utn.dsi.bolsines.adapter;

public interface IGpsTracker {
    String[][] recibirDatosLocalizacion(int[] nroBolsines, String codigoCMOrigen, String[] codigosCMDestino);
}
