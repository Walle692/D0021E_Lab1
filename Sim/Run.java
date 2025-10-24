package Sim;

// An example of how to build a topology and starting the simulation engine


public class Run {
	public static void main (String [] args)
	{
		new java.io.File("gauss.csv").delete();
		new java.io.File("out.csv").delete();
		new java.io.File("poisson.csv").delete();
		new java.io.File("uniform.csv").delete();

		final boolean LOOP = true; // <-- set to false to disable the C–E–D loop

		// === Network A (10.x) ===
		Link aHost1Link = new Link();
		Node aHost1 = new Node(10, 1);   // 10.1

		Link aHost2Link = new Link();
		Node aHost2 = new Node(10, 2);   // 10.2

		Router routerA = new Router(10, 10); // plenty of ifaces

		routerA.connectInterface(0, aHost1Link, aHost1); // A-if0 ↔ A host1
		routerA.connectInterface(1, aHost2Link, aHost2); // A-if1 ↔ A host2
		aHost1.setPeer(aHost1Link);
		aHost2.setPeer(aHost2Link);

		// === Network B (20.x) ===
		Link bHost1Link = new Link();
		Node bHost1 = new Node(20, 1);   // 20.1
		bHost1.setPeer(bHost1Link);

		Router routerB = new Router(20, 4); // B-if0..3
		routerB.connectInterface(0, bHost1Link, bHost1); // B-if0 ↔ B host1

		// === A ↔ B ===
		Link abBackboneLink = new Link();
		routerA.connectInterface(2, abBackboneLink, routerB); // A-if2
		routerB.connectInterface(2, abBackboneLink, routerA); // B-if2

		// === Network C (30.x) ===
		Link cHost1Link = new Link();
		Node cHost1 = new Node(30, 1);   // 30.1
		Router routerC = new Router(30, 4); // C-if0..3 (needs extra iface for E)
		routerC.connectInterface(0, cHost1Link, cHost1); // C-if0 ↔ C host1
		cHost1.setPeer(cHost1Link);

		// === B ↔ C ===
		Link bcBackboneLink = new Link();
		routerB.connectInterface(1, bcBackboneLink, routerC); // B-if1
		routerC.connectInterface(1, bcBackboneLink, routerB); // C-if1

		// === Network D (40.x) ===
		Link dHost1Link = new Link();
		Node dHost1 = new Node(40, 1);   // 40.1
		dHost1.setPeer(dHost1Link);

		Router routerD = new Router(40, 4); // D-if0..3
		routerD.connectInterface(0, dHost1Link, dHost1); // D-if0 ↔ D host1

		// === C ↔ D (line A–B–C–D) ===
		Link cdBackboneLink = new Link();
		routerC.connectInterface(2, cdBackboneLink, routerD); // C-if2
		routerD.connectInterface(1, cdBackboneLink, routerC); // D-if1

		// === OPTIONAL SMALL LOOP: C — E — D ===
		Router routerE = null;
		Link eHost1Link = null;
		Node eHost1 = null;
		if (LOOP) {
			// Router E (50.x) + optional host
			eHost1Link = new Link();
			eHost1 = new Node(50, 1); // 50.1
			eHost1.setPeer(eHost1Link);

			routerE = new Router(50, 3); // E-if0..2
			routerE.connectInterface(0, eHost1Link, eHost1); // E-if0 ↔ E host1

			// C ↔ E
			Link ceLink = new Link();
			routerC.connectInterface(3, ceLink, routerE); // C-if3
			routerE.connectInterface(1, ceLink, routerC); // E-if1

			// E ↔ D
			Link edLink = new Link();
			routerE.connectInterface(2, edLink, routerD); // E-if2
			routerD.connectInterface(2, edLink, routerE); // D-if2
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
