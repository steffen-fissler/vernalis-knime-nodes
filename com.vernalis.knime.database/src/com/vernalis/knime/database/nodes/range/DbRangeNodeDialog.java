/*******************************************************************************
 * Copyright (c) 2018, Vernalis (R&D) Ltd
 *  This program is free software; you can redistribute it and/or modify it 
 *  under the terms of the GNU General Public License, Version 3, as 
 *  published by the Free Software Foundation.
 *  
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *   
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>
 ******************************************************************************/
package com.vernalis.knime.database.nodes.range;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.database.DatabasePortObjectSpec;

public class DbRangeNodeDialog extends NodeDialogPane {

	DialogComponentColumnFilter2 colFilt;

	/**
	 * 
	 */
	protected DbRangeNodeDialog() {
		colFilt = new DialogComponentColumnFilter2(createColNamesModel(), 0,
				false);
		super.addTab("DISTINCT Columns", colFilt.getComponentPanel());
	}

	@SuppressWarnings("unchecked")
	static SettingsModelColumnFilter2 createColNamesModel() {
		return new SettingsModelColumnFilter2("Columns", DoubleValue.class,
				IntValue.class, LongValue.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane#
	 * loadAdditionalSettingsFrom(org.knime.core.node.NodeSettingsRO,
	 * org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	public void loadSettingsFrom(NodeSettingsRO settings,
			PortObjectSpec[] specs) throws NotConfigurableException {

		DatabasePortObjectSpec dbSpec = (DatabasePortObjectSpec) specs[0];
		final DataTableSpec[] dts;
		if (dbSpec == null) {
			dts = new DataTableSpec[] { null };
		} else {
			dts = new DataTableSpec[] { dbSpec.getDataTableSpec() };
		}
		colFilt.loadSettingsFrom(settings, dts);
	}

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		colFilt.saveSettingsTo(settings);

	}
}
