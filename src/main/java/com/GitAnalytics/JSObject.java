/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GitAnalytics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author nodas
 */
public class JSObject
{
    private Map<String, Object> mItems;
    
    public JSObject()
    {
        mItems = new HashMap<>();
    }
    
    public JSObject put(String name, Object value)
    {
        mItems.put(name, value);
        
        return this;
    }
    
    public String toString()
    {
        String s = "{";
        
        for (Entry<String, Object> e : mItems.entrySet())
        {
            s += e.getKey() + ":" + e.getValue().toString() + ",";
        }
        
        return s + "}";
    }
            
}
