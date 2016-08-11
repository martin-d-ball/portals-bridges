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
package org.apache.portals.bridges.mapserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This portlet is executes the mapserv binary and encapsulating the query 
 *parameters passed to it.
 * 
 * @author <a href="mailto:philip.donaghy@gmail.com">Philip Mark Donaghy</a>
 */

public class MapServerPortlet extends GenericPortlet {
    
    private static final Log log = LogFactory.getLog(MapServerPortlet.class);

    private static String MAPSERV_BINARY = "MapServBinary";

    private static String LAYERS = "Layers";

    private static String ZOOM_DIRECTION = "ZoomDirection";

    private static String ZOOM_SIZE = "ZoomSize";

    private static String MAP_FILE = "MapFile";

    private static String PROGRAM = "Program";

    private static String ROOT_URL = "RootURL";

    private static String MAP_WEB_IMAGE_PATH = "MapWebImagePath";

    private static String MAP_WEB_IMAGE_URL = "MapWebImageURL";

    private static String MAP_WEB_TEMPLATE = "MapWebTemplate";
    
    private String mapservBinary;
    
    private String layers;
    
    private String zoomDirection;
    
    private String zoomSize;
    
    private String mapFile;

    private String program;
    
    private String rootURL;
    
    private String mapWebImagePath;
    
    private String mapWebImageURL;
    
    private String mapWebTemplate;
    
    public void init(PortletConfig config) throws PortletException
    {
    
        super.init(config);
        
        // Get the INIT PARAMETERS for this portlet.
        this.mapservBinary = config.getInitParameter(MAPSERV_BINARY);
        this.layers = config.getInitParameter(LAYERS);
        this.zoomDirection = config.getInitParameter(ZOOM_DIRECTION);
        this.zoomSize = config.getInitParameter(ZOOM_SIZE);
        this.mapFile = config.getInitParameter(MAP_FILE);
        this.program = config.getInitParameter(PROGRAM);
        this.rootURL = config.getInitParameter(ROOT_URL);
        this.mapWebImagePath = config.getInitParameter(MAP_WEB_IMAGE_PATH);
        this.mapWebImageURL = config.getInitParameter(MAP_WEB_IMAGE_URL);
        this.mapWebTemplate = config.getInitParameter(MAP_WEB_TEMPLATE);

        // If any of the values are missing throw an exception
        if (mapservBinary == null)
        {
            throw new PortletException("Portlet " + config.getPortletName()
            + " is incorrectly configured. Init parameter "
            + MAPSERV_BINARY + " not specified");
        }
        if (layers == null)
        {
            throw new PortletException("Portlet " + config.getPortletName()
            + " is incorrectly configured. Init parameter "
            + LAYERS + " not specified");
        }
        if (zoomDirection == null)
        {
            throw new PortletException("Portlet " + config.getPortletName()
            + " is incorrectly configured. Init parameter "
            + ZOOM_DIRECTION + " not specified");
        }
        if (zoomSize == null)
        {
            throw new PortletException("Portlet " + config.getPortletName()
            + " is incorrectly configured. Init parameter "
            + ZOOM_SIZE + " not specified");
        }
        if (mapFile == null)
        {
            throw new PortletException("Portlet " + config.getPortletName()
            + " is incorrectly configured. Init parameter "
            + MAP_FILE + " not specified");
        }
        if (program == null)
        {
            throw new PortletException("Portlet " + config.getPortletName()
            + " is incorrectly configured. Init parameter "
            + PROGRAM + " not specified");
        }
        if (rootURL == null)
        {
            throw new PortletException("Portlet " + config.getPortletName()
            + " is incorrectly configured. Init parameter "
            + ROOT_URL + " not specified");
        }
        if (mapWebImagePath == null)
        {
            throw new PortletException("Portlet " + config.getPortletName()
            + " is incorrectly configured. Init parameter "
            + MAP_WEB_IMAGE_PATH + " not specified");
        }
        if (mapWebImageURL == null)
        {
            throw new PortletException("Portlet " + config.getPortletName()
            + " is incorrectly configured. Init parameter "
            + MAP_WEB_IMAGE_URL + " not specified");
        }
        if (mapWebTemplate == null)
        {
            throw new PortletException("Portlet " + config.getPortletName()
            + " is incorrectly configured. Init parameter "
            + MAP_WEB_TEMPLATE + " not specified");
        }

     }
    
    /**
     * processAction()
     * Process actions made to the MapServer
     * @param actionRequest
     * @param actionResponse
     * @throws PortletException
     * @throws IOException
     */
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse)
                throws PortletException, IOException
    {
    
        Map parameterMap = actionRequest.getParameterMap();
        String queryString = ((HttpServletRequest)
                    ((HttpServletRequestWrapper) actionRequest)
                    .getRequest()).getQueryString();
    }
    
    /**
     * doView
     */
    public void doView(RenderRequest request, RenderResponse response)
                throws PortletException, IOException
    {
        
        // Set the content type
        response.setContentType("text/html");

        // Execute mapserv and return data to portal
        // TODO multiple layers
        String command = this.mapservBinary
                + " QUERY_STRING='layer=" + this.layers
                + "&zoomdir=" + this.zoomDirection
                + "&zoomsize=" + this.zoomSize
                + "&map=" + this.mapFile
                + "&program=" + this.program
                + "&root=" + this.rootURL
                + "&map_web_imagepath=" + this.mapWebImagePath
                + "&map_web_imageurl=" + this.mapWebImageURL
                + "&map_web_template=" + this.mapWebTemplate + "'";
        
        // Overwrite the command with the values present in the query
        String queryString = ((HttpServletRequest)
                    ((HttpServletRequestWrapper) request)
                    .getRequest()).getQueryString();
        System.out.println("QUERY_STRING : " + queryString);
        if (queryString != null)
        {
            command = this.mapservBinary
                + " QUERY_STRING=" + queryString;
        }

        System.out.println("COMMAND : " + command);
        Process proc = Runtime.getRuntime().exec(command);
        
        // Get stdout of process and create a buffered reader
        InputStream in = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(in, "UTF-8");
        BufferedReader perlResult = new BufferedReader(isr);
        StringBuffer page = new StringBuffer();
        
        //Wait until proc is done
        boolean bProcDone = false;
        while (bProcDone == false)
        {
            try
            {
                proc.exitValue() ;
                bProcDone = true;
            }
            catch(IllegalThreadStateException e)
            {
                bProcDone = false; //Not done yet
                
                // Read the buffer otherwise the process will be blocked 
                //because it can't write to the stdout (max size of buffer)
                int ln;
                while ((ln = perlResult.read()) != -1)
                {
                    char c  = (char)ln;
                    if (c != '\n' && c != '\r')
                    page.append((char)ln);
                }
            }
        }
        
        // Perl execution done read the remaining  buffer
        int ln = -1;
        
        while ((ln = perlResult.read()) != -1)
        {
            char c = (char)ln;
            if (c != '\n' && c != '\r')
            page.append((char)ln);
        }
        // Close stream
        perlResult.close();    
    
        // Get a writer object that can be used to generate the output
        HttpServletResponse httpResponse = (HttpServletResponse)
                    ((HttpServletResponseWrapper) response).getResponse();
        
        PrintWriter writer = httpResponse.getWriter();
        writer.println(page.toString());
        writer.flush();
        writer.close();
    } 
    
}
