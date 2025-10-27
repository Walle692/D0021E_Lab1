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
    private int _homeAgent;



    Random r = new Random(3);

    public Node (int network, int node, int homeAgent)
    {
        super();
        _id = new NetworkAddr(network, node);
        _homeAgent = homeAgent;
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

    private boolean _losslessAccepted = false;
    private int _losslesslastack = 0;
    private int _losslessSentmsg = 0;
    private Message _lastSentMessage = null;
    private int _Timeout = 5;

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

    public void StartLossLessSending(int network, int node, int number){
        // this is used to start lossless sending
        this.currentMode = "LossLess";
        _stopSendingAfter = number;
        _toNetwork = network;
        _toHost = node;
        Message requestMsg = new Message(_id, new NetworkAddr(_toNetwork, _toHost), _stopSendingAfter, Message.MsgType.LT_REQUEST, 10);
        _lastSentMessage = requestMsg;
        send(_peer, requestMsg,0);
        System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" sent LT_REQUEST for packets: " + this._stopSendingAfter + " at time "+SimEngine.getTime());
        send(this, new TimerEvent(), 1);
    }

    public void updateNetworkaddr(int network){
        this._id = new NetworkAddr(network, _id.nodeId());
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

    public void sendBU(){
        send(_peer, new Message(_id, new NetworkAddr(_homeAgent, 0),0, Message.MsgType.BINDING_UPDATE, 2),0);
        System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" sent BU with seq: "+_seq + " at time "+SimEngine.getTime());
        _seq++;
    }

//**********************************************************************************

    // This method is called upon that an event destined for this node triggers.

    public void recv(SimEnt src, Event ev)
    {
        if (ev instanceof TimerEvent && this.currentMode.equals("LossLess"))
        {
            //if timeout is not zero, check if we can send next message, otherwise decrease timeout
            if (_Timeout > 0){
                // check if we have accepted the last sent message and not reached the limit
                if (_losslessAccepted && _losslessSentmsg < _stopSendingAfter && _losslesslastack == _losslessSentmsg + 1){
                    //if success, send next message
                    Message nextMsg = new Message(_id, new NetworkAddr(_toNetwork, _toHost), _losslesslastack, Message.MsgType.LT_MESSAGE, 10);
                    //reset timeout
                    _Timeout = 5;
                    //increment sent msg count
                    _losslessSentmsg++;
                    //save last sent message
                    _lastSentMessage = nextMsg;
                    System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_losslessSentmsg + " at time "+SimEngine.getTime());
                    send(_peer, nextMsg,0);
                    send(this, new TimerEvent(), 1);
                } else if (_losslessSentmsg >= _stopSendingAfter){
                    return;
                }
                else{
                    _Timeout--;
                    send(this, new TimerEvent(),1);
                }
            }
            //timeout reached zero, resend last message
            else{
                // reset timeout
                _Timeout = 5;
                // resend last message
                send(_peer, _lastSentMessage,0);
                // log resend
                System.out.println("timeout reached, Node "+_id.networkId()+ "." + _id.nodeId() +" resent message with seq: "+_lastSentMessage.seq() + " at time "+SimEngine.getTime());
                // schedule next timer event
                send(this, new TimerEvent(),1);
            }
        }

        else if (ev instanceof TimerEvent)
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
            Message m = (Message) ev;

            //check if the message is an LT_ACKNOWLEDGEMENT
            if(m.getType() == Message.MsgType.LT_ACKNOWLEDGEMENT){
                System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" received LT_ACK with seq: "+m.seq() + " at time "+SimEngine.getTime());
                //set accepted to true
                _losslessAccepted = true;
                //update last ack
                _losslesslastack = m.seq();
                //reset timeout
                _Timeout = 5;
                return;
            }

            // check if the message is an LT_REQUEST
            if(m.getType() == Message.MsgType.LT_REQUEST) {
                System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " received LT_REQUEST for packets: " + m.seq() + " at time " + SimEngine.getTime());
                //send LT_ACKNOWLEDGEMENT seqnr 1 to the requester
                Message ackMsg = new Message(_id, m.source(), 1, Message.MsgType.LT_ACKNOWLEDGEMENT, 10);
                send(_peer, ackMsg, 0);
                _lastSentMessage = ackMsg;
            }

            //check if the message is LT_MESSAGE
            if(m.getType() == Message.MsgType.LT_MESSAGE) {
                System.out.println("Node " + _id.networkId() + "." + _id.nodeId() + " received LT_MESSAGE with seq: " + m.seq() + " at time " + SimEngine.getTime());
                Message ackMsg = new Message(_id, m.source(), m.seq() + 1, Message.MsgType.LT_ACKNOWLEDGEMENT, 10);
                send(_peer, ackMsg, 0);
                _lastSentMessage = ackMsg;
            }

            //check if the message is a BINDING_ACKNOWLEDGEMENT
            if(m.getType() == Message.MsgType.BINDING_ACKNOWLEDGEMENT){
                System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" received BA with seq: "+m.seq() + " at time "+SimEngine.getTime());
                return;
            }

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("out.csv", true));
                writer.write(_id.networkId() + "." + _id.nodeId() + "," + ((Message) ev).source().networkId() +","+ ((Message) ev).seq() + ',' + SimEngine.getTime());
                writer.newLine();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime() + " from "+((Message) ev).source().networkId()+ "." + ((Message) ev).source().nodeId());

        }
    }
}
