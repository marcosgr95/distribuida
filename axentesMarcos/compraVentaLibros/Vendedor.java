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

import java.util.*;
//import axentesMarcos.compraVentaLibros.marcos.onto.impl.*;
import axentesMarcos.compraVentaLibros.marcos.onto.*;

public class Vendedor extends Agent{
	//Clase para os libros, a cal debe gardar info sobre o nome do libro, o prezo base e o incremento
	private class LibroPrivado{
		private String nome;
		private Integer prezoBase;
		private Integer incremento;

		public LibroPrivado(){
			this.nome=new String();
			this.prezoBase=0;
			this.incremento=0;
		}

		public LibroPrivado(String _nome, Integer _prezo, Integer _incremento){
			this.nome=_nome;
			this.prezoBase=_prezo;
			this.incremento=_incremento;
		}

		//Getters
		public String getNome(){
			return this.nome;
		}

		public Integer getPrezoBase(){
			return this.prezoBase;
		}

		public Integer getIncremento(){
			return this.incremento;
		}

		//Setters
		public void setNome(String _nome){
			this.nome=_nome;
		}

		public void setPrezoBase(Integer _prezo){
			this.prezoBase=_prezo;
		}

		public void setIncremento(Integer _incremento){
			this.incremento=_incremento;
		}
	}


	//Atributos do vendedor
	private HashMap<String, LibroPrivado> catalogo; //Catálogo dos libros (NomeLibro, Libro)

	private VendedorGui gui; //Interface gráfica do vendedor

	private Codec codec;
	
	private Ontology ontoloxia;

	protected void setup(){

		//Inícase o hashmap do catálogo
		catalogo = new HashMap<String, LibroPrivado>();

		//Iníciase o codec e a ontoloxía
		codec = new SLCodec();
		ontoloxia = MarcosOntology.getInstance();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontoloxia);

		//Iníciase a interface gráfica, gui
		gui= new VendedorGui(this);
		gui.mostrarInterface();

		//A partir de este momento contrólase todo a través da interface gráfica
	}


	protected void takeDown(){
		System.out.println("O vendedor vaise, adeus!");
	}

	//Método que será chamado pola interface gráfica, gui, para engadir un novo libro ao catálogo
	public void engadirLibro(final String titulo, final int prezo, final int incremento) {

		//Engádese o libro no catálogo
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				System.out.println(titulo+" foi engadido aos libros do catálogo con prezo base "+prezo+" euros.");
				catalogo.put(titulo, new LibroPrivado(titulo,prezo,incremento));
			}
		} );

		//Comézase a subasta
		//Engádese o comportamento para iniciar unha subasta dun libro
		addBehaviour(new Behaviour(){
			private int paso=4; //Variable para saber en qué paso do protocolo nos atopamos
			private Integer prezoActual = prezo; //Variable para levar a conta do prezo actual
			private int numeroRespostas=0; //Variable para contar as respostas de contadores obtidas
			private ArrayList<AID> compradores= new ArrayList(); //Compradores atopados no servizo de páxinas amarelas (DF)
			private ArrayList<AID> ganadores = new ArrayList(); //Gañadores parciais
			private AID ganadorFinal; //Gañador final da subasta
			private ArrayList<AID> accept = new ArrayList(); //Compradores aos que se lle enviará un accept_proposal
			private ArrayList<AID> reject = new ArrayList(); //Compradores aos que se lle enviará un reject_proposal
			private boolean haveToSleep = false;

			public void action(){
				
				switch(paso){

					case 0: //Está comezando unha nova ronda da subasta, polo que hai que mirar que compradores están interesados
						//Deste xeito, hai que mirar o servizo de páxinas amarelas e ver quen está interesado no libro dado

						System.out.println("\n\nVOU POÑER EN SUBASTA "+titulo+" POR "+prezoActual+" EUROS.\n");
						
						//Mírase que compradores están interesados no libro
						DFAgentDescription template = new DFAgentDescription();
						ServiceDescription sd = new ServiceDescription();
						sd.setType("compra-venta-libros"); //O tipo é o nome definido
						sd.setName(titulo);
						template.addServices(sd);
						try {
							//Búscanse os compradores no servizo de páxinas amarelas
							DFAgentDescription[] result = DFService.search(myAgent, template); 
							System.out.println("Os seguintes compradores están interesados en "+titulo+":");
							//compradores = new AID[result.length];
							for (int i = 0; i < result.length; ++i) {
								compradores.add(result[i].getName());
								System.out.println("\t - "+compradores.get(i).getName());
							}
						}
						catch (FIPAException fe) {
							fe.printStackTrace();
						}

						//Compróbase se se desconectou algún comprador (gañador) da ronda anterior
						for(int i=0; i < ganadores.size(); i++){
							if(!compradores.contains(ganadores.get(i))) //O gañador desconectouse
								ganadores.remove(i);
						}


						//Agora haille que enviar o call-for-proposal a todos os compradores
						ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
						for (int i = 0; i <  compradores.size(); ++i) {
							cfp.addReceiver(compradores.get(i));
						} 
						//cfp.setContent(prezoActual.toString()); //O contido da mensaxe é o prezo
						cfp.setConversationId(titulo); //O id é o título
						cfp.setOntology(ontoloxia.getName());
						cfp.setLanguage(codec.getName());

						Libro l = new Libro();
						Oferta o = new Oferta();
						Ofertar of = new Ofertar();
						l.setTitulo(titulo);
						o.setLibroOfertado(l);
						o.setPrecioOferta(prezoActual);
						of.setOferta(o);

						

						try {
							getContentManager().fillContent(cfp, new Action(getAID(), of));
						} catch (CodecException | OntologyException e) {
							e.printStackTrace();
						}

						cfp.setReplyWith("cfp"+System.currentTimeMillis()); //Valor único (para incrementar a seguridade)
						myAgent.send(cfp);

						//Prepárase a plantilla ou template para recibir respostas
						/*MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId(titulo),
								MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));*/
						if(compradores.size() > 0) //Se non se atoparon resultados, séguese neste punto
							paso = 1;
						else{
							//Para non buscar inmediatamente
							try{
								Thread.sleep(3000);
							}catch(Exception e){}
						}

						haveToSleep=false;
						
						break;

					case 1: //Agora espérase polas respostas dos compradores
						//MessageTemplate mt=MessageTemplate.and(MessageTemplate.MatchConversationId(titulo),
							//MessageTemplate.MatchPerformative(ACLMessage.PROPOSE)); //Plantilla para as respostas
						MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
							MessageTemplate.and(MessageTemplate.MatchOntology(ontoloxia.getName()), //MessageTemplate.MatchLanguage(codec.getName())));
								MessageTemplate.and(MessageTemplate.MatchLanguage(codec.getName()), MessageTemplate.MatchConversationId(titulo))));
						
						ACLMessage resposta = myAgent.receive(mt);

						if (resposta != null) {
							//Resposta recibida
							//Se o ACLMessage é de tipo PROPOSE correspóndese cunha resposta de un dos compradores

							//Agora mírase que respondeu o comprador
							//String respostaComprador = resposta.getContent();
							Action a = null;
							try{
								a = (Action) getContentManager().extractContent(resposta);
							} catch (CodecException | OntologyException e){e.printStackTrace();}
							Pedir pr = (Pedir) a.getAction();
							Peticion p = pr.getLibroPedido();
							Integer prezo = p.getPrecio();
							l = p.getLibro();
							String t = l.getTitulo();

							//Se o comprador respondeu "si", engádese á lista de gañadores
							//if (respostaComprador.equals("si")) {
							if(prezo >= prezoActual){
								System.out.println("--> "+resposta.getSender().getLocalName()+" é un dos gañadores parciais para "+titulo);
								if(! ganadores.contains(resposta.getSender()))
									ganadores.add(resposta.getSender());
								accept.add(resposta.getSender());
							}


							else{ //En caso contrario (recibiuse nope), elimínase da lista
								ganadores.remove(resposta.getSender());
								reject.add(resposta.getSender());
							}

							numeroRespostas++;

							if (numeroRespostas >= compradores.size()) { //Recibimos todas as respostas

								//Envío dos accept_proposals
								ACLMessage ap = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
								//ap.setContent("");
								ap.setOntology(ontoloxia.getName());
								ap.setLanguage(codec.getName());

								String g = new String();//String para mostrar os gañadores na lista

								ap.setConversationId(titulo); //O id é o título
								//ap.setReplyWith(prezoActual.toString()); //Aquí vai o prezo
								//an.setGanadores("");

								if(ganadores.size()==0){ //Se non hai gañadores, o gañador é o escollido na ronda anterior
									g=ganadorFinal.getLocalName();
									//an.setGanadores(ganadorFinal.getLocalName());
								}

								for(int i=0; i < accept.size(); i++){
									ap.addReceiver(accept.get(i));
									//ap.setContent(ap.getContent()+" / "+accept.get(i).getLocalName()); //O contido da mensaxe son os gañadores parciais
									g+= accept.get(i).getLocalName()+" / ";
									//an.setGanadores(an.getGanadores()+"/"+accept.get(i).getLocalName());
								}

								l = new Libro();
								Anuncio an = new Anuncio();
								Anunciar ar = new Anunciar();
								l.setTitulo(titulo);
								an.setLibroAnunciado(l);
								an.setPrecioAnunciado(prezoActual);
								an.setGanadores(g);
								ar.setAnuncio(an);

								try{
									getContentManager().fillContent(ap, new Action(getAID(), ar));
								
								} catch (CodecException | OntologyException e){e.printStackTrace();}
								System.out.println("***Envío do ACCEPT_PROPOSAL***");
								myAgent.send(ap);

								//Envío dos reject_proposals
								ACLMessage rp = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
								//rp.setContent("");
								rp.setOntology(ontoloxia.getName());
								rp.setLanguage(codec.getName());

								for(int i=0; i < reject.size(); i++){
									rp.addReceiver(reject.get(i));
									
								}
								
								//rp.setContent(g); //O contido da mensaxe son os gañadores parciais
								rp.setConversationId(titulo); //O id é o título
								//rp.setReplyWith(prezoActual.toString());

								try{
									getContentManager().fillContent(rp, new Action(getAID(), ar));
								} catch (CodecException | OntologyException e){e.printStackTrace();}
								myAgent.send(rp);


								//Actualízase a lista de subastas
								gui.modificarLista("ACTIVA", titulo, prezoActual.toString(), g);

								paso = 2; //Podemos pasar a comprobar se xa se pode finalizar a subasta
							}
						}

						else {
							block();
						}

						break;

					case 2: //Debemos comprobar se xa rematou a subasta
						//No caso de que rematase a subasta, isto indícaselle a todos os compradores cun inform
						//Ao gañador, ademais, enviaráselle un request

						if(ganadores.size() <= 1){ //Xa hai gañador


							block(1000);

							//Primeiro infórmase de que a subasta rematou
							//Agora haille que enviar o inform a todos os gañadores da anterior ronda
							ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
							inform.setOntology(ontoloxia.getName());
							inform.setLanguage(codec.getName());
							//Envío do correspondente ACLMessage ao gañador
							ACLMessage mensaxe = new ACLMessage(ACLMessage.REQUEST);
							mensaxe.setOntology(ontoloxia.getName());
							mensaxe.setLanguage(codec.getName());
							Integer p = 0; //Prezo sen o incremento

							l = new Libro();
							Anuncio an = new Anuncio();
							Anunciar ar = new Anunciar();
							l.setTitulo(titulo);
							an.setLibroAnunciado(l);
							
							
							for(int i = 0; i < compradores.size(); i++)
								inform.addReceiver(compradores.get(i));

							if(ganadores.size() == 1){ //Hai gañador claro
								//Se hai un gañador, envíaselle isto a el; En caso contrario, ao primeiro do arraylist (pero sempre será o 0)
								ganadorFinal=ganadores.get(0);
								//inform.addReceiver(ganadores.get(0));
								an.setPrecioAnunciado(prezoActual);
								an.setGanadores(ganadorFinal.getLocalName());
								//inform.setReplyWith(prezoActual.toString()); //O contido da mensaxe é o prezo
								//mensaxe.setReplyWith(prezoActual.toString());
							}
							
							else{ //Elíxese un dos gañadores da ronda anterior
								//inform.addReceiver(ganadorFinal);
								p = prezoActual - incremento;
								an.setPrecioAnunciado(p);
								an.setGanadores(ganadorFinal.getLocalName());
								//inform.setReplyWith(p.toString());
								//mensaxe.setReplyWith(p.toString());
							}

							ar.setAnuncio(an);
							try{
								getContentManager().fillContent(inform, new Action(getAID(), ar));
							
							} catch (CodecException | OntologyException e){e.printStackTrace();}
							
							inform.setConversationId(titulo); //O id é o título
							//inform.setContent(ganadorFinal.getLocalName()); //Indícase o gañador
							myAgent.send(inform);

							

							mensaxe.addReceiver(ganadorFinal);

							try{
								getContentManager().fillContent(mensaxe, new Action(getAID(), ar));
							
							} catch (CodecException | OntologyException e){e.printStackTrace();}
							
							mensaxe.setConversationId(titulo);
							//mensaxe.setContent(ganadorFinal.getLocalName());
							myAgent.send(mensaxe);

							//Actualízase a lista de subastas
							if(p == 0)
								gui.modificarLista("FINALIZADA", titulo, prezoActual.toString(), ganadorFinal.getLocalName());
							else
								gui.modificarLista("FINALIZADA", titulo, p.toString(), ganadorFinal.getLocalName());
							

							//Para rematar o comportamento:
							paso=3;
						}

						else{ //A subasta segue, pois polo menos hai dous compradores que seguen puxando
							//Hai que esperar os 10 segundos
							//block(10000);

							if(!haveToSleep){
								haveToSleep=true;
								myAgent.addBehaviour(new WakerBehaviour(myAgent, 10000){
									protected void onWake(){
										System.out.println("Nooooova ronda de "+titulo+"!\n");
										//Hai que actualizar o prezo e volver a facer un cfp
										prezoActual += incremento;
										paso = 0;
										
									}
								});
							}
							
							else{
								accept.clear();
								reject.clear();
								ganadorFinal=ganadores.get(0); //Por se na seguinte ronda non hai ningún gañador
								compradores.clear();
								numeroRespostas=0;
							}

							//paso = 0;
						}

						break;
					case 4:
						//Envíase o inform
						System.out.println("\n\n***Ola, son "+myAgent.getLocalName()+" e vou poñer en venta "+titulo+"***\n");
						
						//Mírase que compradores están interesados no libro
						template = new DFAgentDescription();
						sd = new ServiceDescription();
						sd.setType("compra-venta-libros"); //O tipo é o nome definido
						sd.setName(titulo);
						template.addServices(sd);
						try {
							//Búscanse os compradores no servizo de páxinas amarelas
							DFAgentDescription[] result = DFService.search(myAgent, template); 
							//compradores = new AID[result.length];
							for (int i = 0; i < result.length; ++i) {
								compradores.add(result[i].getName());
							}
						}
						catch (FIPAException fe) {
							fe.printStackTrace();
						}


						//Agora haille que enviar o call-for-proposal a todos os compradores
						ACLMessage inf = new ACLMessage(ACLMessage.INFORM_REF);
						for (int i = 0; i <  compradores.size(); ++i) {
							inf.addReceiver(compradores.get(i));
						} 
						//cfp.setContent(prezoActual.toString()); //O contido da mensaxe é o prezo
						inf.setConversationId(titulo); //O id é o título
						inf.setOntology(ontoloxia.getName());
						inf.setLanguage(codec.getName());

						l = new Libro();
						o = new Oferta();
						of = new Ofertar();
						l.setTitulo(titulo);
						o.setLibroOfertado(l);
						o.setPrecioOferta(prezoActual);
						of.setOferta(o);

						

						try {
							getContentManager().fillContent(inf, new Action(getAID(), of));
						} catch (CodecException | OntologyException e) {
							e.printStackTrace();
						}

						myAgent.send(inf);
						compradores.clear();
						paso=0;
						break;
					default:
						break;
				}


			}


			public boolean done(){
				boolean isDone = false;

				if(paso == 3){
					isDone=true;
					System.out.println("\n*** O LIBRO "+titulo+" FOI VENDIDO POR "+prezoActual+" EUROS A "+ganadorFinal.getLocalName()+".***\n\n\n");
					//Elimínase o libro do catálogo
					catalogo.remove(titulo);
				}

				return isDone;
			}


		});

	}



}