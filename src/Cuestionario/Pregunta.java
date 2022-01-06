package Cuestionario;

public class Pregunta {
    private String Categoria;
    private String Enunciado;
    
    public Pregunta(String Categoria, String Enunciado) {
        this.Categoria = Categoria;
        this.Enunciado = Enunciado;
    }

    public String getEnunciado() {
        return Enunciado;
    }

    public void setEnunciado(String Enunciado) {
        this.Enunciado = Enunciado;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String Categoria) {
        this.Categoria = Categoria;
    }
}
