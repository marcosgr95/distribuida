/*Marcos García Rouco

Práctica 5, Computación Distribuída

Abril 2017*/



import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.HashMap;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;

//import axentesMarcos.compraVentaLibros.marcos.onto.impl.*;
import axentesMarcos.compraVentaLibros.marcos.onto.*;

public class Comprador extends Agent {
	//Títulos dos libros a comprar e prezo máximo de compra de cada un deles
	private HashMap<String, Integer> libros; 

	//Interface gráfica do comprador
	private CompradorGui gui;

	private Codec codec;
	
	private Ontology ontoloxia;

	public HashMap<String, Integer> getLibros(){
		return this.libros;
	}

	//Inicialización do axente
	protected void setup() {
		//Imprímese un mensaxe de benvida
		System.out.println("Ola! Son "+getAID().getLocalName()+" e quero comprar libros!");

		//Inicialízase o hashmap dos libros
		this.libros = new HashMap<String, Integer>();

		//Rexístrase o axente
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		//Iníciase o codec e a ontoloxía
		codec = new SLCodec();
		ontoloxia = MarcosOntology.getInstance();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontoloxia);

		//Iníciase a interface gráfica
		gui = new CompradorGui(this);
		gui.mostrarInterface();

		//A partir de agora faise todo pola interface

		
	}

	//Finalización do axente
	protected void takeDown() {
		//Imprímese unha mensaxe de finalización
		System.out.println("Son o comprador "+getAID().getLocalName()+" e rematei.");

		//Débese eliminar o rexistro do cliente no servizo de páxinas amarelas
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

	}


	//Método que será chamado pola interface gráfica, gui, para engadir un novo libro no que estamos interesados
	public boolean engadirLibro(final String titulo, final int prezo) {

		if(libros.get(titulo) == null){
			//Engádese o libro no hashmap de libros nos que estamos interesados
			addBehaviour(new OneShotBehaviour() {
				public void action() {
					libros.put(titulo, new Integer(prezo));
					System.out.println("Son "+ myAgent.getAID().getLocalName()+" e " +titulo+" foi engadido aos libros nos que estou interesado. Como moito pagarei por el "+prezo+" euros.");

					//Rexístrase o servizo nas páxinas amarelas
					DFAgentDescription dfd = new DFAgentDescription();
					dfd.setName(getAID());
					ServiceDescription sd = new ServiceDescription();

					//Para ter todos os servizos anteriores tamén
					for(String clave : libros.keySet()){
						sd.setType("compra-venta-libros"); //Quere comprarse un libro
						sd.setName(clave); //O libro chámase tal e como indica o título
						dfd.addServices(sd);
						sd = new ServiceDescription();
					}


					try {
						DFService.modify(myAgent, dfd);
					}
					catch (FIPAException fe) {
						fe.printStackTrace();
					}
				}
			} );

			


			//Engádense os comportamentos pertinentes
			//Comportamento para recibir o inform_ref inicial
			addBehaviour(new CyclicBehaviour(){
				public void action() {
					MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF),
							MessageTemplate.and(MessageTemplate.MatchOntology(ontoloxia.getName()), //MessageTemplate.MatchLanguage(codec.getName())));
								MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()), MessageTemplate.MatchConversationId(titulo))));
					ACLMessage msg = myAgent.receive(mt);
					if (msg != null) {
						try{
							Action a = (Action) getContentManager().extractContent(msg);
							Ofertar of = (Ofertar) a.getAction();
							Oferta o = of.getOferta();
							Integer prezo = o.getPrecioOferta();
							Libro l = o.getLibroOfertado();
							String t = l.getTitulo();
							
							//Agora hai que actualizar a lista de subastas, de modo que se mostren os gañadores parciais
							gui.modificarLista("ACTIVA", t, prezo.toString(), "");
							gui.indicarNovaSubasta(t, prezo.toString());
						}catch (CodecException | OntologyException e){e.printStackTrace();}
					}
					else {
						block();
					}
				}
			});

			//Comportamento para recibir cfp do vendedor
			addBehaviour(new CyclicBehaviour(){
				public void action() {

					//Primeiro compróbase que sigamos interesados no libro
					if(libros.get(titulo) != null){
						MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP),
							MessageTemplate.and(MessageTemplate.MatchOntology(ontoloxia.getName()), //MessageTemplate.MatchLanguage(codec.getName())));
								MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()), MessageTemplate.MatchConversationId(titulo))));
							//MessageTemplate.MatchConversationId(titulo)); //Estamos á espera de cfp (calls-for-proposal) co título que queremos
						ACLMessage msg = myAgent.receive(mt);
						if (msg != null) {
							//Se a mensaxe recibida non é nula, procesámola
							//String mensaxe = msg.getContent();
							String comprobador= msg.getReplyWith();
							try{

								Action a = (Action) getContentManager().extractContent(msg);
								Ofertar of = (Ofertar) a.getAction();
								Oferta o = of.getOferta();
								Integer prezo = o.getPrecioOferta();
								Libro l = o.getLibroOfertado();
								String t = l.getTitulo();
								

								//O contido da mensaxe será o prezo
								//Integer prezo = Integer.parseInt(mensaxe);

								if(titulo.equals(t)){
									ACLMessage reply = msg.createReply();
									reply.setOntology(ontoloxia.getName());
									reply.setLanguage(codec.getName());

									//Agora compróbase se o prezo non se pasa do que estamos dispostos a pagar polo libro
									Integer prezoMaximo = libros.get(titulo);

									//if (l.equals(titulo) && prezo <= prezoMaximo) { //O prezo é asequible
										//Estamos dispostos a seguir na puxa, contestamos si
										reply.setPerformative(ACLMessage.PROPOSE);
										reply.setConversationId(titulo);
										/*reply.setContent("si");
										reply.setReplyWith(comprobador);
										reply.setConversationId(titulo);*/

										//Estamos dispostos a seguir na puxa, polo que enviamos unha petición do libro
										l = new Libro();
										Peticion p = new Peticion();
										Pedir pr = new Pedir();
										l.setTitulo(titulo);
										p.setLibro(l);
										p.setPrecio(prezoMaximo); //Como prezo ponse o que estamos dispostos a pagar
										pr.setLibroPedido(p);
									//}
									/*else { //O prezo pásase do que estamos dispostos a pagar
										reply.setPerformative(ACLMessage.PROPOSE);
										reply.setContent("nope");
										reply.setReplyWith(comprobador);
										reply.setConversationId(titulo);
									}*/

									getContentManager().fillContent(reply, new Action(getAID(), pr));
							

									myAgent.send(reply);
									gui.modificarLista("ACTIVA", titulo, prezo.toString(), "");
								}
							} catch (CodecException | OntologyException e){e.printStackTrace();}
						}
						else {
							block();
						}
					}
				}
			});

			//Comportamento para recibir os accept_proposals
			addBehaviour(new CyclicBehaviour(){
				public void action() {
					//MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
						//MessageTemplate.MatchConversationId(titulo)); //Estamos á espera dun accept_proposal
					MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
							MessageTemplate.and(MessageTemplate.MatchOntology(ontoloxia.getName()), //MessageTemplate.MatchLanguage(codec.getName())));
								MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()), MessageTemplate.MatchConversationId(titulo))));
					ACLMessage msg = myAgent.receive(mt);
					if (msg != null) {
						//Se a mensaxe recibida non é nula, procesámola
						//String ganadores = msg.getContent();
						//String prezoActual = msg.getReplyWith();

						try{
							Action a = (Action) getContentManager().extractContent(msg);
							Anunciar ar = (Anunciar) a.getAction();
							Anuncio an = ar.getAnuncio();
							Libro l = an.getLibroAnunciado();
							Integer prezoActual = an.getPrecioAnunciado();
							String ganadores = an.getGanadores();
							
							String t = l.getTitulo();
							//Agora hai que actualizar a lista de subastas, de modo que se mostren os gañadores parciais
							gui.modificarLista("ACTIVA", titulo, prezoActual.toString(), ganadores);
						}catch (CodecException | OntologyException e){e.printStackTrace();}

						
					}
					else {
						block();
					}
				}
			});

			//Comportamento para recibir os reject_proposals
			addBehaviour(new CyclicBehaviour(){
				public void action() {
					//MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL),
						//MessageTemplate.MatchConversationId(titulo)); //Estamos á espera dun reject_proposal
					MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL),
							MessageTemplate.and(MessageTemplate.MatchOntology(ontoloxia.getName()), //MessageTemplate.MatchLanguage(codec.getName())));
								MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()), MessageTemplate.MatchConversationId(titulo))));
					ACLMessage msg = myAgent.receive(mt);
					if (msg != null) {
						//Se a mensaxe recibida non é nula, procesámola
						//String ganadores = msg.getContent();
						//String prezoActual = msg.getReplyWith();

						//Agora hai que actualizar a lista de subastas
						//gui.modificarLista("ABANDONADA", titulo, prezoActual, ganadores);
						try{
							Action a = (Action) getContentManager().extractContent(msg);
							Anunciar ar = (Anunciar) a.getAction();
							Anuncio an = ar.getAnuncio();
							Libro l = an.getLibroAnunciado();
							Integer prezoActual = an.getPrecioAnunciado();
							String ganadores = an.getGanadores();
							
							String t = l.getTitulo();
							//Agora hai que actualizar a lista de subastas, de modo que se mostren os gañadores parciais
							gui.modificarLista("ABANDONADA", titulo, prezoActual.toString(), ganadores);
						}catch (CodecException | OntologyException e){e.printStackTrace();}
					}
					else {
						block();
					}
				}
			});


			//Comportamento para indicar que rematou unha subasta
			addBehaviour(new CyclicBehaviour(){
				public void action() {
					MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.and(MessageTemplate.MatchOntology(ontoloxia.getName()), //MessageTemplate.MatchLanguage(codec.getName())));
								MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()), MessageTemplate.MatchConversationId(titulo))));
					ACLMessage msg = myAgent.receive(mt);
					if (msg != null) {
						try{
							Action a = (Action) getContentManager().extractContent(msg);
							Anunciar ar = (Anunciar) a.getAction();
							Anuncio an = ar.getAnuncio();
							Libro l = an.getLibroAnunciado();
							Integer prezoActual = an.getPrecioAnunciado();
							String ganadores = an.getGanadores();
							
							String t = l.getTitulo();
							//Agora hai que actualizar a lista de subastas, de modo que se mostren os gañadores parciais
							gui.modificarLista("REMATADA", titulo, prezoActual.toString(), ganadores);
						}catch (CodecException | OntologyException e){e.printStackTrace();}
					}
					else {
						block();
					}
				}
			});

			//Comportamento para indicar que se gañou unha subasta
			addBehaviour(new CyclicBehaviour(){
				public void action() {
					MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
							MessageTemplate.and(MessageTemplate.MatchOntology(ontoloxia.getName()), //MessageTemplate.MatchLanguage(codec.getName())));
								MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()), MessageTemplate.MatchConversationId(titulo))));
					ACLMessage msg = myAgent.receive(mt);
					Integer prezoActual=0;
					if (msg != null) {
						//Se a mensaxe recibida non é nula, procesámola
						try{
							Action a = (Action) getContentManager().extractContent(msg);
							Anunciar ar = (Anunciar) a.getAction();
							Anuncio an = ar.getAnuncio();
							Libro l = an.getLibroAnunciado();
							prezoActual = an.getPrecioAnunciado();
							String ganadores = an.getGanadores();
							
							String t = l.getTitulo();
							//Agora hai que actualizar a lista de subastas, de modo que se mostren os gañadores parciais
							gui.modificarLista("GAÑADA", titulo, prezoActual.toString(), ganadores);
						}catch (CodecException | OntologyException e){e.printStackTrace();}


						//Tamén se debe eliminar o libro, pois xa o compramos
						libros.remove(titulo);


						//Actualízase o que se quere no servizo de páxinas amarelas
						DFAgentDescription dfd = new DFAgentDescription();
						dfd.setName(getAID());
						ServiceDescription sd = new ServiceDescription();

						for(String clave : libros.keySet()){
							sd.setType("compra-venta-libros"); //Quere comprarse un libro
							sd.setName(clave); //O libro chámase tal e como indica o título
							dfd.addServices(sd);
							sd = new ServiceDescription();
						}
						
						try {
							DFService.modify(myAgent, dfd);
						}
						catch (FIPAException fe) {
							fe.printStackTrace();
						}

						//Indícase a victoria
						gui.eliminarTitulo(titulo);
						gui.indicarVictoria(titulo, prezoActual.toString());
						
					}
					else {
						block();
					}
				}
			});

			return true;
		}
		else
			return false;
	}



}