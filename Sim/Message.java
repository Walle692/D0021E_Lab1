package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number

public class Message implements Event{
	private NetworkAddr _source;
	private NetworkAddr _destination;
	private int _seq=0;
	
	Message (NetworkAddr from, NetworkAddr to, int seq)
	{
		_source = from;
		_destination = to;
		_seq=seq;
        System.out.println("new Message created from "+from.networkId()+"."+from.nodeId()+" to "+to.networkId()+"."+to.nodeId()+" seq: "+seq+this);
	}
	
	public NetworkAddr source()
	{
		return _source; 
	}
	
	public NetworkAddr destination()
	{
		return _destination; 
	}
	
	public int seq()
	{
		return _seq; 
	}

	public void entering(SimEnt locale)
	{
	}
}
	
