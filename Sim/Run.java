package Sim;

// An example of how to build a topology and starting the simulation engine

public class Run {
	public static void main (String [] args)
	{

		Link link1 = new Link();
 		Link link2 = new Link();
		Link link3 = new Link();


		

		NodeGaussian host1 = new NodeGaussian(1, 1);
		NodePoisson host2 = new NodePoisson(2,1);
		Node host3 = new Node(3,1);


		//Connect links to hosts
		host1.setPeer(link1);
		host2.setPeer(link2);
		host3.setPeer(link3);

		// Creates as router and connect
		// links to it. Information about 
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(3);
		routeNode.connectInterface(0, link1, host1);
		routeNode.connectInterface(1, link2, host2);
		routeNode.connectInterface(2, link3, host3);
		
		// Generate some traffic

		//host1.StartSending(3, 1, 5, 3, 10, 0);
		host2.poissonStartSending(3,1, 5, 10, 0);
		//host2.StartSending(3, 1, 5, 5, 10);

		// host2 will send 2 messages with time interval 10 to network 1, node 1. Sequence starts with number 10
		//host2.StartSending(1, 1, 2, 10, 10);
		
		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());
	
		t.start();
		try
		{
			t.join();
		}
		catch (Exception e)
		{
			System.out.println("The motor seems to have a problem, time for service?");
		}		



	}
}
