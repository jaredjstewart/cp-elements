/*
 * Copyright 2016 Author or Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cp.elements.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import org.cp.elements.beans.event.ChangeListener;
import org.cp.elements.beans.event.ChangeSupport;
import org.cp.elements.beans.event.ChangeTracker;
import org.cp.elements.lang.Assert;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.Visitor;
import org.cp.elements.util.ComparatorUtils;

/**
 * The AbstractBean class is an abstract base class for modeling application data as domain model objects.
 * 
 * @author John J. Blum
 * @param <ID> the Comparable class type of the identifier uniquely identifying this Bean.
 * @param <USER> the class type of the object identifying the user for auditing information.
 * @param <PROCESS> the class type of the object identifying the process for auditing information.
 * @see java.beans.PropertyChangeEvent
 * @see java.beans.PropertyChangeListener
 * @see java.beans.PropertyChangeSupport
 * @see java.beans.VetoableChangeListener
 * @see java.beans.VetoableChangeSupport
 * @see java.lang.Comparable
 * @see java.time.LocalDateTime
 * @see org.cp.elements.beans.Bean
 * @see org.cp.elements.beans.event.ChangeEvent
 * @see org.cp.elements.beans.event.ChangeListener
 * @see org.cp.elements.beans.event.ChangeSupport
 * @see org.cp.elements.beans.event.ChangeTracker
 * @see org.cp.elements.lang.Visitor
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractBean<ID extends Comparable<ID>, USER, PROCESS> implements Bean<ID, USER, PROCESS> {

  private static final boolean DEFAULT_EVENT_DISPATCH_ENABLED = true;

  private volatile boolean eventDispatchEnabled = DEFAULT_EVENT_DISPATCH_ENABLED;

  private final ChangeSupport changeSupport = new ChangeSupport(this);

  private final ChangeTracker changeTracker = new ChangeTracker();

  private ID id;

  private LocalDateTime createdOn;
  private LocalDateTime lastModifiedOn;
  private LocalDateTime modifiedOn;

  private final Map<String, String> propertyNameToFieldNameMapping = new TreeMap<>();

  private final Map<String, ParameterizedStateChangeCallback> propertyNameToParameterizedStateChangeCallbackMapping =
    new TreeMap<>();

  private PROCESS createdWith;
  private PROCESS lastModifiedWith;
  private PROCESS modifiedWith;

  private transient final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this, null, null, null);

  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  private USER createdBy;
  private USER lastModifiedBy;
  private USER modifiedBy;

  private final VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);

  /**
   * Default constructor for creating an instance of the AbstractBean class.
   */
  public AbstractBean() {
    addPropertyChangeListener(changeTracker);
  }

  /**
   * Creates an instance of the AbstractBean class initialized with the specified typed identifier.  It is recommended
   * that the identifier uniquely identify objects within this Bean class.
   * 
   * @param id the generically typed identifier uniquely identifying objects of this particular Bean class.
   * @see org.cp.elements.lang.Identifiable
   */
  public AbstractBean(ID id) {
    this();
    this.id = id;
  }

  /**
   * Gets the user who is responsible for the creation of this object.
   * 
   * @return an object denoting the user who created this object.
   * @see org.cp.elements.lang.Auditable
   */
  public USER getCreatedBy() {
    return createdBy;
  }

  /**
   * Sets the user who is responsible for the creation of this object.
   * 
   * @param createdBy an object denoting the user who created this object.
   * @see org.cp.elements.lang.Auditable
   */
  public void setCreatedBy(USER createdBy) {
    processChange("createdBy", this.createdBy, createdBy);
  }

  /**
   * Gets the date and time when this object was created.
   * 
   * @return a Calendar object denoting the date and time when this object was created.
   * @see org.cp.elements.lang.Auditable
   */
  public LocalDateTime getCreatedOn() {
    return createdOn;
  }

  /**
   * Sets the date and time when this object was created.
   * 
   * @param createdOn a Calendar object denoting the date and time when this object was created.
   * @see org.cp.elements.lang.Auditable
   */
  public void setCreatedOn(LocalDateTime createdOn) {
    processChange("createdOn", this.createdOn, createdOn);
  }

  /**
   * Gets the process (the what) that functionally created this object.
   * 
   * @return an object denoting the process that created this object.
   * @see org.cp.elements.lang.Auditable
   */
  public PROCESS getCreatedWith() {
    return createdWith;
  }

  /**
   * Sets the process (the what) that functionally created this object.
   * 
   * @param creatingProcess an object denoting the process that created this object.
   * @see org.cp.elements.lang.Auditable
   */
  public void setCreatedWith(PROCESS creatingProcess) {
    processChange("createdWith", this.createdWith, creatingProcess);
  }

  /**
   * Determines whether event dispatching is enabled for property changes on this Bean.
   * 
   * @return a boolean value indicating whether event dispatching is enabled for property changes on this Bean.
   */
  public boolean isEventDispatchEnabled() {
    return eventDispatchEnabled;
  }

  /**
   * Enables or disables event dispatching for property changes on this Bean.
   * 
   * @param eventDispatchEnabled a boolean value indicating whether to enable or disable event dispatching
   * for property changes to this Bean.
   */
  protected final void setEventDispatchEnabled(boolean eventDispatchEnabled) {
    this.eventDispatchEnabled = eventDispatchEnabled;
  }

  /**
   * Gets the name of the field mapped to the given property.  If no such mapping exists, then the given property name
   * is returned.
   * 
   * @param propertyName a String value specifying the name of the property to retrieve the corresponding field name.
   * @return a String value indicating the field name corresponding to the specified property.
   */
  protected String getFieldName(String propertyName) {
    return ObjectUtils.defaultIfNull(propertyNameToFieldNameMapping.get(propertyName), propertyName);
  }

  /**
   * Gets the identifier uniquely identifying this object.
   * 
   * @return the value of type T indicating this object's assigned unique identifier.
   * @see org.cp.elements.lang.Identifiable
   */
  public ID getId() {
    return id;
  }

  /**
   * Sets the identifier uniquely identifying this object.
   * 
   * @param id a value of type T assigned as this object's unique identifier.
   * @see org.cp.elements.lang.Identifiable
   */
  public final void setId(ID id) {
    processChange("id", this.id, id);
  }

  /**
   * Gets the user who was last responsible for modifying this object.
   * 
   * @return an object denoting the last user responsible for modifying this object.
   * @see org.cp.elements.lang.Auditable
   */
  public USER getLastModifiedBy() {
    return lastModifiedBy;
  }

  /**
   * Gets the last date and time when this object was modified.
   * 
   * @return a Calendar object denoting the date and time when this object was last modified.
   * @see org.cp.elements.lang.Auditable
   */
  public LocalDateTime getLastModifiedOn() {
    return lastModifiedOn;
  }

  /**
   * Gets the process (the what) that was last responsible for modifying this object.
   * 
   * @return an object denoting the last process responsible for modifying this object.
   * @see org.cp.elements.lang.Auditable
   */
  public PROCESS getLastModifiedWith() {
    return lastModifiedWith;
  }

  /**
   * Determines whether this Auditable object has been modified.  One particular implementation suggests that
   * if the last modified date/time does not match the current modified date/time then the Auditable object has
   * been modified.
   * 
   * @return a boolean value indicating whether this Auditable object has been modified or not.
   * @see org.cp.elements.lang.Auditable
   */
  public boolean isModified() {
    return changeTracker.isModified();
  }

  /**
   * Determines whether the specified property of this Auditable object has been modified.  The property has been
   * changed if the old and new value are not equal in value.
   * 
   * @param propertyName a String value specifying the name of the property to check for modification.
   * @return a boolean value indicating whether the specified property of this Auditable object, identified by name,
   * has been modified.
   */
  public boolean isModified(String propertyName) {
    return changeTracker.isModified(propertyName);
  }

  /**
   * Gets the user who is responsible for modifying this object.
   * 
   * @return an object denoting the user who modified this object.
   * @see org.cp.elements.lang.Auditable
   */
  public USER getModifiedBy() {
    return modifiedBy;
  }

  /**
   * Sets the user who is responsible for modifying this object.
   * 
   * @param modifiedBy an object denoting the user who modified this object.
   * @see org.cp.elements.lang.Auditable
   */
  @SuppressWarnings("unchecked")
  public void setModifiedBy(USER modifiedBy) {
    processChange("modifiedBy", this.modifiedBy, modifiedBy);
    this.lastModifiedBy = ObjectUtils.defaultIfNull(this.lastModifiedBy, this.modifiedBy);
  }

  /**
   * Gets the date and time when this object was modified.
   * 
   * @return a Calendar object denoting the date and time when this object was modified.
   * @see org.cp.elements.lang.Auditable
   */
  public LocalDateTime getModifiedOn() {
    return modifiedOn;
  }

  /**
   * Sets the date and time when this object was modified.
   * 
   * @param modifiedOn a Calendar object denoting the date and time when this object was modified.
   * @see org.cp.elements.lang.Auditable
   */
  public void setModifiedOn(LocalDateTime modifiedOn) {
    processChange("modifiedOn", this.modifiedOn, modifiedOn);
    this.lastModifiedOn = ObjectUtils.defaultIfNull(this.lastModifiedOn, this.modifiedOn);
  }

  /**
   * Gets the process (the what) that functionally modified this object.
   * 
   * @return an object denoting the process that modified this object.
   * @see org.cp.elements.lang.Auditable
   */
  public PROCESS getModifiedWith() {
    return modifiedWith;
  }

  /**
   * Sets the process (the what) that functionally modified this object.
   * 
   * @param modifyingProcess an object denoting the process that modified this object.
   * @see org.cp.elements.lang.Auditable
   */
  @SuppressWarnings("unchecked")
  public void setModifiedWith(PROCESS modifyingProcess) {
    processChange("modifiedWith", this.modifiedWith, modifyingProcess);
    this.lastModifiedWith = ObjectUtils.defaultIfNull(this.lastModifiedWith, this.modifiedWith);
  }

  /**
   * Makes a determination, or assessment for whether this Bean (object) is new.
   * 
   * @return a boolean value indicating whether this Bean is new or not.
   * @see org.cp.elements.lang.Identifiable
   */
  public boolean isNew() {
    return (getId() == null);
  }

  /**
   * Accepts a Visitor implementation for performing some operation or inspection on this object.
   * 
   * @param visitor an object who's class implements the Visitor interface to walk a graph of objects
   * in order to perform some inspection or operation on the object graph.
   * @see org.cp.elements.lang.Visitable
   * @see org.cp.elements.lang.Visitor
   */
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  /**
   * Adds the specified ChangeListener to the list of listeners to be notified of change events (or changes)
   * to this Bean.
   * 
   * @param listener the ChangeListener to add to the list of listeners being notified of change events on
   * this Bean.
   * @see org.cp.elements.beans.event.ChangeListener
   * @see org.cp.elements.beans.event.ChangeSupport
   */
  protected void addChangeListener(ChangeListener listener) {
    changeSupport.add(listener);
  }

  /**
   * Adds the specified PropertyChangeListener to listen for and be notified of all property change events on
   * this Bean.
   * 
   * @param listener the PropertyChangeListener listening for all property change events on this Bean.
   * @see java.beans.PropertyChangeListener
   * @see java.beans.PropertyChangeSupport
   */
  protected void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  /**
   * Adds the specified PropertyChangeListener to listen for and receive notifications for changes to the specified
   * property of this Bean.
   * 
   * @param propertyName a String value specifying the name of the property on this Bean for which the specified
   * listener will receive property change events.
   * @param listener the PropertyChangeListener listening for property changes events on the specified property.
   * @see java.beans.PropertyChangeListener
   * @see java.beans.PropertyChangeSupport
   */
  protected void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * Adds the specified VetoableChangeListener to this Bean listening for property change events in order to monitor
   * and veto any undesired change to this Bean's state.
   * 
   * @param listener the VetoableChangeListener listening for and potentially vetoing any undesired property change
   * events to this Bean.
   * @see java.beans.VetoableChangeListener
   * @see java.beans.VetoableChangeSupport
   */
  protected void addVetoableChangeListener(VetoableChangeListener listener) {
    vetoableChangeSupport.addVetoableChangeListener(listener);
  }

  /**
   * Adds the specified VetoableChangeListener to this Bean listening for change events on the specified property
   * in order to monitor and veto any undesired changes.
   * 
   * @param propertyName a String value specifying the name of the property on this Bean for wihch the specified
   * listener will receive property change events.
   * @param listener the VetoableChangeListener listening for and potentially vetoing any undesired property change
   * events to this Bean.
   * @see java.beans.VetoableChangeListener
   * @see java.beans.VetoableChangeSupport
   */
  protected void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
    vetoableChangeSupport.addVetoableChangeListener(propertyName, listener);
  }

  /**
   * Changes the value of the given property, referenced by name, to the new Object value, effectively modifying
   * the internal state of this Bean.  This method uses the Java reflective APIs to set the field corresponding
   * to the specified property.
   * 
   * @param propertyName a String value specifying the name of the property (internally, the field) to set to
   * the new Object value.
   * @param newValue an Object containing the new value for the given property (internally, the field).
   * @throws PropertyNotFoundException if property referenced by name is not a property of this Bean.  This Exception
   * occurs when the field for the corresponding property does not exist.
   * @see #getFieldName(String)
   */
  void changeState(String propertyName, Object newValue) {
    try {
      ObjectUtils.setField(this, getFieldName(propertyName), newValue);
    }
    catch (IllegalArgumentException e) {
      throw new PropertyNotFoundException(String.format(
        "The property (%1$s) corresponding to field (%2$s) was not found in this Bean (%3$s)!",
          propertyName, getFieldName(propertyName), getClass().getName()), e);
    }
  }

  /**
   * Compares the specified Bean with this Bean to determine order.  The default implementation is to
   * order by identifier, where null identifiers are ordered before non-null identifiers.
   * 
   * @param obj the Bean being compared with this Bean.
   * @return an integer value indicating the relative ordering of the specified Bean with this Bean.
   * A negative value implies this Bean logically occurs before the specified Bean.  A zero value indicates
   * the specified Bean and this Bean are comparatively equal, and a positive value indicates this Bean logically
   * occurs after the specified Bean.
   * @throws IllegalArgumentException if the specified Bean is not an instance of this Bean class.
   * @see java.lang.Comparable#compareTo(Object)
   */
  @SuppressWarnings("all")
  public int compareTo(Bean<ID, USER, PROCESS> obj) {
    Assert.isInstanceOf(obj, getClass(), new ClassCastException(String.format(
      "The Bean being compared with this Bean must be an instance of %1$s!", getClass().getName())));
    return ComparatorUtils.compareIgnoreNull(getId(), obj.getId());
  }

  /**
   * Creates a new instance of the PropertyChangeEvent initialized with this Bean as the source as well as the
   * name of the property that is changing along with the property's old and new values.  A PropertyChangeEvent
   * will be created only if event dispatching to registered listeners is enabled and there are either
   * PropertyChangeListeners or VetoableChangeListeners registered on this Bean.
   * 
   * @param propertyName a String value specifying the name of the property on this Bean that is being changed.
   * @param oldValue an Object containing the old value of the specified property.
   * @param newValue an Object containing the new value for the specified property.
   * @return a PropertyChangeEvent for this Bean specifying the name of the property changing along with the
   * property's old and new value.
   * @see java.beans.PropertyChangeEvent
   */
  protected PropertyChangeEvent newPropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
    if (isEventDispatchEnabled()) {
      if (vetoableChangeSupport.hasListeners(propertyName) || propertyChangeSupport.hasListeners(propertyName)) {
        return new PropertyChangeEvent(this, propertyName, oldValue, newValue);
      }
    }

    return this.propertyChangeEvent;
  }

  /**
   * Determines whether this Bean and the specified Bean are equal in value.  This Bean and the specified Bean
   * are considered equal in value if their identifiers match.  Note, a null identifier is not logically equivalent
   * to any other Bean, even other Beans with a null identifier regardless of the Bean's other state.
   * 
   * @param obj the Bean object being compared for equality with this Bean.
   * @return a boolean value indicating whether this Bean and the specified Bean are logically equal.
   * @see java.lang.Object#equals(Object)
   * @see org.cp.elements.lang.ObjectUtils#equals(Object, Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Bean)) {
      return false;
    }

    final Bean that = (Bean) obj;

    return ObjectUtils.equals(this.getId(), that.getId());
  }

  /**
   * Notifies all ChangeListeners of a change event happening on this Bean.
   * 
   * @see org.cp.elements.beans.event.ChangeEvent
   * @see org.cp.elements.beans.event.ChangeListener
   * @see org.cp.elements.beans.event.ChangeSupport#fireChangeEvent()
   * @see org.cp.elements.beans.event.ChangeSupport#hasListeners()
   */
  protected void fireChange() {
    if (isEventDispatchEnabled()) {
      if (isModified() && changeSupport.hasListeners()) {
        changeSupport.fireChangeEvent();
      }
    }
  }

  /**
   * Notifies all PropertyChangeListeners of a property change event occurring on this Bean.
   * 
   * @param event the PropertyChangeEvent indicating the property who's value is changing.
   * @throws NullPointerException if the PropertyChangeEvent is null.
   * @see java.beans.PropertyChangeEvent
   * @see java.beans.PropertyChangeListener
   * @see java.beans.PropertyChangeSupport#firePropertyChange(java.beans.PropertyChangeEvent)
   * @see java.beans.PropertyChangeSupport#hasListeners(String)
   */
  protected void firePropertyChange(PropertyChangeEvent event) {
    if (isEventDispatchEnabled()) {
      Assert.notNull(event, "The PropertyChangeEvent cannot be null!");
      if (propertyChangeSupport.hasListeners(event.getPropertyName())) {
        propertyChangeSupport.firePropertyChange(event);
      }
    }
  }

  /**
   * Notifies all VetoableChangeListeners of a property change event occurring on this Bean.  The change to
   * the property may potentially be vetoed by one of the VetoableChangeListeners listening to property changes
   * on this Bean, resulting in preventing the value of the property to change.
   * 
   * @param event the PropertyChangeEvent indicating the property who's value is changing.
   * @throws NullPointerException if the PropertyChangeEvent is null.
   * @throws PropertyVetoException if the property change is vetoed by a VetoableChangeListener listening to
   * property changes on this Bean.
   * @see java.beans.PropertyChangeEvent
   * @see java.beans.VetoableChangeListener
   * @see java.beans.VetoableChangeSupport#fireVetoableChange(java.beans.PropertyChangeEvent)
   * @see java.beans.VetoableChangeSupport#hasListeners(String)
   */
  protected void fireVetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
    if (isEventDispatchEnabled()) {
      Assert.notNull(event, "The PropertyChangeEvent cannot be null!");
      if (vetoableChangeSupport.hasListeners(event.getPropertyName())) {
        vetoableChangeSupport.fireVetoableChange(event);
      }
    }
  }

  /**
   * Computes the hash code value for this Bean as determined by this Bean's internal state.
   * 
   * @return an integer value representing the computed hash code of this Bean.
   * @see java.lang.Object#hashCode()
   * @see org.cp.elements.lang.ObjectUtils#hashCode(Object)
   */
  @Override
  public int hashCode() {
    int hashValue = 17;
    hashValue = 37 * hashValue + ObjectUtils.hashCode(getId());
    return hashValue;
  }

  /**
   * Creates a mapping from the specified property on this Bean class to one of the Bean object's fields.
   * 
   * @param propertyName a String value specifying the name of a property on this Bean class.
   * @param fieldName a String value specifying the name of the Bean object's field.
   * @return a boolean value indicating whether the mapping between the property and field was successfully
   * created.
   */
  protected boolean mapPropertyNameToFieldName(final String propertyName, final String fieldName) {
    // TODO add possible validation for the property name and field name of this Bean class.
    propertyNameToFieldNameMapping.put(propertyName, fieldName);
    return propertyNameToFieldNameMapping.containsKey(propertyName);
  }

  /**
   * Creates a mapping from the specified property on this Bean to the specified ParameterizedStateChangeCallback
   * for affecting state transitions and changes.
   * 
   * @param propertyName a String value specifying the name of the property on this Bean class.
   * @param callback a ParameterizedStateChangeCallback affecting the state change/transition.
   * @return a boolean indicating that the mapping was added successfully.
   */
  protected boolean mapPropertyNameToParameterizedStateChangeCallback(String propertyName,
      ParameterizedStateChangeCallback callback) {

    propertyNameToParameterizedStateChangeCallbackMapping.put(propertyName, callback);
    return propertyNameToParameterizedStateChangeCallbackMapping.containsKey(propertyName);
  }

  /**
   * Processes the change in state to the particular property of this Bean.  A PropertyChangeEvent is created
   * to notify all listeners of the state change.  First, VetoableChangeListeners are notified of the pending change
   * in order to validate the change and veto any undesired change to the specified property.  Next, the state change
   * is effected followed by notification of all PropertyChangeListeners.  Finally, a change event is sent to all
   * ChangeListeners notifying them this Bean has been changed.
   * 
   * This particular implementation uses property-to-field mapping and reflection to change the state of this Bean.
   * Subclasses are allowed to create a mapping of property names to actual fields of this particular Bean by calling
   * the mapPropertyNameToFieldName method.  However, if no such mapping exists, then the field name is derived from
   * the name of the specified property.  Essentially, the field name is expected to be the same as the property name.
   * 
   * @param propertyName a String value specifying the name of the property on this Bean that is being changed.
   * @param oldValue an Object containing the old value of the specified property.
   * @param newValue an Object containing the new value for the specified property.
   * @throws IllegalPropertyValueException if the property change violates a constraint imposed by one of the
   * VetoableChangeListeners listening to property change events on this Bean.
   * @see #processChange(String, Object, Object, org.cp.elements.beans.AbstractBean.StateChangeCallback)
   */
  protected void processChange(String propertyName, Object oldValue, Object newValue) {
    processChange(propertyName, oldValue, newValue, null);
  }

  /**
   * Processes the change in state to the particular property of this Bean.  A PropertyChangeEvent is created
   * to notify all listeners of the state change.  First, VetoableChangeListeners are notified of the pending change
   * in order to validate the change and veto any undesired change to the specified property.  Next, the state change
   * is effected followed by notification of all PropertyChangeListeners.  Finally, a change event is sent to all
   * ChangeListeners notifying them this Bean has been changed.
   * 
   * This particular implementation employs a callback style approach to effectively changing the state of this Bean.
   * As it is not always possible to access the particular fields of an object's class, the callback allows the subclass
   * to effect the change.
   * 
   * @param propertyName a String value specifying the name of the property on this Bean that is being changed.
   * @param oldValue an Object containing the old value of the specified property.
   * @param newValue an Object containing the new value for the specified property.
   * @param callback the StateChangeCallback implementation used to actually effect the state transition on the
   * specified property of this Bean.
   * @throws IllegalPropertyValueException if the property change violates a constraint imposed by one of the
   * VetoableChangeListeners listening to property change events on this Bean.
   * @see #processChange(String, Object, Object)
   */
  @SuppressWarnings("unchecked")
  protected void processChange(String propertyName, Object oldValue, Object newValue, StateChangeCallback callback) {
    try {
      PropertyChangeEvent event = newPropertyChangeEvent(propertyName, oldValue, newValue);

      fireVetoableChange(event);

      if (callback != null) {
        callback.changeState();
      }
      else if (propertyNameToParameterizedStateChangeCallbackMapping.containsKey(propertyName)) {
        propertyNameToParameterizedStateChangeCallbackMapping.get(propertyName).changeState(newValue);
      }
      else {
        changeState(propertyName, newValue);
      }

      firePropertyChange(event);
      fireChange();
    }
    catch (PropertyVetoException e) {
      throw new IllegalPropertyValueException(String.format(
        "The new value (%1$s) for property (%2$s) on Bean (%3$s) is not valid!",
          newValue, propertyName, getClass().getName()), e);
    }
  }

  /**
   * Removes the specified ChangeListener from the list of listeners being notified of change events to this Bean.
   * 
   * @param listener the ChangeListener to to remove from the list of listeners being notified of change events on
   * this Bean.
   * @see org.cp.elements.beans.event.ChangeListener
   * @see org.cp.elements.beans.event.ChangeSupport
   */
  protected void removeChangeListener(ChangeListener listener) {
    changeSupport.remove(listener);
  }

  /**
   * Removes the specified PropertyChangeListener from listening and being notified of property change events on
   * this Bean.
   * 
   * @param listener the PropertyChangeListener to remove and stop notifying of property change events on this Bean.
   * @see java.beans.PropertyChangeListener
   * @see java.beans.PropertyChangeSupport
   */
  protected void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  /**
   * Removes the specified PropertyChangeListener from listening and being notified of property change events
   * for the specified property on this Bean.
   * 
   * @param propertyName a String value specifying the name of the property on this Bean.
   * @param listener the PropertyChangeListener to remove and stop notifying of property change events for
   * the specified property on this Bean.
   * @see java.beans.PropertyChangeListener
   * @see java.beans.PropertyChangeSupport
   */
  protected void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
  }

  /**
   * Removes the specified VetoableChangeListener from listening and being notified of property change events
   * to this Bean.  In addition, the listeners vote in vetoing potentially undesirable property changes is now
   * void.
   * 
   * @param listener the VetoableChangeListener to remove and stop notifying of property change events on this Bean.
   * @see java.beans.VetoableChangeListener
   * @see java.beans.VetoableChangeSupport
   */
  protected void removeVetoableChangeListener(VetoableChangeListener listener) {
    vetoableChangeSupport.removeVetoableChangeListener(listener);
  }

  /**
   * Removes the specified VetoableChangeListener from listening and being notified of property change events
   * from the specified property on this Bean.  In addition, the listeners vote in vetoing potentially undesirable
   * property changes to the specified property is now void.
   * 
   * @param propertyName a String value specifying the name of the property on this Bean.
   * @param listener the VetoableChangeListener to remove and stop notifying of property change events on this Bean.
   * @see java.beans.VetoableChangeListener
   * @see java.beans.VetoableChangeSupport
   */
  protected void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
    vetoableChangeSupport.removeVetoableChangeListener(propertyName, listener);
  }

  /**
   * Removes the mapping between the specified property and the corresponding field of this Bean.
   * 
   * @param propertyName a String value indicating the name of the property on this Bean to remove the mapping for.
   * @return a String value indicating the name of the Bean object's field mapped to the specified property.
   */
  protected String unmapPropertyNameToFieldName(String propertyName) {
    return propertyNameToFieldNameMapping.remove(propertyName);
  }

  /**
   * Removes the mapping from the specified property on this Bean to the registered ParameterizedStateChangeCallback
   * for affecting state transitions and changes.
   * 
   * @param propertyName a String value specifying the name of the property on this Bean class.
   * @return a boolean indicating that the mapping was removed.
   */
  protected boolean unmapPropertyNameToParameterizedStateChangeCallback(String propertyName) {
    return (propertyNameToParameterizedStateChangeCallbackMapping.remove(propertyName) != null);
  }

  /**
   * The StateChangeCallback interface is a callback interface allowing the invocation of a method to be called
   * by the invoked method.
   */
  protected interface StateChangeCallback {
    void changeState();
  }

  /**
   * The ParameterizedStateChangeCallback interface is a callback interface affecting the state of this Bean.
   * @param <T> the Class type of the value.
   */
  protected interface ParameterizedStateChangeCallback<T> {
    void changeState(T newValue);
  }
}
