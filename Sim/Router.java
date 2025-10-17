package Sim;

// This class implements a simple router

public class Router extends SimEnt{

	private RouteTableEntry [] _routingTable;
	private int _interfaces;
	private int _now=0;
    private NetworkAddr _id;
	// When created, number of interfaces are defined
	
	Router(int interfaces, int network, int routerID)
	{
		_routingTable = new RouteTableEntry[interfaces];
		_interfaces=interfaces;
        _id = new NetworkAddr(network, routerID);
	}

    public NetworkAddr getAddr()
    {
        return _id;
    }
	
	// This method connects links to the router and also informs the 
	// router of the host connects to the other end of the link
	
	public void connectInterface(int interfaceNumber, SimEnt link, SimEnt node)
	{
		if (interfaceNumber<_interfaces)
		{
            for (int i=0;i<_interfaces;i++){
                if(_routingTable[i]!=null && _routingTable[i].node().equals(node)){
                    System.out.println("Router already connected to "+_routingTable[i].node()+" Removing Table entry");
                    _routingTable[i] = null;
                }
            }
			_routingTable[interfaceNumber] = new RouteTableEntry(link, node);
            System.out.println("Router connected to "+_routingTable[interfaceNumber].node()+" Adding Table entry on interface "+interfaceNumber);
            //Send router solicitation here? if node to see if node is router

		}
		else
			System.out.println("Trying to connect to port not in router");
		
		((Link) link).setConnector(this);
        sendRouterSolicitation(interfaceNumber);
	}

    private void sendRouterSolicitation(int interfaceNumber){
        System.out.println("Solicitation Sent");
        RouterSolicitationMessage rsm = new RouterSolicitationMessage(this.getAddr());
        send (_routingTable[interfaceNumber].link(), rsm, _now);
    }


	// This method searches for an entry in the routing table that matches
	// the network number in the destination field of a messages. The link
	// represents that network number is returned
	
	private SimEnt getInterface(int networkAddress)
	{
		SimEnt routerInterface=null;
		for(int i=0; i<_interfaces; i++)
			if (_routingTable[i] != null)
			{
                if (_routingTable[i].node().getClass() == Router.class){

                } else if (((Node) _routingTable[i].node()).getAddr().networkId() == networkAddress)
				{
					routerInterface = _routingTable[i].link();
				}
			}
		return routerInterface;
	}
	
	
	// When messages are received at the router this method is called
	
	public void recv(SimEnt source, Event event)
	{
        System.out.println("Router Recieved"+event.getClass().getName());
		if (event instanceof Message)
		{
            SimEnt sendNext = getInterface(((Message) event).destination().networkId());

			send (sendNext, event, _now);
	
		} else if(event instanceof RouterSolicitationMessage){
            System.out.println("Solicitation RCV");
            RouterAdvertismentMessage msg = new RouterAdvertismentMessage(this.getAddr(), ((RouterSolicitationMessage) event).source());
            SimEnt sendNext = getInterface(((RouterSolicitationMessage) event).source().networkId());
            send (sendNext, msg, _now);
        }
        //else if (event instanceof RouterSolicitaiton) send routeradvertisment
	}
}
