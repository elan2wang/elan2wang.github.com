/* 
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
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

package javafx.scene.input;

/**
Builder class for javafx.scene.input.ClipboardContent
@see javafx.scene.input.ClipboardContent
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class ClipboardContentBuilder<B extends javafx.scene.input.ClipboardContentBuilder<B>> implements javafx.util.Builder<javafx.scene.input.ClipboardContent> {
    protected ClipboardContentBuilder() {
    }
    
    /** Creates a new instance of ClipboardContentBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.input.ClipboardContentBuilder<?> create() {
        return new javafx.scene.input.ClipboardContentBuilder();
    }
    
    private boolean __set;
    public void applyTo(javafx.scene.input.ClipboardContent x) {
        if (__set) { x.getFiles().clear(); x.getFiles().addAll(this.files); }
    }
    
    private java.util.Collection<? extends java.io.File> files;
    /**
    Add the given items to the List of items in the {@link javafx.scene.input.ClipboardContent#getFiles() files} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B files(java.util.Collection<? extends java.io.File> x) {
        this.files = x;
        __set = true;
        return (B) this;
    }
    
    /**
    Add the given items to the List of items in the {@link javafx.scene.input.ClipboardContent#getFiles() files} property for the instance constructed by this builder.
    */
    public B files(java.io.File... x) {
        return files(java.util.Arrays.asList(x));
    }
    
    /**
    Make an instance of {@link javafx.scene.input.ClipboardContent} based on the properties set on this builder.
    */
    public javafx.scene.input.ClipboardContent build() {
        javafx.scene.input.ClipboardContent x = new javafx.scene.input.ClipboardContent();
        applyTo(x);
        return x;
    }
}
