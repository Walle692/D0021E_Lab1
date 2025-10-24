package Sim;


public class BindingAck implements Event {
    private NetworkAddr _source;
    private NetworkAddr _destination;
    private int _seq=0;


    BindingAck (NetworkAddr from, NetworkAddr to, int seq)
    {
        _source = from;
        _destination = to;
        _seq = seq;
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