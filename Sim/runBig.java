package Sim;

public class runBig {
    public static void main(String[] args) {
        // wipe prior artifacts (optional)
        new java.io.File("gauss.csv").delete();
        new java.io.File("out.csv").delete();
        new java.io.File("poisson.csv").delete();
        new java.io.File("uniform.csv").delete();

        // ===== knobs =====
        final int TO_NET  = 100; // target host network (e.g., 60 = F:60.1)
        final int TO_NODE = 1;  // target host node id
        // =================

        // Routers A..J (10,20,...,100) — give each plenty of ports
        Router rA = new Router(10, 10);
        Router rB = new Router(20, 10);
        Router rC = new Router(30, 10);
        Router rD = new Router(40, 10);
        Router rE = new Router(50, 10);
        Router rF = new Router(60, 10);
        Router rG = new Router(70, 10);
        Router rH = new Router(80, 10);
        Router rI = new Router(90, 10);
        Router rJ = new Router(100,10);

        // One host per network: <net>.1  (iface 0 reserved for local host)
        Link aH = new Link(); Node aHost1 = new Node(10, 1, 10); rA.connectInterface(0, aH, aHost1); aHost1.setPeer(aH);
        Link bH = new Link(); Node bHost1 = new Node(20, 2, 20); rB.connectInterface(0, bH, bHost1); bHost1.setPeer(bH);
        Link cH = new Link(); Node cHost1 = new Node(30, 3, 30); rC.connectInterface(0, cH, cHost1); cHost1.setPeer(cH);
        Link dH = new Link(); Node dHost1 = new Node(40, 4, 40); rD.connectInterface(0, dH, dHost1); dHost1.setPeer(dH);
        Link eH = new Link(); Node eHost1 = new Node(50, 5, 50); rE.connectInterface(0, eH, eHost1); eHost1.setPeer(eH);
        Link fH = new Link(); Node fHost1 = new Node(60, 6, 60); rF.connectInterface(0, fH, fHost1); fHost1.setPeer(fH);
        Link gH = new Link(); Node gHost1 = new Node(70, 7, 70); rG.connectInterface(0, gH, gHost1); gHost1.setPeer(gH);
        Link hH = new Link(); Node hHost1 = new Node(80, 8, 80); rH.connectInterface(0, hH, hHost1); hHost1.setPeer(hH);
        Link iH = new Link(); Node iHost1 = new Node(90, 9, 90); rI.connectInterface(0, iH, iHost1); iHost1.setPeer(iH);
        Link jH = new Link(); Node jHost1 = new Node(100,10, 100); rJ.connectInterface(0, jH, jHost1); jHost1.setPeer(jH);

        // ========= Inter-router links (first big topology) =========
        // Keep exactly one link per router-pair (your Router.connectInterface duplicate check doesn’t like re-pairs)

        // A — B
        Link ab = new Link(); rA.connectInterface(1, ab, rB); rB.connectInterface(1, ab, rA);

        // B — C
        Link bc = new Link(); rB.connectInterface(2, bc, rC); rC.connectInterface(1, bc, rB);

        // B — D
        Link bd = new Link(); rB.connectInterface(3, bd, rD); rD.connectInterface(1, bd, rB);

        // B — E   (cross that helps close a big loop via H–G–C)
        Link be = new Link(); rB.connectInterface(4, be, rE); rE.connectInterface(3, be, rB);

        // C — G
        Link cg = new Link(); rC.connectInterface(2, cg, rG); rG.connectInterface(1, cg, rC);

        // G — H
        Link gh = new Link(); rG.connectInterface(2, gh, rH); rH.connectInterface(1, gh, rG);

        // H — E   (closes the big loop B–C–G–H–E–B)
        Link he = new Link(); rH.connectInterface(2, he, rE); rE.connectInterface(4, he, rH);

        // E — F
        Link ef = new Link(); rE.connectInterface(2, ef, rF); rF.connectInterface(1, ef, rE);

        // F — D   (small loop D–E–F–D)
        Link fd = new Link(); rF.connectInterface(2, fd, rD); rD.connectInterface(2, fd, rF);

        // D — I
        Link di = new Link(); rD.connectInterface(3, di, rI); rI.connectInterface(1, di, rD);

        // I — J
        Link ij = new Link(); rI.connectInterface(2, ij, rJ); rJ.connectInterface(1, ij, rI);

        // ========= BRUTE-FORCE TEST: everyone sends to the same host (TO_NET.TO_NODE) =========
        final int dstNet  = TO_NET;
        final int dstNode = TO_NODE;

        aHost1.StartSending(dstNet, dstNode, 2, 1, 10);
        bHost1.StartSending(dstNet, dstNode, 2, 1, 20);
        cHost1.StartSending(dstNet, dstNode, 2, 1, 30);
        dHost1.StartSending(dstNet, dstNode, 2, 1, 40);
        eHost1.StartSending(dstNet, dstNode, 2, 1, 50);
        fHost1.StartSending(dstNet, dstNode, 2, 1, 60); // ignored if dst is 60.1 (self)
        gHost1.StartSending(dstNet, dstNode, 2, 1, 70);
        hHost1.StartSending(dstNet, dstNode, 2, 1, 80);
        iHost1.StartSending(dstNet, dstNode, 2, 1, 90);
        jHost1.StartSending(dstNet, dstNode, 2, 1, 100);

        // ===== run engine =====
        Thread t = new Thread(SimEngine.instance());
        t.start();
        try {
            t.sleep(30);
            rA.disconnectInterface(0); // disconnect A:10.1
            rB.connectInterface(2, aH, aHost1); // reconnect A:10.1 to B:20.2
            aHost1.updateNetworkaddr(20); //update aHost1's network address to reflect new network
            aHost1.sendBU(); //send binding update to home agent
            t.join(3000);
        } catch (Exception e) {
            System.out.println("The motor seems to have a problem, time for service?");
        }
    }
}
