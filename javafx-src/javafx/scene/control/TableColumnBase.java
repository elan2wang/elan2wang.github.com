/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package javafx.scene.control;

import java.text.Collator;
import java.util.Comparator;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;

import com.sun.javafx.scene.control.skin.Utils;
import com.sun.javafx.event.EventHandlerManager;
import java.util.HashMap;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;

/**
 * Table-like controls (such as {@link TableView} and {@link TreeTableView}) are
 * made up of zero or more instances of a concrete TableColumnBase subclass 
 * ({@link TableColumn} and {@link TreeTableColumn}, respectively). Each
 * table column in a table is responsible for displaying (and editing) the contents
 * of that column. As well as being responsible for displaying and editing data 
 * for a single column, a table column also contains the necessary properties to:
 * <ul>
 *    <li>Be resized (using {@link #minWidthProperty() minWidth}/{@link #prefWidthProperty() prefWidth}/{@link #maxWidthProperty() maxWidth}
 *      and {@link #widthProperty() width} properties)
 *    <li>Have its {@link #visibleProperty() visibility} toggled
 *    <li>Display {@link #textProperty() header text}
 *    <li>Display any {@link #getColumns() nested columns} it may contain
 *    <li>Have a {@link #contextMenuProperty() context menu} when the user 
 *      right-clicks the column header area
 *    <li>Have the contents of the table be sorted (using 
 *      {@link #comparatorProperty() comparator}, {@link #sortable sortable} and
 *      sortType).
 * </ul>
 * </p>
 * 
 * When instantiating a concrete subclass of TableColumnBase, perhaps the two 
 * most important properties to set are the column {@link #textProperty() text} 
 * (what to show in the column header area), and the column 
 * {@code cell value factory} (which is used to populate individual cells in the 
 * column). Refer to the class documentation for {@link TableColumn} and 
 * {@link TreeTableColumn} for more information.
 * 
 * @param <S> The type of the UI control (e.g. the type of the 'row').
 * @param <T> The type of the content in all cells in this table column.
 * @see TableColumn
 * @see TreeTableColumn
 * @see TablePositionBase
 * @since JavaFX 8.0
 */
public abstract class TableColumnBase<S,T> implements EventTarget, Styleable {
    
    /***************************************************************************
     *                                                                         *
     * Static properties and methods                                           *
     *                                                                         *
     **************************************************************************/
    
    // NOTE: If these numbers change, update the copy of this value in TableColumnHeader
    static final double DEFAULT_WIDTH = 80.0F;
    static final double DEFAULT_MIN_WIDTH = 10.0F;
    static final double DEFAULT_MAX_WIDTH = 5000.0F;
    
    /**
     * By default all columns will use this comparator to perform sorting. This
     * comparator simply performs null checks, and checks if the object is 
     * {@link Comparable}. If it is, the {@link Comparable#compareTo(java.lang.Object)}
     * method is called, otherwise this method will defer to
     * {@link Collator#compare(java.lang.String, java.lang.String)}.
     */
    public static final Comparator DEFAULT_COMPARATOR = new Comparator() {
        @Override public int compare(Object obj1, Object obj2) {
            if (obj1 == null && obj2 == null) return 0;
            if (obj1 == null) return -1;
            if (obj2 == null) return 1;
            
            if (obj1 instanceof Comparable && (obj1.getClass() == obj2.getClass() || obj1.getClass().isAssignableFrom(obj2.getClass()))) {
                return ((Comparable)obj1).compareTo(obj2);
            }

            return Collator.getInstance().compare(obj1.toString(), obj2.toString());
        }
    };
    
    
    
    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/    

    /**
     * Creates a default TableColumn with default cell factory, comparator, and
     * onEditCommit implementation.
     */
    protected TableColumnBase() {
        this("");
    }

    /**
     * Creates a TableColumn with the text set to the provided string, with
     * default cell factory, comparator, and onEditCommit implementation.
     * @param text The string to show when the TableColumn is placed within the TableView.
     */
    protected TableColumnBase(String text) {
        setText(text);
    }
    
    
    
    /***************************************************************************
     *                                                                         *
     * Listeners                                                               *
     *                                                                         *
     **************************************************************************/
    
    
    
    /***************************************************************************
     *                                                                         *
     * Instance Variables                                                      *
     *                                                                         *
     **************************************************************************/
    
    final EventHandlerManager eventHandlerManager = new EventHandlerManager(this);
    
    
    
    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/
    
    
    // --- Text
    /**
     * This is the text to show in the header for this column.
     */
    private StringProperty text = new SimpleStringProperty(this, "text", "");
    public final StringProperty textProperty() { return text; }
    public final void setText(String value) { text.set(value); }
    public final String getText() { return text.get(); }
    
    
    // --- Visible
    /**
     * Toggling this will immediately toggle the visibility of this column,
     * and all children columns.
     */
    private BooleanProperty visible = new SimpleBooleanProperty(this, "visible", true) {
        @Override protected void invalidated() {
            // set all children columns to be the same visibility. This isn't ideal,
            // for example if a child column is hidden, then the parent hidden and
            // shown, all columns will be visible again.
            //
            // TODO It may make sense for us to cache the visibility so that we may
            // return to exactly the same state.
            // set all children columns to be the same visibility. This isn't ideal,
            // for example if a child column is hidden, then the parent hidden and
            // shown, all columns will be visible again.
            //
            // TODO It may make sense for us to cache the visibility so that we may
            // return to exactly the same state.
            for (TableColumnBase<S,?> col : getColumns()) {
                col.setVisible(isVisible());
            }
        }
    };
    public final void setVisible(boolean value) { visibleProperty().set(value); }
    public final boolean isVisible() { return visible.get(); }
    public final BooleanProperty visibleProperty() { return visible; }
    
    
    // --- Parent Column
    /**
     * This read-only property will always refer to the parent of this column,
     * in the situation where nested columns are being used.
     *
     * <p>In the currently existing subclasses, to create a nested
     * column is simply a matter of placing the relevant TableColumnBase instances
     * inside the columns ObservableList (for example, see
     * {@link javafx.scene.control.TableColumn#getColumns()} and
     * {@link javafx.scene.control.TreeTableColumn#getColumns()}.
     */
    private ReadOnlyObjectWrapper<TableColumnBase<S,?>> parentColumn;
    void setParentColumn(TableColumnBase<S,?> value) { parentColumnPropertyImpl().set(value); }
    public final TableColumnBase<S,?> getParentColumn() {
        return parentColumn == null ? null : parentColumn.get();
    }

    public final ReadOnlyObjectProperty<TableColumnBase<S,?>> parentColumnProperty() {
        return parentColumnPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyObjectWrapper<TableColumnBase<S,?>> parentColumnPropertyImpl() {
        if (parentColumn == null) {
            parentColumn = new ReadOnlyObjectWrapper<TableColumnBase<S,?>>(this, "parentColumn");
        }
        return parentColumn;
    }
    
    
    // --- Menu
    /**
     * This menu will be shown whenever the user right clicks within the header
     * area of this TableColumnBase.
     */
    private ObjectProperty<ContextMenu> contextMenu;
    public final void setContextMenu(ContextMenu value) { contextMenuProperty().set(value); }
    public final ContextMenu getContextMenu() { return contextMenu == null ? null : contextMenu.get(); }
    public final ObjectProperty<ContextMenu> contextMenuProperty() {
        if (contextMenu == null) {
            contextMenu = new SimpleObjectProperty<ContextMenu>(this, "contextMenu");
        }
        return contextMenu;
    }
    
    
    // --- Id
    /**
     * The id of this TableColumnBase. This simple string identifier is useful 
     * for finding a specific TableColumnBase within a UI control that uses 
     * TableColumnBase instances. The default value is {@code null}.
     */
    private StringProperty id;
    public final void setId(String value) { idProperty().set(value); }
    @Override public final String getId() { return id == null ? null : id.get(); }
    public final StringProperty idProperty() {
        if (id == null) {
            id = new SimpleStringProperty(this, "id");
        }
        return id;
    }

    
    // --- style
    /**
     * A string representation of the CSS style associated with this
     * TableColumnBase instance. This is analogous to the "style" attribute of an
     * HTML element. Note that, like the HTML style attribute, this
     * variable contains style properties and values and not the
     * selector portion of a style rule.
     * <p>
     * Parsing this style might not be supported on some limited
     * platforms. It is recommended to use a standalone CSS file instead.
     */
    private StringProperty style;
    public final void setStyle(String value) { styleProperty().set(value); }
    @Override public final String getStyle() { return style == null ? null : style.get(); }
    public final StringProperty styleProperty() {
        if (style == null) {
            style = new SimpleStringProperty(this, "style");
        }
        return style;
    }
    
    
    // --- Style class
    private final ObservableList<String> styleClass = FXCollections.observableArrayList();
    /**
     * A list of String identifiers which can be used to logically group
     * Nodes, specifically for an external style engine. This variable is
     * analogous to the "class" attribute on an HTML element and, as such,
     * each element of the list is a style class to which this Node belongs.
     *
     * @see <a href="http://www.w3.org/TR/css3-selectors/#class-html">CSS3 class selectors</a>
     */
    @Override public ObservableList<String> getStyleClass() {
        return styleClass;
    }
    
    
    // --- Graphic
    /**
     * <p>The graphic to show in the table column to allow the user to
     * indicate graphically what is in the column. </p>
     */
    private ObjectProperty<Node> graphic;
    public final void setGraphic(Node value) {
        graphicProperty().set(value);
    }
    public final Node getGraphic() {
        return graphic == null ? null : graphic.get();
    }
    public final ObjectProperty<Node> graphicProperty() {
        if (graphic == null) {
            graphic = new SimpleObjectProperty<Node>(this, "graphic");
        }
        return graphic;
    }
    
    
    // --- Sort node
    /**
     * <p>The node to use as the "sort arrow", shown to the user in situations where
     * the table column is part of the sort order. It may be the only item in
     * the sort order, or it may be a secondary, tertiary, or latter sort item, 
     * and the node should reflect this visually. This is only used in the case of
     * the table column being in the sort order (refer to, for example, 
     * {@link TableView#getSortOrder()} and {@link TreeTableView#getSortOrder()}).
     * If not specified, the table column skin implementation is responsible for 
     * providing a default sort node.
     * 
     * <p>The sort node is commonly seen represented as a triangle that rotates
     * on screen to indicate whether the table column is part of the sort order, 
     * and if so, whether the sort is ascending or descending, and what position in 
     * the sort order it is in.
     */
    private ObjectProperty<Node> sortNode = new SimpleObjectProperty<Node>(this, "sortNode");
    public final void setSortNode(Node value) { sortNodeProperty().set(value); }
    public final Node getSortNode() { return sortNode.get(); }
    public final ObjectProperty<Node> sortNodeProperty() { return sortNode; }
    
    
    // --- Width
    /**
     * The width of this column. Modifying this will result in the column width
     * adjusting visually. It is recommended to not bind this property to an
     * external property, as that will result in the column width not being
     * adjustable by the user through dragging the left and right borders of
     * column headers.
     */
    public final ReadOnlyDoubleProperty widthProperty() { return width.getReadOnlyProperty(); }
    public final double getWidth() { return width.get(); }
    void setWidth(double value) { width.set(value); }
    private ReadOnlyDoubleWrapper width = new ReadOnlyDoubleWrapper(this, "width", DEFAULT_WIDTH);
    
    
    // --- Minimum Width
    /**
     * The minimum width the table column is permitted to be resized to.
     */
    private DoubleProperty minWidth;
    public final void setMinWidth(double value) { minWidthProperty().set(value); }
    public final double getMinWidth() { return minWidth == null ? DEFAULT_MIN_WIDTH : minWidth.get(); }
    public final DoubleProperty minWidthProperty() {
        if (minWidth == null) {
            minWidth = new SimpleDoubleProperty(this, "minWidth", DEFAULT_MIN_WIDTH) {
                @Override protected void invalidated() {
                    if (getMinWidth() < 0) {
                        setMinWidth(0.0F);
                    }

                    impl_setWidth(getWidth());
                }
            };
        }
        return minWidth;
    }
    
    
    // --- Preferred Width
    /**
     * The preferred width of the TableColumn.
     */
    public final DoubleProperty prefWidthProperty() { return prefWidth; }
    public final void setPrefWidth(double value) { prefWidthProperty().set(value); }
    public final double getPrefWidth() { return prefWidth.get(); }
    private final DoubleProperty prefWidth = new SimpleDoubleProperty(this, "prefWidth", DEFAULT_WIDTH) {
        @Override protected void invalidated() {
            impl_setWidth(getPrefWidth());
        }
    };
    
    
    // --- Maximum Width
    // The table does not resize properly if this is set to Number.MAX_VALUE,
    // so I've arbitrarily chosen a better, smaller number.
    /**
     * The maximum width the table column is permitted to be resized to.
     */
    public final DoubleProperty maxWidthProperty() { return maxWidth; }
    public final void setMaxWidth(double value) { maxWidthProperty().set(value); }
    public final double getMaxWidth() { return maxWidth.get(); }
    private DoubleProperty maxWidth = new SimpleDoubleProperty(this, "maxWidth", DEFAULT_MAX_WIDTH) {
        @Override protected void invalidated() {
            impl_setWidth(getWidth());
        }
    };
    
    
    // --- Resizable
    /**
     * Used to indicate whether the width of this column can change. It is up
     * to the resizing policy to enforce this however.
     */
    private BooleanProperty resizable;
    public final BooleanProperty resizableProperty() {
        if (resizable == null) {
            resizable = new SimpleBooleanProperty(this, "resizable", true);
        }
        return resizable;
    }
    public final void setResizable(boolean value) {
        resizableProperty().set(value);
    }
    public final boolean isResizable() {
        return resizable == null ? true : resizable.get();
    }

    
    
    // --- Sortable
    /**
     * <p>A boolean property to toggle on and off the 'sortability' of this column.
     * When this property is true, this column can be included in sort
     * operations. If this property is false, it will not be included in sort
     * operations, even if it is contained within the sort order list of the
     * underlying UI control (e.g. {@link TableView#getSortOrder()} or
     * {@link TreeTableView#getSortOrder()}).</p>
     *
     * <p>For example, iIf a TableColumn instance is contained within the TableView sortOrder
     * ObservableList, and its sortable property toggles state, it will force the
     * TableView to perform a sort, as it is likely the view will need updating.</p>
     */
    private BooleanProperty sortable;
    public final BooleanProperty sortableProperty() {
        if (sortable == null) {
            sortable = new SimpleBooleanProperty(this, "sortable", true);
        }
        return sortable;
    }
    public final void setSortable(boolean value) {
        sortableProperty().set(value);
    }
    public final boolean isSortable() {
        return sortable == null ? true : sortable.get();
    }
    
    
    
    // --- Reorderable
    private BooleanProperty reorderable;
    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public final BooleanProperty impl_reorderableProperty() {
        if (reorderable == null) {
            reorderable = new SimpleBooleanProperty(this, "reorderable", true);
        }
        return reorderable;
    }
    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public final void impl_setReorderable(boolean value) {
        impl_reorderableProperty().set(value);
    }
    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public final boolean impl_isReorderable() {
        return reorderable == null ? true : reorderable.get();
    }


    // --- fixed
    // (not used in JavaFX 8.0, but added for easier exploration of the domain
    // for releases post-8.0, as well as open source projects)
    private BooleanProperty fixed;
    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public final BooleanProperty impl_fixedProperty() {
        if (fixed == null) {
            fixed = new SimpleBooleanProperty(this, "fixed", false);
        }
        return fixed;
    }
    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public final void impl_setFixed(boolean value) {
        impl_fixedProperty().set(value);
    }
    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public final boolean impl_isFixed() {
        return fixed == null ? false : fixed.get();
    }


    // --- Comparator
    /**
     * Comparator function used when sorting this table column. The two Objects
     * given as arguments are the cell data for two individual cells in this
     * column.
     */
    private ObjectProperty<Comparator<T>> comparator;
    public final ObjectProperty<Comparator<T>> comparatorProperty() {
        if (comparator == null) {
            comparator = new SimpleObjectProperty<Comparator<T>>(this, "comparator", DEFAULT_COMPARATOR);
        }
        return comparator;
    }
    public final void setComparator(Comparator<T> value) {
        comparatorProperty().set(value);
    }
    public final Comparator<T> getComparator() {
        return comparator == null ? DEFAULT_COMPARATOR : comparator.get();
    }

    
    // --- Editable
    /**
     * Specifies whether this table column allows editing. This, unlike 
     * {@link TableView#editableProperty()} and 
     * {@link TreeTableView#editableProperty()}, is true by default.
     */
    private BooleanProperty editable;
    public final void setEditable(boolean value) {
        editableProperty().set(value);
    }
    public final boolean isEditable() {
        return editable == null ? true : editable.get();
    }
    public final BooleanProperty editableProperty() {
        if (editable == null) {
            editable = new SimpleBooleanProperty(this, "editable", true);
        }
        return editable;
    }
    
    
    // --- Properties
    private static final Object USER_DATA_KEY = new Object();
    
    // A map containing a set of properties for this TableColumn
    private ObservableMap<Object, Object> properties;

    /**
      * Returns an observable map of properties on this table column for use 
      * primarily by application developers.
      *
      * @return an observable map of properties on this table column for use 
      * primarily by application developers
     */
    public final ObservableMap<Object, Object> getProperties() {
        if (properties == null) {
            properties = FXCollections.observableMap(new HashMap<Object, Object>());
        }
        return properties;
    }
    
    /**
     * Tests if this table column has properties.
     * @return true if node has properties.
     */
    public boolean hasProperties() {
        return properties != null && ! properties.isEmpty();
    }

     
    // --- UserData
    /**
     * Convenience method for setting a single Object property that can be
     * retrieved at a later date. This is functionally equivalent to calling
     * the getProperties().put(Object key, Object value) method. This can later
     * be retrieved by calling {@link TableColumnBase#getUserData()}.
     *
     * @param value The value to be stored - this can later be retrieved by calling
     *          {@link TableColumnBase#getUserData()}.
     */
    public void setUserData(Object value) {
        getProperties().put(USER_DATA_KEY, value);
    }

    /**
     * Returns a previously set Object property, or null if no such property
     * has been set using the {@link TableColumnBase#setUserData(java.lang.Object)} method.
     *
     * @return The Object that was previously set, or null if no property
     *          has been set or if null was set.
     */
    public Object getUserData() {
        return getProperties().get(USER_DATA_KEY);
    }
    
    
    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/    
    
    /**
     * This enables support for nested columns, which can be useful to group
     * together related data. For example, we may have a 'Name' column with
     * two nested columns for 'First' and 'Last' names.
     *
     * <p>This has no impact on the table as such - all column indices point to the
     * leaf columns only, and it isn't possible to sort using the parent column,
     * just the leaf columns. In other words, this is purely a visual feature.</p>
     *
     * @return An ObservableList containing TableColumnBase instances (or subclasses)
     *      that are the children of this TableColumnBase. If these children 
     *      TableColumnBase instances are set as visible, they will appear 
     *      beneath this table column.
     */
    public abstract ObservableList<? extends TableColumnBase<S,?>> getColumns();
    
    /**
     * Returns the actual value for a cell at a given row index (and which 
     * belongs to this table column).
     * 
     * @param index The row index for which the data is required.
     * @return The data that belongs to the cell at the intersection of the given
     *      row index and the table column that this method is called on.
     */
    public final T getCellData(final int index) {
        ObservableValue<T> result = getCellObservableValue(index);
        return result == null ? null : result.getValue();
    }

    /**
     * Returns the actual value for a cell from the given item.
     * 
     * @param item The item from which a value of type T should be extracted.
     * @return The data that should be used in a specific cell in this 
     *      column, based on the item passed in as an argument.
     */
    public final T getCellData(final S item) {
        ObservableValue<T> result = getCellObservableValue(item);
        return result == null ? null : result.getValue();
    }

    /**
     * Attempts to return an ObservableValue&lt;T&gt; for the item in the given
     * index (which is of type S). In other words, this method expects to receive
     * an integer value that is greater than or equal to zero, and less than the
     * size of the underlying data model. If the index is
     * valid, this method will return an ObservableValue&lt;T&gt; for this 
     * specific column.
     * 
     * <p>This is achieved by calling the {@code cell value factory}, and 
     * returning whatever it returns when passed a {@code CellDataFeatures} (see,
     * for example, the CellDataFeatures classes belonging to 
     * {@link TableColumn.CellDataFeatures TableColumn} and 
     * {@link TreeTableColumn.CellDataFeatures TreeTableColumn} for more 
     * information).
     * 
     * @param index The index of the item (of type S) for which an 
     *      ObservableValue&lt;T&gt; is sought.
     * @return An ObservableValue&lt;T&gt; for this specific table column.
     */
    public abstract ObservableValue<T> getCellObservableValue(int index);
    
    /**
     * Attempts to return an ObservableValue&lt;T&gt; for the given item (which
     * is of type S). In other words, this method expects to receive an object from
     * the underlying data model for the entire 'row' in the table, and it must 
     * return an ObservableValue&lt;T&gt; for the value in this specific column.
     * 
     * <p>This is achieved by calling the {@code cell value factory}, and 
     * returning whatever it returns when passed a {@code CellDataFeatures} (see,
     * for example, the CellDataFeatures classes belonging to 
     * {@link TableColumn.CellDataFeatures TableColumn} and 
     * {@link TreeTableColumn.CellDataFeatures TreeTableColumn} for more 
     * information).
     * 
     * @param item The item (of type S) for which an ObservableValue&lt;T&gt; is
     *      sought.
     * @return An ObservableValue&lt;T&gt; for this specific table column.
     */
    public abstract ObservableValue<T> getCellObservableValue(S item);

    /** {@inheritDoc} */
    @Override public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return tail.prepend(eventHandlerManager);
    }

    /**
     * Registers an event handler to this table column. The TableColumnBase class allows 
     * registration of listeners which will be notified when editing occurs.
     * Note however that TableColumnBase is <b>not</b> a Node, and therefore no visual
     * events will be fired on it.
     *
     * @param eventType the type of the events to receive by the handler
     * @param eventHandler the handler to register
     * @throws NullPointerException if the event type or handler is null
     */
    public <E extends Event> void addEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
        eventHandlerManager.addEventHandler(eventType, eventHandler);
    }
    
    /**
     * Unregisters a previously registered event handler from this table column. One
     * handler might have been registered for different event types, so the
     * caller needs to specify the particular event type from which to
     * unregister the handler.
     *
     * @param eventType the event type from which to unregister
     * @param eventHandler the handler to unregister
     * @throws NullPointerException if the event type or handler is null
     */
    public <E extends Event> void removeEventHandler(EventType<E> eventType, EventHandler<E> eventHandler) {
        eventHandlerManager.removeEventHandler(eventType, eventHandler);
    }

    
    
    /***************************************************************************
     *                                                                         *
     * Private Implementation                                                  *
     *                                                                         *
     **************************************************************************/        

    /**
     * @treatAsPrivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public void impl_setWidth(double width) {
        setWidth(Utils.boundedSize(width, getMinWidth(), getMaxWidth()));
    }

    void updateColumnWidths() {
        if (! getColumns().isEmpty()) {
            // zero out the width and min width values, and iterate to 
            // ensure the new value is equal to the sum of all children
            // columns
            double _minWidth = 0.0f;
            double _prefWidth = 0.0f;
            double _maxWidth = 0.0f;

            for (TableColumnBase col : getColumns()) {
                col.setParentColumn(this);

                _minWidth += col.getMinWidth();
                _prefWidth += col.getPrefWidth();
                _maxWidth += col.getMaxWidth();
            }

            setMinWidth(_minWidth);
            setPrefWidth(_prefWidth);
            setMaxWidth(_maxWidth);
        }
    }

    
    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * {@inheritDoc}
     */
    public final ObservableSet<PseudoClass> getPseudoClassStates() {
        return FXCollections.emptyObservableSet();
    }

    
    
    /***************************************************************************
     *                                                                         *
     * Support Interfaces                                                      *
     *                                                                         *
     **************************************************************************/

}
