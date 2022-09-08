/*******************************************************************************
 * Copyright (c) 2022, Vernalis (R&D) Ltd
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
package com.vernalis.knime.db.nodes.switches;

import org.knime.workflow.migration.NodeMigrationRule;

import com.vernalis.knime.db.SimpleNodeMigrationRule;

/**
 * {@link NodeMigrationRule} to map the Database Table Exists (Flow Variable)
 * (Legacy) node to the DB Table Exists node
 * 
 * @author S Roughley
 *
 */
public class DBTableExistsFlowVarNodeMigrationRule
		extends SimpleNodeMigrationRule {

	/**
	 * Constructor
	 *
	 * @since 07-Sep-2022
	 * @since v1.36.0
	 */
	public DBTableExistsFlowVarNodeMigrationRule() {
		super(DBTableExistsFlowVarNodeFactory.class,
				"com.vernalis.knime.database.nodes.switches.DatabaseTableExistsFlowVarNodeFactory");

	}

}
