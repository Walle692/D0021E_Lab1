package Sim;

// This class implements a link without any loss, jitter or delay

public class Link extends SimEnt{
	protected SimEnt _connectorA=null;
	protected SimEnt _connectorB=null;
	protected int _now=0;
	
	public Link()
	{
		super();	
	}
	
	// Connects the link to some simulation entity like
	// a node, switch, router etc.
	
	public void setConnector(SimEnt connectTo)
	{
		if (_connectorA == null) {
			_connectorA = connectTo;
			System.out.println("ConnectorA set to: " + connectTo);
		} else {
			_connectorB = connectTo;
			System.out.println("ConnectorB set to: " + connectTo);
		}
	}

	// Method to check if the link is connected on both ends
	public boolean isFullyConnected() {
		return (_connectorA != null && _connectorB != null);
	}

	// Called when a message enters the link
	
	public void recv(SimEnt src, Event ev)
	{
		if (!isFullyConnected()) {
			System.err.println("Error: Link is not fully connected. ConnectorA: " + _connectorA + ", ConnectorB: " + _connectorB);
			return;
		}

		// Ensure all event types, including BindingUpdate, are forwarded
        System.out.println("Link received event: " + ev + " from: " + src);
        if (src == _connectorA) {
            System.out.println("Link forwarding event from ConnectorA to ConnectorB: " + ev);
            send(_connectorB, ev, _now);
        } else {
            System.out.println("Link forwarding event from ConnectorB to ConnectorA: " + ev);
            send(_connectorA, ev, _now);
        }
	}
}