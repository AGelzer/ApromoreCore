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

package de.hpi.bpmn2_0.model.bpmndi;

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
import de.hpi.bpmn2_0.model.bpmndi.di.LabeledShape;
import de.hpi.bpmn2_0.model.bpmndi.di.ParticipantBandKind;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;

/**
 * <p/>
 * Java class for BPMNShape complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="BPMNShape">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/DD/20100524/DI}LabeledShape">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/spec/BPMN/20100524/DI}BPMNLabel" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="bpmnElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="isHorizontal" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isExpanded" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isMarkerVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isMessageVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="participantBandKind" type="{http://www.omg.org/spec/BPMN/20100524/DI}ParticipantBandKind" />
 *       &lt;attribute name="choreographyActivityShape" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "BPMNShape")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BPMNShape", propOrder = {"bpmnLabel"})
public class BPMNShape extends LabeledShape {

    @XmlElement(name = "BPMNLabel")
    protected BPMNLabel bpmnLabel;

    @XmlIDREF
    @XmlAttribute(name = "bpmnElement")
    protected BaseElement bpmnElement;

    @XmlAttribute(name = "isHorizontal")
    protected Boolean isHorizontal;

    @XmlAttribute(name = "isExpanded")
    protected Boolean isExpanded;

    @XmlAttribute(name = "isMarkerVisible")
    protected Boolean isMarkerVisible;

    @XmlAttribute(name = "isMessageVisible")
    protected Boolean isMessageVisible;

    @XmlAttribute(name = "participantBandKind")
    protected ParticipantBandKind participantBandKind;

    @XmlIDREF
    @XmlAttribute(name = "choreographyActivityShape")
    protected BPMNShape choreographyActivityShape;

    public void acceptVisitor(Visitor v) {
        v.visitBpmnShape(this);
    }

    /**
     * Gets the value of the bpmnLabel property.
     *
     * @return possible object is {@link BPMNLabel }
     */
    public BPMNLabel getBPMNLabel() {
        return bpmnLabel;
    }

    /**
     * Sets the value of the bpmnLabel property.
     *
     * @param value allowed object is {@link BPMNLabel }
     */
    public void setBPMNLabel(BPMNLabel value) {
        this.bpmnLabel = value;
    }

    /**
     * Gets the value of the bpmnElement property.
     *
     * @return possible object is {@link BaseElement }
     */
    public BaseElement getBpmnElement() {
        return bpmnElement;
    }

    /**
     * Sets the value of the bpmnElement property.
     *
     * @param value allowed object is {@link BaseElement }
     */
    public void setBpmnElement(BaseElement value) {
        this.bpmnElement = value;
    }

    /**
     * Gets the value of the isHorizontal property.
     *
     * @return possible object is {@link Boolean }
     */
    public Boolean isIsHorizontal() {
        return isHorizontal;
    }

    public boolean isIsHorizontalNoNull() {
        if (this.isHorizontal == null) {
            return false;
        }

        return this.isHorizontal.booleanValue();
    }

    /**
     * Sets the value of the isHorizontal property.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setIsHorizontal(Boolean value) {
        this.isHorizontal = value;
    }

    /**
     * Gets the value of the isExpanded property.
     *
     * @return possible object is {@link Boolean }
     */
    public Boolean isIsExpanded() {
        return isExpanded;
    }

    /**
     * Sets the value of the isExpanded property.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setIsExpanded(Boolean value) {
        this.isExpanded = value;
    }

    /**
     * Gets the value of the isMarkerVisible property.
     *
     * @return possible object is {@link Boolean }
     */
    public Boolean isIsMarkerVisible() {
        return isMarkerVisible;
    }

    /**
     * Sets the value of the isMarkerVisible property.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setIsMarkerVisible(Boolean value) {
        this.isMarkerVisible = value;
    }

    /**
     * Gets the value of the isMessageVisible property.
     *
     * @return possible object is {@link Boolean }
     */
    public Boolean isIsMessageVisible() {
        return isMessageVisible;
    }

    /**
     * Sets the value of the isMessageVisible property.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setIsMessageVisible(Boolean value) {
        this.isMessageVisible = value;
    }

    /**
     * Gets the value of the participantBandKind property.
     *
     * @return possible object is {@link ParticipantBandKind }
     */
    public ParticipantBandKind getParticipantBandKind() {
        return participantBandKind;
    }

    /**
     * Sets the value of the participantBandKind property.
     *
     * @param value allowed object is {@link ParticipantBandKind }
     */
    public void setParticipantBandKind(ParticipantBandKind value) {
        this.participantBandKind = value;
    }

    /**
     * Gets the value of the choreographyActivityShape property.
     *
     * @return possible object is {@link BPMNShape }
     */
    public BPMNShape getChoreographyActivityShape() {
        return choreographyActivityShape;
    }

    /**
     * Sets the value of the choreographyActivityShape property.
     *
     * @param value allowed object is {@link BPMNShape }
     */
    public void setChoreographyActivityShape(BPMNShape value) {
        this.choreographyActivityShape = value;
    }

}
