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

package javafx.scene.paint;

/**
Builder class for javafx.scene.paint.RadialGradient
@see javafx.scene.paint.RadialGradient
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public final class RadialGradientBuilder implements javafx.util.Builder<javafx.scene.paint.RadialGradient> {
    protected RadialGradientBuilder() {
    }
    
    /** Creates a new instance of RadialGradientBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.paint.RadialGradientBuilder create() {
        return new javafx.scene.paint.RadialGradientBuilder();
    }
    
    private double centerX;
    /**
    Set the value of the {@link javafx.scene.paint.RadialGradient#getCenterX() centerX} property for the instance constructed by this builder.
    */
    public javafx.scene.paint.RadialGradientBuilder centerX(double x) {
        this.centerX = x;
        return this;
    }
    
    private double centerY;
    /**
    Set the value of the {@link javafx.scene.paint.RadialGradient#getCenterY() centerY} property for the instance constructed by this builder.
    */
    public javafx.scene.paint.RadialGradientBuilder centerY(double x) {
        this.centerY = x;
        return this;
    }
    
    private javafx.scene.paint.CycleMethod cycleMethod;
    /**
    Set the value of the {@link javafx.scene.paint.RadialGradient#getCycleMethod() cycleMethod} property for the instance constructed by this builder.
    */
    public javafx.scene.paint.RadialGradientBuilder cycleMethod(javafx.scene.paint.CycleMethod x) {
        this.cycleMethod = x;
        return this;
    }
    
    private double focusAngle;
    /**
    Set the value of the {@link javafx.scene.paint.RadialGradient#getFocusAngle() focusAngle} property for the instance constructed by this builder.
    */
    public javafx.scene.paint.RadialGradientBuilder focusAngle(double x) {
        this.focusAngle = x;
        return this;
    }
    
    private double focusDistance;
    /**
    Set the value of the {@link javafx.scene.paint.RadialGradient#getFocusDistance() focusDistance} property for the instance constructed by this builder.
    */
    public javafx.scene.paint.RadialGradientBuilder focusDistance(double x) {
        this.focusDistance = x;
        return this;
    }
    
    private boolean proportional = true;
    /**
    Set the value of the {@link javafx.scene.paint.RadialGradient#isProportional() proportional} property for the instance constructed by this builder.
    */
    public javafx.scene.paint.RadialGradientBuilder proportional(boolean x) {
        this.proportional = x;
        return this;
    }
    
    private double radius = 1;
    /**
    Set the value of the {@link javafx.scene.paint.RadialGradient#getRadius() radius} property for the instance constructed by this builder.
    */
    public javafx.scene.paint.RadialGradientBuilder radius(double x) {
        this.radius = x;
        return this;
    }
    
    private java.util.List<javafx.scene.paint.Stop> stops;
    /**
    Add the given items to the List of items in the {@link javafx.scene.paint.RadialGradient#getStops() stops} property for the instance constructed by this builder.
    */
    public javafx.scene.paint.RadialGradientBuilder stops(java.util.List<javafx.scene.paint.Stop> x) {
        this.stops = x;
        return this;
    }
    
    /**
    Add the given items to the List of items in the {@link javafx.scene.paint.RadialGradient#getStops() stops} property for the instance constructed by this builder.
    */
    public javafx.scene.paint.RadialGradientBuilder stops(javafx.scene.paint.Stop... x) {
        return stops(java.util.Arrays.asList(x));
    }
    
    /**
    Make an instance of {@link javafx.scene.paint.RadialGradient} based on the properties set on this builder.
    */
    public javafx.scene.paint.RadialGradient build() {
        javafx.scene.paint.RadialGradient x = new javafx.scene.paint.RadialGradient(this.focusAngle, this.focusDistance, this.centerX, this.centerY, this.radius, this.proportional, this.cycleMethod, this.stops);
        return x;
    }
}
