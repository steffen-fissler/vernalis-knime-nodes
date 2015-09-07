/*******************************************************************************
 * Copyright (c) 2014, 2015, Vernalis (R&D) Ltd
 *  This program is free software; you can redistribute it and/or modify it 
 *  under the terms of the GNU General Public License, Version 3, as 
 *  published by the Free Software Foundation.
 *  
 *   This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *   
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>
 *******************************************************************************/
/* @(#)$RCSfile$ 
 * $Revision$ $Date$ $Author$
 *
 */
package com.vernalis.knime.mmp;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * This is the eclipse bundle activator. This has to do some activation stuff to
 * ensure that RDKit works properly Based on
 * org.rdkit.knime.RDKitTypesPluginActivator.java
 * 
 * Note: KNIME node developers probably won't have to do anything in here, as
 * this class is only needed by the eclipse platform/plugin mechanism. If you
 * want to move/rename this file, make sure to change the plugin.xml file in the
 * project root directory accordingly.
 * 
 * @author S. Roughley
 */
public class MatchedPairsMultipleCutsNodePlugin extends Plugin {
	// The shared instance.
	private static MatchedPairsMultipleCutsNodePlugin plugin;

	

	/**
	 * The constructor.
	 */
	public MatchedPairsMultipleCutsNodePlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation.
	 * 
	 * @param context
	 *            The OSGI bundle context
	 * @throws Exception
	 *             If this plugin could not be started
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);

	}

	/**
	 * This method is called when the plug-in is stopped.
	 * 
	 * @param context
	 *            The OSGI bundle context
	 * @throws Exception
	 *             If this plugin could not be stopped
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return Singleton instance of the Plugin
	 */
	public static MatchedPairsMultipleCutsNodePlugin getDefault() {
		return plugin;
	}

}
