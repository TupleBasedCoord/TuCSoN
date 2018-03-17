package alice.tuple.logic;

import java.lang.invoke.MethodHandles;
import java.util.Collection;

import alice.tuple.logic.exceptions.InvalidLogicTupleException;
import alice.tuprolog.InvalidTermException;
import alice.tuprolog.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory Class for LogicTuples
 *
 * @author Enrico Siboni
 */
public final class LogicTuples {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().getClass());

    private LogicTuples() {
    }

    /**
     * Static method to get a LogicTuple from a textual representation
     *
     * @param tupleString the text representing the tuple
     * @return the logic tuple interpreted from the text
     * @throws InvalidLogicTupleException if the text does not represent a valid logic tuple
     */
    public static LogicTuple parse(final String tupleString) throws InvalidLogicTupleException {
        try {
            final Term t = alice.tuprolog.Term.createTerm(tupleString,
                    new LogicTupleOpManager());
            return LogicTuples.newInstance(TupleArguments.newInstance(t));
        } catch (final InvalidTermException ex) {
            throw new InvalidLogicTupleException(
                    "Exception occurred while parsing the string: \"" + tupleString
                            + "\"", ex);
        }
    }

    /**
     * Static method for copying a LogicTuple
     *
     * @param logicTuple the logicTuple to copy
     * @return the copy
     */
    public static LogicTuple newInstance(final LogicTuple logicTuple) {
        try {
            return parse(logicTuple.toString());
        } catch (final InvalidLogicTupleException e) {

            /* Never here, because the argument is a LogicTuple */
            LOGGER.error(e.getMessage(), e);
        }

        /* Never here, because the argument is a LogicTuple */
        throw new IllegalStateException("Due to modified LogicTupleDefault#toString !! Cannot parse: " + logicTuple.toString());
    }

    /**
     * Constructs a logic tuple providing the tuple name and argument list (or nothing for a tuple without arguments)
     *
     * @param name the name of the tuple (the functor)
     * @param args the list of tuple arguments
     * @return the newly created LogicTuple
     */
    public static LogicTuple newInstance(final String name, final TupleArgument... args) {
        return new LogicTupleDefault(name, args);
    }

    /**
     * Constructs a logic tuple providing the tuple name and argument list
     *
     * @param name the name of the tuple (the functor)
     * @param args the list of tuple arguments
     * @return the newly created LogicTuple
     */
    public static <T extends Collection<? extends TupleArgument>> LogicTuple newInstance(final String name, final T args) {
        return new LogicTupleDefault(name, args.toArray(new TupleArgument[args.size()]));
    }

    /**
     * Constructs the logic tuple from a tuprolog term
     *
     * @param term the tuprolog term
     */
    public static LogicTuple newInstance(final Term term) {
        return new LogicTupleDefault(term);
    }

    /**
     * Constructs the logic tuple from a tuple argument (free form of
     * construction)
     *
     * @param tupleArg the tuple argument
     */
    public static LogicTuple newInstance(final TupleArgument tupleArg) {
        return new LogicTupleDefault(tupleArg);
    }
}