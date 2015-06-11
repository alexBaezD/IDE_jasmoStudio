/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Alejandro Báez
 */
public class Archivo {
    
   
    /**
	 * Objeto de la clase File que representa de 
         * forma abstracta a un archivo fisico en disco
	 */
	private File archivo;
	
	/**
	 * Contruye un objeto de la clase Archivo
	 * @param nombreArchivo la ruta completa del archivo que se va a crear
	 */
     
	public Archivo( String nombreArchivo ) {
		archivo = new File( nombreArchivo );
	}
	
	/**
	 * Retorna el contenido del archivo 
	 * @return String que tiene el contenido
	 * @throws IOException cuando hay problemas abriendo o leyendo el archivo.
	 */
	public String darContenido() throws IOException {
		String contenido = "";
		FileReader fr = new FileReader( archivo );
		BufferedReader lector = new BufferedReader(fr);
		String linea = lector.readLine();
		while( linea != null ) {
			contenido += linea + "\n";
			linea = lector.readLine();
		}
		
		lector.close();
		fr.close();
		
		return contenido;
	}
	
	/**
	 * Guarda el contenido en un archivo nuevo o existente
	 * @param contenido String que tiene el contenido que se va a guardar en el archivo
	 * @throws IOException cuando hay problemas tratando de escribir en el archivo
	 */
	public void guardar( String contenido ) throws IOException {
		PrintWriter escritor = new PrintWriter(archivo);
		escritor.write(contenido);
		escritor.close();
	}
      /**
	 * Retorna la direccion absoluta del archivo con el que se trabaja
	 * @return String que da el contenido de la ruta absoluta.
	 * 
	 */
        public String direccion(){
          
           return archivo.getAbsolutePath();
        }
        
        /**
	 * Retorna la direccion Carpeta que lo contiene
	 * @return String que da la ruta de la carpeta
	 * 
	 */
        public String direccionCarpeta(){
            return archivo.getParent();
        }
    
}
