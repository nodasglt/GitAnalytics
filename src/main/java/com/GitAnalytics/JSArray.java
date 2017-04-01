/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GitAnalytics;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author nodas
 */
public class JSArray
{
    private final List<Object> mItems;
    
    public JSArray()
    {
        mItems = new LinkedList<>();
    }
    
    public JSArray put(Object value)
    {
        mItems.add(value);
        
        return this;
    }
    
    @Override
    public String toString()
    {
        String s = "[";
        
        s = mItems.stream().map((e) -> e.toString() + ",").reduce(s, String::concat);
        
        return s + "]";
    }
            
}
