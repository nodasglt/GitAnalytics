/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GitAnalytics;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nodas
 */
public class JSArray
{
    private List<Object> mItems;
    
    public JSArray()
    {
        mItems = new LinkedList<>();
    }
    
    public JSArray put(Object value)
    {
        mItems.add(value);
        
        return this;
    }
    
    public String toString()
    {
        String s = "[";
        
        for (Object e : mItems)
        {
            s += e.toString() + ",";
        }
        
        return s + "]";
    }
            
}
