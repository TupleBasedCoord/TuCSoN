package alice.tucson.examples.timedDiningPhilos;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgent;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * A Dining Philosopher: thinks and eats in an endless loop.
 * 
 * @author s.mariani@unibo.it
 */
public class DiningPhilosopher extends TucsonAgent {

    private SynchACC acc;
    private final int chop1, chop2;
    private final TucsonTupleCentreId myTable;
    private final int time, step;

    public DiningPhilosopher(final String aid, final TucsonTupleCentreId table,
            final int left, final int right, final int eatingTime,
            final int eatingStep) throws TucsonInvalidAgentIdException {
        super(aid);
        this.myTable = table;
        this.chop1 = left;
        this.chop2 = right;
        this.time = eatingTime;
        this.step = eatingStep;
    }

    @Override
    public void operationCompleted(final ITucsonOperation arg0) {
        /*
         * 
         */
    }

    @Override
    protected void main() {
        this.acc = this.getContext();
        // Ugly but effective, pardon me...
        while (true) {
            this.say("Now thinking...");
            this.think();
            this.say("I'm hungry, let's try to eat something...");
            /*
             * Try to get needed chopsticks.
             */
            if (this.acquireChops()) {
                /*
                 * If successful eat.
                 */
                if (this.eat()) {
                    this.say("I'm done, wonderful meal :)");
                    /*
                     * Then release chops.
                     */
                    this.releaseChops();
                } else {
                    this.say("OMG my chopsticks disappeared!");
                }
            } else {
                this.say("I'm starving!");
            }
        }
    }

    private boolean acquireChops() {
        ITucsonOperation op = null;
        try {
            /*
             * NB: The 2 needed chopsticks are "perceived" as a single item by
             * the philosophers, while the coordination medium correctly handle
             * them separately.
             */
            op =
                    this.acc.in(
                            this.myTable,
                            LogicTuple.parse("chops(" + this.chop1 + ","
                                    + this.chop2 + ")"), null);
        } catch (final InvalidLogicTupleException e) {
            // TODO Properly handle Exception
        } catch (final TucsonOperationNotPossibleException e) {
            // TODO Properly handle Exception
        } catch (final UnreachableNodeException e) {
            // TODO Properly handle Exception
        } catch (final OperationTimeOutException e) {
            // TODO Properly handle Exception
        }
        if (op != null) {
            return op.isResultSuccess();
        }
        return false;
    }

    private boolean eat() {
        this.say("...gnam gnam...chomp chomp...munch munch...");
        ITucsonOperation op = null;
        try {
            for (int i = 0; i < (this.time / this.step); i++) {
                Thread.sleep(this.step);
                op =
                        this.acc.rdp(
                                this.myTable,
                                LogicTuple.parse("used(" + this.chop1 + ","
                                        + this.chop2 + ",_)"), null);
                if (!op.isResultSuccess()) {
                    break;
                }
            }
        } catch (final InterruptedException e) {
            // TODO Properly handle Exception
        } catch (final InvalidLogicTupleException e) {
            // TODO Properly handle Exception
        } catch (final TucsonOperationNotPossibleException e) {
            // TODO Properly handle Exception
        } catch (final UnreachableNodeException e) {
            // TODO Properly handle Exception
        } catch (final OperationTimeOutException e) {
            // TODO Properly handle Exception
        }
        if (op != null) {
            return op.isResultSuccess();
        }
        return false;
    }

    private void releaseChops() {
        try {
            this.acc.out(
                    this.myTable,
                    LogicTuple.parse("chops(" + this.chop1 + "," + this.chop2
                            + ")"), null);
        } catch (final InvalidLogicTupleException e) {
            // TODO Properly handle Exception
        } catch (final TucsonOperationNotPossibleException e) {
            // TODO Properly handle Exception
        } catch (final UnreachableNodeException e) {
            // TODO Properly handle Exception
        } catch (final OperationTimeOutException e) {
            // TODO Properly handle Exception
        }
    }

    private void think() {
        this.say("...mumble mumble...rat rat...mumble mumble...");
        try {
            Thread.sleep(5000);
        } catch (final InterruptedException e) {
            // TODO Properly handle Exception
        }
    }

}
