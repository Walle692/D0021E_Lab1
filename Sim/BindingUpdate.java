package Sim;

import java.util.TimerTask;

public class BindingUpdate extends Message {

    private int _lifetime;
    private Node _node;
    private NetworkAddr _hoa;

    BindingUpdate (NetworkAddr from, NetworkAddr to, int seq, int lifetime, Node node, NetworkAddr homeAdress)
    {
        super(from, to, seq);
        _lifetime = lifetime;
        _node = node;
        _hoa = homeAdress;
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