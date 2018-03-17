/*
 * Tuple Centre media - Copyright (C) 2001-2002 aliCE team at deis.unibo.it This
 * library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package alice.tuplecentre.core;

/**
 * Defines the services that must be provided by any object which must receive
 * commounication output events produced by a tuple centre
 *
 * @see AbstractEvent
 *
 * @author Alessandro Ricci
 */
public interface OutputEventListener {

    /**
     * This service is invoked (by a tuple centre virtual machine able to
     * dispatch output events) when a new output communication is produced
     *
     * @param ev
     *            the output events to notify
     */
    void notify(OutputEvent ev);
}