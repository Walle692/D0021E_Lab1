package Sim;


public class RouterAdvertismentMessage implements Event{
    private NetworkAddr _source;
    private NetworkAddr _destination;

    RouterAdvertismentMessage (NetworkAddr from, NetworkAddr to)
    {
        _source = from;
        _destination = to;
    }

    public NetworkAddr source()
    {
        return _source;
    }

    public NetworkAddr destination()
    {
        return _destination;
    }


    public void entering(SimEnt locale)
    {
    }
}