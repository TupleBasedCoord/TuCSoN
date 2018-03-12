package alice.tuplecentre.tucson.network.messages;

import alice.tuplecentre.tucson.network.messages.events.InputEventMsg;

/**
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 * @author (contributor) Saverio Cicora
 * @author (contributor) Michele Bombardi (mailto:
 * michele.bombardi@studio.unibo.it)
 * @author (contributor) Piscaglia Nicola
 */
public class TucsonMessageRequest extends AbstractTucsonMessage {

    /**
     * serialVersionUID
     **/
    private static final long serialVersionUID = 1L;

    /**
     * @param ev the events to transmit
     */
    public TucsonMessageRequest(final InputEventMsg ev) {
        setEventMsg(ev);
    }

    public TucsonMessageRequest() {
    }

    @Override
    public InputEventMsg getEventMsg() {
        return (InputEventMsg) super.getEventMsg();
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(45);
        final InputEventMsg iEv = this.getEventMsg();
        s.append("ID: ");
        s.append(iEv.getOpId());
        s.append("; Type: ");
        s.append(iEv.getOpType());
        s.append("; TID: ");
        s.append(iEv.getReactingTC());
        s.append("; Tuple: ");
        s.append(iEv.getTuple());
        s.append("; Time: ");
        s.append(iEv.getTime());
        s.append("; Place: ");
        s.append(iEv.getPlace());
        return s.toString();
    }
}
