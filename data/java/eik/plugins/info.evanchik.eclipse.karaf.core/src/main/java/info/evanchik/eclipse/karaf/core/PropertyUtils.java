/**
 * Copyright (c) 2009 Stephen Evanchik
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Stephen Evanchik - initial implementation
 */
package info.evanchik.eclipse.karaf.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public final class PropertyUtils {

    private static final String DELIM_START = "${";

    private static final String DELIM_STOP = "}";

    /*
     * From Karaf Main.java
     */

    /**
     * Interpolates variables found in the property string values. A variable is
     * {@code $ name} and can be nested {@code $ outer-$ inner} .
     *
     * @param configProperties
     *            the {@link Properties} object that will have its variables
     *            interpolated
     *
     * @see StrSubstitutor
     */
    public static void interpolateVariables(final Properties configProperties, final Properties initialProperties) {

        for (final Enumeration<?> e = configProperties.propertyNames(); e.hasMoreElements();) {
            final String name = (String) e.nextElement();
            final String value = (String) configProperties.get(name);

            final String newValue = PropertyUtils.substVars(value, name, null, initialProperties);
            configProperties.put(name, newValue);
        }
    }

    /**
     * <p>
     * This method performs property variable substitution on the specified
     * value. If the specified value contains the syntax
     * <tt>${&lt;prop-name&gt;}</tt>, where <tt>&lt;prop-name&gt;</tt> refers to
     * either a configuration property or a system property, then the
     * corresponding property value is substituted for the variable placeholder.
     * Multiple variable placeholders may exist in the specified value as well
     * as nested variable placeholders, which are substituted from inner most to
     * outer most. Configuration properties override system properties.
     * </p>
     *
     * @param val
     *            The string on which to perform property substitution.
     * @param currentKey
     *            The key of the property being evaluated used to detect cycles.
     * @param cycleMap
     *            Map of variable references used to detect nested cycles.
     * @param configProps
     *            Set of configuration properties.
     * @return The value of the specified string after system property
     *         substitution.
     * @throws IllegalArgumentException
     *             If there was a syntax error in the property placeholder
     *             syntax or a recursive variable reference.
     */
    public static String substVars(String val, final String currentKey, Map<String, String> cycleMap, final Properties configProps)
            throws IllegalArgumentException {
        // If there is currently no cycle map, then create
        // one for detecting cycles for this invocation.
        if (cycleMap == null) {
            cycleMap = new HashMap<String, String>();
        }

        // Put the current key in the cycle map.
        cycleMap.put(currentKey, currentKey);

        // Assume we have a value that is something like:
        // "leading ${foo.${bar}} middle ${baz} trailing"

        // Find the first ending '}' variable delimiter, which
        // will correspond to the first deepest nested variable
        // placeholder.
        final int stopDelim = val.indexOf(DELIM_STOP);

        // Find the matching starting "${" variable delimiter
        // by looping until we find a start delimiter that is
        // greater than the stop delimiter we have found.
        int startDelim = val.indexOf(DELIM_START);
        while (stopDelim >= 0) {
            final int idx = val.indexOf(DELIM_START, startDelim + DELIM_START.length());
            if (idx < 0 || idx > stopDelim) {
                break;
            } else if (idx < stopDelim) {
                startDelim = idx;
            }
        }

        // If we do not have a start or stop delimiter, then just
        // return the existing value.
        if (startDelim < 0 && stopDelim < 0) {
            return val;
        }
        // At this point, we found a stop delimiter without a start,
        // so throw an exception.
        else if ((startDelim < 0 || startDelim > stopDelim) && stopDelim >= 0) {
            throw new IllegalArgumentException("stop delimiter with no start delimiter: " + val);
        }

        // At this point, we have found a variable placeholder so
        // we must perform a variable substitution on it.
        // Using the start and stop delimiter indices, extract
        // the first, deepest nested variable placeholder.
        final String variable = val.substring(startDelim + DELIM_START.length(), stopDelim);

        // Verify that this is not a recursive variable reference.
        if (cycleMap.get(variable) != null) {
            throw new IllegalArgumentException("recursive variable reference: " + variable);
        }

        // Get the value of the deepest nested variable placeholder.
        // Try to configuration properties first.
        String substValue = configProps != null ? configProps.getProperty(variable, null) : null;
        if (substValue == null) {
            // Ignore unknown property values.
            substValue = System.getProperty(variable, "");
        }

        // Remove the found variable from the cycle map, since
        // it may appear more than once in the value and we don't
        // want such situations to appear as a recursive reference.
        cycleMap.remove(variable);

        // Append the leading characters, the substituted value of
        // the variable, and the trailing characters to get the new
        // value.
        val = val.substring(0, startDelim) + substValue + val.substring(stopDelim + DELIM_STOP.length(), val.length());

        // Now perform substitution again, since there could still
        // be substitutions to make.
        val = substVars(val, currentKey, cycleMap, configProps);

        // Return the value.
        return val;
    }
}
