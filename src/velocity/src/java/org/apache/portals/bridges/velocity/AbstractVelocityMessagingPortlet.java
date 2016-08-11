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
package org.apache.portals.bridges.velocity;

import javax.portlet.PortletRequest;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.portals.messaging.PortletMessaging;

/**
 * velocity abstract messaging portlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: AbstractVelocityMessagingPortlet.java,v 1.3 2005/03/23 06:24:38 david Exp $
 */
public abstract class AbstractVelocityMessagingPortlet extends GenericVelocityPortlet
{
    private String topic = null;
    public static final String STATUS_MESSAGE = "statusMsg";
    
    protected boolean isEmpty(String s)
    {
        if (s == null)
            return true;
        
        if (s.trim().length() == 0)
            return true;
        
        return false;
    }
    
    protected String getTopic()
    {
        return topic;
    }
    protected void setTopic(String topic)
    {
        this.topic = topic;
    }
    
    protected void cancelRenderMessage(PortletRequest request, String message)
    {
        try
        {
            if (topic == null)
                PortletMessaging.cancel(request, message);
            else
                PortletMessaging.cancel(request, topic, message);
        }
        catch (Exception e)
        {}
    }
    
    protected Object receiveRenderMessage(PortletRequest request, String message)
    {
        try
        {
            if (topic == null)
                return PortletMessaging.receive(request, message);
            else
                return PortletMessaging.receive(request, topic, message);
        }
        catch (Exception e)
        {}
        return null;
    }
    
    protected Object consumeRenderMessage(PortletRequest request, String message)
    {
        try
        {
            if (topic == null)
                return PortletMessaging.consume(request, message);            
            else
                return PortletMessaging.consume(request, topic, message);            
        }
        catch (Exception e)
        {}        
        return null;
    }
    
    protected void publishRenderMessage(PortletRequest request, String message, Object value)
    {
        try
        {
            if (topic == null)
                PortletMessaging.publish(request, message, value);
            else
                PortletMessaging.publish(request, topic, message, value);
        }
        catch (Exception e)
        {}
    }

}
