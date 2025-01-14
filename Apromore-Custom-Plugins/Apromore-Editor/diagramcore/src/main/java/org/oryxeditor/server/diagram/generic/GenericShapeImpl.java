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

package org.oryxeditor.server.diagram.generic;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.label.LabelSettings;
import org.oryxeditor.server.diagram.util.NumberUtil;

import java.util.*;


/**
 * Implementation for the {@link GenericShape} interface
 *
 * @param <S> the actual type of shape to be used (must inherit from {@link GenericShape}); calls to {@link #getChildShapesReadOnly()}, ... will return this type
 * @param <D> the actual type of diagram to be used (must inherit from {@link GenericDiagram}); {@link #getDiagram()} will return this type
 * @author Philipp Maschke, Robert Gurol
 */
public abstract class GenericShapeImpl<S extends GenericShape<S, D>, D extends GenericDiagram<S, D>> implements GenericShape<S, D> {

    /**
     * a shape is an edge if it has more than 1 docker; a shape with only 1 or 0 dockers will always be a node
     *
     * @param dockers
     * @return whether the shape having the given dockers is an edge (if false, it is a node)
     */
    protected static boolean isEdge(List<Point> dockers) {
        return dockers.size() > 1;
    }

    // Refers to a stencil; in contrast to the stencil, contains a stencilset
    // reference as well.
//	protected StencilReference stencilRef;
    protected String stencilId;

    // shape's (resource) ID
    protected String resourceId;

    // properties (such as "name")
    protected Map<String, String> properties;

    protected Map<String, Class<?>> propertyTypes;

    // all direct children of the shape (e.g., tasks etc. in a BPMN pool)
    protected List<S> childShapes;

    // direct parent of the shape (e.g., a pool as parent of a BPMN tasks etc.)
    protected S parent;

    //TODO change to Set<X>?
    // All outgoing shapes. Outgoing shapes are somehow connected to this shape
    // (like BPMN boundary events, or outgoing sequence flows of a task)
    protected List<S> outgoings;

    // All incoming shapes. Incoming shapes are somehow connected to this shape
    // (like a BPMN sequence flow that points to a task (this)).

    protected List<S> incomings;

    // Docker points within the shape. BPMN's events have one central docker,
    // other shapes with (more) dockers are edges. For edges, dockers are points
    // between which there are straight lines (e.g., a straight sequence flow
    // can have only two dockers; one that makes a 90 degree turn has at least
    // three).
    protected List<Point> dockers;

    //
    protected Map<String, LabelSettings> labelSettings;

    // bounds represent the boundary of a shape, its maximum extension; they are
    // defined by an upper left and a lower right Point
    protected Bounds bounds;

    // the diagram containing the shape; is calculated
    protected D diagram;

    /**
     * Constructs a new shape with id and stencilRef
     *
     * @param resourceId unique shape id, generated by the editor
     * @param stencilId  StencilType with stencilId
     */
    public GenericShapeImpl(String resourceId, String stencilId) {
        this(resourceId);
        this.stencilId = stencilId;
    }

    /**
     * Set a new id for the shape
     *
     * @param resourceId
     */
    public GenericShapeImpl(String resourceId) {
        this.resourceId = resourceId;
        this.properties = new HashMap<String, String>();
        this.propertyTypes = new HashMap<String, Class<?>>();
        this.childShapes = new LinkedList<S>();
        this.outgoings = new LinkedList<S>();
        this.incomings = new LinkedList<S>();
        this.dockers = new ArrayList<Point>(5);//needs random access, usually no more than 4 dockers
        this.labelSettings = new HashMap<String, LabelSettings>();

        this.bounds = new Bounds();

        this.stencilId = null;
    }


    public boolean isEdge() {
        return this instanceof GenericEdge<?, ?>;
    }


    public boolean isNode() {
        return this instanceof GenericNode<?, ?>;
    }


    public String getStencilId() {
        return stencilId;
    }


    public void setStencilId(String stencilId) {
        this.stencilId = stencilId;
    }


    public String getResourceId() {
        return resourceId;
    }


    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }


    public Map<String, String> getPropertiesReadOnly() {
        return Collections.unmodifiableMap(this.properties);
    }


    public void setProperties(Map<String, String> properties) {
        this.properties.clear();
        propertyTypes.clear();
        if (properties != null) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                setProperty(entry.getKey(), entry.getValue());
            }
        }
    }


    public boolean hasProperty(String name) {
        return this.properties.containsKey(name);
    }


    public Set<String> getPropertyNames() {
        return this.properties.keySet();
    }


    public String getProperty(String name) {
        return this.properties.get(name);
    }


    public Object getPropertyObject(String name) {
        Class<?> type = propertyTypes.get(name);

        if (type == null || type.equals(String.class))
            return getProperty(name);
        else if (type.equals(Integer.class))
            return getPropertyInteger(name);
        else if (type.equals(Long.class))
            return getPropertyLong(name);
        else if (type.equals(Float.class))
            return getPropertyFloat(name);
        else if (type.equals(Double.class))
            return getPropertyDouble(name);
        else if (type.equals(Boolean.class))
            return getPropertyBoolean(name);
        else if (type.equals(JSONObject.class))
            return getPropertyJsonObject(name);
        else if (type.equals(JSONArray.class))
            return getPropertyJsonArray(name);
        else
            throw new RuntimeException("Property supposedly of type '" +
                    type.getName() +
                    "' but only the following are supported: String, Integer, Long, Double, Float, Boolean, JSONObject, JSONArray");
    }

    public Integer getPropertyInteger(String name) {
        return NumberUtil.createInt(getProperty(name));
    }

    public Long getPropertyLong(String name) {
        return NumberUtil.createLong(getProperty(name));
    }

    public Float getPropertyFloat(String name) {
        return NumberUtil.createFloat(getProperty(name));
    }

    public Double getPropertyDouble(String name) {
        return NumberUtil.createDouble(getProperty(name));
    }

    public Boolean getPropertyBoolean(String name) {
        String value = getProperty(name);
        if ("true".equalsIgnoreCase(value))
            return true;
        else if ("false".equalsIgnoreCase(value))
            return false;
        else
            return null;
    }

    public JSONObject getPropertyJsonObject(String name) {
        String value = getProperty(name);
        if (value != null && value.startsWith("{") && value.endsWith("}")) {
            try {
                return new JSONObject(value);
            } catch (JSONException e) {
                return null;
            }
        } else
            return null;
    }

    public JSONArray getPropertyJsonArray(String name) {
        String value = getProperty(name);
        if (value != null && value.startsWith("[") && value.endsWith("]")) {
            try {
                return new JSONArray(value);
            } catch (JSONException e) {
                return null;
            }
        } else
            return null;
    }


    public String removeProperty(String name) {
        propertyTypes.remove(name);
        return this.properties.remove(name);
    }


    public String setProperty(String name, String value) {
        propertyTypes.put(name, String.class);
        return this.properties.put(name, (value == null) ? null : value.trim());
    }


    public String setProperty(String name, Object value) {
        if (value == null || value instanceof String)
            return setProperty(name, (String) value);
        else if (value instanceof Integer)
            return setProperty(name, ((Integer) value).intValue());
        else if (value instanceof Long)
            return setProperty(name, ((Long) value).longValue());
        else if (value instanceof Double)
            return setProperty(name, ((Double) value).doubleValue());
        else if (value instanceof Float)
            return setProperty(name, ((Float) value).floatValue());
        else if (value instanceof Boolean)
            return setProperty(name, ((Boolean) value).booleanValue());
        else if (value instanceof JSONObject)
            return setProperty(name, (JSONObject) value);
        else if (value instanceof JSONArray)
            return setProperty(name, (JSONArray) value);
        else
            throw new IllegalArgumentException("Value is of type '" +
                    value.getClass().getName() +
                    "' but must be one of the following: String, Integer, Long, Double, Float, Boolean, JSONObject, JSONArray");
    }

    public String setProperty(String name, int value) {
        propertyTypes.put(name, Integer.class);
        return properties.put(name, Integer.toString(value));
    }

    public String setProperty(String name, long value) {
        propertyTypes.put(name, Long.class);
        return properties.put(name, Long.toString(value));
    }

    public String setProperty(String name, float value) {
        propertyTypes.put(name, Float.class);
        return properties.put(name, Float.toString(value));
    }

    public String setProperty(String name, double value) {
        propertyTypes.put(name, Double.class);
        return properties.put(name, Double.toString(value));
    }

    public String setProperty(String name, boolean value) {
        propertyTypes.put(name, Boolean.class);
        return properties.put(name, Boolean.toString(value));
    }

    public String setProperty(String name, JSONObject value) {
        propertyTypes.put(name, JSONObject.class);
        return properties.put(name, (value == null) ? null : value.toString());
    }

    public String setProperty(String name, JSONArray value) {
        propertyTypes.put(name, JSONArray.class);
        return properties.put(name, (value == null) ? null : value.toString());
    }


    public List<S> getChildShapesReadOnly() {
        return Collections.unmodifiableList(this.childShapes);
    }


    public Set<S> getDescendantShapesReadOnly() {
        Set<S> childShapes = new HashSet<S>();
        for (S childShape : this.getChildShapesReadOnly()) {
            childShapes.addAll(childShape.getDescendantShapesReadOnly());
            childShapes.add(childShape);
        }
        return Collections.unmodifiableSet(childShapes);
    }


    public List<S> getAncestorShapesReadOnly() {
        List<S> resList = new LinkedList<S>();
        S parent = this.getParent();
        while (parent != null) {
            resList.add(parent);
            parent = (S) parent.getParent();
        }
        return Collections.unmodifiableList(resList);
    }


    public void setChildShapes(List<S> childShapes) {
        this.childShapes.clear();
        if (childShapes == null)
            return;

        for (S shape : childShapes) {
            addChildShape(shape);
        }
    }


    public void addChildShape(S shape) {
        if (shape == null || shape == this)
            return;

        if (shape instanceof GenericDiagram)
            return;

        //TODO add circular dependency check! (check whether new shape is already an ancestor of this shape)

        if (!this.childShapes.contains(shape))
            this.childShapes.add(shape);

        // distinction unnecessary, as setParent is now side effect free again
        // if (shape.getParent() != this)
        shape.setParent((S) this);

        addToDiagramShapeCache(shape);
    }


    public int getNumChildShapes() {
        return childShapes.size();
    }


    public void setDiagram(D diagram2) {
        this.diagram = diagram2;
    }


    public void removeChildShape(S shape) {
        if (shape == null)
            return;

        childShapes.remove(shape);

        removeFromDiagramShapeCache(shape);

        // has to be last, as the getDiagram method relies on a parent being
        // present
        shape.setParent(null);
    }


    public void removeAllChildShapes() {
        D diagram = getDiagram();

        for (S child : childShapes) {
            child.removeAllChildShapes();

            if (diagram != null)
                diagram.removeFromAllShapes(child);

            child.setDiagram(null);

            // has to be last, as the getDiagram method relies on a parent being
            // present
            child.setParent(null);
        }

        childShapes.clear();
    }


    public S getParent() {
        return parent;
    }


    public void setParent(S parent) {
        this.parent = parent;
    }


    public void setParentAndUpdateItsChildShapes(S parent) {
        if (this.parent != null) {
            this.parent.getChildShapesReadOnly().remove(this);
        }
        this.parent = parent;
        if (parent != null && !parent.hasChild((S) this))
            parent.addChildShape((S) this);
    }


    public D getDiagram() {
        if (this.diagram == null) {
            // Lookup the parent till diagram is found
            S parent = this.getParent();
            while (parent != null && !(parent instanceof GenericDiagram))
                parent = (S) parent.getParent();
            if (parent instanceof GenericDiagram)
                this.diagram = (D) parent;
        }
        return this.diagram;
    }


    public void addDocker(Point p) {
        this.dockers.add(p);
    }


    public void addDocker(Point p, int position) {
        this.dockers.add(position, p);
    }


    /**
     * Returns an unmodifiable view on the shape's dockers.
     */
    public List<Point> getDockersReadOnly() {
        return Collections.unmodifiableList(dockers);
    }


    public int getNumDockers() {
        return this.dockers.size();
    }


    public void setDockers(List<Point> dockers) {
        this.dockers.clear();
        if (dockers != null)
            this.dockers.addAll(dockers);
    }


    public Point getDockerAt(int index) {
        if (index < 0 || index >= this.dockers.size())
            return null;
        else
            return this.dockers.get(index);
    }


    public void removeDockerAt(int index) {
        this.dockers.remove(index);
    }


    public Bounds getBounds() {
        return bounds.copy();
    }


    public Bounds getAbsoluteBounds() {
        Bounds bounds = this.bounds.copy();
        S parent = this.getParent();
        while (parent != null) {
            bounds.moveBy(parent.getUpperLeft());
            parent = (S) parent.getParent();
        }
        return bounds;
    }


    public void setBounds(Bounds bounds) {
        if (bounds == null)
            this.bounds = null;
        else
            this.bounds = bounds.copy();
    }


    /**
     * Returns an unmodifiable view on the shape's incoming shapes
     */
    public List<S> getIncomingsReadOnly() {
        return Collections.unmodifiableList(this.incomings);
    }


    protected void setIncomings(List<S> incomings) {
        this.incomings.clear();
        if (incomings != null)
            this.incomings.addAll(incomings);
    }


    public boolean hasIncoming(S shape) {
        return this.incomings.contains(shape);
    }


    public int getNumIncomings() {
        return incomings.size();
    }


    public void setIncomingsAndUpdateTheirOutgoings(List<S> incomings) {
        for (S shape : this.incomings) {
//			if (shape.hasOutgoing((X) this))
            getImplClass(shape).removeOutgoing((S) this);
        }

        this.setIncomings(incomings);

        for (S shape : this.incomings) {
            if (!shape.hasOutgoing((S) this))
                getImplClass(shape).addOutgoing((S) this);
        }
    }


    protected boolean addIncoming(S shape) {
        if (shape != null && !this.incomings.contains(shape))
            return this.incomings.add(shape);
        return false;
    }


    public boolean addIncomingAndUpdateItsOutgoings(S shape) {
        if (shape != null && !shape.hasOutgoing((S) this))
            getImplClass(shape).addOutgoing((S) this);

        return this.addIncoming(shape);
    }


    protected boolean removeIncoming(S shape) {
        return this.incomings.remove(shape);
    }


    public boolean removeIncomingAndUpdateItsOutgoings(S shape) {
        if (shape != null)
            getImplClass(shape).removeOutgoing((S) this);
        return removeIncoming(shape);
    }


    public List<S> getOutgoingsReadOnly() {
        return Collections.unmodifiableList(outgoings);
    }


    protected void setOutgoings(List<S> outgoings) {
        this.outgoings.clear();
        if (outgoings != null)
            this.outgoings.addAll(outgoings);
    }


    public boolean hasOutgoing(S shape) {
        return this.outgoings.contains(shape);
    }


    public int getNumOutgoings() {
        return outgoings.size();
    }


    public void setOutgoingsAndUpdateTheirIncomings(List<S> outgoings) {
        for (S shape : this.outgoings) {
//			if (shape.hasIncoming((X) this))
            getImplClass(shape).removeIncoming((S) this);
        }

        this.setOutgoings(outgoings);

        for (S shape : this.outgoings) {
            if (!shape.hasIncoming((S) this))
                getImplClass(shape).addIncoming((S) this);
        }
    }


    protected boolean addOutgoing(S shape) {
        if (shape != null && !this.outgoings.contains(shape))
            return this.outgoings.add(shape);
        return false;
    }


    public boolean addOutgoingAndUpdateItsIncomings(S shape) {
        if (shape != null && !shape.hasIncoming((S) this))
            getImplClass(shape).addIncoming((S) this);

        return this.addOutgoing(shape);
    }


    protected boolean removeOutgoing(S shape) {
        return this.outgoings.remove(shape);
    }


    public boolean removeOutgoingAndUpdateItsIncomings(S shape) {
        if (shape != null)
            getImplClass(shape).removeIncoming((S) this);
        return this.removeOutgoing(shape);
    }


    public List<S> getConnectedShapesReadOnly() {
        List<S> shapes = new ArrayList<S>();
        shapes.addAll(this.getIncomingsReadOnly());
        shapes.addAll(this.getOutgoingsReadOnly());

        return Collections.unmodifiableList(shapes);
    }


    public Point getUpperLeft() {
        if (this.bounds != null)
            return this.bounds.getUpperLeft();
        return null;
    }


    public Point getLowerRight() {
        if (this.bounds != null)
            return this.bounds.getLowerRight();
        return null;
    }


    public double getHeight() {
        return this.getBounds().getHeight();
    }


    public double getWidth() {
        return this.getBounds().getWidth();
    }


    public int hashCode() {
        return resourceId.hashCode();
    }


    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        if (obj instanceof GenericShapeImpl<?, ?>) {
            GenericShapeImpl<?, ?> other = (GenericShapeImpl<?, ?>) obj;
            if (resourceId == null) {
                if (other.getResourceId() != null)
                    return false;
            } else if (!resourceId.equals(other.getResourceId()))
                return false;
            return true;
        }
        return false;
    }


    public LabelSettings getLabelSettingsForReference(String referencedLabel) {
        return labelSettings.get(referencedLabel);
    }


    public Collection<LabelSettings> getLabelSettings() {
        return labelSettings.values();
    }


    public void setLabelSettings(Collection<LabelSettings> labelPositionings) {
        this.labelSettings.clear();
        if (labelPositionings == null)
            return;

        for (LabelSettings alignment : labelPositionings) {
            this.labelSettings.put(alignment.getReference(), alignment);
        }
    }


    public void addLabelSetting(LabelSettings newSetting) {
        if (newSetting != null)
            this.labelSettings.put(newSetting.getReference(), newSetting);
    }


    public boolean isPointIncluded(Point p) {
        return this.bounds.isPointIncluded(p);
    }


    public boolean isPointIncludedAbsolute(Point p) {
        return this.getAbsoluteBounds().isPointIncluded(p);
    }


    public boolean hasChild(S s) {
        if (this.getChildShapesReadOnly().contains(s))
            return true;
        return false;
    }


    public boolean contains(S s) {
        if (this.hasChild(s))
            return true;
        for (S s2 : this.getChildShapesReadOnly()) {
            if (s2.hasChild(s))
                return true;
        }
        return false;
    }

    public String getQualifiedStencilId() {
        if (getDiagram() != null && getDiagram().getStencilsetRef() != null && getDiagram().getStencilsetRef().getNamespace() != null)
            return getDiagram().getStencilsetRef().getNamespace() + getStencilId();
        else
            return getStencilId();
    }


    /**
     * Adds the shape and if needed all its child shapes (recursively) to the cache of all shapes in the diagram
     *
     * @param shape
     */
    private void addToDiagramShapeCache(S shape) {
        // method is used in Diagram as well, but no special treatment is
        // necessary, as Diagram#getDiagram returns "this"
        D diagram = this.getDiagram();

        if (diagram != null && !diagram.containsShape(shape)) {
            diagram.addToAllShapes(shape);
            shape.setDiagram(diagram);

            //update all child shapes of the new shape too!
            for (S child : shape.getChildShapesReadOnly())
                addToDiagramShapeCache(child);
        }
    }


    /**
     * Removes the shape and if needed all its child shapes (recursively) from the cache of all shapes in the diagram
     *
     * @param shape
     */
    private void removeFromDiagramShapeCache(S shape) {
        D diagram = (D) shape.getDiagram();

        if (diagram != null && diagram.containsShape(shape)) {
            diagram.removeFromAllShapes(shape);
            shape.setDiagram(null);

            //update all child shapes of the new shape too!
            for (S child : shape.getChildShapesReadOnly())
                removeFromDiagramShapeCache(child);
        }
    }


    protected GenericShapeImpl<S, D> getImplClass(S shape) {
        if (shape instanceof GenericShapeImpl)
            return (GenericShapeImpl<S, D>) shape;
        else
            throw new RuntimeException("Discovered instance of " + GenericShape.class.getSimpleName() +
                    ", whose implementing class is not " + GenericShapeImpl.class.getSimpleName());
    }
}
