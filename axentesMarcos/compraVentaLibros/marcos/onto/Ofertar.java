package axentesMarcos.compraVentaLibros.marcos.onto;



/**
* Protege name: Ofertar
* @author OntologyBeanGenerator v4.1
* @version 2017/04/28, 20:11:32
*/
public class Ofertar implements jade.content.AgentAction {

   private static final long serialVersionUID = 6879412765850074215L;

  private String _internalInstanceName = null;

  public Ofertar() {
    this._internalInstanceName = "";
  }

  public Ofertar(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: oferta
   */
   private Oferta oferta;
   public void setOferta(Oferta value) { 
    this.oferta=value;
   }
   public Oferta getOferta() {
     return this.oferta;
   }

}
