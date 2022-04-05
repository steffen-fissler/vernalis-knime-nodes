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
package com.vernalis.knime.ui.actions;

import java.util.List;
import java.util.Set;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.knime.workbench.core.util.ImageRepository;
import org.knime.workbench.editor2.WorkflowEditor;
import org.knime.workbench.editor2.actions.AbstractNodeAction;
import org.knime.workbench.editor2.editparts.NodeContainerEditPart;

import com.vernalis.knime.ui.VernalisUIPluginActivator;

import static com.vernalis.knime.ui.WorkflowHelpers.getAllConnectingNodes;
import static com.vernalis.knime.ui.actions.NodeActionHelpers.castList;

/**
 * AbstractNodeAction implementation to extend the selection to include all
 * nodes connecting the currently selected nodes
 * 
 * @author S.Roughley knime@vernalis.com
 *
 */
public class SelectConnectingNodesAction extends AbstractNodeAction {

	/**
	 * Constructor
	 * 
	 * @param editor
	 *            The current active {@link WorkflowEditor}
	 */
	public SelectConnectingNodesAction(WorkflowEditor editor) {
		super(editor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return "Select Previous Node(s)";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return "Select all nodes directly preceding any selected nodes";
	}

	@Override
	public String getId() {
		return this.getClass().getCanonicalName();
	}

	@Override
	public void runOnNodes(NodeContainerEditPart[] nodeParts) {

		// We go via the setSelection on EditPartView to avoid a fresh call to
		// calculateEnabled() after every new selected node
		Set<NodeContainerEditPart> toSelect = getAllConnectingNodes(nodeParts);
		if (toSelect != null) {
			for (NodeContainerEditPart tgt : toSelect) {
				final EditPartViewer viewer = tgt.getViewer();
				StructuredSelection sel =
						(StructuredSelection) viewer.getSelection();
				List<EditPart> selected =
						castList(EditPart.class, sel.toList());
				if (selected.containsAll(toSelect)) {
					// We are done
					break;
				}
				selected.addAll(toSelect);
				sel = new StructuredSelection(selected);
				viewer.setSelection(sel);
			}
			try {
				// Give focus to the editor again. Otherwise the actions
				// (selection)
				// is not updated correctly.
				getWorkbenchPart().getSite().getPage()
						.activate(getWorkbenchPart());
			} catch (Exception e) {
				// ignore
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getDisabledImageDescriptor()
	 */
	@Override
	public ImageDescriptor getDisabledImageDescriptor() {
		return ImageRepository.getIconDescriptor(
				VernalisUIPluginActivator.PLUGIN_ID,
				"icons/select_conn_disabled.png");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return ImageRepository.getIconDescriptor(
				VernalisUIPluginActivator.PLUGIN_ID, "icons/select_conn.png");
	}

	@Override
	protected boolean internalCalculateEnabled() {
		// We need at least two nodes, and at least one pair of those to be
		// connected by at least one unselected node
		NodeContainerEditPart[] selected =
				getSelectedParts(NodeContainerEditPart.class);
		if (selected.length < 2) {
			return false;
		}
		Set<NodeContainerEditPart> connectingNodes =
				getAllConnectingNodes(selected);
		return !connectingNodes.isEmpty();
	}

}
