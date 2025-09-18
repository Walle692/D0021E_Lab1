package Sim;

import java.util.Random;

public class LossyLink extends Link {

    private int delay;
    private double jitter;
    private double drop;

    private int test = 0;

    Random r = new Random(2);

    public LossyLink(int delay, double jitter, double drop){
        super();
        this.delay = delay;
        this.jitter = jitter;
        this.drop = drop;
    }

    private boolean dropOrNot(){
        boolean dropBoolean;

        double r1 = r.nextDouble();

        dropBoolean = r1 < drop;

        return dropBoolean;

    }

    private double jitterTime(){
        double jitterTime;
        jitterTime = r.nextDouble(-jitter, +jitter);
        return jitterTime;
    }



    @Override
    public void recv(SimEnt src, Event ev)
    {
        if (ev instanceof Message)
        {
            if(this.dropOrNot()){
                System.out.println("Link lost msg, HASKDHASKLDH");
                return;
            }
            System.out.println("Link recv msg, passes it through");
            if (src == _connectorA) {
                send(_connectorB, ev, _now + delay + jitterTime());
            } else {
                send(_connectorA, ev, _now + delay + jitterTime());
            }

        }
    }
}
