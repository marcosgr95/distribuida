package axentesMarcos.compraVentaLibros.marcos.onto;


/**
* Protege name: Anunciar
* @author OntologyBeanGenerator v4.1
* @version 2017/04/28, 20:11:32
*/
public class Anunciar implements jade.content.AgentAction {

  private static final long serialVersionUID = 6879412765850074215L;

  private String _internalInstanceName = null;

  public Anunciar() {
    this._internalInstanceName = "";
  }

  public Anunciar(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: anuncio
   */
   private Anuncio anuncio;
   public void setAnuncio(Anuncio value) { 
    this.anuncio=value;
   }
   public Anuncio getAnuncio() {
     return this.anuncio;
   }

}
