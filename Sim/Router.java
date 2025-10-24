package Sim;

// This class implements a simple router

import java.util.ArrayList;

public class Router extends SimEnt{

	private RouteTableEntry [] _routingTable;

    private NetworkAddr[] _previouslySeen = new NetworkAddr[10];

    //creates a binding table
    private BindingTableEntry[] _bindingTable = new BindingTableEntry[10];

	private ArrayList<Integer> forwardTableMatch = new ArrayList<>();
	private ArrayList<Link> forwardTableSend = new ArrayList<>();
	private int _interfaces;
	private int _now=0;

    private int _networkId;

	// When created, number of interfaces are defined
	
	Router(int networkId,int interfaces)
	{
		_routingTable = new RouteTableEntry[interfaces];
		_interfaces=interfaces;
        _networkId = networkId;
	}

    public void disconnectInterface(int interfaceNumber){
        if (interfaceNumber<_interfaces)
        {
            //finds emtpy spot in previously seen and adds the disconnected node there
            for(int i=0;i<_interfaces;i++){
                if (_previouslySeen[i] == null){
                    //adds the node and its address to the previously seen table(retains address for future use)
                    _previouslySeen[i] = ((Node)_routingTable[interfaceNumber].node()).getAddr();
                    break;
                }
            }
            _routingTable[interfaceNumber] = null;
        }
        else
            System.out.println("Trying to disconnect from port not in router");
    }

	// This method connects links to the router and also informs the 
	// router of the host connects to the other end of the link


	public void connectInterface(int interfaceNumber, SimEnt link, SimEnt node)
	{
		if (interfaceNumber<_interfaces)
		{
            for (int i=0;i<_interfaces;i++){
                if(_routingTable[i]!=null && _routingTable[i].node().equals(node)){

					NetworkAddr printOut = ((Node) _routingTable[i].node()).getAddr();
                    System.out.println("Router already connected to "+printOut.networkId() +"." + printOut.nodeId()+" Removing Table entry");
                    _routingTable[i] = null;
                }
            }
			_routingTable[interfaceNumber] = new RouteTableEntry(link, node);

			//Print node connection or Router connection dependent on type
			if(node instanceof Node) {
				NetworkAddr printOut = ((Node) _routingTable[interfaceNumber].node()).getAddr();
				System.out.println("Router connected to " + printOut.networkId() + "." + printOut.nodeId() + " Adding Table entry on interface " + interfaceNumber);
			}
			else{
				Router printOut = (Router) _routingTable[interfaceNumber].node();
				System.out.println("Router connected to other router with Network: "+ printOut._networkId +". Adding Table entry on interface "+ interfaceNumber);
			}

		}
		else
			System.out.println("Trying to connect to port not in router");


		((Link) link).setConnector(this);
		this.RS();
	}

	// This method searches for an entry in the routing table that matches
	// the network number in the destination field of a messages. The link
	// represents that network number is returned
	
	private SimEnt getInterface(int networkAddress, int nodeId)
	{
		SimEnt routerInterface=null;
        for(int i=0; i<_interfaces; i++) {
			if (_routingTable[i] == null) continue;

			if (_routingTable[i].node() instanceof Node) {
				NetworkAddr compare = ((Node) _routingTable[i].node()).getAddr();
				if (compare.networkId() == networkAddress && compare.nodeId() == nodeId) {
					routerInterface = _routingTable[i].link();
				}
			}
			else{
				Router tempRouter = (Router) _routingTable[i].node();
				if (tempRouter._networkId == networkAddress) {
					routerInterface = _routingTable[i].link();
				}

			}
		}
        //Check if we have a binding for that address in that case, we forward to the care-of address
        if(routerInterface == null){
            for(int i=0; i<_bindingTable.length; i++){
                //if binding entry is empty continue
                if(_bindingTable[i] == null) continue;
                //get the home address from the binding entry
                NetworkAddr hoa = _bindingTable[i].get_hoa();
                //check if the home address matches the requested address
                if(hoa.networkId() == networkAddress && hoa.nodeId() == nodeId){
                    NetworkAddr coa = _bindingTable[i].get_coa();
                    //return null to signal that we need to repacket the message to the care-of address
                    return routerInterface = null;
                }
            }
        }

		// This case will happen if the "ip adress" isn't a neighbor to this router
		//In which case we will check what link to forward it to
		if(routerInterface == null){
			int currentIndex = 0;
			for(int i: forwardTableMatch){
				if(i == networkAddress) routerInterface = forwardTableSend.get(currentIndex);
				currentIndex++;
			}
		}
		// Upon no match maybe send RS


		return routerInterface;

	}
	
	
	// When messages are received at the router this method is called

	public void RS(){
		NetworkAddr thisRouterAddress = new NetworkAddr(_networkId,0);
		Message m = new Message(thisRouterAddress, null, 0, Message.MsgType.ROUTER_SOLICITATION, 100);
		for(int i=0; i<_interfaces; i++) {
			if (_routingTable[i] == null) continue;
			if (_routingTable[i].node() instanceof Router) {
				send(_routingTable[i].link(), m, _now);
			}
		}
	}
	
	public void recv(SimEnt source, Event event)
	{
		if (!(event instanceof  Message)) return;
        //System.out.println("message recieved at router "+ _networkId + " at time "+ SimEngine.getTime()+ " message type: "+ ((Message) event).getType());

		Message m = (Message) event;
		switch (m.getType()) {

			case ROUTER_SOLICITATION:

                //Send router advertisement
				NetworkAddr thisRouterAddress = new NetworkAddr(_networkId,0);
                //Message sendRouterAdvertisement = new Message(thisRouterAddress, m.source(), m.seq(), Message.MsgType.ROUTER_ADVERTISEMENT, 10);

				Message sendRouterAdvertisement =
						new Message(thisRouterAddress, m.source(), m.seq(),
								Message.MsgType.ROUTER_ADVERTISEMENT, 100);
				send(source, sendRouterAdvertisement, _now);

				//Continue Sending RS to all neighbor routers
				if(m.updateTTL()<=0) return;
				//S
				for(int i=0; i<_interfaces; i++) {
					if (_routingTable[i] == null) continue;
					if (_routingTable[i].node() instanceof Router) {
						if(_routingTable[i].link() != source) {
							send(_routingTable[i].link(), m, _now);
						}
					}
				}

				return;

			case ROUTER_ADVERTISEMENT:

				//
                //Add stuff from the recived measage into the table?

				//Is this RA for me?
				if (m.destination().networkId() == _networkId){
					if (forwardTableMatch.contains(m.source().networkId())) return;
					//System.out.println("Router " + _networkId + " CONSUMED NOW");

					this.forwardTableMatch.add(m.source().networkId());
					this.forwardTableSend.add((Link) source);
					//System.out.println(_networkId + "Router conatins");
					for(int i : forwardTableMatch){
						//System.out.println(i);
					}
					return;
				}
				//Not for me -> do I know where to send it, if yes send it to that?
				if (forwardTableMatch.contains(m.destination().networkId())){
					SimEnt sendThis = getInterface(m.destination().networkId(),0);
					send(sendThis, m, _now);
					return;
				}else {
					//System.out.println("Clueless");
					this.RS();

				}
				//I have not seen this address before

				//Continue Sending RA to all neighbor routers
//				if(m.updateTTL()<=0) return;
//				//S
//				for(int i=0; i<_interfaces; i++) {
//					if (_routingTable[i] == null) continue;
//					if (_routingTable[i].node() instanceof Router) {
//						if(_routingTable[i].link() != source) {
//							send(_routingTable[i].link(), m, _now);
//						}
//					}
//				}


				return;

            case BINDING_UPDATE:

                //if binding update is for me, update binding table
                if (m.destination().networkId() == _networkId) {
                    System.out.println("BU recieved on home agent");
                    //Update binding table
                    //Find empty spot
                    for (int i = 0; i < _bindingTable.length; i++) {
                        if (_bindingTable[i] == null) {
                            //need to find the home address from the message source
                            NetworkAddr hoa = null;
                            for (int j = 0; j < _previouslySeen.length; j++) {
                                //check if previously seen entry matches source node id
                                if (_previouslySeen[j] != null && _previouslySeen[j].nodeId() == m.source().nodeId()) {
                                    hoa = _previouslySeen[j];
                                    break;
                                }
                            }
                            if (hoa == null) {
                                System.out.println("No home address found for node: " + m.source().networkId() + "." + m.source().nodeId());
                                return;
                            } else {
                                //add new binding entry
                                _bindingTable[i] = new BindingTableEntry(hoa, m.source());
                                System.out.println("Router " + _networkId + " added binding: " + hoa.networkId() + "." + hoa.nodeId() +
                                        " -> " + m.source().networkId() + "." + m.source().nodeId());
                                //send binding acknowledgement
                                NetworkAddr tra = new NetworkAddr(_networkId,0);
                                Message bA = new Message(
                                        tra,
                                        m.source(),
                                        m.seq(),
                                        Message.MsgType.BINDING_ACKNOWLEDGEMENT,
                                        10
                                );
                                SimEnt sendNext = getInterface(
                                        m.source().networkId(),
                                        m.source().nodeId()
                                );
                                System.out.println("sending BINDING_ACKNOWLEDGEMENT to " + m.source().networkId() + "." + m.source().nodeId());
                                send(sendNext, bA, _now);
                                break;
                            }
                        }
                    }
                } else {
                    System.out.println("message not for me");
                    //forward binding update to correct router
                    //same as default case
                    SimEnt sendNext = getInterface(
                            ((Message) event).destination().networkId(),
                            ((Message) event).destination().nodeId()
                    );
                    send(sendNext, event, _now);
                }

                return;

            case BINDING_ACKNOWLEDGEMENT: {

                //if binding ack is for me, print confirmation
                if (m.destination().networkId() == _networkId) {
                    //System.out.println("BINDING_ACKNOWLEDGEMENT recieved at router belonging to message: " + m.destination().networkId() + "." + m.destination().nodeId());
                    SimEnt sendNext = getInterface(
                            ((Message) event).destination().networkId(),
                            ((Message) event).destination().nodeId()
                    );
                    send(sendNext, event, _now);
                } else {
                    //System.out.println("message not for me");
                    //forward binding ack to correct router
                    //same as default case
                    SimEnt sendNext = getInterface(
                            ((Message) event).destination().networkId(),
                            ((Message) event).destination().nodeId()
                    );
                    send(sendNext, event, _now);
                }
                return;
            }

			default:
			//System.out.println("Router handles packet with seq: " + ((Message) event).seq()+" from node: "+((Message) event).source().networkId()+"." + ((Message) event).source().nodeId() );
			SimEnt sendNext = getInterface(
					((Message) event).destination().networkId(),
					((Message) event).destination().nodeId()
			);

			if (sendNext == null){
                //we need to repack the packet
                for (BindingTableEntry bindingTableEntry : _bindingTable) {
                    //if binding entry is empty continue
                    if (bindingTableEntry == null) continue;
                    //get the home address from the binding entry
                    NetworkAddr hoa = bindingTableEntry.get_hoa();
                    //check if the home address matches the requested address
                    if (hoa.networkId() == m.destination().networkId() && hoa.nodeId() == m.destination().nodeId()) {
                        NetworkAddr coa = bindingTableEntry.get_coa();
                        //repack the message to the care-of address
                        Message repackedMessage = new Message(
                                m.source(),
                                coa,
                                m.seq(),
                                m.getType(),
                                m.updateTTL()
                        );
                        //get the interface to the care-of address
                        sendNext = getInterface(coa.networkId(), coa.nodeId());
                        System.out.println("Router " + _networkId + " repacked message for node "+ m.destination().networkId() + "." + m.destination().nodeId() +
                                " to care-of address "+ coa.networkId() + "." + coa.nodeId());
                        //send the repacked message
                        send(sendNext, repackedMessage, _now);
                        return;
                    }
                }
            }
            if(sendNext ==null) {return;}
			send(sendNext, event, _now);

		}
	}
}
