package Sim;

// An example of how to build a topology and starting the simulation engine


public class Run {
	public static void main (String [] args)
	{
		new java.io.File("gauss.csv").delete();
		new java.io.File("out.csv").delete();
		new java.io.File("poisson.csv").delete();
		new java.io.File("uniform.csv").delete();

		Link link1 = new Link();
 		Link link2 = new Link();
		Link link3 = new Link();
		Link link4 = new Link();





		Node host1 = new Node(1, 1);
		Node host2 = new Node(2, 1);
		Node host3 = new Node(3, 1);
		Node host4 = new Node(4, 1);
		//NodePoisson host2 = new NodePoisson(2,1);
		//Node host3 = new Node(3,1);


		//Connect links to hosts
		host1.setPeer(link1);
		host2.setPeer(link2);
		host3.setPeer(link3);
		host4.setPeer(link4);

		// Creates as router and connect
		// links to it. Information about 
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(4);
		routeNode.connectInterface(0, link1, host1);
		routeNode.connectInterface(1, link2, host2);
		routeNode.connectInterface(2, link3, host3);
		routeNode.connectInterface(3, link4, host4);
		
		// Generate some traffic

		host1.StartSendingGauss(4, 1, 100, 5, 10, 1000);
		host2.poissonStartSending(4,1, 100, 5, 2000);
		host3.StartSending(4, 1, 100, 5, 3000);

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
