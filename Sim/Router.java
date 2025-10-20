package Sim;

// This class implements a simple router

public class Router extends SimEnt{

    // used to store the prefix table for routing
    private RouteTableEntry [] _prefixTable;
	private RouteTableEntry [] _routingTable;
	private int _interfaces;
	private int _now=0;
    private NetworkAddr _id;
	// When created, number of interfaces are defined
	
	Router(int interfaces, int network, int routerID)
	{
        _prefixTable = new RouteTableEntry[interfaces];
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

        //check if link is fully connected before sending RS
        if(_routingTable[interfaceNumber].link() instanceof Link && ((Link) _routingTable[interfaceNumber].link()).isFullyConnected()){
            sendRouterSolicitation(interfaceNumber);
        }

	}

    private void sendRouterSolicitation(int interfaceNumber){
        //both statements send routersolicitation but due to different node types need to be handled differently
        if (_routingTable[interfaceNumber].node() instanceof Node) {
            RouterSolicitationMessage rsm = new RouterSolicitationMessage(
                    this.getAddr(),
                    ((Node) _routingTable[interfaceNumber].node()).getAddr()
            );
            send(_routingTable[interfaceNumber].link(), rsm, _now);
        } else {
            System.out.println("Router connected to another Router, sending RS");
            RouterSolicitationMessage rsm = new RouterSolicitationMessage(
                    this.getAddr(),
                    ((Router) _routingTable[interfaceNumber].node()).getAddr()
            );
            send(_routingTable[interfaceNumber].link(), rsm, _now);
        }
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
                //check if node is a router
                if (_routingTable[i].node().getClass() == Router.class) {
                    Router tempRouter = (Router) _routingTable[i].node();
                    int tempAddr = tempRouter.getAddr().networkId();
                    if (tempAddr == networkAddress) {
                        routerInterface = _routingTable[i].link();
                    }
                }
                //checks if node is a normal node and connected to this router
                else if (((Node) _routingTable[i].node()).getAddr().networkId() == networkAddress)
				{
					routerInterface = _routingTable[i].link();
				}
                //checks if node is normal and not connected to this router but has prefix to router we know.
                else if (_prefixTable[i] != null){
                    Router temp = (Router) _prefixTable[i].node();
                    int prefixNetwork = temp.getAddr().networkId();
                    int destPrefix = networkAddress / 10; // Assuming a fixed prefix length of 10 for simplicity
                    if (prefixNetwork == destPrefix) {
                        routerInterface = _routingTable[i].link();
                    }
                }
			}
        //if no matching interface found, send to first available interface might give eternal loop but at least something
        if (routerInterface == null){
            for(int i=0; i<_interfaces; i++){
                if(_prefixTable[i] != null){
                    routerInterface = _prefixTable[i].link();
                }
            }
        }
		return routerInterface;
	}

    private void addPrefixEntry(NetworkAddr sourceAddr) {
        //find which interface the advertisment came from
        for (int i = 0; i < _interfaces; i++) {
            if (_routingTable[i] != null){
                if (_routingTable[i].node().getClass() == Router.class) {
                    Router connectedRouter = (Router) _routingTable[i].node();
                    //check if advertisment source matches connected router
                    if (connectedRouter.getAddr().equals(sourceAddr)) {
                        _prefixTable[i] = _routingTable[i];
                    }
                }
            }
        }

    }

	
	// When messages are received at the router this method is called
	
	public void recv(SimEnt source, Event event)
	{
        //System.out.println("Router received event: " + event + " from: " + source + " in " + this);
		if (event instanceof Message)
		{
            SimEnt sendNext = getInterface(((Message) event).destination().networkId());
            //System.out.println("Routing Message to: " + sendNext);
            send(sendNext, event, _now);

		} else if(event instanceof RouterSolicitationMessage){
            System.out.println("Solicitation RCV");
            RouterAdvertismentMessage msg = new RouterAdvertismentMessage(this.getAddr(), ((RouterSolicitationMessage) event).source());
            SimEnt sendNext = getInterface(((RouterSolicitationMessage) event).source().networkId());
            addPrefixEntry(((RouterSolicitationMessage) event).source());
            //System.out.println("Sending Router Advertisement to: " + sendNext.getClass().getSimpleName());
            send(sendNext, msg, _now);
        } else if(event instanceof RouterAdvertismentMessage){
            System.out.println("Advertisment RCV");
            addPrefixEntry(((RouterAdvertismentMessage) event).source());
        }
	}
}
