package Sim;

// This class implements a simple router

import java.util.ArrayList;

public class Router extends SimEnt{

	private RouteTableEntry [] _routingTable;

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

		//Brute force table for test
		if(_interfaces == 13){
			forwardTableMatch.add(30);
			forwardTableSend.add((Link)this.getInterface(20, 3));
		}

		((Link) link).setConnector(this);
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

//		for(int i=0; i<_interfaces; i++)
//			if (_routingTable[i] != null)
//			{
//				if ( ( (Node) _routingTable[i].node() ).getAddr().networkId() == networkAddress)
//				{
//					routerInterface = _routingTable[i].link();
//				}
//			}
//		return routerInterface;
	}
	
	
	// When messages are received at the router this method is called

	public void RS(){
		NetworkAddr thisRouterAddress = new NetworkAddr(_networkId,0);
		Message m = new Message(thisRouterAddress, null, 0, Message.MsgType.ROUTER_SOLICITATION, 10);
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

		Message m = (Message) event;
		switch (m.getType()) {

			case ROUTER_SOLICITATION:
                //Send router advertisement
				NetworkAddr thisRouterAddress = new NetworkAddr(_networkId,0);
                //Message sendRouterAdvertisement = new Message(thisRouterAddress, m.source(), m.seq(), Message.MsgType.ROUTER_ADVERTISEMENT, 10);

				Message sendRouterAdvertisement =
						new Message(thisRouterAddress, m.source(), m.seq(),
								Message.MsgType.ROUTER_ADVERTISEMENT, 10);
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

				if (m.destination().networkId() == _networkId){
					this.forwardTableMatch.add(m.source().networkId());
					this.forwardTableSend.add((Link) source);
					return;
				}

				//Continue Sending RA to all neighbor routers
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

			default:
			//System.out.println("Router handles packet with seq: " + ((Message) event).seq()+" from node: "+((Message) event).source().networkId()+"." + ((Message) event).source().nodeId() );
			SimEnt sendNext = getInterface(
					((Message) event).destination().networkId(),
					((Message) event).destination().nodeId()
			);
			//System.out.println("Router sends to node: " + ((Message) event).destination().networkId()+"." + ((Message) event).destination().nodeId());
				if (sendNext == null) return;
			send(sendNext, event, _now);

		}
	}
}
