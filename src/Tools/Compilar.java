/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import java.io.File;
import javax.swing.JOptionPane;

/**
 *
 * @author Alejandro Báez
 */
public class Compilar {
    
        /**
	 * Invoca a Runtime y parser del lenguaje mapic
	 * @param file String que el contenido del archivo (Programa)
         * @param dir String que tiene la ruta donde se guardara el producto final
	 * 
	 */
    public void compilar(String file,String dir){
        Runtime obj=Runtime.getRuntime();
     Process p=null;
     File f = new File(file);
     System.out.println("Direccion de carpeta"+f.getParent());
      //JOptionPane.showMessageDialog(null, "Direccion de carpeta"+f.getParent());
     try
     {
         
          p=obj.exec("cmd.exe /k compilar.bat "+file +" "+ dir);
          //JOptionPane.showMessageDialog(null, "Direccion de carpeta despues del c -->"+f.getParentFile().getPath());
          //p=obj.exec("cmd.exe /c cd \""+Destino+"\"  & start cmd.exe /k \"xc8 --chip=16F887 mapic.c & exit");
         p=obj.exec("xc8 --chip=16F887 mapic.c",null,new File(f.getParentFile().getPath()+"\\"));
     }
     catch(Exception x)
        {
            JOptionPane.showMessageDialog(null, x);
            System.out. println("En compilacion "+ x);
        }
    }
    
    
}
