package Cuestionario;

public class ListaPregunta {
    private NodoPregunta inicio;
        
    public ListaPregunta() {
        this.inicio = null;
    }
          
    public void agregarPregunta(String Categoria, String Enunciado){
        try {
            Pregunta pregunta = new Pregunta(Categoria, Enunciado);
            NodoPregunta nodoPregunta = new NodoPregunta(pregunta);
            inicio = nodoPregunta;
        } catch (Exception e) {
            System.out.println("Error agregando la pregunta "+e);
        }
    }
    
    public boolean validar_respuesta(String elegida){
        NodoOpciones nodoOpciones = inicio.getAbajo();
        
        if(inicio.getAbajo() != null){
            if(inicio.getAbajo().getOpciones().getOpciones().equals(elegida)){
                return inicio.getAbajo().getOpciones().isValidar_opciones();
            }
            while(nodoOpciones.getAbajo() != null){
                if(nodoOpciones.getAbajo().getOpciones().getOpciones().equals(elegida)){
                    return nodoOpciones.getAbajo().getOpciones().isValidar_opciones();
                }
                nodoOpciones = nodoOpciones.getAbajo();
            }                               
        }
        return false;
    }
    
    public void agregarOpcion(String categoria, String opcion, boolean validar){
        try {
            Opciones res = new Opciones(opcion, validar);
            NodoOpciones nodoOpciones = new NodoOpciones(res);
            
            if(inicio == null){
                System.out.println("No existe ninguna pregunta");
            }else{
                if(inicio.getAbajo() == null){
                    inicio.setAbajo(nodoOpciones);
                }else{
                    NodoOpciones nr = inicio.getAbajo();
                    while(nr.getAbajo() != null){
                        nr=nr.getAbajo();
                    }
                    nr.setAbajo(nodoOpciones);
                }
            }
        } catch (Exception e) {
            System.out.println("Error "+e);
        }
    }

    public NodoPregunta getInicio() {
        return inicio;
    }

    public void setInicio(NodoPregunta inicio) {
        this.inicio = inicio;
    }
}
