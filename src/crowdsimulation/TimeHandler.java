package crowdsimulation;

import crowd.sim.HlaInteractionManager;
import crowd.sim.datatypes.TimeType;
import sun.awt.Mutex;

import java.util.Iterator;

public class TimeHandler extends Thread {

    private TimeType currentPhase;
    public Mutex mutex = new Mutex();
    public static TimeHandler instance;

    void advanceSimulation(TimeType phase) throws Exception {

        try {
            mutex.lock();
            try {
                /*
                 Make sure the given phase is not equal to this simulation's phase. Otherwise
                 the simulation executes the same phase multiple times in a tick.
                * */
                if (currentPhase == phase) {
                    return;
                }

                switch (phase) {
                    case PREPARE:

                        // Make every agent decide where it wants to go next
                        for (Agent agent : CrowdSimulation.agents) {
                            agent.prepare();
                        }

                        break;
                    case ADVANCE:
                        // Make every agent set there previously chosen step
                        for (Agent agent : CrowdSimulation.agents) {
                            agent.advance();
                        }

                        break;
                    case COMPLETE:
                        CrowdSimulation.agents.removeIf(a -> a.state == Agent.State.TRANSFERRED);
                        break;
                }

                GUI.drawOccupiers();
                currentPhase = phase;
                sendTimeGrantCallback(phase, true);
            } finally {
                mutex.unlock();
            }
        } catch (InterruptedException ie) {
            // ...
        }
    }

    public static void freeze() {
        //System.out.println("thread frozen");
        instance.interrupt();

        //instance.interrupt();
    }

    public static void unfreeze() {
        //System.out.println("thread unfrozen");
        instance.resume();
    }

    // The the federation this federate is done with the given timephase
    public void sendTimeGrantCallback(TimeType phase, boolean ready) throws Exception {
        HlaInteractionManager.HlaTimeGrantInteraction timeGrantCallback =
                CrowdSimulation.world.getHlaInteractionManager().getHlaTimeGrantInteraction();

        timeGrantCallback.setType(phase);
        timeGrantCallback.setReady(ready);
        timeGrantCallback.sendInteraction();
       // System.out.println("Sending callback \n------------------------");
    }
}
