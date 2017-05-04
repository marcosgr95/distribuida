package axentesMarcos.compraVentaLibros.marcos.onto;



/**
* Protege name: Anuncio
* @author OntologyBeanGenerator v4.1
* @version 2017/04/28, 20:11:32
*/
public class Anuncio implements jade.content.Concept {

   private static final long serialVersionUID = 6879412765850074215L;

  private String _internalInstanceName = null;

  public Anuncio() {
    this._internalInstanceName = "";
  }

  public Anuncio(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: ganadores
   */
   private String ganadores;
   public void setGanadores(String value) { 
    this.ganadores=value;
   }
   public String getGanadores() {
     return this.ganadores;
   }

   /**
   * Protege name: precioAnunciado
   */
   private int precioAnunciado;
   public void setPrecioAnunciado(int value) { 
    this.precioAnunciado=value;
   }
   public int getPrecioAnunciado() {
     return this.precioAnunciado;
   }

   /**
   * Protege name: libroAnunciado
   */
   private Libro libroAnunciado;
   public void setLibroAnunciado(Libro value) { 
    this.libroAnunciado=value;
   }
   public Libro getLibroAnunciado() {
     return this.libroAnunciado;
   }

}
