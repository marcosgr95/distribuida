/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marcos
 */
//package axentesMarcos.compraVentaLibros;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CompradorGui extends javax.swing.JFrame {

    /**
     * Creates new form CompradorGui
     */
    private Comprador comprador;

    public CompradorGui() {
        initComponents();
        DefaultListModel d= new DefaultListModel();
        DefaultListModel d2= new DefaultListModel();
        this.listaSubastas.setModel(d);
        this.listaDesexos.setModel(d2);
        d.addElement("ESTADO - TítuloLibro - Prezo Máximo - Prezo Actual - Gañador (es)");
        d.addElement( "-------------------------------------------------------------------------------------");
        d2.addElement("Título do libro - Prezo máximo a pagar");
        d2.addElement( "-----------------------------------------------------------------------------");
    }

    public CompradorGui(Comprador _comprador){
        
        comprador=_comprador;
        initComponents();
        DefaultListModel d= new DefaultListModel();
        DefaultListModel d2= new DefaultListModel();
        this.listaSubastas.setModel(d);
        this.listaDesexos.setModel(d2);
        d.addElement("ESTADO - TítuloLibro - Prezo Máximo - Prezo Actual - Gañador (es)");
        d.addElement( "-------------------------------------------------------------------------------------");
        d2.addElement("Título do libro - Prezo máximo a pagar");
        d2.addElement( "-----------------------------------------------------------------------------");
    }

    public void mostrarInterface(){
        pack();
        super.setVisible(true);
    }

    private void engadirNovaSubasta(String _titulo, Integer _prezo){
        DefaultListModel d=  (DefaultListModel) this.listaSubastas.getModel();
        DefaultListModel d2=  (DefaultListModel) this.listaDesexos.getModel();
        String novaEntrada= "BUSCANDO... - " + _titulo + " - " + _prezo + " - " + "*** - ";
        d.addElement(novaEntrada);

        eliminarTitulo(_titulo);

        
        String novoDesexo = " "+_titulo+" - "+_prezo;
        d2.addElement(novoDesexo);
        
    }

    //Función para modificar a lista de subastas 
    public void modificarLista(String estado, String titulo, String prezo, String ganadores){
        DefaultListModel d=  (DefaultListModel) this.listaSubastas.getModel();
        boolean a = false;

        //Recórrense todas as subastas para ver as de este libro:
        for(int i=0; i < d.getSize(); i++){
            String subasta = (String)d.get(i);
            if(estado.equals("GAÑADA")){
            	if(subasta.contains(" - "+titulo+" - ") && subasta.contains("REMATADA") &&  subasta.contains(ganadores)){
            		d.set(i, estado + " - "+titulo+"#F - "+ comprador.getLibros().get(titulo)+" - "+prezo+" - "+ganadores);
                	a = true;
            	}
            }
            else{
	            if(subasta.contains(" - "+titulo+" - ") ){
	            	if(! subasta.contains("REMATADA") ){
	                	d.set(i, estado + " - "+titulo+ " - " + comprador.getLibros().get(titulo)+" - "+prezo+" - "+ganadores);
	                	a = true;
	            	}
	            }
	        }
        }

        if(!a){
        	engadirNovaSubasta(titulo, Integer.parseInt(prezo));
        	modificarLista(estado, titulo, prezo, ganadores);
        }
    }

    public void eliminarTitulo(String titulo){
    	DefaultListModel d=  (DefaultListModel) this.listaDesexos.getModel();

        //Recórrense todas as subastas para ver as de este libro:
        for(int i=0; i < d.getSize(); i++){
        	String s = (String)d.get(i);

        	if(s.contains(" "+titulo+" - ")){
                d.remove(i);
            }
        }
    }


    public void indicarVictoria(String titulo, String prezo){
        JOptionPane.showMessageDialog(this, "Conseguín comprar "+titulo+ " por "+prezo+" euros", "Comprador "+comprador.getAID().getLocalName()
            , JOptionPane.PLAIN_MESSAGE);
    }

    public void indicarNovaSubasta(String titulo, String prezo){
        JOptionPane.showMessageDialog(this, "Vou entrar na subasta de "+titulo+" por "+prezo+" euros!", "Comprador "+comprador.getAID().getLocalName()
            , JOptionPane.PLAIN_MESSAGE);
    }

    public void xaInteresado(String titulo){
    	JOptionPane.showMessageDialog(this, "Xa indicaches que estás interesado en "+titulo, "Comprador "+comprador.getAID().getLocalName()
            , JOptionPane.ERROR_MESSAGE);
    }

    public void stringBaleiro(){
    	JOptionPane.showMessageDialog(this, "Introduce un título válido", "Comprador "+comprador.getAID().getLocalName()
                    		, JOptionPane.ERROR_MESSAGE);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setTitle("Comprador "+comprador.getAID().getLocalName());
        panelBase = new javax.swing.JTabbedPane();
        panelLibros = new javax.swing.JPanel();
        labelTitulo = new javax.swing.JLabel();
        labelTitulo1 = new javax.swing.JLabel();
        titulo = new javax.swing.JTextField();
        prezo = new javax.swing.JSpinner();
        labelTitulo3 = new javax.swing.JLabel();
        botonSubasta = new javax.swing.JButton();
        panelDesexos = new javax.swing.JScrollPane();
        listaDesexos = new javax.swing.JList<>();
        subastas = new javax.swing.JPanel();
        panelSubastas = new javax.swing.JScrollPane();
        listaSubastas = new javax.swing.JList<>();
        //Acción realizada ao premer o botón
        botonSubasta.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
              	if(!titulo.getText().equals("")){
                    
                    if(comprador.engadirLibro(titulo.getText(), (Integer) prezo.getValue())){
                    	engadirNovaSubasta(titulo.getText(), (Integer) prezo.getValue());
                    	titulo.setText("");
                    }
                    else{
                    	xaInteresado(titulo.getText());
                    }
            
                }  
                else{
                	stringBaleiro();
                }


            }
        } );

        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new   WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                comprador.doDelete();
            }
        } );
        setPreferredSize(new java.awt.Dimension(500, 600));
        setResizable(false);

        panelBase.setBackground(new java.awt.Color(255,255,255));
        panelBase.setFont(new java.awt.Font("LM Roman Caps 10", 0, 15)); // NOI18N

        panelLibros.setBackground(new java.awt.Color(200, 25, 3));
        panelLibros.setFont(new java.awt.Font("LM Roman Caps 10", 0, 15)); // NOI18N

        labelTitulo.setFont(new java.awt.Font("LM Roman Caps 10", 0, 15)); // NOI18N
        labelTitulo.setForeground(new java.awt.Color(255, 255, 255));
        labelTitulo.setText("Título");

        labelTitulo1.setFont(new java.awt.Font("LM Roman Caps 10", 0, 15)); // NOI18N
        labelTitulo1.setForeground(new java.awt.Color(255, 255, 255));
        labelTitulo1.setText("Prezo Máximo");

        titulo.setFont(new java.awt.Font("LM Roman Caps 10", 0, 15)); // NOI18N
        titulo.setForeground(new java.awt.Color(200, 25, 3));

        prezo.setFont(new java.awt.Font("LM Roman Caps 10", 0, 15)); // NOI18N
        prezo.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        labelTitulo3.setFont(new java.awt.Font("LM Roman Caps 10", 0, 15)); // NOI18N
        labelTitulo3.setForeground(new java.awt.Color(255, 255, 255));
        labelTitulo3.setText("€");

        botonSubasta.setFont(new java.awt.Font("LM Roman Caps 10", 0, 15)); // NOI18N
        botonSubasta.setForeground(new java.awt.Color(200, 25, 3));
        botonSubasta.setText("Buscar Subastas");
        botonSubasta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSubastaActionPerformed(evt);
            }
        });

        panelDesexos.setForeground(new java.awt.Color(255, 255, 255));

        listaDesexos.setFont(new java.awt.Font("LM Roman Caps 10", 0, 15)); // NOI18N
        listaDesexos.setForeground(new java.awt.Color(200, 25, 3));
        listaDesexos.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Título do libro - Prezo máximo a pagar", "----------------------------------------------------------------" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        panelDesexos.setViewportView(listaDesexos);

        javax.swing.GroupLayout panelLibrosLayout = new javax.swing.GroupLayout(panelLibros);
        panelLibros.setLayout(panelLibrosLayout);
        panelLibrosLayout.setHorizontalGroup(
            panelLibrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLibrosLayout.createSequentialGroup()
                .addGroup(panelLibrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLibrosLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(panelLibrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelTitulo1)
                            .addComponent(labelTitulo)
                            .addComponent(titulo, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelLibrosLayout.createSequentialGroup()
                                .addComponent(prezo, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelTitulo3))
                            .addComponent(panelDesexos, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelLibrosLayout.createSequentialGroup()
                        .addGap(151, 151, 151)
                        .addComponent(botonSubasta, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        panelLibrosLayout.setVerticalGroup(
            panelLibrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLibrosLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(labelTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(titulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(labelTitulo1)
                .addGap(18, 18, 18)
                .addGroup(panelLibrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prezo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTitulo3))
                .addGap(54, 54, 54)
                .addComponent(botonSubasta, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(panelDesexos, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        botonSubasta.getAccessibleContext().setAccessibleDescription("");

        panelBase.addTab("Engadir libros", panelLibros);

        subastas.setBackground(new java.awt.Color(200, 25, 3));
        subastas.setFont(new java.awt.Font("LM Roman Caps 10", 0, 15)); // NOI18N

        panelSubastas.setForeground(new java.awt.Color(255, 255, 255));

        listaSubastas.setFont(new java.awt.Font("LM Roman Caps 10", 0, 15)); // NOI18N
        listaSubastas.setForeground(new java.awt.Color(200, 25, 3));
        listaSubastas.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "ESTADO - TítuloLibro - Prezo Actual - Gañador (es)", "-------------------------------------------------------------------------------------" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        panelSubastas.setViewportView(listaSubastas);

        javax.swing.GroupLayout subastasLayout = new javax.swing.GroupLayout(subastas);
        subastas.setLayout(subastasLayout);
        subastasLayout.setHorizontalGroup(
            subastasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subastasLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(panelSubastas, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        subastasLayout.setVerticalGroup(
            subastasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subastasLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(panelSubastas, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelBase.addTab("Ver Subastas", subastas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBase)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBase)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonSubastaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSubastaActionPerformed
     
    }//GEN-LAST:event_botonSubastaActionPerformed

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
            java.util.logging.Logger.getLogger(CompradorGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CompradorGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CompradorGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CompradorGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CompradorGui().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonSubasta;
    private javax.swing.JLabel labelTitulo;
    private javax.swing.JLabel labelTitulo1;
    private javax.swing.JLabel labelTitulo3;
    private javax.swing.JList<String> listaSubastas;
    private javax.swing.JList<String> listaDesexos;
    private javax.swing.JTabbedPane panelBase;
    private javax.swing.JScrollPane panelDesexos;
    private javax.swing.JPanel panelLibros;
    private javax.swing.JScrollPane panelSubastas;
    private javax.swing.JSpinner prezo;
    private javax.swing.JPanel subastas;
    private javax.swing.JTextField titulo;
    // End of variables declaration//GEN-END:variables
}
