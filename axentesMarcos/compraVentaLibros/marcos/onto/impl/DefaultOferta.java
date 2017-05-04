package axentesMarcos.compraVentaLibros.marcos.onto.impl;


import axentesMarcos.compraVentaLibros.marcos.onto.*;

/**
* Protege name: Oferta
* @author OntologyBeanGenerator v4.1
* @version 2017/04/28, 20:11:32
*/
public class DefaultOferta implements Oferta {

  private static final long serialVersionUID = 6879412765850074215L;

  private String _internalInstanceName = null;

  public DefaultOferta() {
    this._internalInstanceName = "";
  }

  public DefaultOferta(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: libroOfertado
   */
   private Libro libroOfertado;
   public void setLibroOfertado(Libro value) { 
    this.libroOfertado=value;
   }
   public Libro getLibroOfertado() {
     return this.libroOfertado;
   }

   /**
   * Protege name: precioOferta
   */
   private int precioOferta;
   public void setPrecioOferta(int value) { 
    this.precioOferta=value;
   }
   public int getPrecioOferta() {
     return this.precioOferta;
   }

}
