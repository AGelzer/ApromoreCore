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
package org.apromore.service.loganimation.modelmapping;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.service.loganimation.recording.ModelMapping;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Mapping from modelling element IDs to indexes and vice versa.
 * @author Bruce Nguyen
 *
 */
public class BpmnModelMapping implements ModelMapping {
	private HashBiMap<String, Integer> elementIdToIndexMap = new HashBiMap<>();
	private HashBiMap<String, Integer> elementIdToSkipIndexMap = new HashBiMap<>();
	
    public BpmnModelMapping(BPMNDiagram diagram) {
        int index=0;
        for (BPMNNode node : diagram.getNodes()) {
            elementIdToIndexMap.put(node.getId().toString(), index);
            index++;
            if (node instanceof Activity) {
                elementIdToSkipIndexMap.put(node.getId().toString(), index);
                index++;
            }
        }
        
        for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getEdges()) {
            elementIdToIndexMap.put(edge.getEdgeID().toString(), index);
            index++;
        }
    }
	
	@Override
    public int getIndex(String elementId) {
		return elementIdToIndexMap.getIfAbsent(elementId, () -> -1);
	}
	
    @Override
    public int getSkipIndex(String elementId) {
        return elementIdToSkipIndexMap.getIfAbsent(elementId, () -> -1);
    }
	
	@Override
    public String getId(int index) {
		return elementIdToIndexMap.inverse().getIfAbsent(index, () -> "");
	}
	
	@Override
    public String getIdFromSkipIndex(int index) {
	    return elementIdToSkipIndexMap.inverse().getIfAbsent(index, () -> "");
	}

	@Override
    public int size() {
        return elementIdToIndexMap.size() + elementIdToSkipIndexMap.size();
    }
    
	@Override
    public void clear() {
		this.elementIdToIndexMap.clear();
		this.elementIdToSkipIndexMap.clear();
	}
	
	// two arrays, first is the mapping from id to normal index
	// second is mapping from id to skip index
	@Override
    public JSONArray getElementJSON() throws JSONException {
	    JSONArray jsonArray = new JSONArray();
        jsonArray.put(this.getJSONMap(this.elementIdToIndexMap));
        jsonArray.put(this.getJSONMap(this.elementIdToSkipIndexMap));
        return jsonArray;
	}
	
    private JSONObject getJSONMap(HashBiMap<String, Integer> idToIndexMap) throws JSONException {
        JSONObject json = new JSONObject();
        for (String elementId : idToIndexMap.keySet()) {
            json.put(idToIndexMap.get(elementId).toString(), elementId);
        }
        return json;
    }
}
