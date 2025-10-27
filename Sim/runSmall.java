package Sim;

// An example of how to build a topology and starting the simulation engine


public class runSmall {
	public static void main (String [] args)
	{
		new java.io.File("gauss.csv").delete();
		new java.io.File("out.csv").delete();
		new java.io.File("poisson.csv").delete();
		new java.io.File("uniform.csv").delete();


		// === Network A (10.x) ===
		Link aHost1Link = new Link();
		Node aHost1 = new Node(10, 1, 10);   // 10.1

		Link aHost2Link = new Link();
		Node aHost2 = new Node(10, 2, 10);   // 10.2

		Router routerA = new Router(10, 10); // plenty of ifaces

		routerA.connectInterface(0, aHost1Link, aHost1); // A-if0 ↔ A host1
		routerA.connectInterface(1, aHost2Link, aHost2); // A-if1 ↔ A host2
		aHost1.setPeer(aHost1Link);
		aHost2.setPeer(aHost2Link);

		// === Network B (20.x) ===
		Link bHost1Link = new Link();
		Node bHost1 = new Node(20, 3,20);   // 20.1
		bHost1.setPeer(bHost1Link);

		Router routerB = new Router(20, 4); // B-if0..3
		routerB.connectInterface(0, bHost1Link, bHost1); // B-if0 ↔ B host1

		// === A ↔ B ===
		Link abBackboneLink = new Link();
		routerA.connectInterface(2, abBackboneLink, routerB); // A-if2
		routerB.connectInterface(2, abBackboneLink, routerA); // B-if2

		// === Network C (30.x) ===
		Link cHost1Link = new Link();
		Node cHost1 = new Node(30, 4, 30);   // 30.1
		Router routerC = new Router(30, 4); // C-if0..3 (needs extra iface for E)
		routerC.connectInterface(0, cHost1Link, cHost1); // C-if0 ↔ C host1
		cHost1.setPeer(cHost1Link);

		// === B ↔ C ===
		Link bcBackboneLink = new Link();
		routerB.connectInterface(1, bcBackboneLink, routerC); // B-if1
		routerC.connectInterface(1, bcBackboneLink, routerB); // C-if1

        // === C ↔ A ===
        Link caBackboneLink = new Link();
        routerC.connectInterface(4, caBackboneLink, routerA);
        routerA.connectInterface(8, caBackboneLink, routerC);




        cHost1.StartSending(10, 2, 100, 1, 0);
        aHost2.StartSending(30, 4, 100, 2, 2000);



		Thread t = new Thread(SimEngine.instance());
		t.start();

		try {
			t.sleep(30);
            // == Move node 10.2 to network 20 ==
            routerA.disconnectInterface(1); // disconnect aHost2
            aHost2.updateNetworkaddr(20); //update its address to new network
            Link aHost2Link2 = new Link(); //create a new link
            aHost2.setPeer(aHost2Link2); //set its peer to the new
            routerB.connectInterface(3, aHost2Link2, aHost2); // connect to routerB
            aHost2.sendBU(); //send binding update to its home agent
            t.sleep(5);
            Link aHost2Link3 = new Link();
            routerB.disconnectInterface(3); // disconnect aHost2 again
            aHost2.updateNetworkaddr(10); //update its address back to original network
            aHost2.setPeer(aHost2Link3); //set its peer to the new link
            routerA.connectInterface(1, aHost2Link3, aHost2); // reconnect to routerA
            aHost2.sendBU(); //send binding update to its home agent

			t.join(3000);
		} catch (Exception e) {
			System.out.println("The motor seems to have a problem, time for service?");
		}
	}

}
