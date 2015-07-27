/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal;

import java.io.File;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Alejandro Báez
 */
public class main {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        
        /**
       * El siguiente codigo , analiza los temas con los que cuenta
       * la maquina , y si esta disponible Nimbus , lo aplica.
       */  
        
      for(UIManager.LookAndFeelInfo laf:UIManager.getInstalledLookAndFeels()){
            if("Nimbus".equals(laf.getName()))
                try {
                UIManager.setLookAndFeel(laf.getClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                System.out.println(ex.getMessage());
            }
        }
      /*
       Carpera de trabajo con jasmo studio
      c:/user/suNombre/MapicProjects
      */
      String x = System.getProperty("user.home");
      File n = new File (x+"\\MapicProjects");
      if(!n.exists())
         n.mkdir();

      /*
       **Crear instancia de la vista de Cargar
       * Con un tamaño predefinido y un Hilo que duerme 
       * el proceso por 5000 milisegundos
       * Y al final se destruye.
       */
        carga c=new carga();
        c.setSize(480, 280);
        c.setResizable(false);
        c.setVisible(true);
        Thread.sleep(5000);
        c.dispose();
        
        
        IDE i =new IDE();
        i.setLocationRelativeTo(null);
        String m=System.getProperty("user.name");
        i.setTitle("Jasmo-Studio    Usuario: "+m+"  , Espacio de Trabajo " +x+"\\MapicProjects   , Modelo: PIC16F887");
        i.setSize(915, 680);
        i.setVisible(true);
      
     }
    
    
}
