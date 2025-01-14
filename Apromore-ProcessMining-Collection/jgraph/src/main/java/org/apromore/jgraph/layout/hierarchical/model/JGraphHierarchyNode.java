/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
/*
 * Copyright (c) 2005, David Benson
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.hierarchical.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An abstraction of an internal node in the hierarchy layout
 */
public class JGraphHierarchyNode extends JGraphAbstractHierarchyCell {
	
	/**
	 * Shared empty connection map to return instead of null in applyMap.
	 */
	public static transient Collection emptyConnectionMap = new ArrayList(0);
	
	public static final int CELL_TYPE_NONE = 0;

	public static final int CELL_TYPE_START = 1;

	public static final int CELL_TYPE_END = 2;

	public static final int CELL_TYPE_BRANCH = 3;

	public static final int CELL_TYPE_JOIN = 4;

	/**
	 * The graph cell this object represents.
	 */
	public Object cell = null;
	
	/**
	 * For future use
	 */
	public int cellType = CELL_TYPE_NONE;
	
	/**
	 * Collection of hierarchy edges that have this node as a target
	 */
	public Collection connectsAsTarget = emptyConnectionMap;
	
	/**
	 * Collection of hierarchy edges that have this node as a source
	 */
	public Collection connectsAsSource = emptyConnectionMap;

	/**
	 * Assigns a unique hashcode for each node. Used by the model dfs instead
	 * of copying HashSets
	 */
	public int[] hashCode;
	
	/**
	 * Constructs an internal node to represent the specified real graph cell
	 * @param cell the real graph cell this node represents
	 */
	public JGraphHierarchyNode(Object cell) {
		this.cell = cell;
	}
	
	/**
	 * Returns the integer value of the layer that this node resides in
	 * @return the integer value of the layer that this node resides in
	 */
	public int getRankValue() {
		return maxRank;
	}

	/**
	 * Returns the cells this cell connects to on the next layer up
	 * @param layer the layer this cell is on
	 * @return the cells this cell connects to on the next layer up
	 */
	public List getNextLayerConnectedCells(int layer) {
		if (nextLayerConnectedCells == null) {
			nextLayerConnectedCells = new ArrayList[1];
			nextLayerConnectedCells[0] = new ArrayList(connectsAsTarget.size());
			Iterator iter = connectsAsTarget.iterator();
			while(iter.hasNext()) {
				JGraphHierarchyEdge edge = (JGraphHierarchyEdge)iter.next();
				if (edge.maxRank == -1 || edge.maxRank == layer + 1) {
					// Either edge is not in any rank or
					// no dummy nodes in edge, add node of other side of edge
					nextLayerConnectedCells[0].add(edge.source);
					if (edge.source.maxRank != layer+1) {
					}
				} else {
					// Edge spans at least two layers, add edge
					nextLayerConnectedCells[0].add(edge);
				}
			}
		}
		return nextLayerConnectedCells[0];
	}

	/**
	 * Returns the cells this cell connects to on the next layer down
	 * @param layer the layer this cell is on
	 * @return the cells this cell connects to on the next layer down
	 */
	public List getPreviousLayerConnectedCells(int layer) {
		if (previousLayerConnectedCells == null) {
			previousLayerConnectedCells = new ArrayList[1];
			previousLayerConnectedCells[0] = new ArrayList(connectsAsSource.size());
			Iterator iter = connectsAsSource.iterator();
			while(iter.hasNext()) {
				JGraphHierarchyEdge edge = (JGraphHierarchyEdge)iter.next();
				if (edge.minRank == -1 || edge.minRank == layer - 1) {
					// No dummy nodes in edge, add node of other side of edge
					previousLayerConnectedCells[0].add(edge.target);
					if (edge.target.maxRank != layer-1) {
					}
				} else {
					// Edge spans at least two layers, add edge
					previousLayerConnectedCells[0].add(edge);
				}
			}
		}
		return previousLayerConnectedCells[0];
	}

	/**
	 * 
	 * @return whether or not this cell is an edge
	 */
	public boolean isEdge() {
		return false;
	}

	/**
	 * 
	 * @return whether or not this cell is a node
	 */
	public boolean isVertex() {
		return true;
	}

	/**
	 * Gets the value of temp for the specified layer
	 * 
	 * @param layer
	 *            the layer relating to a specific entry into temp
	 * @return the value for that layer
	 */
	public int getGeneralPurposeVariable(int layer) {
		return temp[0];
	}
	
	/**
	 * Set the value of temp for the specified layer
	 * 
	 * @param layer
	 *            the layer relating to a specific entry into temp
	 * @param value
	 *            the value for that layer
	 */
	public void setGeneralPurposeVariable(int layer, int value) {
		temp[0] = value;
	}
	
	public boolean isAncestor(JGraphHierarchyNode otherNode) {
		// Firstly, the hash code of this node needs to be shorter than the
		// other node
		if (otherNode != null && hashCode != null && otherNode.hashCode != null
				&& hashCode.length < otherNode.hashCode.length) {
			// Secondly, this hash code must match the start of the other
			// node's hash code. Arrays.equals cannot be used here since
			// the arrays are different length, and we do not want to
			// perform another array copy.
			if (hashCode == otherNode.hashCode) {
				return true;
			}
			if (hashCode == null || hashCode == null) {
				return false;
			}
			
			for (int i = 0; i < hashCode.length; i++) {
				if (hashCode[i] != otherNode.hashCode[i]) {
					return false;
				}
			}
			
			return true;
		}
		return false;
	}

}
