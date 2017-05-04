package axentesMarcos.compraVentaLibros.marcos.onto;



/**
* Protege name: Peticion
* @author OntologyBeanGenerator v4.1
* @version 2017/04/28, 20:11:32
*/
public class Peticion implements jade.content.Concept {

   private static final long serialVersionUID = 6879412765850074215L;

  private String _internalInstanceName = null;

  public Peticion() {
    this._internalInstanceName = "";
  }

  public Peticion(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: precio
   */
   private int precio;
   public void setPrecio(int value) { 
    this.precio=value;
   }
   public int getPrecio() {
     return this.precio;
   }

   /**
   * Protege name: libro
   */
   private Libro libro;
   public void setLibro(Libro value) { 
    this.libro=value;
   }
   public Libro getLibro() {
     return this.libro;
   }

}
