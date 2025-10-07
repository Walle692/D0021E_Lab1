package Sim;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

import java.util.Random;

public class NodePoisson extends Node {
    private NetworkAddr _id;
    private SimEnt _peer;
    private int _sentmsg=0;
    private int _seq = 0;

    Random r = new Random(3);


    public NodePoisson (int network, int node)
    {
        super(network, node);
        _id = new NetworkAddr(network, node);
    }


    // Sets the peer to communicate with. This node is single homed

    public void setPeer (SimEnt peer)
    {
        _peer = peer;

        if(_peer instanceof Link )
        {
            ((Link) _peer).setConnector(this);
        }
    }

    public NetworkAddr getAddr()
    {
        return _id;
    }

    private int _stopSendingAfter = 0; //messages
    private double _timeBetweenSending = 0; //time between messages
    private int _toNetwork = 0;
    private int _toHost = 0;
    // Standard values
    private double median;

    private static int getPoissonRandom(double mean) {
        Random r = new Random();
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }


    public void poissonStartSending(int network, int node, int number, double mean, int startSeq)
    {

        this.median = mean;

        _stopSendingAfter = number;
        _timeBetweenSending = getPoissonRandom(mean);
        _toNetwork = network;
        _toHost = node;
        _seq = startSeq;
        send(this, new TimerEvent(),0);
    }



//**********************************************************************************

    // This method is called upon that an event destined for this node triggers.

    public void recv(SimEnt src, Event ev)
    {
        if (ev instanceof TimerEvent)
        {
            if (_stopSendingAfter > _sentmsg)
            {
                _sentmsg++;
                send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost),_seq),0);
                send(this, new TimerEvent(),_timeBetweenSending);
                this._timeBetweenSending = getPoissonRandom(this.median);
                System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+SimEngine.getTime());
                _seq++;
            }
        }
        if (ev instanceof Message)
        {
            System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());

        }
    }
}