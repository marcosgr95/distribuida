package axentesMarcos.compraVentaLibros.marcos.onto;



/**
* Protege name: Pedir
* @author OntologyBeanGenerator v4.1
* @version 2017/04/28, 20:11:32
*/
public class Pedir implements jade.content.AgentAction {

   private static final long serialVersionUID = 6879412765850074215L;

  private String _internalInstanceName = null;

  public Pedir() {
    this._internalInstanceName = "";
  }

  public Pedir(String instance_name) {
    this._internalInstanceName = instance_name;
  }

  public String toString() {
    return _internalInstanceName;
  }

   /**
   * Protege name: libroPedido
   */
   private Peticion libroPedido;
   public void setLibroPedido(Peticion value) { 
    this.libroPedido=value;
   }
   public Peticion getLibroPedido() {
     return this.libroPedido;
   }

}
