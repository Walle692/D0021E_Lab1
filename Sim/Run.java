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
		Link link4 = new Link();

		Node host1 = new Node(1, 1);
		Node host2 = new Node(2, 2);



		//Connect links to hosts
		host1.setPeer(link1);

		host2.setPeer(link4);

		// Creates as router and connect
		// links to it. Information about 
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(4);

		routeNode.connectInterface(0, link1, host1);
		routeNode.connectInterface(3, link4, host2);
		
		// Generate some traffic

        host1.StartSending(2, 2, 50, 5, 0);

		
		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());
	
		t.start();



        routeNode.connectInterface(2, link4, host2);

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
