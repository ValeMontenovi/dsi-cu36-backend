package ar.edu.utn.dsi.bolsines.adapter;

public class GpsException extends RuntimeException {

    public GpsException(String msg) {
        super(msg);
    }

    public GpsException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
