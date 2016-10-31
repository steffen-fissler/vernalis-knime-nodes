/*
 * ------------------------------------------------------------------------
 *  Copyright (C) 2013, Vernalis (R&D) Ltd
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 * ------------------------------------------------------------------------
 */
package com.vernalis.nodes.io.txt;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.vernalis.io.FileEncodingWithGuess;
import com.vernalis.io.FileHelpers;

/**
 * This is the model implementation of LoadTxt. Node to Load txt files to a
 * column in the table
 * 
 * @author SDR
 */
public class LoadTxtNodeModel extends NodeModel {
	// the logger instance
	private static final NodeLogger logger = NodeLogger
			.getLogger(LoadTxtNodeModel.class);

	/**
	 * the settings keys which are used to retrieve and store the settings (from
	 * the dialog or from a settings file) (package visibility to be usable from
	 * the dialog).
	 */

	static final String CFG_PATH_COLUMN_NAME = "Path_column_name";
	static final String CFG_FILE_COLUMN_NAME = "File_column_name";
	static final String CFG_ENCODING = "File Encoding";

	private final SettingsModelString m_PathColumnName = new SettingsModelString(
			CFG_PATH_COLUMN_NAME, null);

	private final SettingsModelString m_FilecolumnName = new SettingsModelString(
			CFG_FILE_COLUMN_NAME, "Text File");

	private final SettingsModelString m_FileEncoding = new SettingsModelString(
			CFG_ENCODING, FileEncodingWithGuess.getDefaultMethod()
					.getActionCommand());

	private FileEncodingWithGuess m_encoding;

	/**
	 * Constructor for the node model.
	 */
	protected LoadTxtNodeModel() {
		super(1, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) throws Exception {

		// the data table spec of the single output table,
		// the table will have three columns:
		ColumnRearranger c = createColumnRearranger(inData[0]
				.getDataTableSpec());
		BufferedDataTable out = exec.createColumnRearrangeTable(inData[0], c,
				exec);
		return new BufferedDataTable[] { out };
	}

	private ColumnRearranger createColumnRearranger(DataTableSpec in) {
		ColumnRearranger c = new ColumnRearranger(in);
		// The column index of the selected column
		final int colIndex = in.findColumnIndex(m_PathColumnName
				.getStringValue());
		// column spec of the appended column
		DataColumnSpec newColSpec = new DataColumnSpecCreator(
				DataTableSpec.getUniqueColumnName(in,
						m_FilecolumnName.getStringValue()), StringCell.TYPE)
				.createSpec();

		// utility object that performs the calculation
		SingleCellFactory factory = new SingleCellFactory(true, newColSpec) {
			public DataCell getCell(DataRow row) {
				DataCell pathcell = row.getCell(colIndex);

				if (pathcell.isMissing() || !(pathcell instanceof StringValue)) {
					return DataType.getMissingCell();
				}
				// Here we actually do the meat of the work and fetch file

				String urlToRetrieve = ((StringValue) pathcell)
						.getStringValue();
				// Now, if it is a Location, convert to a URL
				urlToRetrieve = FileHelpers.forceURL(urlToRetrieve);

				// Only try to load the files - do not check type! Encoding and
				// un-zipping will be handled
				String r = FileHelpers.readURLToString(urlToRetrieve,
						m_encoding);
				if (!(r == null || "".equals(r))) {
					return new StringCell(r);
				}
				return DataType.getMissingCell();
			}
		};
		c.append(factory);
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// Do Nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {

		int colIndex = -1;
		if (m_PathColumnName.getStringValue() == null) {
			int i = 0;
			for (DataColumnSpec cs : inSpecs[0]) {
				if (cs.getType().isCompatible(StringValue.class)) {
					if (colIndex != -1) {
						throw new InvalidSettingsException(
								"No column selected.");
					}
					colIndex = i;
				}
				i++;
			}

			if (colIndex == -1) {
				throw new InvalidSettingsException("No column selected.");
			}
			m_PathColumnName.setStringValue(inSpecs[0].getColumnSpec(colIndex)
					.getName());
			setWarningMessage("Column '" + m_PathColumnName.getStringValue()
					+ "' auto selected");
		} else {
			colIndex = inSpecs[0].findColumnIndex(m_PathColumnName
					.getStringValue());
			if (colIndex < 0) {
				throw new InvalidSettingsException("No such column: "
						+ m_PathColumnName.getStringValue());
			}

			DataColumnSpec colSpec = inSpecs[0].getColumnSpec(colIndex);
			if (!colSpec.getType().isCompatible(StringValue.class)) {
				throw new InvalidSettingsException("Column \""
						+ m_PathColumnName
						+ "\" does not contain string values: "
						+ colSpec.getType().toString());
			}
		}
		if (m_PathColumnName.getStringValue().equals("")
				|| m_PathColumnName == null) {
			setWarningMessage("Path column name cannot be empty");
			throw new InvalidSettingsException(
					"Path column name cannot be empty");
		}

		m_encoding = FileEncodingWithGuess.valueOf(m_FileEncoding
				.getStringValue());
		if (m_encoding != FileEncodingWithGuess.GUESS) {
			logger.warn("Using " + m_encoding.getActionCommand()
					+ " file encoding.  Nonsense may result!");
		}

		// everything seems to fine
		ColumnRearranger c = createColumnRearranger(inSpecs[0]);
		return new DataTableSpec[] { c.createSpec() };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		m_FilecolumnName.saveSettingsTo(settings);
		m_PathColumnName.saveSettingsTo(settings);
		m_FileEncoding.saveSettingsTo(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {

		m_FilecolumnName.loadSettingsFrom(settings);
		m_PathColumnName.loadSettingsFrom(settings);
		try {
			m_FileEncoding.loadSettingsFrom(settings);
		} catch (Exception e) {
			logger.info("No file encoding setting found, using default ("
					+ FileEncodingWithGuess.getDefaultMethod()
							.getActionCommand() + ")");
			m_FileEncoding.setStringValue(FileEncodingWithGuess
					.getDefaultMethod().getActionCommand());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {

		m_FilecolumnName.validateSettings(settings);
		m_PathColumnName.validateSettings(settings);
		//Do not validate m_fileEncoding - new setting added to v 1.1.5

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		// DO nothing

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		// DO Nothing

	}

}
