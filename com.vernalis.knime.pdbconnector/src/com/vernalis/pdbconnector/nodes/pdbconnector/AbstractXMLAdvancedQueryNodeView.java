/*******************************************************************************
 * Copyright (c) 2016, Vernalis (R&D) Ltd, based on earlier PDB Connector work.
 * 
 * Copyright (c) 2012, 2014 Vernalis (R&D) Ltd and Enspiral Discovery Limited
 * 
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
package com.vernalis.pdbconnector.nodes.pdbconnector;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.knime.core.node.NodeView;

import com.vernalis.pdbconnector.XMLFormatter;

/**
 * A simple node view which shows the Advanced XML Query string as indented XML
 * text
 *
 * @param <T>
 *            The node model (must be a subclass of
 *            {@link AbstractXMLQueryProviderNodeModel})
 */
public class AbstractXMLAdvancedQueryNodeView<T extends AbstractXMLQueryProviderNodeModel>
		extends NodeView<T> {
	JTextArea txtPane;
	JScrollPane panel;

	/**
	 * Constructor for the node view
	 * 
	 * @param nodeModel
	 *            The model instance
	 */
	public AbstractXMLAdvancedQueryNodeView(T nodeModel) {
		super(nodeModel);
		txtPane = new JTextArea(40, 80);
		txtPane.setEditable(false);
		panel = new JScrollPane(txtPane);
		setComponent(panel);
	}

	@Override
	protected void onClose() {

	}

	@Override
	protected void onOpen() {

	}

	@Override
	protected void modelChanged() {
		T model = getNodeModel();
		String xmlQuery = model.getXMLQuery();
		if (xmlQuery != null && !xmlQuery.isEmpty()) {
			String xml = XMLFormatter.indentXML(xmlQuery).replace("\t", "    ");
			txtPane.setText(xml);
		}
	}

}
