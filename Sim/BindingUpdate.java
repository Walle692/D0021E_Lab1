package Sim;

import java.util.TimerTask;

public class BindingUpdate extends Message {

    private int _lifetime;
    private Node _node;

    BindingUpdate (NetworkAddr from, NetworkAddr to, int seq, int lifetime, Node node)
    {
        super(from, to, seq);
        _lifetime = lifetime;
        _node = node;
    }

    public NetworkAddr source()
    {
        return super.source();
    }

    public NetworkAddr destination()
    {
        return super.destination();
    }

    public int seq()
    {
        return super.seq();
    }

    public void entering(SimEnt locale)
    {
    }
}