/*******************************************************************************
 * Copyright (c) 2014, Vernalis (R&D) Ltd
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
package com.vernalis.knime.flowcontrol.nodes.fv.varvalifswitch;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;

import com.vernalis.knime.flowcontrol.nodes.abstrct.varvalifswitch.AbstractFvvalIfSwitchNodeDialog;
import com.vernalis.knime.flowcontrol.nodes.abstrct.varvalifswitch.AbstractFvvalIfSwitchNodeModel;

/**
 * <code>NodeFactory</code> for the "FlowVarIfSwitch" Node.
 * 
 * 
 * @author "Stephen Roughley  knime@vernalis.com"
 */
public class FlowVarFvvalIfSwitchNodeFactory extends
		NodeFactory<AbstractFvvalIfSwitchNodeModel> {

	/** The port type. */
	PortType m_port = FlowVariablePortObject.TYPE;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractFvvalIfSwitchNodeModel createNodeModel() {
		return new AbstractFvvalIfSwitchNodeModel(m_port);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNrNodeViews() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<AbstractFvvalIfSwitchNodeModel> createNodeView(
			final int viewIndex, final AbstractFvvalIfSwitchNodeModel nodeModel) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDialog() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeDialogPane createNodeDialogPane() {
		return new AbstractFvvalIfSwitchNodeDialog();
	}

}
