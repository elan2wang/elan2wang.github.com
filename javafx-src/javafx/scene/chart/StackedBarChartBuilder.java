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

package javafx.scene.chart;

/**
Builder class for javafx.scene.chart.StackedBarChart
@see javafx.scene.chart.StackedBarChart
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.1
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class StackedBarChartBuilder<X, Y, B extends javafx.scene.chart.StackedBarChartBuilder<X, Y, B>> extends javafx.scene.chart.XYChartBuilder<X, Y, B> {
    protected StackedBarChartBuilder() {
    }
    
    /** Creates a new instance of StackedBarChartBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static <X, Y> javafx.scene.chart.StackedBarChartBuilder<X, Y, ?> create() {
        return new javafx.scene.chart.StackedBarChartBuilder();
    }
    
    private boolean __set;
    public void applyTo(javafx.scene.chart.StackedBarChart<X, Y> x) {
        super.applyTo(x);
        if (__set) x.setCategoryGap(this.categoryGap);
    }
    
    private double categoryGap;
    /**
    Set the value of the {@link javafx.scene.chart.StackedBarChart#getCategoryGap() categoryGap} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B categoryGap(double x) {
        this.categoryGap = x;
        __set = true;
        return (B) this;
    }
    
    private javafx.scene.chart.Axis<X> XAxis;
    /**
    Set the value of the {@link javafx.scene.chart.StackedBarChart#getXAxis() XAxis} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B XAxis(javafx.scene.chart.Axis<X> x) {
        this.XAxis = x;
        return (B) this;
    }
    
    private javafx.scene.chart.Axis<Y> YAxis;
    /**
    Set the value of the {@link javafx.scene.chart.StackedBarChart#getYAxis() YAxis} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B YAxis(javafx.scene.chart.Axis<Y> x) {
        this.YAxis = x;
        return (B) this;
    }
    
    /**
    Make an instance of {@link javafx.scene.chart.StackedBarChart} based on the properties set on this builder.
    */
    public javafx.scene.chart.StackedBarChart<X, Y> build() {
        javafx.scene.chart.StackedBarChart<X, Y> x = new javafx.scene.chart.StackedBarChart<X, Y>(this.XAxis, this.YAxis);
        applyTo(x);
        return x;
    }
}
