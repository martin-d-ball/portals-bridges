/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.portals.applications.springmvc;

import java.io.Serializable;
import java.lang.Comparable;

public class DOMTree implements Comparable, Serializable
{
    private static final long serialVersionUID = 1L;

    private String name;
    private String path;
    private org.w3c.dom.Document doc;
    private String message;
    
    private int hashCode = Integer.MIN_VALUE;
    
    
	public DOMTree()
	{
		super();
	}

	public DOMTree( String name, String path )
	{
		super();
		setName( name );
		setPath( path );
	}
	
	public String getName()
	{
        return name;
    }

    public void setName( String name )
    {
        if (name == null) name = "";
        this.name = name;
        this.hashCode = Integer.MIN_VALUE;
    }
    
	public String getPath()
	{
        return path;
    }

    public void setPath( String path )
    {
        if (path == null) path = "";
        this.path = path;
        this.hashCode = Integer.MIN_VALUE;
    }
    
    public void setParsedDocument( org.w3c.dom.Document doc )
    {
    	this.doc = doc;
    }
    public org.w3c.dom.Document getParsedDocument()
    {
    	return doc;
    }
    
    public String getMessage()
	{
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }
    
    public int compareTo(Object obj)
    {
        if (obj == null) throw new NullPointerException( "Cannot compare to null object" );
        if (!(obj instanceof DOMTree)) throw new ClassCastException( "Can only compare to class" + this.getClass().getName() );
        if (this.name == null || this.path == null) throw new NullPointerException( "This object is not initialized yet" );
        if (this.equals(obj)) return 0;
        DOMTree dt = (DOMTree)obj;
        int res = getName().compareTo(dt.getName());
        if (res != 0) return res;
        return getPath().compareTo(dt.getPath());
    }

    public boolean equals(Object obj)
    {
        if ( obj == null ) return false;
        if ( !(obj instanceof DOMTree) ) return false;
        if ( this.name == null || this.path == null ) return false;
        DOMTree dt = (DOMTree)obj;
        return (this.name.equals(dt.getName()) &&
                 this.path.equals(dt.getPath()));
    }
    
    public int hashCode()
    {
		if (Integer.MIN_VALUE == this.hashCode)
		{
			String hashStr = this.getClass().getName() + ":" + this.toString();
			this.hashCode = hashStr.hashCode();
		}
		return this.hashCode;
    }

    public String toString()
    {
        return this.name + ":" + this.path;
    }
    
}
