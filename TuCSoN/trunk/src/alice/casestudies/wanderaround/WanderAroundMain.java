package alice.casestudies.wanderaround;

import java.io.IOException;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.respect.api.TupleCentreId;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * 
 * Main per il caso di studio "Wander Around".
 * 
 * @author Steven maraldi
 * 
 */

public class WanderAroundMain extends AbstractTucsonAgent {

    // ACC
    private static SynchACC acc;
    private static TupleCentreId tcAvoid;
    // Environment configuration tuple centre
    private static TupleCentreId tcId;
    // Actuators
    private static TupleCentreId tcM1; // Left servomotor actuator
    private static TupleCentreId tcM2; // Right servomotor actuator
    private static TupleCentreId tcMotor;
    private static TupleCentreId tcRunAway;
    // Application tuple centres
    private static TupleCentreId tcSonar;
    // Sensors
    private static TupleCentreId tcU1; // Front ultrasonic sensor
    private static TupleCentreId tcU2; // Right ultrasonic sensor

    private static TupleCentreId tcU3; // Back ultrasonic sensor

    private static TupleCentreId tcU4; // Left ultrasonic sensor

    public static void main(final String[] args) {
        try {
            WanderAroundMain.tcU1 =
                    new TupleCentreId("tc_u1", "localhost",
                            String.valueOf(20504));
            WanderAroundMain.tcU2 =
                    new TupleCentreId("tc_u2", "localhost",
                            String.valueOf(20504));
            WanderAroundMain.tcU3 =
                    new TupleCentreId("tc_u3", "localhost",
                            String.valueOf(20504));
            WanderAroundMain.tcU4 =
                    new TupleCentreId("tc_u4", "localhost",
                            String.valueOf(20504));
            WanderAroundMain.tcM1 =
                    new TupleCentreId("tc_m1", "localhost",
                            String.valueOf(20504));
            WanderAroundMain.tcM2 =
                    new TupleCentreId("tc_m2", "localhost",
                            String.valueOf(20504));
            WanderAroundMain.tcSonar =
                    new TupleCentreId("tc_sonar", "localhost",
                            String.valueOf(20504));
            WanderAroundMain.tcRunAway =
                    new TupleCentreId("tc_runaway", "localhost",
                            String.valueOf(20504));
            WanderAroundMain.tcMotor =
                    new TupleCentreId("tc_motor", "localhost",
                            String.valueOf(20504));
            WanderAroundMain.tcAvoid =
                    new TupleCentreId("tc_avoid", "localhost",
                            String.valueOf(20504));
            WanderAroundMain.tcId =
                    new TupleCentreId("envConfigTC", "localhost",
                            String.valueOf(20504));

            // Starting test
            final WanderAroundMain mainTest = new WanderAroundMain("main");
            mainTest.go();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void createAgents() throws TucsonInvalidAgentIdException {
        WanderAroundMain.speak("Creating agents");
        final AG_FeelForce ag_feelforce = new AG_FeelForce("ag_feelforce");
        final AG_Collide ag_collide = new AG_Collide("ag_collide");
        final AG_Wander ag_wander = new AG_Wander("ag_wander");

        WanderAroundMain.speak("Starting agents");
        ag_collide.go();
        ag_feelforce.go();
        ag_wander.go();
        WanderAroundMain.speak("Agents created and started");
    }

    private static void createTCs() throws IOException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        WanderAroundMain.speak("Creating TCs...");
        // Sensor's TCs
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.tcU1.toString() + " >...");
        LogicTuple specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcU1, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.tcU2.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcU2, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.tcU3.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcU3, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.tcU4.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcU4, specTuple, null);

        // Actuator's TCs
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.tcM1.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcAttuatoreLeft.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcM1, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.tcM2.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcAttuatoreRight.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcM2, specTuple, null);

        // TC Sonar
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.tcSonar.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcSonar.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcSonar, specTuple, null);

        // TC Run Away
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.tcRunAway.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcRunAway.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcRunAway, specTuple, null);

        // TC Motor
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.tcMotor.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcMotor.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcMotor, specTuple, null);

        // TC Avoid
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.tcAvoid.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcAvoid.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcAvoid, specTuple, null);
        WanderAroundMain.speak("TCs created");
    }

    private static void createTransducers()
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        WanderAroundMain.speak("Creating transducers");
        LogicTuple t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.tcU1.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerSens"),
                        new Value("tSensorFront"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_UltrasonicSensor"),
                        new Value("ultrasonicSensorFront"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.tcU2.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerSens"),
                        new Value("tSensorRight"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_UltrasonicSensor"),
                        new Value("ultrasonicSensorRight"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.tcU3.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerSens"),
                        new Value("tSensorBack"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_UltrasonicSensor"),
                        new Value("ultrasonicSensorBack"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.tcU4.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerSens"),
                        new Value("tSensorLeft"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_UltrasonicSensor"),
                        new Value("ultrasonicSensorLeft"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);

        t =
                new LogicTuple(
                        "createTransducerActuator",
                        new Value(WanderAroundMain.tcM1.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerAct"),
                        new Value("tMotorLeft"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_ServoMotorActuator"),
                        new Value("servoMotorActuatorLeft"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerActuator",
                        new Value(WanderAroundMain.tcM2.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerAct"),
                        new Value("tMotorRight"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_ServoMotorActuator"),
                        new Value("servoMotorActuatorRight"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        WanderAroundMain.speak("Transducers created");
    }

    private static void speak(final String msg) {
        System.out.println(msg);
    }

    public WanderAroundMain(final String id)
            throws TucsonInvalidAgentIdException {
        super(id);
    }

    @Override
    public void operationCompleted(final ITucsonOperation op) {
        /*
         * 
         */
    }

    @Override
    protected void main() {
        try {
            WanderAroundMain.acc = this.getContext();

            WanderAroundMain.speak("Configuring application environment");

            WanderAroundMain.createTCs();
            WanderAroundMain.createTransducers();

            Thread.sleep(1500);

            WanderAroundMain.createAgents();

            System.out.println("Configuration complete");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
