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
Builder class for javafx.scene.chart.PieChart
@see javafx.scene.chart.PieChart
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class PieChartBuilder<B extends javafx.scene.chart.PieChartBuilder<B>> extends javafx.scene.chart.ChartBuilder<B> {
    protected PieChartBuilder() {
    }
    
    /** Creates a new instance of PieChartBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.chart.PieChartBuilder<?> create() {
        return new javafx.scene.chart.PieChartBuilder();
    }
    
    private int __set;
    public void applyTo(javafx.scene.chart.PieChart x) {
        super.applyTo(x);
        int set = __set;
        if ((set & (1 << 0)) != 0) x.setClockwise(this.clockwise);
        if ((set & (1 << 1)) != 0) x.setData(this.data);
        if ((set & (1 << 2)) != 0) x.setLabelLineLength(this.labelLineLength);
        if ((set & (1 << 3)) != 0) x.setLabelsVisible(this.labelsVisible);
        if ((set & (1 << 4)) != 0) x.setStartAngle(this.startAngle);
    }
    
    private boolean clockwise;
    /**
    Set the value of the {@link javafx.scene.chart.PieChart#isClockwise() clockwise} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B clockwise(boolean x) {
        this.clockwise = x;
        __set |= 1 << 0;
        return (B) this;
    }
    
    private javafx.collections.ObservableList<javafx.scene.chart.PieChart.Data> data;
    /**
    Set the value of the {@link javafx.scene.chart.PieChart#getData() data} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B data(javafx.collections.ObservableList<javafx.scene.chart.PieChart.Data> x) {
        this.data = x;
        __set |= 1 << 1;
        return (B) this;
    }
    
    private double labelLineLength;
    /**
    Set the value of the {@link javafx.scene.chart.PieChart#getLabelLineLength() labelLineLength} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B labelLineLength(double x) {
        this.labelLineLength = x;
        __set |= 1 << 2;
        return (B) this;
    }
    
    private boolean labelsVisible;
    /**
    Set the value of the {@link javafx.scene.chart.PieChart#getLabelsVisible() labelsVisible} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B labelsVisible(boolean x) {
        this.labelsVisible = x;
        __set |= 1 << 3;
        return (B) this;
    }
    
    private double startAngle;
    /**
    Set the value of the {@link javafx.scene.chart.PieChart#getStartAngle() startAngle} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B startAngle(double x) {
        this.startAngle = x;
        __set |= 1 << 4;
        return (B) this;
    }
    
    /**
    Make an instance of {@link javafx.scene.chart.PieChart} based on the properties set on this builder.
    */
    public javafx.scene.chart.PieChart build() {
        javafx.scene.chart.PieChart x = new javafx.scene.chart.PieChart();
        applyTo(x);
        return x;
    }
}
