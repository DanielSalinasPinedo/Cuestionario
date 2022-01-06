/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cuestionario;

import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author SF
 */
public class Main extends javax.swing.JFrame {

    /**
     * Creates new form Main
     */
    ListaPregunta listaPregunta = new ListaPregunta();
    String opcion_selec;
    Ronda ronda = new Ronda();
    private Timer t;
    private int seg;
    Jugador jugador = new Jugador();
    int cont = 1;
    public static DefaultTableModel tabla;
    Timer timer;
    public Main() {
        initComponents();
        setLocationRelativeTo(null);
        t = new Timer(1000, acciones);
        OcultarUI();        
    }
    
    private void Persistir(){
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/db_puntaje", "root","");
            PreparedStatement pst = con.prepareStatement("insert into jugadores values(?,?,?)");            
            pst.setString(1, "0");
            pst.setString(2, jugador.getNombre());
            pst.setNString(3, Integer.toString(jugador.getPuntos()));
            pst.executeUpdate();    
        } catch (Exception e) {
            System.out.println("Error "+e);
        }
    }   
        
    private String aleatorio(){
        String categorias[] = {"Matematicas", "Programacion", "Ciencias Sociales", "Español", "Fisica"};
        
        int randon = (int)(Math.random()*5);
        return categorias[randon];
    }

    private void Preguntas(){
        try {
            String archivo = "preguntas_n"+ronda.getNivel()+".txt";
            File f = new File(archivo);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String linea;
            String categoria = aleatorio();
            while((linea = br.readLine()) != null){
                if(linea.equals(categoria)){
                    categoria = linea;
                    linea = br.readLine();
                    listaPregunta.agregarPregunta(categoria, linea);
                    for (int i = 0; i < 4; i++) {
                        linea = br.readLine();
                        if(linea.contains("V-")){
                            String[] correcta = linea.split("-");
                            listaPregunta.agregarOpcion(categoria, correcta[1], true);
                        }else{
                            listaPregunta.agregarOpcion(categoria, linea, false);
                        }
                    }
                }
            }
        }catch(IOException e){
            System.out.println("Error "+e);
        }
    }
    
    private void MostrarUI(){
        jPRespuestas.setVisible(true);
        btnIniciar.setVisible(false);
        lblPregunta.setVisible(true);
        lblNombreCategoria.setVisible(true);
        lblCategoria.setVisible(true);
        lblnameRonda.setVisible(true);
        lblRonda.setVisible(true);
        lblTiempo.setVisible(true);
        lblRonda.setText(Integer.toString(ronda.getNivel()));
        btnSiguiente.setVisible(true);
        btnAbandonarPartida.setVisible(true);
        Opciones.clearSelection();
        try {       
            NodoOpciones nodoOpciones = listaPregunta.getInicio().getAbajo();
            
            Main.lblPregunta.setText(listaPregunta.getInicio().getPregunta().getEnunciado());
            Main.lblNombreCategoria.setText(listaPregunta.getInicio().getPregunta().getCategoria());
            Main.RbRespuesta1.setText(nodoOpciones.getOpciones().getOpciones());
            Main.RbRespuesta2.setText(nodoOpciones.getAbajo().getOpciones().getOpciones());
            Main.RbRespuesta3.setText(nodoOpciones.getAbajo().getAbajo().getOpciones().getOpciones());
            Main.RbRespuesta4.setText(nodoOpciones.getAbajo().getAbajo().getAbajo().getOpciones().getOpciones());              
        } catch (Exception e) {
            System.out.println("Error "+e);
        }
    }
    
    private void OcultarUI(){
        jPRespuestas.setVisible(false);
        btnIniciar.setVisible(true);
        lblPregunta.setVisible(false);
        lblNombreCategoria.setVisible(false);
        lblCategoria.setVisible(false);
        lblnameRonda.setVisible(false);
        lblRonda.setVisible(false);
        lblTiempo.setVisible(false);
        btnSiguiente.setVisible(false);
        btnAbandonarPartida.setVisible(false);  
        Opciones.clearSelection();
        t.stop();
    }
    
    private void Retiro(){
        if(JOptionPane.showConfirmDialog(this, "¿Estas seguro que quieres hacer esto?", "Confirmar Retirada", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == 0){
            t.stop();
            lblPuntos.setText("0");
            JOptionPane.showMessageDialog(null, "¡Te has retirado!\n Intenta completarlo en la proxima.");
            if(jugador.getPuntos() > 0){
                Persistir();
            }
            OcultarUI();
        }
    }
    
    private void Tiempo_Acabado(){
        lblPuntos.setText("0");
        JOptionPane.showMessageDialog(null, "¡Se ha acabado el tiempo!\n Intenta completarlo en la proxima.");
        OcultarUI();
        if(jugador.getPuntos() > 0){
            Persistir();
        }
    }
    
    private void Ganaste(){
        if(ronda.getNivel() < 5){
            nivel(ronda.getNivel()+1, ronda.getTiempo_limite()-5, ronda.getPuntos_Partida()*ronda.getNivel());
            t.stop();
            JOptionPane.showMessageDialog(this, "Respuesta correcta!");
            t.start();
            lblPuntos.setText(Integer.toString(ronda.getPuntos_Partida()));
            jugador.setPuntos(ronda.getPuntos_Partida());
            Preguntas();
            MostrarUI();
        }else{
            t.stop();
            jugador.setPuntos(ronda.getPuntos_Partida()*ronda.getNivel());
            lblPuntos.setText(Integer.toString(jugador.getPuntos()));
            JOptionPane.showMessageDialog(this, "Has completado todos los niveles, ¡Felicidades!");
            lblPuntos.setText("0");            
            Persistir();
            OcultarUI();
        }
    }
    
    private void Perdiste(){
        t.stop();
        JOptionPane.showConfirmDialog(this, "HAS PERDIDO", "Mala eleccion", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        ronda.setPuntos_Partida(0);
        lblPuntos.setText("0");
        OcultarUI();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Opciones = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        btnPuntaje = new javax.swing.JButton();
        lblPuntaje = new javax.swing.JLabel();
        lblPregunta = new javax.swing.JLabel();
        jPRespuestas = new javax.swing.JPanel();
        RbRespuesta1 = new javax.swing.JRadioButton();
        RbRespuesta4 = new javax.swing.JRadioButton();
        RbRespuesta2 = new javax.swing.JRadioButton();
        RbRespuesta3 = new javax.swing.JRadioButton();
        btnIniciar = new javax.swing.JButton();
        lblTiempo = new javax.swing.JLabel();
        lblPuntos = new javax.swing.JLabel();
        lblCategoria = new javax.swing.JLabel();
        lblNombreCategoria = new javax.swing.JLabel();
        btnAbandonarPartida = new javax.swing.JButton();
        btnSiguiente = new javax.swing.JButton();
        lblnameRonda = new javax.swing.JLabel();
        lblRonda = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setText("CUESTIONARIO");

        btnPuntaje.setText("Puntajes");
        btnPuntaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPuntajeActionPerformed(evt);
            }
        });

        lblPuntaje.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblPuntaje.setText("Puntaje");

        lblPregunta.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblPregunta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPregunta.setText("PREGUNTA");
        lblPregunta.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jPRespuestas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        Opciones.add(RbRespuesta1);
        RbRespuesta1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        RbRespuesta1.setText("Respuesta 1");
        RbRespuesta1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RbRespuesta1ActionPerformed(evt);
            }
        });

        Opciones.add(RbRespuesta4);
        RbRespuesta4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        RbRespuesta4.setText("Respuesta 4");
        RbRespuesta4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RbRespuesta4ActionPerformed(evt);
            }
        });

        Opciones.add(RbRespuesta2);
        RbRespuesta2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        RbRespuesta2.setText("Respuesta 2");
        RbRespuesta2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RbRespuesta2ActionPerformed(evt);
            }
        });

        Opciones.add(RbRespuesta3);
        RbRespuesta3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        RbRespuesta3.setText("Respuesta 3");
        RbRespuesta3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RbRespuesta3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPRespuestasLayout = new javax.swing.GroupLayout(jPRespuestas);
        jPRespuestas.setLayout(jPRespuestasLayout);
        jPRespuestasLayout.setHorizontalGroup(
            jPRespuestasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPRespuestasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPRespuestasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(RbRespuesta1, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RbRespuesta2, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RbRespuesta3, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RbRespuesta4, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPRespuestasLayout.setVerticalGroup(
            jPRespuestasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPRespuestasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RbRespuesta1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(RbRespuesta2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(RbRespuesta3, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(RbRespuesta4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        btnIniciar.setFont(new java.awt.Font("Tahoma", 3, 24)); // NOI18N
        btnIniciar.setText("Iniciar");
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });

        lblTiempo.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lblTiempo.setText("0");

        lblPuntos.setFont(new java.awt.Font("Tahoma", 1, 22)); // NOI18N
        lblPuntos.setText("0");

        lblCategoria.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblCategoria.setText("Categoria");

        lblNombreCategoria.setFont(new java.awt.Font("Tahoma", 1, 22)); // NOI18N
        lblNombreCategoria.setText("Null");

        btnAbandonarPartida.setText("Abandonar Partida");
        btnAbandonarPartida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbandonarPartidaActionPerformed(evt);
            }
        });

        btnSiguiente.setText("Siguiente");
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });

        lblnameRonda.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        lblnameRonda.setText("RONDA ");

        lblRonda.setFont(new java.awt.Font("Tahoma", 1, 22)); // NOI18N
        lblRonda.setText("0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPuntaje, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(lblPuntos)))
                .addGap(73, 73, 73)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPuntaje, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(lblTiempo))))
            .addGroup(layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPRespuestas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(btnIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCategoria)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(lblNombreCategoria)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(168, 168, 168)
                                .addComponent(lblnameRonda))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(196, 196, 196)
                                .addComponent(lblRonda)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSiguiente)
                            .addComponent(btnAbandonarPartida, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, 742, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPuntaje)
                        .addGap(6, 6, 6)
                        .addComponent(lblTiempo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPuntaje)
                                .addGap(1, 1, 1)
                                .addComponent(lblPuntos)))))
                .addGap(16, 16, 16)
                .addComponent(lblPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPRespuestas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(btnIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(btnSiguiente)
                        .addGap(11, 11, 11)
                        .addComponent(btnAbandonarPartida, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCategoria)
                            .addComponent(lblnameRonda))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNombreCategoria)
                            .addComponent(lblRonda))))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
       
    
    private ActionListener acciones = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            tiempo(seg);
            if(seg == 0){
                Tiempo_Acabado();
                t.stop();
            }            
            seg--;
        }
    };
    
    private void tiempo(int tiempo){
        lblTiempo.setText(Integer.toString(tiempo));
    }
    
    private void nivel(int nivel, int tiempo, int puntos){
        ronda.setNivel(nivel);
        ronda.setTiempo_limite(tiempo);
        ronda.setPuntos_Partida(puntos);
        seg = ronda.getTiempo_limite();       
        t.start(); 
    }
    
    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
        try {
            String user = JOptionPane.showInputDialog("Escriba su nombre por favor");
            if(!user.trim().equals("")){
                jugador.setNombre(user);
                nivel(1, 45, 5);
                Preguntas();
                MostrarUI();
            }else{
                JOptionPane.showMessageDialog(this, "Por favor ingrese un nombre de usuario");
            } 
        } catch (Exception e) {
        }
    
    }//GEN-LAST:event_btnIniciarActionPerformed

    private void btnPuntajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPuntajeActionPerformed
        Puntaje puntaje = new Puntaje();
        puntaje.setVisible(true);
        puntaje.Mostrar();
    }//GEN-LAST:event_btnPuntajeActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        if(JOptionPane.showConfirmDialog(this, "¿Estas seguro de tu respuesta?", "Confirmar respuesta", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == 0){
            if(listaPregunta.validar_respuesta(opcion_selec)){
                Ganaste();         
            }else{
                Perdiste();
            }
        }
    }//GEN-LAST:event_btnSiguienteActionPerformed

    private void RbRespuesta1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RbRespuesta1ActionPerformed
        opcion_selec = RbRespuesta1.getText();
    }//GEN-LAST:event_RbRespuesta1ActionPerformed

    private void RbRespuesta2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RbRespuesta2ActionPerformed
        opcion_selec = RbRespuesta2.getText();
    }//GEN-LAST:event_RbRespuesta2ActionPerformed

    private void RbRespuesta3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RbRespuesta3ActionPerformed
        opcion_selec = RbRespuesta3.getText();
    }//GEN-LAST:event_RbRespuesta3ActionPerformed

    private void RbRespuesta4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RbRespuesta4ActionPerformed
        opcion_selec = RbRespuesta4.getText();
    }//GEN-LAST:event_RbRespuesta4ActionPerformed

    private void btnAbandonarPartidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbandonarPartidaActionPerformed
        Retiro();
    }//GEN-LAST:event_btnAbandonarPartidaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.ButtonGroup Opciones;
    public static javax.swing.JRadioButton RbRespuesta1;
    public static javax.swing.JRadioButton RbRespuesta2;
    public static javax.swing.JRadioButton RbRespuesta3;
    public static javax.swing.JRadioButton RbRespuesta4;
    private javax.swing.JButton btnAbandonarPartida;
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnPuntaje;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPRespuestas;
    private javax.swing.JLabel lblCategoria;
    public static javax.swing.JLabel lblNombreCategoria;
    public static javax.swing.JLabel lblPregunta;
    private javax.swing.JLabel lblPuntaje;
    private javax.swing.JLabel lblPuntos;
    private javax.swing.JLabel lblRonda;
    private javax.swing.JLabel lblTiempo;
    private javax.swing.JLabel lblnameRonda;
    // End of variables declaration//GEN-END:variables
}
