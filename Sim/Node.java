package Sim;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Objects;
import java.util.Random;

public class Node extends SimEnt {
    private NetworkAddr _id;
    private SimEnt _peer;
    private int _sentmsg=0;
    private int _seq = 0;



    Random r = new Random(3);

    public Node (int network, int node)
    {
        super();
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

//**********************************************************************************
    // Just implemented to generate some traffic for demo.
    // In one of the labs you will create some traffic generators

    private int _stopSendingAfter = 0; //messages
    private double _timeBetweenSending = 0; //time between messages
    private int _toNetwork = 0;
    private int _toHost = 0;

    private double median, stddev;

    private String currentMode = "uniform";

    public void StartSending(int network, int node, int number, int timeInterval, int startSeq)
    {
        this.currentMode = "uniform";
        _stopSendingAfter = number;
        _timeBetweenSending = timeInterval;
        _toNetwork = network;
        _toHost = node;
        _seq = startSeq;
        send(this, new TimerEvent(),0);
    }

    public void StartSendingDelay(int network, int node, int number, int timeInterval, int startSeq, double delay)
    {
        this.currentMode = "uniform";
        _stopSendingAfter = number;
        _timeBetweenSending = timeInterval;
        _toNetwork = network;
        _toHost = node;
        _seq = startSeq;
        send(this, new TimerEvent(), delay);
    }

    private double nextGauss(){
        double time = r.nextGaussian(this.median, this.stddev);
        if (time < 0){
            time = r.nextGaussian(this.median, this.stddev);
            this.nextGauss();
        }
        return time;
    }
    public void StartSendingGauss(int network, int node, int number, double timeInterval, double stddev, int startSeq)
    {
        this.median = timeInterval;
        this.stddev = stddev;
        this.currentMode = "gauss";

        _stopSendingAfter = number;
        _timeBetweenSending = nextGauss();
        _toNetwork = network;
        _toHost = node;
        _seq = startSeq;
        send(this, new TimerEvent(),0);
    }


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
        this.currentMode = "poisson";
        this.median = mean;
        _stopSendingAfter = number;
        _timeBetweenSending = getPoissonRandom(mean);
        _toNetwork = network;
        _toHost = node;
        _seq = startSeq;
        send(this, new TimerEvent(),0);
    }

    private void updateTimeBetweenSending(){
        if (Objects.equals(this.currentMode, "uniform")){
            return;
        }
        if (Objects.equals(this.currentMode, "gauss")){
            this._timeBetweenSending = nextGauss();
        }
        if (Objects.equals(this.currentMode, "poisson")){
            this._timeBetweenSending = getPoissonRandom(this.median);
        }

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
                updateTimeBetweenSending();

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(currentMode + ".csv", true));
                    writer.write((_id.networkId() + "." + _id.nodeId() + "," + _seq + ',' + SimEngine.getTime()));
                    writer.newLine();
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+SimEngine.getTime());
                _seq++;
            }
        }
        if (ev instanceof Message)
        {

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("out.csv", true));
                writer.write(_id.networkId() + "." + _id.nodeId() + "," + ((Message) ev).source().networkId() +","+ ((Message) ev).seq() + ',' + SimEngine.getTime());
                writer.newLine();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());

        }
    }
}
