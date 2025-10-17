package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence
// number

public class RouterSolicitationMessage implements Event{
    private NetworkAddr _source;
    //private NetworkAddr _destination;

    RouterSolicitationMessage (NetworkAddr from)
    {
        _source = from;
        //_destination = to;
    }

    public NetworkAddr source()
    {
        return _source;
    }

    //public NetworkAddr destination()
    //{
    //     return _destination;
    //}

    public void entering(SimEnt locale)
    {
    }
}