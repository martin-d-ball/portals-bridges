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

import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletContext;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ApplicationObjectSupport;

import org.w3c.dom.Node ;
import org.w3c.dom.NodeList ;

public class DOMTreeService extends ApplicationObjectSupport
{
	private static final String     DOM_TREE_NO_PATH = "domtree_no_path";
	private static final String     DOM_TREE_NO_PARSE = "domtree_no_parse";
	private static final Log        log = LogFactory.getLog( DOMTreeService.class);
	
    protected DocumentBuilderFactory domFactory = null;
    
	public void initApplicationContext() throws BeansException
	{
		domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setValidating(false);
	}

	public DOMTree getDOMTree( String name, PortletRequest request )
    {
		if ( name == null ) name = "";
		PortletPreferences prefs = request.getPreferences();
		String path = prefs.getValue( name, "" );
        return new DOMTree( name, path );
    }
	
	public void saveDOMTree( String name, String path, PortletRequest request )
	{
		DOMTree dt = new DOMTree( name, path );
		saveDOMTree( dt, request );
    }
	
	public void saveDOMTree( DOMTree dt, PortletRequest request )
	{
		if ( dt == null ) return ;
		PortletPreferences prefs = request.getPreferences();
		try
		{
			prefs.setValue( dt.getName(), dt.getPath() );
			prefs.store();
		}
		catch ( ReadOnlyException e ) { }
		catch ( IOException e ) { }
		catch ( ValidatorException e ) { }
    }

    public void deleteDOMTree( String name, PortletRequest request )
    {
    	if ( name == null ) name = "";
    	PortletPreferences prefs = request.getPreferences();
    	try
		{
    		prefs.reset( name );
    		prefs.store();
		}
		catch ( ReadOnlyException e ) { }
		catch ( IOException e ) { }
		catch ( ValidatorException e ) { }
    }
	
    public SortedSet getAllDOMTrees( PortletRequest request )
    {
    	return getAllDOMTrees( request, null );
    }
	public SortedSet getAllDOMTrees( PortletRequest request, List addTo )
	{
		if ( addTo == null )
		{
			addTo = new ArrayList();
		}
		PortletPreferences prefs = request.getPreferences();
        Enumeration e = prefs.getNames();
        while ( e.hasMoreElements() )
        {
        	String name = (String)e.nextElement();
        	String path = prefs.getValue( name, "" );
        	addTo.add( new DOMTree( name, path ) );
        }
    	return (SortedSet) new TreeSet( addTo );
    }
	
	public SortedSet parseAllDOMTrees( PortletRequest request, PortletContext context, List addTo )
	{
		SortedSet domTreeSet = getAllDOMTrees( request, addTo );
		Iterator domTreeSetIter = domTreeSet.iterator();
		while ( domTreeSetIter.hasNext() )
		{
			DOMTree dt = (DOMTree)domTreeSetIter.next();
			if ( dt.getPath() == null || dt.getPath().length() == 0 )
			{
				dt.setMessage( DOM_TREE_NO_PATH );
			}
			else
			{
				InputStream is = context.getResourceAsStream( dt.getPath() );
				org.w3c.dom.Document doc = parseXml( is );
				dt.setParsedDocument( doc );
				if ( doc == null )
				{
					dt.setMessage( DOM_TREE_NO_PARSE );
				}
			}
		}
		return domTreeSet;
	}
	
	
	// general xml parsing utilities
	
	protected org.w3c.dom.Document parseXml( InputStream is )
	{		
		DocumentBuilder docBuilder = null;
		org.w3c.dom.Document doc = null;
	    try
	    {
	    	docBuilder = domFactory.newDocumentBuilder();
	    }
	    catch (ParserConfigurationException e)
	    {
	        log.error( "Cannot create DocumentBuilder due to " + e.getClass().getName() + " " + e.getMessage() );
	    }
	    if ( docBuilder != null )
	    {
	    	try
	    	{
	    		doc = docBuilder.parse(is);
	    	}
	    	catch (Exception e)
	    	{
	    		log.error( "Cannot parse due to " + e.getClass().getName() + " " + e.getMessage() );
	    	}
	    }
		return doc;
	}
	public static class DOMNodeHelper
	{
		public DOMNodeHelper()
		{
		}
		public List createNodeList( NodeList nl )
		{
			List domNodeList = new ArrayList();
			if ( nl != null )
			{
				for ( int i = 0 ; i < nl.getLength() ; i++ )
				{
					domNodeList.add( nl.item( i ) );
				}
			}
			return domNodeList;
		}
		public boolean isElementNode( Node n )
		{
			return n != null && n.getNodeType() == Node.ELEMENT_NODE;
		}
		public boolean isTextNode( Node n )
		{
			return n != null && n.getNodeType() == Node.TEXT_NODE;
		}
		public boolean isNonEmptyTextNode( Node n )
		{
			if ( n != null && n.getNodeType() == Node.TEXT_NODE )
			{
				String nodeVal = n.getNodeValue();
				if ( nodeVal != null && nodeVal.trim().length() > 0 )
				{
					return true ;
				}
			}
			return false;
		}
		public boolean isAttributeNode( Node n )
		{
			return n != null && n.getNodeType() == Node.ATTRIBUTE_NODE;
		}
		public boolean isDocumentNode( Node n )
		{
			return n != null && n.getNodeType() == Node.DOCUMENT_NODE;
		}
		public String replaceLineBreaks( String s )
		{
			Pattern p = Pattern.compile( "\\s*((\\r\\n)|\\n)\\s*" );
			Matcher m = p.matcher( s );
			return m.replaceAll( " " );
		}
	}
}
