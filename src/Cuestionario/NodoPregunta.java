package Cuestionario;

public class NodoPregunta {
    private Pregunta pregunta;
    private NodoOpciones abajo;

    public NodoPregunta() {
    }    
    
    public NodoPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
        this.abajo = null;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    public NodoOpciones getAbajo() {
        return abajo;
    }

    public void setAbajo(NodoOpciones abajo) {
        this.abajo = abajo;
    }
}
