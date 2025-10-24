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

        homerouter.connectInterface(2, inbetweenLink, foreignRouter);
        foreignRouter.connectInterface(2, inbetweenLink, homerouter);


		// move node to foreign network
        // creating new moverlink bc reasons
        Link ml2 = new Link();
        moverNode.setPeer(ml2);


        homerouter.disconnectInterface(1);
        foreignRouter.connectInterface(1, ml2, moverNode);


		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());
	
		t.start();

		try
		{

            t.sleep(2000);
            moverNode.set_Network(22);
            t.sleep(2000);


            t.join(3000);
		}

		// === Kick off discovery (use whichever routers you want) ===

		//routerA.RS();
//		routerB.RS();
//		routerC.RS();
//		routerD.RS();
//		routerA.RS();

		//if (LOOP) routerE.RS();

		// === Traffic tests ===
		// C host → B host

		// From A:10.1
//		aHost1.StartSending(10, 2, 2, 1, 0);  // 10.1 -> 10.2
//		aHost1.StartSending(20, 1, 2, 1, 0);  // 10.1 -> 20.1
//		aHost1.StartSending(30, 1, 2, 1, 0);  // 10.1 -> 30.1
		aHost1.StartSending(10, 1, 2, 1, 0);  // 10.1 -> 40.1

// From A:10.2
//		aHost2.StartSending(10, 1, 2, 1, 0);  // 10.2 -> 10.1
//		aHost2.StartSending(20, 1, 2, 1, 0);  // 10.2 -> 20.1
//		aHost2.StartSending(30, 1, 2, 1, 0);  // 10.2 -> 30.1
		aHost2.StartSending(10, 1, 2, 1, 0);  // 10.2 -> 40.1

// From B:20.1
//		bHost1.StartSending(10, 1, 2, 1, 0);  // 20.1 -> 10.1
//		bHost1.StartSending(10, 2, 2, 1, 0);  // 20.1 -> 10.2
//		bHost1.StartSending(30, 1, 2, 1, 0);  // 20.1 -> 30.1
		bHost1.StartSending(10, 1, 2, 1, 0);  // 20.1 -> 40.1

// From C:30.1
//		cHost1.StartSending(10, 1, 2, 1, 0);  // 30.1 -> 10.1
//		cHost1.StartSending(10, 2, 2, 1, 0);  // 30.1 -> 10.2
//		cHost1.StartSending(20, 1, 2, 1, 0);  // 30.1 -> 20.1
		cHost1.StartSending(10, 1, 2, 1, 0);  // 30.1 -> 40.1

// From D:40.1
//		dHost1.StartSending(10, 1, 2, 1, 0);  // 40.1 -> 10.1
//		dHost1.StartSending(10, 2, 2, 1, 0);  // 40.1 -> 10.2
//		dHost1.StartSending(20, 1, 2, 1, 0);  // 40.1 -> 20.1
		dHost1.StartSending(10, 1, 2, 1, 0);  // 40.1 -> 30.1



		Thread t = new Thread(SimEngine.instance());
		t.start();

		try {
			t.sleep(30);
			t.join(3000);
		} catch (Exception e) {
			System.out.println("The motor seems to have a problem, time for service?");
		}
	}

}
