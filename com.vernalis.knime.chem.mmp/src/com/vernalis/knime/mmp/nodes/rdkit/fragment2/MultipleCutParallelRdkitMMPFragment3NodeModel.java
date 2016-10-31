/*******************************************************************************
 * Copyright (c) 2015, Vernalis (R&D) Ltd
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License, Version 3, as 
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>
 *******************************************************************************/
package com.vernalis.knime.mmp.nodes.rdkit.fragment2;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.RDKit.ROMol;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.StringValue;
import org.knime.core.data.append.AppendedColumnRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;

import com.vernalis.exceptions.RowExecutionException;
import com.vernalis.knime.mmp.BondIdentifier;
import com.vernalis.knime.mmp.CombinationFinder;
import com.vernalis.knime.mmp.MulticomponentSmilesFragmentParser;
import com.vernalis.knime.mmp.RDKitFragmentationUtils;
import com.vernalis.knime.mmp.fragmentors.MoleculeFragmentationFactory;
import com.vernalis.knime.mmp.fragmentors.ROMolFragmentFactory;
import com.vernalis.knime.parallel.MultiTableParallelResult;
import com.vernalis.knime.swiggc.SWIGObjectGarbageCollector;

/**
 * The {@link NodeModel} implementation for the MMP Molecule Fragment node
 * 
 * @author s.roughley {@literal <knime@vernalis.com>}
 * 
 */
public class MultipleCutParallelRdkitMMPFragment3NodeModel
		extends AbstractParallelRdkitMMPFragment3NodeModel {

	/**
	 * Constructor
	 */
	public MultipleCutParallelRdkitMMPFragment3NodeModel() {
		super(true);
	}

	/**
	 * Method to fragment a row according the the settings supplied
	 * 
	 * @param row
	 *            The row to fragment
	 * @param index
	 *            The row index for GC of SWIG objects
	 * @param numCols
	 *            The number of columns in the output table
	 * @param molIdx
	 *            The molecule column index
	 * @param addFailReasons
	 *            Should failure reasons be added to 2nd output table?
	 * @param bondMatch
	 *            The matching structure for the bond to be used for
	 *            fragmentation
	 * @param numCuts
	 *            The Maximum number of cuts to make
	 * @param prochiralAsChiral
	 *            Should prochiral centres be assigned chirality if there are no
	 *            known or unknown centres?
	 * @param addHs
	 *            Should H's be added for 1 cut?
	 * @param stripHsAtEnd
	 *            Should H's be removed from the output?
	 * @param allowTwoCutsToBondValue
	 *            Should a bond be cut twice for 2 cuts?
	 * @param maxNumVarAtm
	 *            The maximum number of changing atoms
	 * @param minCnstToVarAtmRatio
	 *            The minimum ratio of constant to changing atoms
	 * @param idColIdx
	 *            The ID column index
	 * @param outputNumChgHAs
	 *            Output the number of changing atoms?
	 * @param outputHARatio
	 *            Output the HA ratio?
	 * @param addFingerprints
	 *            Add fingerprints to the output?
	 * @param morganRadius
	 *            The morgan fp radius
	 * @param fpLength
	 *            The fp length
	 * @param useChirality
	 *            Use chirality in the fingerprints?
	 * @param useBondTypes
	 *            Use bond types in the fingerprints?
	 * @param swigGC
	 *            The garbage collector object
	 * @param exec
	 *            The execution context
	 * @param logger
	 *            The node logger
	 * @param verboseLogging
	 *            Should verbose logging be enabled?
	 * @return A {@link MultiTableParallelResult} object containing either
	 *         fragmentation rows of a failed row
	 * @throws CanceledExecutionException
	 */
	@Override
	protected MultiTableParallelResult fragmentRow(DataRow row, long index, int numCols, int molIdx,
			boolean addFailReasons, ROMol bondMatch, int numCuts, boolean prochiralAsChiral,
			boolean addHs, boolean stripHsAtEnd, boolean allowTwoCutsToBondValue,
			Integer maxNumVarAtm, Double minCnstToVarAtmRatio, int idColIdx,
			boolean outputNumChgHAs, boolean outputHARatio, boolean addFingerprints,
			int morganRadius, int fpLength, boolean useChirality, boolean useBondTypes,
			SWIGObjectGarbageCollector swigGC, ExecutionContext exec, NodeLogger logger,
			boolean verboseLogging) throws CanceledExecutionException {
		MultiTableParallelResult retVal = new MultiTableParallelResult(2);

		/*
		 * Firstly, try getting the molecule cell as an ROMol object Feed to
		 * second table if we fail or have a missing cell
		 */
		DataCell molCell = row.getCell(molIdx);
		if (molCell.isMissing()) {
			// Deal with missing mols
			retVal.addRowToTable((addFailReasons)
					? new AppendedColumnRow(row, new StringCell("Missing value in Molecule Column"))
					: row, 1);
			return retVal;
		}

		if (row.getCell(idColIdx).isMissing()) {
			// Missing ID - causes problems later!
			retVal.addRowToTable((addFailReasons)
					? new AppendedColumnRow(row, new StringCell("Missing value in ID Column"))
					: row, 1);
			return retVal;
		}

		ROMol roMol;
		try {
			roMol = swigGC.markForCleanup(getROMolFromCell(row.getCell(molIdx)), (int) index);
		} catch (RowExecutionException e) {
			// Log the failed row
			if (verboseLogging) {
				logger.info("Error parsing molecule (Row: " + row.getKey().getString() + ") "
						+ e.getMessage());
			}
			// And add it to the second output
			retVal.addRowToTable((addFailReasons)
					? new AppendedColumnRow(row, new StringCell(e.getMessage())) : row, 1);

			return retVal;
		}

		if (roMol == null || "".equals(roMol.MolToSmiles(true))) {
			// Deal with when we cannot get an ROMol object - e.g. for 'No
			// Structure' Mol files
			// And add it to the second output
			retVal.addRowToTable((addFailReasons)
					? new AppendedColumnRow(row, new StringCell("'No Structure' input molecule"))
					: row, 1);
			return retVal;
		}

		// Multicomponent molecules make no sense... (and duplicate salts crash
		// the duplicate key resolver!)
		// Checking the SMILES for a '.' is simpler that garbage collecting the
		// ROMol_Vect from RDKFunc#getComponents()

		if (roMol.MolToSmiles().contains(".")) {
			retVal.addRowToTable(
					(addFailReasons)
							? new AppendedColumnRow(row,
									new StringCell(
											"Multi-component structures cannot be fragmented"))
							: row,
					1);
			return retVal;
		}

		/*
		 * Do the fragmentation and apply filters, adding rows as we go...
		 */

		DataCell idCell = new StringCell(((StringValue) row.getCell(idColIdx)).getStringValue());

		// Build a list of all the valid fragmentations
		Set<MulticomponentSmilesFragmentParser> fragmentations = new TreeSet<>();

		// Identify all the matching bonds (NB - Cuttable combos are picked
		// later
		Set<BondIdentifier> cuttableBonds = RDKitFragmentationUtils.identifyAllMatchingBonds(roMol,
				bondMatch);

		// Deal with 1 cut
		MoleculeFragmentationFactory fragFactory;
		if (addHs) {
			ROMol roMol2 = swigGC.markForCleanup(new ROMol(roMol), (int) index);
			roMol2 = swigGC.markForCleanup(roMol2.addHs(false, false), (int) index);
			fragFactory = new ROMolFragmentFactory(roMol2, stripHsAtEnd, verboseLogging,
					maxNumVarAtm, minCnstToVarAtmRatio);
			fragmentations.addAll(breakMoleculeAlongBonds(fragFactory,
					RDKitFragmentationUtils.identifyAllMatchingBonds(roMol2, bondMatch),
					prochiralAsChiral, exec, logger, verboseLogging));

			// Now return the fragFactory to the main unhydrogenated molecule -
			// and in which case we dont strip Hs
			fragFactory = new ROMolFragmentFactory(roMol, false, verboseLogging, maxNumVarAtm,
					minCnstToVarAtmRatio);
		} else {
			// Otherwise we just cut along every bond
			fragFactory = new ROMolFragmentFactory(roMol, false, verboseLogging, maxNumVarAtm,
					minCnstToVarAtmRatio);
			fragmentations.addAll(breakMoleculeAlongBonds(fragFactory, cuttableBonds,
					prochiralAsChiral, exec, logger, verboseLogging));
		}

		boolean couldCut = fragmentations.size() > 0;
		// Check we have anything to do
		if (!couldCut) {
			// No bonds to cut
			retVal.addRowToTable(
					(addFailReasons)
							? new AppendedColumnRow(row,
									new StringCell("No matching bonds or found, or too few to cut"))
							: row,
					1);
			return retVal;
		}

		// Deal with the special case of 2 cuts, and allowing *-* as a value
		if (numCuts >= 2 && allowTwoCutsToBondValue) {
			fragmentations.addAll(doDoubleCutToSingleBond(fragFactory, cuttableBonds,
					prochiralAsChiral, exec, logger, verboseLogging));
		}

		// Now generate the combinations of bonds to cut for 2 or more cuts,
		// removing higher
		// graphs of invalid triplets where appropriate
		// Why doesnt this take cuttableBonds as an argument? - Because cuttable
		// bonds change with number of cuts!
		Set<Set<BondIdentifier>> bondCombos = generateCuttableBondCombos(roMol, bondMatch, numCuts);
		fragmentations.addAll(breakMoleculeAlongBondCombos(fragFactory, bondCombos,
				prochiralAsChiral, exec, logger, verboseLogging));

		// Now add the fragmentations to output rows. NB, we still need to
		// filter as 1 cut will not be filtered, hence the
		// 'smiParser.getNumCuts() > 1'
		boolean addedFragmentations = false;
		for (MulticomponentSmilesFragmentParser smiParser : fragmentations) {
			if (smiParser.getNumCuts() > 1 || RDKitFragmentationUtils.filterFragment(
					smiParser.getKey(), smiParser.getValue(), maxNumVarAtm, minCnstToVarAtmRatio)) {
				addedFragmentations = true;
				addRowToTable(retVal, stripHsAtEnd, idCell, smiParser, numCols, outputNumChgHAs,
						outputHARatio, addFingerprints, morganRadius, fpLength, useChirality,
						useBondTypes, isMulticut);
			}
		}
		if (!addedFragmentations) {
			// There were no valid fragmentatins after filtering
			retVal.addRowToTable(
					(addFailReasons)
							? new AppendedColumnRow(row,
									new StringCell(
											"No fragmentations passed the specified filters"))
							: row,
					1);
		}

		fragmentations.clear();
		cuttableBonds.clear();
		return retVal;
	}

	/**
	 * A collection of bonds which have been identified as cuttable (i.e. match
	 * the required substructure, and ideally, for 3 or more cuts, have had
	 * bonds which can never give a valid cut pattern removed) are parsed into
	 * combinations of {@code numCuts} bonds. When more than 3 cuts are made,
	 * any combinations including an invalid triplet are removed
	 * 
	 * @param roMol
	 *            The molecule to cut
	 * @param cuttableBonds
	 *            A collection of cuttable bonds
	 * @return A Set of Sets of {@code numCuts} bonds
	 * @throws IllegalArgumentException
	 */
	@Override
	protected Set<Set<BondIdentifier>> generateCuttableBondCombos(ROMol roMol, ROMol bondMatch,
			int numCuts) throws IllegalArgumentException {

		Collection<BondIdentifier> cuttableBonds;
		// Generate the combinations of upto numCuts bonds. NB we start at 2 as
		// 1 is handled separately
		Set<Set<BondIdentifier>> bondCombos = new LinkedHashSet<Set<BondIdentifier>>();
		Set<Set<BondIdentifier>> invalidTriplets = new LinkedHashSet<>();// Will
																			// hold
																			// valid
																			// triplets
		// once 3 or more cuts made
		for (int i = 2; i <= numCuts; i++) {
			cuttableBonds = RDKitFragmentationUtils.identifyAllCuttableBonds(roMol, bondMatch, i);
			Set<Set<BondIdentifier>> newBondCombos = CombinationFinder
					.getCombinationsFor(cuttableBonds, i);
			if (i == 3) {
				Iterator<Set<BondIdentifier>> iter = newBondCombos.iterator();
				while (iter.hasNext()) {
					Set<BondIdentifier> triplet = iter.next();
					if (!RDKitFragmentationUtils.isValidCutTriplet(roMol, triplet)) {
						// Invalid combo - remove and save for later rounds...
						iter.remove();
						invalidTriplets.add(triplet);
					}
				}
			}
			if (i > 3) {
				Iterator<Set<BondIdentifier>> tripletIter = invalidTriplets.iterator();
				while (tripletIter.hasNext()) {
					Set<BondIdentifier> triplet = tripletIter.next();
					// First check that all triplet bonds are still valid
					boolean invalidTripletStillMakeable = true;
					for (BondIdentifier bd : triplet) {
						if (!cuttableBonds.contains(bd)) {
							tripletIter.remove();
							invalidTripletStillMakeable = false;
							break;
						}
					}

					if (invalidTripletStillMakeable) {
						// Now check none of the new combos contain that triplet
						Iterator<Set<BondIdentifier>> newCombosIter = newBondCombos.iterator();
						while (newCombosIter.hasNext()) {
							if (newCombosIter.next().containsAll(triplet)) {
								newCombosIter.remove();
							}
						}
						if (newBondCombos.size() == 0) {
							break;
						}
					}
				}
				if (newBondCombos.size() <= 0) {
					break;
				}
			}
			bondCombos.addAll(newBondCombos);
		}

		return bondCombos;
	}

}
