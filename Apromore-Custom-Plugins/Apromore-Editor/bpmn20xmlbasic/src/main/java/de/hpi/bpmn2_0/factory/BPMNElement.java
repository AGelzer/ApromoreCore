/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package de.hpi.bpmn2_0.factory;

/**
 * Copyright (c) 2006
 *
 * Philipp Berger, Martin Czuchra, Gero Decker, Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.bpmndi.dc.Bounds;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.bpmndi.di.ParticipantBandKind;
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.participant.Participant;

import java.util.HashMap;
import java.util.Map;

public class BPMNElement {
    private DiagramElement shape;
    private BaseElement node;
    private String id;
    private String[] customNamespaces;
    private Map<String, String> externalNamespaceDefinitions;

    public BPMNElement(DiagramElement shape, BaseElement node, String id) {
        this.shape = shape;
        this.node = node;
        this.id = id;
    }

    /**
     * Adds a {@link BPMNElement} as child to the current {@link BPMNElement}
     *
     * @param child The child element
     */
    public void addChild(BPMNElement child) {
        if (this.getNode() != null) {
            /* Set the lane reference */
            if (child.getNode() != null) {
                child.getNode().setLane(this.getNode().getLane());
            }
            this.getNode().addChild(child.getNode());

            /* Special handling of choreography activities */
            if (this.getNode() instanceof ChoreographyActivity
                    && child.getShape() != null
                    && child.getShape() instanceof BPMNShape
                    && child.getNode() instanceof Participant) {
                ((BPMNShape) child.getShape()).setChoreographyActivityShape((BPMNShape) this.getShape());
            }

            /* Set attributes of participant band kind of choreography activities */
            if (getNode() instanceof ChoreographyActivity && child.getNode() instanceof Participant) {
                /* Retrieve bounds of the activity */
                Bounds actBounds = ((BPMNShape) getShape()).getBounds();
                Bounds participantBounds = ((BPMNShape) child.getShape()).getBounds();
                BPMNShape participantShape = (BPMNShape) child.getShape();
                Participant participant = (Participant) child.getNode();

                /* Top participant with rounded corners */
                if (actBounds.getY() == participantBounds.getY()) {
                    if (participant.isInitiating())
                        participantShape.setParticipantBandKind(ParticipantBandKind.TOP_INITIATING);
                    else
                        participantShape.setParticipantBandKind(ParticipantBandKind.TOP_NON_INITIATING);
                }

                /* Bottom participant with rounded corners */
                else if (actBounds.getY() + actBounds.getHeight() == participantBounds.getY() + participantBounds.getHeight()) {
                    if (participant.isInitiating())
                        participantShape.setParticipantBandKind(ParticipantBandKind.BOTTOM_INITIATING);
                    else
                        participantShape.setParticipantBandKind(ParticipantBandKind.BOTTOM_NON_INITIATING);
                }

                /* Middle participant */
                else {
                    if (participant.isInitiating())
                        participantShape.setParticipantBandKind(ParticipantBandKind.MIDDLE_INITIATING);
                    else
                        participantShape.setParticipantBandKind(ParticipantBandKind.MIDDLE_NON_INITIATING);
                }
            }
        }
    }

    /* Getter & Setter */

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    public DiagramElement getShape() {
        return shape;
    }

    public void setShape(DiagramElement shape) {
        this.shape = shape;
    }

    public BaseElement getNode() {
        return node;
    }

    public void setNode(BaseElement node) {
        this.node = node;
    }

    public String[] getCustomNamespaces() {
        return customNamespaces;
    }

    public void setCustomNamespaces(String[] customNamespaces) {
        this.customNamespaces = customNamespaces;
    }

    public Map<String, String> getExternalNamespaceDefinitions() {
        if (this.externalNamespaceDefinitions == null) {
            this.externalNamespaceDefinitions = new HashMap<String, String>();
        }
        return externalNamespaceDefinitions;
    }
}
