/**
 * ResourcesChief.java
 */
package alice.respect.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import alice.respect.situatedness.AbstractProbeId;
import alice.respect.situatedness.ISimpleProbe;
import alice.respect.situatedness.TransducerId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 04/nov/2013
 * 
 */
public enum ResourceManager {
    INSTANCE;

    /**
     * Utility method used to communicate an output message to the console.
     * 
     * @param msg
     *            message to print
     */
    private static void speak(final String msg) {
        System.out.println("..[ResourceManager]: " + msg);
    }

    private static void speakErr(final String msg) {
        System.err.println("[ResourceManager]: " + msg);
    }

    /** List of all probes on a single node **/
    private final Map<AbstractProbeId, ISimpleProbe> probeList;

    private ResourceManager() {
        this.probeList = new HashMap<AbstractProbeId, ISimpleProbe>();
    }

    /**
     * Creates a resource
     * 
     * @param className
     *            the concrete implementative class of the resource
     * @param id
     *            the identifier of the resource
     * @return wether the Resource has been succesfully created.
     * 
     * @throws ClassNotFoundException
     *             if the given Java full class name cannot be found within
     *             known paths
     * @throws NoSuchMethodException
     *             if the Java method name cannot be found
     * @throws InstantiationException
     *             if the given Java class cannot be instantiated
     * @throws IllegalAccessException
     *             if the caller has no rights to access class, methods, or
     *             fields
     * @throws InvocationTargetException
     *             if the callee cannot be found
     */
    public synchronized boolean createResource(final String className,
            final AbstractProbeId id) throws ClassNotFoundException,
            NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        if (this.probeList.containsKey(id)) {
            ResourceManager.speakErr("Probe '" + id.getLocalName()
                    + "' already exists!");
            return false;
        }

        final String normClassName =
                className.substring(1, className.length() - 1);
        final Class<?> c = Class.forName(normClassName);
        final Constructor<?> ctor =
                c.getConstructor(new Class[] { AbstractProbeId.class });
        final ISimpleProbe probe =
                (ISimpleProbe) ctor.newInstance(new Object[] { id });

        this.probeList.put(id, probe);
        ResourceManager.speak("Resource '" + id.getLocalName()
                + "' has been registered.");
        return true;
    }

    /**
     * Gets the resource by its identifier
     * 
     * @param id
     *            the resource's identifier
     * @return an interface toward the resource whose identifier has been given
     */
    // FIXME Check correctness (synchronization needed?)
    public ISimpleProbe getResource(final AbstractProbeId id) {
        if (this.probeList.containsKey(id)) {
            return this.probeList.get(id);
        }
        ResourceManager.speakErr("Resource '" + id.getLocalName()
                + "' isn't registered yet!");
        return null;
    }

    /**
     * Gets the resource by its local name
     * 
     * @param name
     *            resource's local name
     * @return an interface toward the resource whose logical name has been
     *         given
     */
    // FIXME Check correctness (synchronization needed?)
    public ISimpleProbe getResourceByName(final String name) {
        final Object[] keySet = this.probeList.keySet().toArray();
        for (final Object element : keySet) {
            if (((AbstractProbeId) element).getLocalName().equals(name)) {
                return this.probeList.get(element);
            }
        }
        ResourceManager.speakErr("'Resource " + name + "' isn't registered yet!");
        return null;
    }

    /**
     * Removes a resource from the list
     * 
     * @param id
     *            the identifier of the resource to remove
     * @return wether the resource has been successfully removed
     * @throws TucsonOperationNotPossibleException
     */
    public synchronized boolean removeResource(final AbstractProbeId id)
            throws TucsonOperationNotPossibleException {
        ResourceManager.speak("Removing probe '" + id.getLocalName()
                + "'...");
        if (!this.probeList.containsKey(id)) {
            ResourceManager.speakErr("Resource '" + id.getLocalName()
                    + "' doesn't exist!");
            return false;
        }
        final TransducerManager tm = TransducerManager.INSTANCE;
        tm.removeResource(id);
        this.probeList.remove(id);
        return true;
    }

    /**
     * Sets the transducer which the probe will communicate with.
     * 
     * @param pId
     *            the probe's identifier
     * @param tId
     *            the transducer's identifier
     */
    public void
            setTransducer(final AbstractProbeId pId, final TransducerId tId) {
        this.getResource(pId).setTransducer(tId);
        if (tId != null) {
            ResourceManager.speak("...transducer '" + tId.getAgentName()
                    + "' set to probe '" + pId.getLocalName() + "'.");
        }
    }

}
