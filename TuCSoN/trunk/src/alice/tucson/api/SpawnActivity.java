package alice.tucson.api;

import java.io.Serializable;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.service.TucsonIdWrapper;
import alice.tucson.service.TucsonOperation;
import alice.tucson.service.TupleCentreContainer;

/**
 * The "parallel computation" to be started with a <code>spawn</code> primitive.
 * The spawned activity should be a PURELY COMPUTATIONAL (algorithmic) process,
 * with the purpose to delegate computations to the coordination medium. For
 * this reason, a set of "constrained" Linda primitives are provided: they
 * CANNOT access a remote space. Furthermore, the programmer is strongly
 * encouraged not to put communications, locks or other potentially
 * "extra-algorithmic" features in its SpawnActivity.
 * 
 * @author ste (mailto: s.mariani@unibo.it)
 */
public abstract class SpawnActivity implements Serializable, Runnable {

    private static final long serialVersionUID = -6354837455366449916L;
    private TucsonAgentId aid;
    private TucsonTupleCentreId target;
    private TucsonTupleCentreId tcid;

    /**
     * Checks if the activity to spawn has been correctly instantiated.
     * 
     * @return true if instantiation is complete, false otherwise.
     */
    public final boolean checkInstantiation() {
        if (((this.aid != null) || (this.tcid != null))
                && (this.target != null)) {
            return true;
        }
        return false;
    }

    /**
     * To be overridden by user
     */
    abstract public void doActivity();

    /**
     * Both agents and the coordination medium itself can <code>spawn</code> a
     * computation, hence we need to handle both.
     * 
     * @return the "spawner" id (actually, a generic wrapper hosting either a
     *         TucsonAgentId or a TucsonTupleCentreId, accessible with method
     *         <code>getId()</code>)
     * 
     * @see alice.tucson.service.TucsonIdWrapper TucsonIdWrapper
     */
    public final TucsonIdWrapper<?> getSpawnerId() {
        if (this.aid == null) {
            return new TucsonIdWrapper<TucsonTupleCentreId>(this.tcid);
        }
        return new TucsonIdWrapper<TucsonAgentId>(this.aid);
    }

    /**
     * Gets the tuplecentre identifier hosting the spawned activity.
     * 
     * @return the identifier of the tuplecentre hosting the spawned activity.
     */
    public final TucsonTupleCentreId getTargetTC() {
        return this.target;
    }

    /**
     * Called by the ReSpecT engine.
     */
    public final void run() {
        if (this.checkInstantiation()) {
            this.doActivity();
        }
    }

    /**
     * Linda operations used in the spawned activity are performed ON BEHALF of
     * the agent who issued the <code>spawn</code> (its "owner").
     * 
     * @param id
     *            the identifier of the agent "owner" of the spawned activity.
     */
    public final void setSpawnerId(final TucsonAgentId id) {
        this.aid = id;
        this.tcid = null;
    }

    /**
     * Linda operations used in the spawned activity are performed ON BEHALF of
     * the tuplecentre who issued the <code>spawn</code> (its "owner").
     * 
     * @param id
     *            the identifier of the tuplecentre "owner" of the spawned
     *            activity.
     */
    public final void setSpawnerId(final TucsonTupleCentreId id) {
        this.aid = null;
        this.tcid = id;
    }

    /**
     * The tuplecentre target, which will "host" the spawned computation. It is
     * automagically set by the ReSpecT engine.
     * 
     * @param id
     *            the identifier of the tuplecentre target of the spawned
     *            activity.
     */
    public final void setTargetTC(final TucsonTupleCentreId id) {
        this.target = id;
    }

    /**
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    protected final LogicTuple in(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.inCode(), this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.inCode(),
                                this.tcid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    protected final List<LogicTuple> in_all(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (List<LogicTuple>) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.in_allCode(),
                                this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (List<LogicTuple>) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.in_allCode(),
                                this.tcid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    protected final LogicTuple inp(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.inpCode(),
                                this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.inpCode(), this.tcid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * Standard output log utility.
     * 
     * @param msg
     *            the message to log on standard output.
     */
    protected void log(final String msg) {
        if (this.aid != null) {
            System.out.println("[" + this.aid.getAgentName() + "-spawned]: "
                    + msg);
        } else {
            System.out
                    .println("[" + this.tcid.toString() + "-spawned]: " + msg);
        }
    }

    /**
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    protected final LogicTuple no(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.noCode(), this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.noCode(),
                                this.tcid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    protected final List<LogicTuple> no_all(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (List<LogicTuple>) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.no_allCode(),
                                this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (List<LogicTuple>) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.no_allCode(),
                                this.tcid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    protected final LogicTuple nop(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.nopCode(),
                                this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.nopCode(), this.tcid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    protected final LogicTuple out(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.outCode(),
                                this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.outCode(), this.tcid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    protected final List<LogicTuple> out_all(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (List<LogicTuple>) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.out_allCode(),
                                this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (List<LogicTuple>) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.out_allCode(),
                                this.tcid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    protected final LogicTuple rd(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.rdCode(), this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.rdCode(),
                                this.tcid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    protected final List<LogicTuple> rd_all(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (List<LogicTuple>) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.rd_allCode(),
                                this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (List<LogicTuple>) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.rd_allCode(),
                                this.tcid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    protected final LogicTuple rdp(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.rdpCode(),
                                this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.rdpCode(), this.tcid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    protected final LogicTuple uin(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.uinCode(),
                                this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.uinCode(), this.tcid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    protected final LogicTuple uinp(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.uinpCode(), this.aid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.uinpCode(), this.tcid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    protected final LogicTuple uno(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.unoCode(),
                                this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.unoCode(), this.tcid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    protected final LogicTuple unop(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.unopCode(), this.aid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.unopCode(), this.tcid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    protected final LogicTuple urd(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer
                        .doBlockingOperation(TucsonOperation.urdCode(),
                                this.aid, this.target, tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.urdCode(), this.tcid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

    /**
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    protected final LogicTuple urdp(final LogicTuple tuple) {
        if (this.aid != null) {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.urdpCode(), this.aid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        } else {
            try {
                return (LogicTuple) TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.urdpCode(), this.tcid, this.target,
                        tuple);
            } catch (final TucsonInvalidLogicTupleException e) {
                // TODO Properly handle Exception
            } catch (final TucsonOperationNotPossibleException e) {
                // TODO Properly handle Exception
            }
        }
        return null;
    }

}
