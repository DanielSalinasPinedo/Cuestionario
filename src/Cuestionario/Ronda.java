package Cuestionario;

public class Ronda {
    private int nivel;
    private int tiempo_limite;
    private int puntos_partida;

    public Ronda() {
    }

    public Ronda(int nivel, int tiempo_limite, int puntos) {
        this.nivel = nivel;
        this.tiempo_limite = tiempo_limite;
        this.puntos_partida = puntos;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getTiempo_limite() {
        return tiempo_limite;
    }

    public void setTiempo_limite(int tiempo_limite) {
        this.tiempo_limite = tiempo_limite;
    }

    public int getPuntos_Partida() {
        return puntos_partida;
    }

    public void setPuntos_Partida(int puntos) {
        this.puntos_partida = puntos;
    }
}
