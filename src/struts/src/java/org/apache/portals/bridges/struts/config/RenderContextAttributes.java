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
package org.apache.portals.bridges.struts.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.digester.Digester;
import org.apache.struts.taglib.tiles.ComponentConstants;

public class RenderContextAttributes extends AbstractConfigComponent
{
    private static class AttributeValue implements Serializable
    {
        private String  name;
        private Object  value;
        
        public AttributeValue(String name, Object value)
        {
            super();
            this.name = name;
            this.value = value;
        }
        
        public String getName()
        {
            return name;
        }
        
        public Object getValue()
        {
            return value;
        }
    }
    
    public static class Attribute
    {
        private String value;
        private boolean prefix;
        private boolean keep;

        public Attribute()
        {        
        }
        
        public boolean isKeep()
        {
            return keep;
        }
        
        public void setKeep(boolean keep)
        {
            this.keep = keep;
        }
        
        public boolean isPrefixAttr()
        {
            return prefix;
        }

        public String getValue()
        {
            return value;
        }
        
        public void setName(String value)
        {
            this.value = value;
            this.prefix = false;
        }
        
        public void setPrefix(String value)
        {
            this.value = value;
            this.prefix = true;
        }
    }
    
    private String name = this.getClass().getName();
    private Attribute[] namedAttributes;
    private Attribute[] prefixAttributes;
    private ArrayList namedAttributesList;
    private ArrayList prefixAttributesList;
    
    public RenderContextAttributes()
    {
        namedAttributesList = new ArrayList();
        prefixAttributesList = new ArrayList();
    }
    
    private static boolean isNotEmpty(String str)
    {
        return str != null && str.length() > 0;
    }
    
    private Attribute[] createArray(List attributes)
    {
        Attribute[] array = null;
        if ( attributes != null && attributes.size() > 0 )
        {
            array = new Attribute[attributes.size()];
            for ( int i = 0; i < array.length; i++ )
            {
                array[i] = (Attribute)attributes.get(i);
            }
        }
        return array;
    }
    
    public void addAttribute(Attribute attribute)
    {
        checkLoaded();
        
        if (attribute.isPrefixAttr())
        {
            prefixAttributesList.add(attribute);
        }
        else
        {
            namedAttributesList.add(attribute);            
        }
    }
    
    public void setName(String name)
    {
        checkLoaded();
        this.name = name;
    }
    
    public void configure(Digester digester)
    {
        digester.addRule("config/render-context", new SetParentRule(this));
        digester.addSetProperties("config/render-context");
        digester.addObjectCreate("config/render-context/attribute", Attribute.class);
        digester.addSetProperties("config/render-context/attribute");
        digester.addSetNext("config/render-context/attribute", "addAttribute");
        digester.addCallMethod("config/render-context", "afterLoad");
        
    }
    
    public void afterLoad()
    {
        super.afterLoad();

        // special handling for Tiles support, see PB-41
        boolean found = false;
        for ( int i = 0, size = namedAttributesList.size(); i < size; i++ )
        {
            Attribute attr = (Attribute)namedAttributesList.get(i);
            if ( ComponentConstants.COMPONENT_CONTEXT.equals(attr.getValue()) )
            {
                found = true;
                break;
            }
        }
        if ( !found )
        {
            // Add Tiles COMPONENT_CONTEXT to named Attributes list
            Attribute tilesContextAttribute = new Attribute();
            tilesContextAttribute.setName(ComponentConstants.COMPONENT_CONTEXT);
            namedAttributesList.add(tilesContextAttribute);
        }
        namedAttributes = createArray(namedAttributesList);
        prefixAttributes = createArray(prefixAttributesList);
        
        namedAttributesList = null;
        prefixAttributesList = null;
    }
    
    /**
     * Save attributes in the PortletSession. This will ensure
     * that each portlet instance will have its own render attributes.
     * @param request The PortletRequest
     */
    public void saveAttributes(PortletRequest request)
    {
        ArrayList keepAttributes = new ArrayList();
        ArrayList tempAttributes = new ArrayList();
        ArrayList savedNames = new ArrayList();
        if ( namedAttributes != null )
        {
            for ( int i = 0; i < namedAttributes.length; i++ )
            {
                Object value = request.getAttribute(namedAttributes[i].getValue());
                if ( value != null )
                {
                    AttributeValue attributeValue = new AttributeValue(namedAttributes[i].getValue(), value);
                    savedNames.add(attributeValue.getName());
                    if ( namedAttributes[i].isKeep() )
                    {
                        keepAttributes.add(attributeValue);
                    }
                    else
                    {
                        tempAttributes.add(attributeValue);
                    }                    
                }
            }
        }
        if ( prefixAttributes != null )
        {
            Enumeration names = request.getAttributeNames();
            while ( names.hasMoreElements() )
            {
                String name = (String)names.nextElement();
                for ( int i = 0; i < prefixAttributes.length; i++ )
                {
                    if (!savedNames.contains(name) && name.startsWith(prefixAttributes[i].getValue()))
                    {
                        AttributeValue attributeValue = new AttributeValue(name, request.getAttribute(name));
                        savedNames.add(name);
                        if (prefixAttributes[i].isKeep())
                        {
                            keepAttributes.add(attributeValue);
                        }
                        else
                        {
                            tempAttributes.add(attributeValue);
                        }                    
                    }
                }
            }
        }
        if (keepAttributes.size() > 0)
        {
            if (tempAttributes.size() > 0)
            {
                keepAttributes.add(null); // indicating subsequent attributeValues are temporarily
                keepAttributes.addAll(tempAttributes);
            }
            request.getPortletSession().setAttribute(name,keepAttributes);
        }
        else if (tempAttributes.size() > 0)
        {
            tempAttributes.add(0,null); // indicating subsequent attributeValues are temporarily
            request.getPortletSession().setAttribute(name,tempAttributes);
        }
    }
    
    /**
     * Remove attributes from the PortletSession
     * @param session The PortletSession
     */
    public void clearAttributes(PortletSession session)
    {
        session.removeAttribute(name);
    }
    
    /**
     * Restore attributes from the PortletSession.
     * @param request The portletRequest
     */
    public void restoreAttributes(PortletRequest request)
    {
        PortletSession portletSession = request.getPortletSession();
        ArrayList attributes = (ArrayList)portletSession.getAttribute(name);
        if ( attributes != null )
        {
            for ( int size = attributes.size(), i = size - 1 ; i > -1; i-- )
            {
                AttributeValue attributeValue = (AttributeValue)attributes.get(i);
                if ( attributeValue == null )
                {
                    if ( i == 0 )
                    {
                        portletSession.removeAttribute(name);
                    }
                    else
                    {
                        // remove this and previously retrieved attributeValues as being temporarily
                        while (size > i )
                        {
                            attributes.remove(--size);
                        }
                    }
                }
                else
                {
                    request.setAttribute(attributeValue.getName(), attributeValue.getValue());
                }
            }
        }
    }
}
