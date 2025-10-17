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

        Router homerouter = new Router(4,1, 10);

        homerouter.connectInterface(0, stableLink, stableNode);
        homerouter.connectInterface(1, moverLink, moverNode);

        stableNode.setPeer(stableLink);
        moverNode.setPeer(moverLink);


        //Setting up the "foreign network"
        Link foreignLink = new Link();
        Node foreignNode = new Node(21, 3);

        foreignNode.setPeer(foreignLink);

        Router foreignRouter = new Router(4,2, 20);

        foreignRouter.connectInterface(0, foreignLink, foreignNode);

        //connecting the networks
        Link inbetweenLink = new Link();




		// Generate some traffic
        stableNode.StartSending(11, 2, 100, 1, 0);



		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());
	
		t.start();

		try
		{
            t.sleep(5);
            foreignRouter.connectInterface(2, inbetweenLink, homerouter);
            homerouter.connectInterface(2, inbetweenLink, foreignRouter);
            //t.sleep(30);
            //homerouter.connectInterface(2, moverLink, moverNode);
            t.join(3000);
		}
		catch (Exception e)
		{
			System.out.println("The motor seems to have a problem, time for service?");
		}		



	}
}
