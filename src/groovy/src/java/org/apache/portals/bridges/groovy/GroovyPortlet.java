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
package org.apache.portals.bridges.groovy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.GenericPortlet;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * <p>
 * GroovyPortlet parses and invokes a groovy-scripted portlet. A groovy-scripted
 * portlet just need to be implemented like any other Java-based portlet. So, a
 * groovy-scripted portlet can support full features of JSR-168 portlet.
 * 
 * @author <a href="mailto:woon_san@yahoo.com">Woonsan Ko</a>
 * @Id@
 */
public class GroovyPortlet extends GenericPortlet
{
    public static final String SCRIPT_SOURCE_INIT_PARAM = "script-source";

    public static final String SCRIPT_SOURCE_URL_ENCODING_INIT_PARAM = "script-source-uri-encoding";

    public static final String AUTO_REFRESH_INIT_PARAM = "auto-refresh";

    protected PortletConfig portletConfig;

    protected String scriptSourceUri;

    protected String scriptSourceUriEncoding = "UTF-8";

    protected boolean autoRefresh;

    protected long parsedFileLastModified;

    protected GroovyCodeSource groovyCodeSource;

    protected Portlet scriptPortletInstance;

    protected GenericPortlet scriptGenericPortletInstance;

    protected Method portletDoEditMethod;

    protected GroovyClassLoader groovyClassLoader;

    public GroovyPortlet()
    {
        super();
    }

    public void init(PortletConfig config) throws PortletException
    {
        this.portletConfig = config;
        this.groovyClassLoader = new GroovyClassLoader();

        this.autoRefresh = "true".equals(config.getInitParameter(AUTO_REFRESH_INIT_PARAM));

        String param = config.getInitParameter(SCRIPT_SOURCE_URL_ENCODING_INIT_PARAM);

        if (param != null)
        {
            this.scriptSourceUriEncoding = param;
        }

        this.scriptSourceUri = config.getInitParameter(SCRIPT_SOURCE_INIT_PARAM);

        if (this.scriptSourceUri == null)
        {
            throw new PortletException("Configuration failed: " + SCRIPT_SOURCE_INIT_PARAM + " should be set properly!");
        }
        else
        {
            try
            {
                if (this.scriptSourceUri.startsWith("file:"))
                {
                    String decodedScriptSourceUri = this.scriptSourceUri;

                    try
                    {
                        decodedScriptSourceUri = URLDecoder.decode(this.scriptSourceUri, this.scriptSourceUriEncoding);
                    }
                    catch (UnsupportedEncodingException encodingEx)
                    {
                        throw new PortletException("Unsupported encoding: " + this.scriptSourceUriEncoding);
                    }

                    this.groovyCodeSource = new GroovyCodeSource(new File(decodedScriptSourceUri.substring(5)));
                }
                else if (this.scriptSourceUri.startsWith("classpath:"))
                {
                    String resourceURL = this.groovyClassLoader.getResource(this.scriptSourceUri.substring(10))
                            .toString();

                    if (resourceURL.startsWith("file:"))
                    {
                        String decodedScriptSourceUri = resourceURL;

                        try
                        {
                            decodedScriptSourceUri = URLDecoder.decode(resourceURL, this.scriptSourceUriEncoding);
                        }
                        catch (UnsupportedEncodingException encodingEx)
                        {
                            throw new PortletException("Unsupported encoding: " + this.scriptSourceUriEncoding);
                        }

                        this.groovyCodeSource = new GroovyCodeSource(new File(decodedScriptSourceUri.substring(5)));
                    }
                    else
                    {
                        throw new PortletException(SCRIPT_SOURCE_INIT_PARAM
                                + " with 'classpath:' prefix should indicate to a local resource");
                    }
                }
                else
                {
                    this.groovyCodeSource = new GroovyCodeSource(new File(config.getPortletContext().getRealPath(
                            this.scriptSourceUri)));
                }
            }
            catch (FileNotFoundException e)
            {
                throw new PortletException("File not found: " + this.scriptSourceUri, e);
            }

            this.groovyCodeSource.setCachable(!this.autoRefresh);
        }

        refreshPortletInstance();

        if (this.scriptPortletInstance == null)
        {
            throw new PortletException("Groovy script portlet is not available!");
        }
    }

    public void destroy()
    {
        if (this.scriptPortletInstance != null)
        {
            this.scriptPortletInstance.destroy();
        }
    }

    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        refreshPortletInstance();

        if (this.scriptPortletInstance == null)
        {
            throw new PortletException("Groovy script portlet is not available!");
        }
        else
        {
            this.scriptPortletInstance.processAction(request, response);
        }
    }

    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        refreshPortletInstance();

        if (this.scriptPortletInstance == null)
        {
            throw new PortletException("Groovy script portlet is not available!");
        }

        this.scriptPortletInstance.render(request, response);
    }

    public PortletConfig getPortletConfig ()
    {
        return this.portletConfig;
    }
    
    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        if (this.scriptGenericPortletInstance != null && this.portletDoEditMethod != null)
        {
            try
            {
                this.portletDoEditMethod.invoke(this.scriptGenericPortletInstance, new Object [] { request, response });
            }
            catch (Exception e)
            {
                throw new PortletException("Failed to invoke doEdit method.", e);
            }
        }
        else
        {
            throw new PortletException("doEdit method not implemented or not public.");
        }
    }

    protected void refreshPortletInstance() throws PortletException
    {
        if (this.scriptPortletInstance == null)
        {
            try
            {
                createScriptPortletInstance();
            }
            catch (Exception ex)
            {
                throw new PortletException("Could not compile script: " + this.scriptSourceUri, ex);
            }
        }
        else if (this.autoRefresh && isScriptFileModified())
        {
            synchronized (this.scriptPortletInstance)
            {
                try
                {
                    createScriptPortletInstance();
                }
                catch (Exception ex)
                {
                    throw new PortletException("Could not compile script: " + this.scriptSourceUri, ex);
                }
            }
        }
    }

    protected boolean isScriptFileModified()
    {
        return (this.groovyCodeSource.getFile().lastModified() > this.parsedFileLastModified);
    }

    protected void createScriptPortletInstance() throws CompilationFailedException, InstantiationException,
            IOException, IllegalAccessException, PortletException
    {
        Class scriptPortletClass = this.groovyClassLoader.parseClass(this.groovyCodeSource);
        this.scriptPortletInstance = (Portlet) scriptPortletClass.newInstance();
        this.scriptGenericPortletInstance = null;
        this.portletDoEditMethod = null;
        
        if (this.scriptPortletInstance instanceof GenericPortlet)
        {
            this.scriptGenericPortletInstance = (GenericPortlet) this.scriptPortletInstance;
            
            try
            {
                Method doEditMethod = this.scriptGenericPortletInstance.getClass().getMethod("doEdit", new Class [] { RenderRequest.class, RenderResponse.class });
                
                if (Modifier.isPublic(doEditMethod.getModifiers()))
                {
                    this.portletDoEditMethod = doEditMethod;
                }
            }
            catch (NoSuchMethodException e)
            {
            }
        }
        
        this.parsedFileLastModified = this.groovyCodeSource.getFile().lastModified();
        this.scriptPortletInstance.init(this.portletConfig);
    }
}
