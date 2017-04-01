/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GitAnalytics;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nodas
 */
public class BarDiagram
{
    private final JSObject mPie;
    private final JSArray mCategories;
    private final Map<String, JSArray> mBarsPerCategory;
    private final JSArray mSeries;
    
    public BarDiagram(String title, boolean enableLegend)
    {
        mPie = new JSObject();
        mBarsPerCategory = new HashMap<>();
        mCategories = new JSArray();
        mSeries = new JSArray();
        
        mPie.put("chart", new JSObject()
            .put("plotBackgroundColor", "null")
            .put("plotBorderWidth", "null")
            .put("plotShadow", "false")
            .put("type", "'bar'"));   
        
        mPie.put("title", new JSObject()
                .put("text", "'" + title + "'"));
 /*       
        mPie.put("tooltip", new JSObject()
                .put("pointFormat", "'" + tagFormat + "'"));
*/
 
        mPie.put("xAxis", new JSObject()
                .put("categories", mCategories)
                .put("title", new JSObject()
                    .put("text", "null")));
 
        mPie.put("yAxis", new JSObject()
                .put("min", 0)
                .put("title", new JSObject()
                    .put("text", "'Commits'")
                    .put("align", "'high'"))
                .put("labels", new JSObject()
                    .put("overflow", "'justify'")));
        
        mPie.put("plotOptions", new JSObject()
                .put("bar", new JSObject()
                    .put("dataLabels", new JSObject()
                        .put("enabled", "true"))));
        
        if (enableLegend)
        {
            mPie.put("legend", new JSObject()
                    .put("layout", "'vertical'")
                    .put("align", "'right'")
                    .put("verticalAlign", "'top'")
                    .put("x", -40)
                    .put("y", 80)
                    .put("floating", "true")
                    .put("borderWidth", 1)
                    .put("backgroundColor", "((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF')")
                    .put("shadow", "true"));
        }
        else
        {
            mPie.put("legend", new JSObject()
                    .put("enabled", "false"));
        }
        
        mPie.put("credits", new JSObject()
                .put("enabled", "false"));
        
        mPie.put("series", mSeries);       
    }
    
    public BarDiagram addBarSet(String name)
    {
        JSArray barValues = new JSArray();
        mBarsPerCategory.put(name, barValues);
        mSeries.put(new JSObject()
                        .put("name", "'" + name + "'")
                        .put("data", barValues));
        
        return this;
    }
    
    public BarDiagram addCategory(String name)
    {
        mCategories.put("'" + name + "'");
         
        return this;
    }
    
    public BarDiagram add(String name, int value)
    {       
        mBarsPerCategory.get(name).put(value);
        
        return this;
    }
    
    @Override
    public String toString()
    {
        return mPie.toString();
    }
}
