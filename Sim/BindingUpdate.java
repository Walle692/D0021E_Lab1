package Sim;

import java.util.TimerTask;

public class BindingUpdate implements Event {

    private NetworkAddr _source;
    private NetworkAddr _destination;
    private int _seq=0;
    private int _lifetime;
    private Node _node;
    private NetworkAddr _hoa;

    BindingUpdate (NetworkAddr from, NetworkAddr to, int seq, int lifetime, Node node, NetworkAddr homeAdress)
    {
        _source = from;
        _destination = to;
        _seq = seq;
        _lifetime = lifetime;
        _node = node;
        _hoa = homeAdress;
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

    public NetworkAddr get_hoa() {
        return _hoa;
    }

    public int get_lifetime() {
        return _lifetime;
    }
    public Node node() {
        return _node;
    }

    public void entering(SimEnt locale)
    {
    }
}