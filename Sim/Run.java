package Sim;

// An example of how to build a topology and starting the simulation engine


public class Run {
	public static void main (String [] args)
	{
		new java.io.File("gauss.csv").delete();
		new java.io.File("out.csv").delete();
		new java.io.File("poisson.csv").delete();
		new java.io.File("uniform.csv").delete();

// === Network A (10.x) ===
		Link aHost1Link = new Link();
		Node aHost1 = new Node(10, 1);   // 10.1

		Link aHost2Link = new Link();
		Node aHost2 = new Node(10, 2);   // 10.2

		Router routerA = new Router(10, 3); // (homeNet=10, ifCount=3)  // iface 0..2

		routerA.connectInterface(0, aHost1Link, aHost1); // A-if0 ↔ A host1
		routerA.connectInterface(1, aHost2Link, aHost2); // A-if1 ↔ A host2

		aHost1.setPeer(aHost1Link);
		aHost2.setPeer(aHost2Link);


// === Network B (20.x) ===
		Link bHost1Link = new Link();
		Node bHost1 = new Node(20, 1);   // 20.1

		bHost1.setPeer(bHost1Link);

		Router routerB = new Router(20, 4); // (homeNet=20, ifCount=4)  // iface 0..3

		routerB.connectInterface(0, bHost1Link, bHost1); // B-if0 ↔ B host1


// === Inter-router link between Network A and B ===
		Link abBackboneLink = new Link();

		routerA.connectInterface(2, abBackboneLink, routerB); // A-if2 ↔ backbone
		routerB.connectInterface(2, abBackboneLink, routerA); // B-if2 ↔ backbone

// === Network C (30.x) ===
		Link cHost1Link = new Link();
		Node cHost1 = new Node(30, 1);   // 30.1

		Router routerC = new Router(30, 3); // iface 0..2

		routerC.connectInterface(0, cHost1Link, cHost1); // C-if0 ↔ C host1
		cHost1.setPeer(cHost1Link);

// === Inter-router link between Network B and C ===
		Link bcBackboneLink = new Link();

		routerB.connectInterface(1, bcBackboneLink, routerC); // B-if1 ↔ backbone to C
		routerC.connectInterface(1, bcBackboneLink, routerB); // C-if1 ↔ backbone to B


// === Inter-router link between Network A and C (rewired) ===
//		Link acBackboneLink = new Link();
//
//		routerA.connectInterface(2, acBackboneLink, routerC); // A-if2 ↔ backbone to C
//		routerC.connectInterface(2, acBackboneLink, routerA); // C-if2 ↔ backbone to A

		aHost1.StartSending(20, 1, 10, 1, 0);  // to 20.1, count=10, interval=1, start=0

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
