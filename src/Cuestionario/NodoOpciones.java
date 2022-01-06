package Cuestionario;

public class NodoOpciones {
    private Opciones opciones;
    private NodoOpciones abajo;

    public NodoOpciones() {
    }

    public NodoOpciones(Opciones Opciones) {
        this.opciones = Opciones;
        this.abajo = null;
    }

    public Opciones getOpciones() {
        return opciones;
    }

    public void setOpciones(Opciones Opciones) {
        this.opciones = Opciones;
    }       

    public NodoOpciones getAbajo() {
        return abajo;
    }

    public void setAbajo(NodoOpciones abajo) {
        this.abajo = abajo;
    }
}
