/**
 * Copyright (c) 2011 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.workbench;

import java.util.List;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public interface WorkbenchServiceManager<T> {

    /**
     * Adds a {@link List} of {@link T}s to this manager.
     * This method will notify all {@link WorkbenchServiceListener<T>}s registered.
     *
     * @param services
     *          the {@code List} of {@code T}s to add
     */
    public void add(List<T> services);

    /**
     * Adds a {@link T} to this manager. This method will
     * notify all {@link WorkbenchServiceListener<T>}s registered.
     *
     * @param service the {@code T} to add
     */
    public void add(T service);

    /**
     * Adds a {@link WorkbenchServiceListener<T>} to the list of listeners for this
     * manager. The listener will be notified whenever a
     * {@link T} is added or removed from the manager.<br>
     * <br>
     * Adding the same listener multiple times has no effect.
     *
     * @param listener the {@code WorkbenchServiceListener<T>} to add
     */
    public void addListener(WorkbenchServiceListener<T> listener);

    /**
     * Returns all of the registered {@link T}s. The
     * resulting {@code List} is read-only.
     *
     * @return a read-only {@code List} of {@code T}s
     */
    public List<T> getServices();

    /**
     * Determines if the given {@link T} is registered with
     * the manager.
     *
     * @param service the {@code T} to search for
     * @return true if the {@code T} was found, false otherwise
     */
    public boolean isRegistered(T service);

    /**
     * Removes a {@link List} of {@link T}s from the
     * manager.<br>
     * <br>
     * {@code Ts} present in the remove list but not
     * registered with the manager are ignored.
     *
     * @param services
     *          the {@code List} of {@code T}s to remove
     */
    public void remove(List<T> services);

    /**
     * Removes the given {@link T} from the manager.<br>
     * <br>
     * If the {@code T} is not registered with the manager,
     * this method has no effect.
     *
     * @param service the {@code T} to remove
     */
    public void remove(T service);

    /**
     * Removes the given {@link WorkbenchServiceListener<T>} from the manager.<br>
     * <br>
     * If the {@code WorkbenchServiceListener<T>} is not registered with the manager,
     * this method has no effect.
     *
     * @param listener the {@code WorkbenchServiceListener<T>} to remove
     */
    public void removeListener(WorkbenchServiceListener<T> listener);
}
