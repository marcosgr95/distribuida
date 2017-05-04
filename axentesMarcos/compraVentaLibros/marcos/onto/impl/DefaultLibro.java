package axentesMarcos.compraVentaLibros.marcos.onto.impl;


import axentesMarcos.compraVentaLibros.marcos.onto.*;

/**
* Protege name: Libro
* @author OntologyBeanGenerator v4.1
* @version 2017/04/28, 20:11:32
*/
public class DefaultLibro implements Libro {

  private static final long serialVersionUID = 6879412765850074215L;

  private String _internalInstanceName = null;

  public DefaultLibro() {
    this._internalInstanceName = "";
  }

  public DefaultLibro(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: titulo
   */
   private String titulo;
   public void setTitulo(String value) { 
    this.titulo=value;
   }
   public String getTitulo() {
     return this.titulo;
   }

}
