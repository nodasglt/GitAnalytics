/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GitAnalytics;

/**
 *
 * @author nodas
 */
public class PieDiagram
{
    private final JSObject mPie;
    private final JSArray mData;
    
    public PieDiagram(String title, String tagFormat)
    {
        mPie = new JSObject();
        mData = new JSArray();
        
        mPie.put("chart", new JSObject()
            .put("plotBackgroundColor", "null")
            .put("plotBorderWidth", "null")
            .put("plotShadow", "false")
            .put("type", "'pie'"));   
        
        mPie.put("title", new JSObject()
                .put("text", "'" + title + "'"));
        
        mPie.put("tooltip", new JSObject()
                .put("pointFormat", "'" + tagFormat + "'"));
        
        mPie.put("plotOptions", new JSObject()
                .put("pie", new JSObject()
                    .put("allowPointSelect", "true")
                    .put("cursor", "'pointer'")
                    .put("dataLabels", new JSObject()
                        .put("enabled", "false"))));
        
        mPie.put("series", new JSArray()
                .put(new JSObject()
                    .put("name", "'commits'")
                    .put("colorByPoint", "true")
                    .put("data", mData)));
        
    }
    
    public PieDiagram add(String name, int value)
    {
        mData.put(new JSObject()
                .put("name", "'" + name + "'")
                .put("y", value));
        
        return this;
    }
    
    @Override
    public String toString()
    {
        return mPie.toString();
    }
}

/*
 JSONObject json = new JSONObject();
        json.put("name", "student");

        JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("information", "test");
        item.put("id", 3);
        item.put("name", "course1");
        array.put(item);

        json.put("course", item);

        System.out.println(json);
*/