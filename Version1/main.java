public class main {


    public static void main(String[] args) {
        GUI gui = new GUI();
        int nbProc = 10 ;

        gui.addProc("   P1", "   0", "   Initialisé");
        gui.addProc("   P2", "   0", "   Initialisé");
        gui.addProc("   P3", "   0", "   Initialisé");
        gui.addProc("   P4", "   0", "   Initialisé");
        gui.addProc("   P5", "   0", "   Initialisé");
        gui.addProc("   P6", "   0", "   Initialisé");
        gui.addProc("   P7", "   0", "   Initialisé");
        gui.addProc("   P8", "   0", "   Initialisé");
        gui.addProc("   P9", "   0", "   Initialisé");
        gui.addProc("   P10", "   0", "   Initialisé");
  
   
        Pi p1 = new Pi(1, gui , nbProc);
        Pi p2 = new Pi(2, gui , nbProc);
        Pi p3 = new Pi(3, gui , nbProc);
        Pi p4 = new Pi(4, gui , nbProc);
        Pi p5 = new Pi(5, gui , nbProc);
        Pi p6 = new Pi(6, gui , nbProc);
        Pi p7 = new Pi(7, gui , nbProc);
        Pi p8 = new Pi(8, gui , nbProc);
        Pi p9 = new Pi(9, gui , nbProc);
        Pi p10 = new Pi(10, gui , nbProc);
  
        
        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(p2);
        Thread t3 = new Thread(p3);
        Thread t4 = new Thread(p4);
        Thread t5 = new Thread(p5);
        Thread t6 = new Thread(p6);
        Thread t7 = new Thread(p7);
        Thread t8 = new Thread(p8);
        Thread t9 = new Thread(p9);
        Thread t10 = new Thread(p10);

       
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        t9.start();
        t10.start();

        Coordinator coordinator = new Coordinator(nbProc, gui );
        Thread coordinatorThread = new Thread(coordinator);
        coordinatorThread.start();

    }
}
