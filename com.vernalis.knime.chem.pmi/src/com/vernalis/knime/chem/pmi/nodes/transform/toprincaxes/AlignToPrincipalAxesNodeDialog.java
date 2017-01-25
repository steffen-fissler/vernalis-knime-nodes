/*******************************************************************************
 * Copyright (c) 2016, Vernalis (R&D) Ltd
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
 ******************************************************************************/
package com.vernalis.knime.chem.pmi.nodes.transform.toprincaxes;

import org.knime.chem.types.MolValue;
import org.knime.chem.types.SdfValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;

import com.vernalis.knime.chem.pmi.nodes.rdkit.abstrct.AbstractVerRDKitRearrangerNodeModel;

/**
 * <code>NodeDialog</code> for the Align to principal axes Node.
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author S.Roughley
 */
public class AlignToPrincipalAxesNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the node.
	 */
	@SuppressWarnings("unchecked")
	protected AlignToPrincipalAxesNodeDialog() {

		// Now actually add the dialog components
		createNewGroup("Input Column");
		addDialogComponent(new DialogComponentColumnNameSelection(
				AbstractVerRDKitRearrangerNodeModel.createMolColNameModel(),
				"Select the SDF or MOL column containing the conformers", 0, true, SdfValue.class,
				MolValue.class));
		addDialogComponent(new DialogComponentBoolean(
				AbstractVerRDKitRearrangerNodeModel.createRemoveInputColModel(),
				"Remove input column"));

	}

}
