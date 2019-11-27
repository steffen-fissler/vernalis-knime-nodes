/*******************************************************************************
 * Copyright (c) 2017, Vernalis (R&D) Ltd
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
package com.vernalis.knime.mmp;

/**
 * Exception to wrap exceptions thrown in fragmentation by the toolkit used
 * 
 * @author S.Roughley
 *
 */
public class ToolkitException extends Exception {

	private static final long serialVersionUID = 7719580530251131717L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 * @param cause
	 *            The cause
	 */
	public ToolkitException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 *            The cause
	 */
	public ToolkitException(Throwable cause) {
		super(cause);

	}

}
