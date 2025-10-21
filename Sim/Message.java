package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number


public class Message implements Event{

	enum MsgType{PING, ROUTER_SOLICITATION, ROUTER_ADVERTISEMENT}

	private NetworkAddr _source;
	private NetworkAddr _destination;
	private int _seq=0;
	private MsgType _type;

	Message(NetworkAddr from, NetworkAddr to, int seq) {
		this(from, to, seq, MsgType.PING);
	}
	
	Message (NetworkAddr from, NetworkAddr to, int seq, MsgType type)
	{
		_source = from;
		_destination = to;
		_seq=seq;
		_type = type;
	}

	public MsgType getType() {
		return _type;
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
	
