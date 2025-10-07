package Sim;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

import java.util.Random;

import static java.lang.Math.abs;

public class NodeGaussian extends Node {
    private NetworkAddr _id;
    private SimEnt _peer;
    private int _sentmsg=0;
    private int _seq = 0;

    Random r = new Random(3);

    public NodeGaussian (int network, int node)
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
    private double median, stddev;


    private double nextGauss(double median, double stddev){
        double time = r.nextGaussian(this.median, this.stddev);
         if (time < 0){
             time = r.nextGaussian(this.median, this.stddev);
             this.nextGauss(this.median, this.stddev);
         }
         return time;
    }
    public void StartSending(int network, int node, int number, double timeInterval, double stddev, int startSeq)
    {

        this.median = timeInterval;
        this.stddev = stddev;

        _stopSendingAfter = number;
        _timeBetweenSending = nextGauss(timeInterval, stddev);
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
                this._timeBetweenSending = this.nextGauss(median, stddev);
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