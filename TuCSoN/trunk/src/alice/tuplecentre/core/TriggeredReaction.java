/*
 * Tuple Centre media - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tuplecentre.core;

/**
 * Represents a reaction which has been triggered
 * inside a tuple centre by a specific event
 *
 * @see Reaction
 * @see Event
 * @see TupleCentreVM
 *
 *
 * @author aricci
 */
@SuppressWarnings("serial")
public class TriggeredReaction implements java.io.Serializable {

    /** the event triggering the reaction */
    private Event      event;

    /** the reaction triggered */
    private Reaction   reaction;

    public TriggeredReaction(Event ev,Reaction re){
        event=ev;
        reaction=re;
    }
    
    public Reaction getReaction(){
    	return reaction;
    }
    
    public Event getEvent(){
    	return event;
    }
    
}
