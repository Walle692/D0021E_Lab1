package Sim;

// An example of how to build a topology and starting the simulation engine


public class Run {
	public static void main (String [] args)
	{
		new java.io.File("gauss.csv").delete();
		new java.io.File("out.csv").delete();
		new java.io.File("poisson.csv").delete();
		new java.io.File("uniform.csv").delete();

		Link stableLink = new Link();
        Link moverLink = new Link();

		Node stableNode = new Node(1, 1);
		Node moverNode = new Node(3, 2);



		//Connect links to hosts
        stableNode.setPeer(stableLink);
        moverNode.setPeer(moverLink);

		// Creates as router and connect
		// links to it. Information about 
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(4);

        routeNode.connectInterface(0, stableLink, stableNode);
        routeNode.connectInterface(1, moverLink, moverNode);

		
		// Generate some traffic
        stableNode.StartSending(3, 2, 40, 1, 0);
		
		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());
	
		t.start();

		try
		{
            t.sleep(30);
            routeNode.connectInterface(2, moverLink, moverNode);
            t.join(3000);
		}
		catch (Exception e)
		{
			System.out.println("The motor seems to have a problem, time for service?");
		}		



	}
}
