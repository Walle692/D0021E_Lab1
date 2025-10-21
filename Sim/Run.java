package Sim;

// An example of how to build a topology and starting the simulation engine


public class Run {
	public static void main (String [] args)
	{
		new java.io.File("gauss.csv").delete();
		new java.io.File("out.csv").delete();
		new java.io.File("poisson.csv").delete();
		new java.io.File("uniform.csv").delete();

		//Setting up the "home network"
		Link stableLink = new Link();
		Link moverLink = new Link();

		Node stableNode = new Node(11, 1);
		Node moverNode = new Node(12, 2);

		Router homerouter = new Router(4);

		homerouter.connectInterface(0, stableLink, stableNode);
		homerouter.connectInterface(1, moverLink, moverNode);

		stableNode.setPeer(stableLink);
		moverNode.setPeer(moverLink);


		//Setting up the "foreign network"
		Link foreignLink = new Link();
		Node foreignNode = new Node(21, 3);

		foreignNode.setPeer(foreignLink);

		Router foreignRouter = new Router(3);

		foreignRouter.connectInterface(0, foreignLink, foreignNode);

		//connecting the networks
		Link inbetweenLink = new Link();

		homerouter.connectInterface(2, inbetweenLink, foreignRouter);
		foreignRouter.connectInterface(2, inbetweenLink, homerouter);


		// Generate some traffic


		stableNode.StartSending(21, 3, 100, 1, 0);

		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());

		t.start();

		try
		{
            t.sleep(30);
            t.join(3000);
		}
		catch (Exception e)
		{
			System.out.println("The motor seems to have a problem, time for service?");
		}		



	}
}
