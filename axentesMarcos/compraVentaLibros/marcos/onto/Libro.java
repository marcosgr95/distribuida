package axentesMarcos.compraVentaLibros.marcos.onto;



/**
* Protege name: Libro
* @author OntologyBeanGenerator v4.1
* @version 2017/04/28, 20:11:32
*/
public class Libro implements jade.content.Concept {

   private static final long serialVersionUID = 6879412765850074215L;

  private String _internalInstanceName = null;

  public Libro() {
    this._internalInstanceName = "";
  }

  public Libro(String instance_name) {
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
