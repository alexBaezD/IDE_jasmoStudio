/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal;

import Tools.Compilar;
import Tools.Editor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.CodeTemplateManager;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.templates.CodeTemplate;
import org.fife.ui.rsyntaxtextarea.templates.StaticCodeTemplate;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;



/**
 *
 * @author Alejandro Báez
 */
public class IDE extends javax.swing.JFrame implements SearchListener{

    /**
     * Creates new form ide
     */
  
    private final Editor editor;
    private String rutaCH="",rutaEs="";
    private RSyntaxTextArea AreaCode;
    private int fontSize;
    private String nombreArchivo;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;
   
    /**
     * Constructor de la clase IDE.
     * Que extiende de JFrame e implementa el 
     * interface SearchListener.bae
     */
  
    public IDE() {
        fontSize=0;
        initComponents();
        buildAreaCode();
        editor = new Editor();
  }
    
   /*
    *Este metodo se encargar de instaciar el Area de texto donde 
    se escribre el codigo de mapic.
    Se establece la sintaxis a reconocer.
    El escuchador para saber exactamente en que linea y columna
    se encuentra el cursor.
    Crea una instacia de un Scroll para el area de codigo.
    */ 
    
   private void buildAreaCode(){
        initSearchDialogs();
        JPanel cp = new JPanel(new BorderLayout()); 
        AreaCode = new RSyntaxTextArea(20, 60);
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/Mapic", "org.fife.ui.rsyntaxtextarea.modes.MapicTokenMaker");
        AreaCode.setSyntaxEditingStyle("text/Mapic");
        AreaCode.setCodeFoldingEnabled(true);
        AreaCode.setMarkOccurrences(true);
        AreaCode.setAntiAliasingEnabled(true);
        ChangeTheme("DEFAULT");    
        AreaCode.setFont(new Font("Lucida Grande", 0, 18));
        AreaCode.addCaretListener(new CaretListener(){
        @Override
            public void caretUpdate( CaretEvent e ) {
            int pos = e.getDot();
               try {
                  int row =  AreaCode.getLineOfOffset( pos ) + 1;
                  int col = pos -  AreaCode.getLineStartOffset( row - 1 ) + 1;
                  jLabel1.setText("Consola:                                            "+"Línea: " + row + " Columna: " + col );
              }
              catch( BadLocationException exc ){ 
                  System.out.println(exc); 
              }
           } 
         });
        
        RTextScrollPane sp = new RTextScrollPane(AreaCode);
        CompletionProvider provider = createCompletionProvider();
        AutoCompletion ac = new AutoCompletion(provider);
       
        ac.setAutoActivationEnabled(true);
        ac.setParameterAssistanceEnabled(true);
        ac.setShowDescWindow(false);
        ac.setAutoCompleteSingleChoices(false);
        ac.setChoicesWindowSize(300, 150);
        ac.install(AreaCode);
        sp.setFoldIndicatorEnabled(true);
        createCodeTemplate();
         cp.add(sp);
        ErrorStrip errorStrip = new ErrorStrip(AreaCode);
        cp.add(errorStrip, BorderLayout.LINE_END);
        this.add(cp,BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    
     
     
       
    }
 
   
   /**
    * Crea las instancias para los dialogos
    * de buscar y reemplazar.
    */
   public void initSearchDialogs() {

		findDialog = new FindDialog(IDE.this,this);
		replaceDialog = new ReplaceDialog(IDE.this,this);
                SearchContext context = findDialog.getSearchContext();
		replaceDialog.setSearchContext(context);
               

	}
 
   
   /**
   *Crea atajos para escribir
   * un codigo y q lo autocomplete el IDE por si solo
   * Por ejemplo si escribre "mtd" y pulsas shift+ctrl+espacio
   * te crea la estructura de un metodo
   */
   private void createCodeTemplate(){
       //Area de teclas rapidas
      RSyntaxTextArea.setTemplatesEnabled(true);
      CodeTemplateManager ctm = RSyntaxTextArea.getCodeTemplateManager();

      CodeTemplate cuerpo = new StaticCodeTemplate("mapic", "Fuses\n" +
                    "\n" 
                    ,"\tSetup {\n" +
                    "\n" +
                    "\t}\n\n" +
                    "\tCiclo {\n" +
                    "\n" +
                    "\t}\n\n" +
                    "\tMain {\n" +
                    "\n" +
                    "\t}");
      ctm.addTemplate(cuerpo);
      CodeTemplate ct = new StaticCodeTemplate("mtd", "Void ","() {\n\n}");
      ctm.addTemplate(ct);
      
      CodeTemplate togle = new StaticCodeTemplate("tm", "TimeMS(",");");
      ctm.addTemplate(togle);
      CodeTemplate togle2 = new StaticCodeTemplate("tg", "Toggle ",null);
      ctm.addTemplate(togle2);
      ct = new StaticCodeTemplate("sw", "switch (", ") Of \n Fin switch");
      ctm.addTemplate(ct);
   }
   
   
   /**
    * Se Establecen las palabras que va a autocompletar 
    * @return CompletionProvider , este objeto contiene toda la
    * información acerca de que palabra puede terminar de escribir.
    */
   private CompletionProvider createCompletionProvider() {

     
      DefaultCompletionProvider provider = new DefaultCompletionProvider();
      provider.setAutoActivationRules(true, ".");
      provider.addCompletion(new BasicCompletion(provider, "TimeMS(time);"));
      provider.addCompletion(new BasicCompletion(provider, "Main"));
      provider.addCompletion(new BasicCompletion(provider, "Setup"));
      provider.addCompletion(new BasicCompletion(provider, "Fuses"));
      provider.addCompletion(new BasicCompletion(provider, "ENTRADA"));
      provider.addCompletion(new BasicCompletion(provider, "SALIDA"));
      provider.addCompletion(new BasicCompletion(provider, "Retardo"));
      provider.addCompletion(new BasicCompletion(provider, "Ciclo"));
      provider.addCompletion(new BasicCompletion(provider, "Void"));
      provider.addCompletion(new BasicCompletion(provider, "Toggle"));
      provider.addCompletion(new BasicCompletion(provider, "Si"));
      provider.addCompletion(new BasicCompletion(provider, "No"));
      provider.addCompletion(new BasicCompletion(provider, "Then"));
      provider.addCompletion(new BasicCompletion(provider, "switch"));
      provider.addCompletion(new BasicCompletion(provider, "Of"));
      provider.addCompletion(new BasicCompletion(provider, "Caso"));
      provider.addCompletion(new BasicCompletion(provider, "default"));
      provider.addCompletion(new BasicCompletion(provider, "while"));
      provider.addCompletion(new BasicCompletion(provider, "Do"));
      provider.addCompletion(new BasicCompletion(provider, "Var"));
      provider.addCompletion(new BasicCompletion(provider, "Cons"));
      provider.addCompletion(new BasicCompletion(provider, "AND"));
      provider.addCompletion(new BasicCompletion(provider, "OR"));
      provider.addCompletion(new BasicCompletion(provider, "true"));
      provider.addCompletion(new BasicCompletion(provider, "false"));
      provider.addCompletion(new BasicCompletion(provider, "Int"));
      provider.addCompletion(new BasicCompletion(provider, "Float"));
      provider.addCompletion(new BasicCompletion(provider, "Char"));
      provider.addCompletion(new BasicCompletion(provider, "Boolean"));
      provider.addCompletion(new BasicCompletion(provider, "return"));
      provider.addCompletion(new BasicCompletion(provider, "PA"));
      provider.addCompletion(new BasicCompletion(provider, "PB"));
      provider.addCompletion(new BasicCompletion(provider, "PC"));
      provider.addCompletion(new BasicCompletion(provider, "PD"));
      provider.addCompletion(new BasicCompletion(provider, "PE"));
      provider.addCompletion(new BasicCompletion(provider, "TA"));
      provider.addCompletion(new BasicCompletion(provider, "TB"));
      provider.addCompletion(new BasicCompletion(provider, "TC" ));
      provider.addCompletion(new BasicCompletion(provider, "TD" ));
      provider.addCompletion(new BasicCompletion(provider, "TE" ));
      provider.addCompletion(new BasicCompletion(provider, "ANSEL"));
      provider.addCompletion(new BasicCompletion(provider, "ANSELH" ));
      provider.addCompletion(new BasicCompletion(provider, "INTCON"));

      provider.addCompletion(new BasicCompletion(provider, "TA0"));
      provider.addCompletion(new BasicCompletion(provider, "TA1"));
      provider.addCompletion(new BasicCompletion(provider, "TA2"));
      provider.addCompletion(new BasicCompletion(provider, "TA3"));
      provider.addCompletion(new BasicCompletion(provider, "TA4"));
      provider.addCompletion(new BasicCompletion(provider, "TA5"));
      provider.addCompletion(new BasicCompletion(provider, "TA6"));
      provider.addCompletion(new BasicCompletion(provider, "TA7"));

      provider.addCompletion(new BasicCompletion(provider, "TB0"));
      provider.addCompletion(new BasicCompletion(provider, "TB1"));
      provider.addCompletion(new BasicCompletion(provider, "TB2"));
      provider.addCompletion(new BasicCompletion(provider, "TB3"));
      provider.addCompletion(new BasicCompletion(provider, "TB4"));
      provider.addCompletion(new BasicCompletion(provider, "TB5"));
      provider.addCompletion(new BasicCompletion(provider, "TB6"));
      provider.addCompletion(new BasicCompletion(provider, "TB7"));

      provider.addCompletion(new BasicCompletion(provider,"TC0"));
      provider.addCompletion(new BasicCompletion(provider,"TC1"));
      provider.addCompletion(new BasicCompletion(provider,"TC2"));
      provider.addCompletion(new BasicCompletion(provider,"TC3"));
      provider.addCompletion(new BasicCompletion(provider,"TC4"));
      provider.addCompletion(new BasicCompletion(provider,"TC5"));
      provider.addCompletion(new BasicCompletion(provider,"TC6"));
      provider.addCompletion(new BasicCompletion(provider,"TC7"));
            
      provider.addCompletion(new BasicCompletion(provider,"TD0"));
      provider.addCompletion(new BasicCompletion(provider,"TD1"));
      provider.addCompletion(new BasicCompletion(provider,"TD2"));
      provider.addCompletion(new BasicCompletion(provider,"TD3"));
      provider.addCompletion(new BasicCompletion(provider,"TD4"));
      provider.addCompletion(new BasicCompletion(provider,"TD5"));
      provider.addCompletion(new BasicCompletion(provider,"TD6"));
      provider.addCompletion(new BasicCompletion(provider,"TD7"));

      provider.addCompletion(new BasicCompletion(provider,"TE0"));
      provider.addCompletion(new BasicCompletion(provider,"TE1"));
      provider.addCompletion(new BasicCompletion(provider,"TE2"));
      provider.addCompletion(new BasicCompletion(provider,"TE3"));

      provider.addCompletion(new BasicCompletion(provider, "PA0" ));
      provider.addCompletion(new BasicCompletion(provider, "PA1" ));
      provider.addCompletion(new BasicCompletion(provider, "PA2" ));
      provider.addCompletion(new BasicCompletion(provider, "PA3" ));
      provider.addCompletion(new BasicCompletion(provider, "PA4" ));
      provider.addCompletion(new BasicCompletion(provider, "PA5" ));
      provider.addCompletion(new BasicCompletion(provider, "PA6" ));
      provider.addCompletion(new BasicCompletion(provider, "PA7" ));
       
      provider.addCompletion(new BasicCompletion(provider,"PB0"  ));
      provider.addCompletion(new BasicCompletion(provider,"PB1"  ));
      provider.addCompletion(new BasicCompletion(provider,"PB2"  ));
      provider.addCompletion(new BasicCompletion(provider,"PB3"  ));
      provider.addCompletion(new BasicCompletion(provider,"PB4"  ));
      provider.addCompletion(new BasicCompletion(provider,"PB5"  ));
      provider.addCompletion(new BasicCompletion(provider,"PB6"  ));
      provider.addCompletion(new BasicCompletion(provider,"PB7"  ));
       
      provider.addCompletion(new BasicCompletion(provider, "PC0" ));
      provider.addCompletion(new BasicCompletion(provider, "PC1" ));
      provider.addCompletion(new BasicCompletion(provider, "PC2" ));
      provider.addCompletion(new BasicCompletion(provider, "PC3" ));
      provider.addCompletion(new BasicCompletion(provider, "PC4" ));
      provider.addCompletion(new BasicCompletion(provider, "PC5" ));
      provider.addCompletion(new BasicCompletion(provider, "PC6" ));
      provider.addCompletion(new BasicCompletion(provider, "PC7" ));
       
      provider.addCompletion(new BasicCompletion(provider, "PD0" ));
      provider.addCompletion(new BasicCompletion(provider, "PD1" ));
      provider.addCompletion(new BasicCompletion(provider, "PD2" ));
      provider.addCompletion(new BasicCompletion(provider, "PD3" ));
      provider.addCompletion(new BasicCompletion(provider, "PD4" ));
      provider.addCompletion(new BasicCompletion(provider, "PD5" ));
      provider.addCompletion(new BasicCompletion(provider, "PD6" ));
      provider.addCompletion(new BasicCompletion(provider, "PD7" ));
       
 		  provider.addCompletion(new BasicCompletion(provider, "PE0" ));
      provider.addCompletion(new BasicCompletion(provider, "PE1" ));
      provider.addCompletion(new BasicCompletion(provider, "PE2" ));
      provider.addCompletion(new BasicCompletion(provider, "PE3" ));

      provider.addCompletion(new BasicCompletion(provider,"ANSEL0"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSEL1"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSEL2"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSEL3"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSEL4"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSEL5"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSEL6"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSEL7"  ));
		
      provider.addCompletion(new BasicCompletion(provider,"ANSELH0"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSELH1"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSELH2"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSELH3"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSELH4"  ));
      provider.addCompletion(new BasicCompletion(provider,"ANSELH5"  ));
    
      
      provider.addCompletion(new BasicCompletion(provider,"INTCONGIE"   ));
      provider.addCompletion(new BasicCompletion(provider,"INTCONPEIE"  ));
      provider.addCompletion(new BasicCompletion(provider,"INTCONT0IE"  ));
      provider.addCompletion(new BasicCompletion(provider,"INTCONINTE"  ));
      provider.addCompletion(new BasicCompletion(provider,"INTCONRBIE"  ));
      provider.addCompletion(new BasicCompletion(provider,"INTCONT0IF"  ));
      provider.addCompletion(new BasicCompletion(provider,"INTCONINTF"  ));
      provider.addCompletion(new BasicCompletion(provider,"INTCONRBIF"  ));
      return provider;

   }
     
    
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton8 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem11 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem18 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(44, 62, 80));
        setIconImage(Toolkit.getDefaultToolkit().getImage("logo.png"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                cerrarPreguntar(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(44, 62, 80));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar1.setBackground(new java.awt.Color(96, 125, 139));
        jToolBar1.setRollover(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(400, 40));
        jToolBar1.setMinimumSize(new java.awt.Dimension(400, 40));

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevo.png"))); // NOI18N
        jButton8.setToolTipText("Crear Nuevo Proyecto");
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton_CrearNuevo(evt);
            }
        });
        jToolBar1.add(jButton8);

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Guardar.png"))); // NOI18N
        jButton7.setToolTipText("Guardar Proyecto");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton_Guardar(evt);
            }
        });
        jToolBar1.add(jButton7);
        jToolBar1.add(jSeparator5);

        jButton4.setBackground(new java.awt.Color(236, 240, 241));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Copy.png"))); // NOI18N
        jButton4.setToolTipText("Copiar");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copiarBoton(evt);
            }
        });
        jToolBar1.add(jButton4);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Cut.png"))); // NOI18N
        jButton5.setToolTipText("Cortar");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cortarBoton(evt);
            }
        });
        jToolBar1.add(jButton5);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Paste.png"))); // NOI18N
        jButton6.setToolTipText("Pegar");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pegarBoton(evt);
            }
        });
        jToolBar1.add(jButton6);

        jSeparator3.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar1.add(jSeparator3);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Aeroplane.png"))); // NOI18N
        jButton1.setToolTipText("Compilar");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compilacion(evt);
            }
        });
        jToolBar1.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Media.png"))); // NOI18N
        jButton2.setToolTipText("Abrir Proteus");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EjecutarCodigo(evt);
            }
        });
        jToolBar1.add(jButton2);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/izq.png"))); // NOI18N
        jButton3.setToolTipText("Rehacer");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton_Deshacer(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/der.png"))); // NOI18N
        jButton9.setToolTipText("Deshacer");
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton_Rehacer(evt);
            }
        });
        jToolBar1.add(jButton9);

        jPanel1.add(jToolBar1, java.awt.BorderLayout.NORTH);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Consola:");
        jLabel1.setMinimumSize(new java.awt.Dimension(75, 5));
        jPanel2.add(jLabel1, java.awt.BorderLayout.NORTH);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jTextArea2.setForeground(new java.awt.Color(0, 102, 51));
        jTextArea2.setRows(5);
        jTextArea2.setText("Jasmo Studio: 0.0.0.0 Segundos");
        jScrollPane3.setViewportView(jTextArea2);

        jPanel2.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/pic16f887 copia.jpg"))); // NOI18N
        jLabel2.setToolTipText("Mostrar Diagrama de PIC16F887");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                abrirDiagrama(evt);
            }
        });
        jPanel4.add(jLabel2, java.awt.BorderLayout.CENTER);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("PIC16F887");
        jPanel4.add(jLabel4, java.awt.BorderLayout.NORTH);

        jPanel2.add(jPanel4, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        jMenuBar1.setBackground(new java.awt.Color(236, 240, 241));

        jMenu1.setBackground(new java.awt.Color(55, 71, 79));
        jMenu1.setText("Archivo");
        jMenu1.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/nuevo.png"))); // NOI18N
        jMenuItem7.setText("Nuevo");
        jMenuItem7.setToolTipText("Crear Nuevo Proyecto\n");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nuevo(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/abrir.png"))); // NOI18N
        jMenuItem1.setText("Abrir");
        jMenuItem1.setToolTipText("Abrir un proyecto");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirArchivo(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Guardar.png"))); // NOI18N
        jMenuItem2.setText("Guardar");
        jMenuItem2.setToolTipText("Guardar un proyecto");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardar(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Cerrar");
        jMenuItem3.setToolTipText("Cerrar Jasmo Studio");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrar(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setBackground(new java.awt.Color(236, 240, 241));
        jMenu2.setText("Edición");
        jMenu2.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setBackground(new java.awt.Color(236, 240, 241));
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Copy.png"))); // NOI18N
        jMenuItem4.setText("Copiar");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copiar(evt);
            }
        });
        jMenu2.add(jMenuItem4);
        jMenu2.add(jSeparator1);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setBackground(new java.awt.Color(236, 240, 241));
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Cut.png"))); // NOI18N
        jMenuItem5.setText("Cortar");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cortar(evt);
            }
        });
        jMenu2.add(jMenuItem5);
        jMenu2.add(jSeparator2);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setBackground(new java.awt.Color(236, 240, 241));
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Paste.png"))); // NOI18N
        jMenuItem6.setText("Pegar");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pegar(evt);
            }
        });
        jMenu2.add(jMenuItem6);
        jMenu2.add(jSeparator4);

        jMenuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/buscar.png"))); // NOI18N
        jMenuItem11.setText("Buscar");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                busqueda_AreaCode(evt);
            }
        });
        jMenu2.add(jMenuItem11);
        jMenu2.add(jSeparator6);

        jMenuItem18.setText("Ir a linea");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ir_aLinea(evt);
            }
        });
        jMenu2.add(jMenuItem18);
        jMenu2.add(jSeparator7);

        jMenuItem19.setText("Buscar y Reemplazar");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscar_reemplazar(evt);
            }
        });
        jMenu2.add(jMenuItem19);

        jMenuBar1.add(jMenu2);

        jMenu3.setBackground(new java.awt.Color(236, 240, 241));
        jMenu3.setText("Ver");
        jMenu3.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N

        jMenuItem10.setText("Barra de Herramientas");
        jMenuItem10.setToolTipText("Mostrar/Ocultar Barra de Herramientas");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ver_ocultarBarraHerramientas(evt);
            }
        });
        jMenu3.add(jMenuItem10);

        jMenu5.setText("Tema");

        jMenuItem12.setText("Default");
        jMenuItem12.setToolTipText("Tema por default");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aplicarTemaDefault(evt);
            }
        });
        jMenu5.add(jMenuItem12);

        jMenuItem13.setText("Dark");
        jMenuItem13.setToolTipText("Tema Obscuro");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aplicarTemaDark(evt);
            }
        });
        jMenu5.add(jMenuItem13);

        jMenuItem14.setText("Pink");
        jMenuItem14.setToolTipText("Tema Blanco");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aplicarTemaBlanco(evt);
            }
        });
        jMenu5.add(jMenuItem14);

        jMenuItem15.setText("Monaki");
        jMenuItem15.setToolTipText("Tema Monaki");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aplicarTemaMonaki(evt);
            }
        });
        jMenu5.add(jMenuItem15);

        jMenuItem21.setText("Mapic");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem21);

        jMenu3.add(jMenu5);

        jMenuItem16.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem16.setText("Aumentar Tamaño Letra");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aumentarTamañoLetra(evt);
            }
        });
        jMenu3.add(jMenuItem16);

        jMenuItem17.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem17.setText("Disminuir Tamaño Letra");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disminuirTamañoLetra(evt);
            }
        });
        jMenu3.add(jMenuItem17);

        jMenuItem20.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem20.setText("Bits de Configuración");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configuracionPIC(evt);
            }
        });
        jMenu3.add(jMenuItem20);

        jMenuBar1.add(jMenu3);

        jMenu4.setBackground(new java.awt.Color(236, 240, 241));
        jMenu4.setText("Ayuda");
        jMenu4.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N

        jMenuItem8.setText("Acerca de..");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acerdaDe(evt);
            }
        });
        jMenu4.add(jMenuItem8);

        jMenuItem9.setText("Manual de Uso");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abrirAyuda(evt);
            }
        });
        jMenu4.add(jMenuItem9);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void abrirArchivo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirArchivo
        // TODO add your handling code here:
     String contenido = "";
     String x = System.getProperty("user.home");
     File f2=new File(x+"\\MapicProjects");
     JFileChooser fc = new JFileChooser();
      System.out.println(f2.getAbsoluteFile());
                    fc.setCurrentDirectory(f2);
		if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			
			try {
                            
			contenido = editor.abrirArchivo(f.getAbsolutePath());
                                nombreArchivo=fc.getSelectedFile().getName();
				AreaCode.setText(contenido);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this,
				JOptionPane.ERROR_MESSAGE);
			}
		}
        
        
        
    }//GEN-LAST:event_abrirArchivo
    
    private void cerrar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrar
        // TODO add your handling code here:
        Object [] opciones ={"Aceptar","Cancelar"};
    int eleccion = JOptionPane.showOptionDialog(rootPane,"Estas a Punto de Cerrar Jasmo Studio, los cambios hechos que no se han guardado"
            + " \n se perderan",
            "Mensaje de Confirmacion",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,opciones,"Aceptar");
    if (eleccion == JOptionPane.YES_OPTION)
            this.dispose();
    }//GEN-LAST:event_cerrar

    private void guardar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardar
        // TODO add your handling code here:
        String x = System.getProperty("user.home");
        String contenido = "";
        String rutaArchivo = "";
        File f=new File(x+"\\MapicProjects");
     if( editor.esArchivoNuevo() ) {
			JFileChooser fc = new JFileChooser();
                        fc.setCurrentDirectory(f);
			if( fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION ) {
				rutaArchivo = fc.getSelectedFile().getAbsolutePath();
                                contenido =AreaCode.getText();
                            try {
                               rutaCH=rutaArchivo;
                                System.out.println(rutaEs);
                                 System.out.println(rutaCH); 
                                 f = new File(rutaCH);
                                 f.mkdir();
                                 System.out.println(fc.getSelectedFile().getName());
                                 nombreArchivo=fc.getSelectedFile().getName();
                                 rutaArchivo=f.getAbsolutePath()+"\\"+fc.getSelectedFile().getName();
                                 System.out.println(rutaArchivo);
                                editor.guardarArchivo(contenido, rutaArchivo);
                                
                                
                                 
                            } catch (Exception ex) {
                               
                            }
			}
		}else{
                        try {
                            contenido=AreaCode.getText();
                            editor.guardarArchivo(contenido, null);
                            System.out.print("Aqui se tiene que guardar");
                        } catch (Exception ex) {
                            Logger.getLogger(IDE.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
 
    }//GEN-LAST:event_guardar

    private void Nuevo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nuevo
        // TODO add your handling code here:
       //se necesita recuperar la direccion seleccionada en la otra ventana y asi se crea el archivo
     
        if(editor.esArchivoNuevo() && !"".equals(AreaCode.getText())){
           int n=JOptionPane.showConfirmDialog(rootPane, "Guardar!!! ", "No has Guardado el archivo",JOptionPane.YES_NO_OPTION);
          if(n==JOptionPane.YES_OPTION){
              guardar(evt);
          }
       }
       editor.crearArchivo();
      
       AreaCode.setText(
                    "Fuses\n" +
                    "\n" +
                    "\tSetup {\n" +
                    "\n" +
                    "\t}\n\n" +
                    "\tCiclo {\n" +
                    "\n" +
                    "\t}\n\n" +
                    "\tMain {\n" +
                    "\n" +
                    "\t}");
        
        
    }//GEN-LAST:event_Nuevo

    private void compilacion(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compilacion
        // TODO add your handling code here:
        guardar(evt);
        Compilar Jasmo=new Compilar();
        Jasmo.compilar(editor.ObtenerDIreccion(),editor.ObtenerDireccionCarpeta()+"\\");
      String contenido="";
        
        try{
            File error=new File(editor.ObtenerDireccionCarpeta()+"\\","ERROR.txt");
           // JOptionPane.showMessageDialog(this, "RUta de ERROR-->"+editor.ObtenerDireccionCarpeta()+"\\"+"ERROR.txt");
           // JOptionPane.showMessageDialog(this, "OTRA ruta--->"+editor.ObtenerDIreccion());
            if(error.length()>0){
                FileReader fr=new FileReader(error);
                BufferedReader b=new BufferedReader(fr);
                String line;
                while((line=b.readLine())!=null){
                    contenido+=line+"\n";
                }
                jTextArea2.setForeground(Color.RED);
               jTextArea2.setText("Algo Anda Mal:\n"+contenido);
            }
            else{
                 jTextArea2.setForeground(new Color(006633));
                jTextArea2.setText("BUILD SUCCESSFUL");
            }
        }catch(IOException e){
            System.out.println("No se pudo leer el archivo Errores");
        }
        
        
        
        
        
        
        
    }//GEN-LAST:event_compilacion

    private void EjecutarCodigo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EjecutarCodigo
        // TODO add your handling code here:
        Runtime rt =Runtime.getRuntime();
        try
        {
          
           rt.exec("C:\\Program Files (x86)\\Labcenter Electronics\\Proteus 8 Professional\\BIN\\PDS.exe");
           
        }
        catch(IOException ex)
        {
            //Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_EjecutarCodigo

    private void acerdaDe(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acerdaDe
        // TODO add your handling code here:
       AcercaDE acerca =new AcercaDE();
       acerca.setSize(350, 310);
       acerca.setResizable(false);
       acerca.setVisible(true);
       
    }//GEN-LAST:event_acerdaDe

    private void copiar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copiar
        // TODO add your handling code here:
       AreaCode.copy();
      
        
    }//GEN-LAST:event_copiar

    private void pegar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pegar
        // TODO add your handling code here:
       AreaCode.paste();
        
    }//GEN-LAST:event_pegar

    private void cortar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cortar
        // TODO add your handling code here:
        AreaCode.cut();
       
    }//GEN-LAST:event_cortar

    private void copiarBoton(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copiarBoton
        // TODO add your handling code here:
        copiar(evt);
    }//GEN-LAST:event_copiarBoton

    private void cortarBoton(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cortarBoton
        // TODO add your handling code here:
        cortar(evt);
    }//GEN-LAST:event_cortarBoton

    private void pegarBoton(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pegarBoton
        // TODO add your handling code here:
        pegar(evt);
    }//GEN-LAST:event_pegarBoton

    private void cerrarPreguntar(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_cerrarPreguntar
        // TODO add your handling code here:
    Object [] opciones ={"Aceptar","Cancelar"};
    int eleccion = JOptionPane.showOptionDialog(rootPane,"Estas a Punto de Cerrar Jasmo Studio,"
            + "\n los cambios hechos que no se han guardado,"
            + "   se perderan",
            "Mensaje de Confirmacion",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,opciones,"Aceptar");
    if (eleccion == JOptionPane.YES_OPTION)
            this.dispose();
    
   
    }//GEN-LAST:event_cerrarPreguntar

    private void ver_ocultarBarraHerramientas(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ver_ocultarBarraHerramientas
        // TODO add your handling code here:
        jMenuItem10.addActionListener((ActionEvent evt1) -> {
            if(jToolBar1.isVisible())
                jToolBar1.setVisible(false);
            else
                jToolBar1.setVisible(true);
        });
    }//GEN-LAST:event_ver_ocultarBarraHerramientas

    private void busqueda_AreaCode(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_busqueda_AreaCode
        // TODO add your handling code here:
      
        if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			findDialog.setVisible(true);
        
    }//GEN-LAST:event_busqueda_AreaCode

    private void boton_CrearNuevo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton_CrearNuevo
        // Boton de Nuevo
        Nuevo(evt);
    }//GEN-LAST:event_boton_CrearNuevo

    private void boton_Guardar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton_Guardar
        // Boton de Guardar
        guardar(evt);
    }//GEN-LAST:event_boton_Guardar

    private void abrirAyuda(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abrirAyuda
        
    try {
            File objetofile = new File ("mapic-ayuda/jasmo.html");
            Desktop.getDesktop().open(objetofile);
        }catch (IOException ex) {
             System.out.println(ex.getMessage());
        }     
    }//GEN-LAST:event_abrirAyuda

    private void boton_Deshacer(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton_Deshacer
        // TODO add your handling code here:
       AreaCode.undoLastAction();
        
    }//GEN-LAST:event_boton_Deshacer

    private void boton_Rehacer(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton_Rehacer
        // TODO add your handling code here:
      
           AreaCode.redoLastAction();
        
    }//GEN-LAST:event_boton_Rehacer

    private void abrirDiagrama(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_abrirDiagrama
        // TODO add your handling code here:
        diagramaPIC16F887 d=new diagramaPIC16F887();
        d.setTitle("Diagrama del PIC16F887");
        d.setSize(800,570);
        d.setLocationByPlatform(false);
        d.setResizable(false);
        d.setVisible(true);
        
    }//GEN-LAST:event_abrirDiagrama

    private void aplicarTemaDefault(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aplicarTemaDefault
        // Tema Default
    ChangeTheme("DEFAULT");    
    }//GEN-LAST:event_aplicarTemaDefault

    private void aplicarTemaDark(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aplicarTemaDark
        // Tema Dark
      ChangeTheme("DARK");   
    }//GEN-LAST:event_aplicarTemaDark

    private void aplicarTemaBlanco(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aplicarTemaBlanco
        // Tema Blanco
         ChangeTheme("WHITE");
    }//GEN-LAST:event_aplicarTemaBlanco

    private void aplicarTemaMonaki(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aplicarTemaMonaki
        // Tema PIC
         ChangeTheme("PIC");
    }//GEN-LAST:event_aplicarTemaMonaki

    private void aumentarTamañoLetra(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aumentarTamañoLetra
      // Aumentar Tamaño de fuente
        fontSize= AreaCode.getFont().getSize();
        fontSize=fontSize+2;
        AreaCode.setFont(new Font(null,0,fontSize));     
    }//GEN-LAST:event_aumentarTamañoLetra

    private void disminuirTamañoLetra(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disminuirTamañoLetra
        //Reducir Tamaño de fuente
        fontSize= AreaCode.getFont().getSize();
        fontSize=fontSize-2;
        AreaCode.setFont(new Font(null,0,fontSize));
    }//GEN-LAST:event_disminuirTamañoLetra

    private void ir_aLinea(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ir_aLinea
        // TODO add your handling code here:
      if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			GoToDialog dialog = new GoToDialog(IDE.this);
			dialog.setMaxLineNumberAllowed(AreaCode.getLineCount());
			dialog.setVisible(true);
			int line = dialog.getLineNumber();
			if (line>0) {
				try {
					AreaCode.setCaretPosition(AreaCode.getLineStartOffset(line-1));
				} catch (BadLocationException ble) { 
					UIManager.getLookAndFeel().provideErrorFeedback(AreaCode);
					ble.printStackTrace();
				}
			}
        
        
        
    }//GEN-LAST:event_ir_aLinea

    private void buscar_reemplazar(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscar_reemplazar
        // TODO add your handling code here:
        if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			replaceDialog.setVisible(true);
        
        
    }//GEN-LAST:event_buscar_reemplazar

    private void configuracionPIC(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configuracionPIC
        // TODO add your handling code here:
        
        conf_bits c = new conf_bits();
        c.setVisible(true);
        c.setResizable(false);
        c.setTitle("Bits de configuracion");
        c.setSize(550,360);
  
    }//GEN-LAST:event_configuracionPIC

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        // TODO add your handling code here:
         ChangeTheme("DARK2");
    }//GEN-LAST:event_jMenuItem21ActionPerformed

 
    public void ChangeTheme(String name){
        try {
         int tmp = AreaCode.getFont().getSize();
          Theme theme = null;
        switch(name){
            case "PIC":
                 theme= Theme.load(getClass().getResourceAsStream(
               "/org/fife/ui/rsyntaxtextarea/themes/pic.xml"));
                  theme.apply(AreaCode);
                break;
            case "DARK":
                theme = Theme.load(getClass().getResourceAsStream(
               "/org/fife/ui/rsyntaxtextarea/themes/dark.xml"));
                 theme.apply(AreaCode);
                break;
            case "WHITE":
                theme = Theme.load(getClass().getResourceAsStream(
               "/org/fife/ui/rsyntaxtextarea/themes/map.xml"));
                 theme.apply(AreaCode);
                break;
            case "DEFAULT":
                theme = Theme.load(getClass().getResourceAsStream(
               "/org/fife/ui/rsyntaxtextarea/themes/default.xml"));
                 theme.apply(AreaCode);
                break;
            case "DARK2":
                theme = Theme.load(getClass().getResourceAsStream(
               "/org/fife/ui/rsyntaxtextarea/themes/intenso.xml"));
                 theme.apply(AreaCode);
                break;
        } 
        AreaCode.setFont(new Font(null,0,tmp));
    } catch (IOException ioe) { 
         ioe.printStackTrace();
      }
    }
    
    
   
    
    
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    public javax.swing.JTextArea jTextArea2;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    //Metodos de Eschuca de la interface SerchListener
    
    @Override
    public void searchEvent(SearchEvent e) {
       SearchEvent.Type type = e.getType();
		SearchContext context = e.getSearchContext();
		SearchResult result = null;

		switch (type) {
			default: 
			case MARK_ALL:
				result = SearchEngine.markAll(AreaCode, context);
				break;
			case FIND:
				result = SearchEngine.find(AreaCode, context);
				if (!result.wasFound()) {
					UIManager.getLookAndFeel().provideErrorFeedback(AreaCode);
				}
				break;
			case REPLACE:
				result = SearchEngine.replace(AreaCode, context);
				if (!result.wasFound()) {
					UIManager.getLookAndFeel().provideErrorFeedback(AreaCode);
				}
				break;
			case REPLACE_ALL:
				result = SearchEngine.replaceAll(AreaCode, context);
				JOptionPane.showMessageDialog(null, result.getCount() +
						" ocurrencias reemplazadas.");
				break;
		}

		String text="" ;
		if (result.wasFound()) {
			text = "Texto Encontrado; Ocurrencias marcadas: " + result.getMarkedCount();
		}
		else if (type==SearchEvent.Type.MARK_ALL) {
			if (result.getMarkedCount()>0) {
				text = "Ocurrencias Marcadas: " + result.getMarkedCount();
			}
			else {
				text = "";
			}
		}
		else {
			text = "Texto No Encontrado";
		}
		JOptionPane.showMessageDialog(null, text);
    }

    @Override
    public String getSelectedText() {
       return AreaCode.getSelectedText();
    }
}

