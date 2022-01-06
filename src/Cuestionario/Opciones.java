package Cuestionario;

public class Opciones {
    private String opciones;
    private boolean validar_respuesta;

    public Opciones(String opciones, boolean validar_respuesta) {
        this.opciones = opciones;
        this.validar_respuesta = validar_respuesta;
    }

    public String getOpciones() {
        return opciones;
    }

    public void setOpciones(String opciones) {
        this.opciones = opciones;
    }

    public boolean isValidar_opciones() {
        return validar_respuesta;
    }

    public void setValidar_opciones(boolean validar_respuesta) {
        this.validar_respuesta = validar_respuesta;
    }
}
