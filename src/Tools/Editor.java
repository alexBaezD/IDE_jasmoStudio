/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import java.io.IOException;

/**
 *
 * @author Alejandro Báez
 */
public class Editor {
    private Archivo archivo;
	

	public Editor() {
		archivo = null;
	}
	
	public String abrirArchivo( String nombreArchivo ) throws Exception {
		String contenido = "";
		archivo = new Archivo( nombreArchivo );
		try {
			contenido = archivo.darContenido();
		} catch (IOException e) {
			throw new Exception("Error al leer el archivo.", e);
		}
		return contenido;
	}
	
	
	public void crearArchivo() {
		archivo = null;
	}
	
	
	
	public void guardarArchivo( String contenido, String rutaArchivo ) throws Exception {
		if( archivo == null ) {
			archivo = new Archivo( rutaArchivo+".mapic" );
		}
		try {
			archivo.guardar(contenido);
                        
		} catch (IOException e) {
			throw new Exception("Error guardando el archivo", e);
		}
	}
	
	
	public boolean esArchivoNuevo() {
		return archivo == null;
	}
        
        public String ObtenerDIreccion(){
            return archivo.direccion();
        }
        
        public String ObtenerDireccionCarpeta(){
            return archivo.direccionCarpeta();
        }
	
}
