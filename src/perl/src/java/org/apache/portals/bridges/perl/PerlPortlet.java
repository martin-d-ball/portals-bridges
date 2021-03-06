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
package org.apache.portals.bridges.perl;

import javax.portlet.GenericPortlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletSession;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.rewriter.JetspeedRewriterController;
import org.apache.jetspeed.rewriter.RewriterController;
import org.apache.jetspeed.rewriter.RewriterException;
import org.apache.jetspeed.rewriter.RulesetRewriter;
import org.apache.jetspeed.rewriter.html.SwingParserAdaptor;
import org.apache.jetspeed.rewriter.rules.Ruleset;
import org.apache.jetspeed.rewriter.xml.SaxParserAdaptor;

import org.apache.portals.bridges.common.ScriptPostProcess;


/**
* This portlet is executes a Perl/cgi files in a portlet.
* 
* Note:
* The Perl Portlet uses the rewriter component that requires config xml files.
* Make sre that the portlet application using the Perl Portlet has the following files included
* in WEB-INF/conf: rewriter-rules-mapping.xml and default-rewriter-rules.xml
*
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
* @version $Id: PerlPortlet.java 517068 2007-03-12 01:44:37Z ate $
*/

public class PerlPortlet extends GenericPortlet {
	/**
	 * INIT parameters required by the Perl Portlet:PerlScript, ScriptPath, DemoMode
	 *
	 * Name of the scrip to to execute
	 */
	public static final String PARAM_PERL_SCRIPT	=	"PerlScript";

	/**
	 * Name of the Script Path where the perl scripts (among others) are located
	 */
	public static final String PARAM_SCRIPT_PATH	=	"ScriptPath";
	
	/**
	 * DemoMode on or off
	 */
	public static final String PARAM_DEMO_MODE	=	"DemoMode";
	
	/**
	 * PARAM_APPLICATION
	 * 
     * ApplicationName identifies the caller so that the portlet only refreshes
     * content that was supposed for the portlet.
     * If the application name is undefined the portlet will process the request.
     * If you have more than one perl-portlet for the same user/session all the portlets
     * will be refreshed with the same content.
	 */
	public static final String PARAM_APPLICATION = "Application";

//  Local variables
	private String perlScript	=	"perl-demo.cgi";
    private String	scriptPath	=	"cgi-bin";
    
    
    private String applicationName = null;
    
    // Switch that shows basic information about the perl script to run
    private boolean bDemoMode	=	false;
    
    private static final Log log = LogFactory.getLog(PerlPortlet.class);
    
       
    // caching status -- cache the last query    
    private String lastQuery = null;
    
    // Cache the last generated page
    String lastPage = null;
    
    /* PerlContent rewriter */
    RulesetRewriter		rewriter = null;
    RewriterController	rewriteController = null;
    
    /** Default encoding */
    public String defaultEncoding = "UTF-8";
    
    
    public void init(PortletConfig config) throws PortletException
    {
    
        super.init(config);
        
        // Get the INIT PARAMETERS for this portlet. If the values are missing
        // throw an exception
        scriptPath		=	config.getInitParameter(PARAM_SCRIPT_PATH);
        perlScript		=	config.getInitParameter(PARAM_PERL_SCRIPT);
        String demoMode =	config.getInitParameter(PARAM_DEMO_MODE); 
        applicationName = config.getInitParameter(PARAM_APPLICATION);
        
        if (demoMode != null && demoMode.compareToIgnoreCase("on") == 0)
        	bDemoMode = true;
        
        if (scriptPath == null)
            throw new PortletException("Portlet " + config.getPortletName()
                    + " is incorrectly configured. Init parameter "
                    + PARAM_SCRIPT_PATH + " not specified");
        
        if (perlScript == null)
            throw new PortletException("Portlet " + config.getPortletName()
                    + " is incorrectly configured. Init parameter "
                    + PARAM_PERL_SCRIPT + " not specified");
     }	
    
    /**
     * processAction()
     * Checks action initiated by the perl portlet (invoking other perl scripts)
     * @param actionRequest
     * @param actionResponse
     * @throws PortletException
     * @throws IOException
     */
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
	{
    	String perlParameter = actionRequest.getParameter(PerlParameters.ACTION_PARAMETER_PERL);
     	/*
    	 * If the perlParameter is not empty create a PerlParameter object and attach it to the session
    	 */
    	if ( perlParameter != null && perlParameter.length() > 0)
    	{
    		// Perl Parameter Object
    		PerlParameters cgi = new PerlParameters();
    		cgi.setApplicationName(this.applicationName);
       		
    		// Separate the values before and after the Query Mark ?
    		int ixQuery = perlParameter.indexOf('?');
    		if ( ixQuery != -1)
    		{
    			// Remove leading slash
    			if (perlParameter.charAt(0) == '/')
    				cgi.setScriptName(perlParameter.substring(1,ixQuery));
    			else
    				cgi.setScriptName(perlParameter.substring(0,ixQuery));
    			
    			String queryArguments = perlParameter.substring(ixQuery+1);
    			System.out.println("ProcessRequest -- Script " + perlParameter.substring(0,ixQuery) + " Query string " + queryArguments);
    			
    			int ixQuerySeparator = queryArguments.indexOf('&');
    			while ( ixQuerySeparator != -1)
    			{
    				cgi.addQueryArgument(queryArguments.substring(0, ixQuerySeparator));
    				queryArguments = queryArguments.substring(ixQuerySeparator+1);
    				ixQuerySeparator = queryArguments.indexOf('&');
    			}
    			
    			cgi.addQueryArgument(queryArguments);
    			
    			// Add the PerlParameters to the session
    			actionRequest.getPortletSession().setAttribute(PerlParameters.PERL_PARAMETER, cgi, PortletSession.APPLICATION_SCOPE);
    		}
    		else
    		{
    			// No query string just the script name
    			cgi.setScriptName(perlParameter);
    			
    			// Get all the parameters from the request and add them as query arguments
    			Enumeration names = actionRequest.getParameterNames();
    			String name, value;
    			while (names.hasMoreElements())
    			{
    				name = (String)names.nextElement();
    				// PERL_PARAMETER already processed just ignore it
    				if (name.compareToIgnoreCase(PerlParameters.ACTION_PARAMETER_PERL) != 0)
    				{
    				    // Same parameter name can have multiple values (multi select view box)
    				    String [] values = actionRequest. getParameterValues(name);
    				    
    				    for (int ii=0; ii < values.length; ii++)
    				    {
    				    
	    					value = values[ii];
	    					
	    					if (value !=null && value.length() > 0)
	    					{
	    					    /*
	    					     * If the query parameter contains any & replace it with %26 (URL encoding)
	    					     */
	    						value = urlEncoding(value, "&", "%26");
	    						value = urlEncoding(value, "+", "%2b");
	    						value = urlEncoding(value, "\\", "%5c");
	    						
		    					cgi.addQueryArgument(name + "=" + value);
		    					
	    					}
    				    }
    				}
    			}
    			// Add the PerlParameters to the session
    			actionRequest.getPortletSession().setAttribute(PerlParameters.PERL_PARAMETER, cgi, PortletSession.APPLICATION_SCOPE);
     		}
    	}
	}
    /**
     * doView
     * Executes the perl script that is defined by the property PerlScript.
     * If the incoming request has the query perl-script= defined this script will be executed.
     */
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
	{
    	// Set the content type
    	response.setContentType("text/html");
    	
    	// Get a writer object that can be used to generate the output
    	HttpServletResponse httpResponse = (HttpServletResponse)((HttpServletResponseWrapper) response).getResponse();
    	
    	//PrintWriter writer = response.getWriter();
    	PrintWriter writer = httpResponse.getWriter();
    	   	
    	String query		= null;
    	String scriptName	= null;
    	PerlParameters perlParam = null;
    	
    	/**
    	 * The Perl parameters are either passed by a session attribute (invoked from a different portlet) or as an action which is replaced
    	 * with a session while processing actions..
    	 */
    	    	
		try
		{
    		perlParam = (PerlParameters)request.getPortletSession().getAttribute(PerlParameters.PERL_PARAMETER, PortletSession.APPLICATION_SCOPE);
    		// Remove perl object from session.
    		request.getPortletSession().removeAttribute(PerlParameters.PERL_PARAMETER, PortletSession.APPLICATION_SCOPE);
		}
    	catch (Exception e )
		{
    		perlParam = null;
		}
    	
    	if (perlParam != null)
    	{
    	    // Only use the values if the call is designated to this script
    	    if (perlParam.getApplicationName().compareToIgnoreCase(this.applicationName) == 0)
    	    {
	    		query = perlParam.getQueryString();
	    		perlScript = perlParam.getScriptName();
    	    }
    	    
    		if (this.applicationName == null ) // not yet initialized
    		{
    			this.applicationName = perlParam.getApplicationName();
    		}
    		else
    		{
	    		// If the application name doesn't match just use the cached version and return
	    		if (         lastPage != null 							// has run at least once
	    				&& this.applicationName != null	// No filtering runs for any perl request
	    				&& perlParam.getApplicationName().compareToIgnoreCase(this.applicationName) != 0)
	    		{
	    			// Use cache
	    			writer.println(this.lastPage);
	    			return;
	    		}
    		}
    	}
    	else
    	{
    		/*
    		 * 	Check for a query string since the portlet could be invoked form JAVA Script and therefore
    		 *	the parameters are not encoded in a Portlet Action or Perl Session.
    		*/
    		StringBuffer queries = new StringBuffer();
    		
    		Enumeration names = request. getParameterNames();
    		for (Enumeration e = request. getParameterNames() ; e.hasMoreElements() ;) {
    			String name = (String)e.nextElement();
    			
				//Same parameter name can have multiple values (multi select view box)
			    String [] values = request. getParameterValues(name);
			    
			    for (int ii=0; ii < values.length; ii++)
			    {
			    	String value = values[ii];
			    	
			    	if (queries.length() > 0)
			    		queries.append("&");
			    	
					// Make sure that any ? are replaced with & since JAVA SCRIPT
			    	// might patch different elements together.
			    	int ix = value.indexOf("?");
					if (ix > -1)
					{
						String tmp = value.substring(0,ix) + "&" + value.substring(ix+1);
						value = tmp;
					}
					// Check if the query string defines the argument file
					if (name.compareToIgnoreCase("file") == 0)
					{
						/* file=hello&arg=hello */
						/* file=test.cgi */
						String reminder = "";
						
						int ixEnd = value.indexOf("&");
						if (ixEnd > -1)
						{
							reminder = value.substring(ixEnd +1);
							perlScript = value.substring(0,ixEnd);
						}
						else
						{
							perlScript = value;
						}
						
						if (reminder.length() > 0)
							queries.append(reminder);
					}
					else
					{
						queries.append(name).append("=").append(value);
					}
			    }
    		}
    		query = queries.toString();
    		
    		//Cleanup of Buffer
    		queries.delete(0, queries.length());
    		
    		System.out.println("Script [" + perlScript +"]");
    		System.out.println("Direct Query [" + queries.toString() +"]");
    	}
    	
    	// Open the perl script and extract the perl executable path. It's the same way as apache HTTP executes PERL
    	String perlExecutable = null;
    	
     	PortletContext portletApplication = getPortletContext(); 
        String path = portletApplication.getRealPath("/WEB-INF");
        String contextPath = path + "/";
 
        String fullScriptPath = contextPath + scriptPath;
    
    	// Build full path to scripts
    	if (perlScript.startsWith("/") == false )
    	    fullScriptPath += "/";
    	fullScriptPath += perlScript;
    	
    	// command to execute
    	String command = null;
    		
    	// Open the script and read the first line to get the executable !/usr/bin/perl OR !c:\bin\perl\perl.exe
    	try
		{
    		BufferedReader in= new BufferedReader(new FileReader(fullScriptPath));
    		String lnExecutable = in.readLine();
    		
    		if (lnExecutable != null )
    		{
    			// Make sure that the executable is defined -- could be a compiled cgi with no executable defined
    			String lnExecutableLower = lnExecutable.toLowerCase();
    			int px = lnExecutableLower.indexOf("perl");
    			int ix = lnExecutable.indexOf('!');
    			if ( ix != -1 && px != -1 )
    			{
                    int ex = lnExecutable.indexOf(' ',ix);
                    if ( ex >= 0 )
                    {
                        perlExecutable = lnExecutable.substring(ix+1, ex);
                    }
                    else
                    {
                        perlExecutable = lnExecutable.substring(ix+1);
                    }
    			} 
    		}
    		//Close file
    		in.close();
    		
    		StringBuffer commandBuffer = new StringBuffer();
    		if (perlExecutable == null)
    			commandBuffer.append(fullScriptPath);
    		else
    			commandBuffer.append(perlExecutable).append(' ').append(fullScriptPath);
    		
    		command = new String(commandBuffer.toString());
    		
		}
    	catch(FileNotFoundException e)
		{
    		writer.println("<P><B>File doesn't exist (" + fullScriptPath + ")</B></P>");
		}
    	catch(IOException e)
		{
    		writer.println("<P><B>IO Exception (" + e.getMessage() + ")</B></P>");
		}
		catch(Exception e)
		{
			writer.println("<P><B>IO Exception (" + e.getMessage() + ")</B></P>");
		}
			
		String envQuery = "QUERY_STRING=" + query ;
		
		String[] env = null;
		env = new String[]{"REQUEST_METHOD=GET", envQuery, "LD_LIBRARY_PATH=/usr/local/groundwork/lib"};
		
		if ( bDemoMode == true)
		{
			// Script info. This is for the Demo only
			//writer.println("<P>The portlet executes the perl script defined by the init-params. If you don't get an output make sure that the perl executable defined in the script is valid.");
			//writer.println("The executable is defined on the first line of your script.</P>Examples<ul><li><B>UNIX/Linux:</B>!/usr/bin/perl</li><li><B>Windows:</B>!c:\\bin\\perl\\perl.exe</li></ul>");
			writer.println("<B><P>Perl Script:</B>" + fullScriptPath + "<BR>");
			//writer.println("<B>Perl executable:</B>" + perlExecutable + "<BR>");
			writer.println("<B>Query String:</B>" + query + "</P>");
		}   	
    	
		
		//Execute the perl script from the command line
		if (command != null )
		{
			
			// Execute command in a separate process. The perl output is written to the stdout
			try
			{	
				long timeStart =0;
				long timeEnd = 0;
				
				if ( bDemoMode == true)
				{
					timeStart = System.currentTimeMillis();
				}
				// Start process
				Process proc = Runtime.getRuntime().exec(command,env);
									
				// Get stdout of process and create a buffered reader
				InputStream in = proc.getInputStream();
				BufferedReader perlResult = new BufferedReader(new InputStreamReader(in));
				StringBuffer page = new StringBuffer();
				
				if ( bDemoMode == true)
				{
					timeEnd = System.currentTimeMillis();
					writer.println("<B>Execution Time create process: </B>" + (timeEnd -timeStart) + " ms </P>");
					timeStart = System.currentTimeMillis();
				}
				
				int BLOCK_SIZE = 8192;
				char[] bytes = new char[BLOCK_SIZE];
				
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
						
						// Read the buffer otherwise the process will be blocked because it can't write to the stdout (max size of buffer)						
						int len = perlResult.read(bytes, 0, BLOCK_SIZE);
						
						while (len > 0)
						{
							page.append(bytes, 0, len);
							len = perlResult.read(bytes, 0, BLOCK_SIZE);
						}
					}
				}
				
				// Perl execution done read the remaining  buffer
				int len = perlResult.read(bytes, 0, BLOCK_SIZE);
				while (len > 0)
				{
						page.append(bytes, 0, len);
						len = perlResult.read(bytes, 0, BLOCK_SIZE);
				}
				
				// Close stream
				perlResult.close();	
				
				//Make sure process is destroyed
				try
				{
					proc.destroy();
				}
				catch(Exception e)
				{
					System.out.println("Error killing perl subprocess. Error " + e);
				}
				
				if ( bDemoMode == true)
				{
					timeEnd = System.currentTimeMillis();
					writer.println("<B>Loading output of perl: </B>" + (timeEnd -timeStart) + " ms </P>");
					timeStart = System.currentTimeMillis();
				}	
				/*
				 * Use rewriter for replacing all URL's with portlet actions
				 */
				/*
		        if (rewriteController == null)
		        {
		            try
		            {
		                // Create rewriter adaptor
		                rewriteController = getController(contextPath);
		            }
		            catch (Exception e)
		            {
		                // Failed to create rewriter controller
		                throw new PortletException("WebContentProtlet failed to create rewriter controller. Error:"
		                        + e.getMessage());
		            }
		        }
		        
		        //	Any HREFs and Form actions should be extended with the ActionURL
				PortletURL actionURL = response.createActionURL();
				byte[] content = this.doWebContent(page, actionURL, PerlParameters.ACTION_PARAMETER_PERL);
				ByteArrayInputStream bais = new ByteArrayInputStream(content);
				OutputStream oswriter = response.getPortletOutputStream();
				
				int BLOCK_SIZE = 4096;
				byte[] bytes = new byte[BLOCK_SIZE];
		        try
		        {
		            int length = bais.read(bytes);
		            while (length != -1)
		            {
		                if (length != 0)
		                {
		                    oswriter.write(bytes, 0, length);
		                }
		                length = bais.read(bytes);
		            }
		        }
		        finally
		        {
		            bytes = null;
		        }
				
				//	Cache page
				lastPage = new String(content);
				*/
				
				
				
				// Post Process for generated page		
				PortletURL actionURL = response.createActionURL();
				ScriptPostProcess processor = new ScriptPostProcess();
				processor.setInitalPage(page);
				processor.postProcessPage(actionURL, PerlParameters.ACTION_PARAMETER_PERL);
				String finalPage = processor.getFinalizedPage();
				
				if ( bDemoMode == true)
				{
					timeEnd = System.currentTimeMillis();
					writer.println("<P><B>Rewriting perl: </B>" + (timeEnd - timeStart) + " ms </P>");
				}
				
				// Write the page
				writer.println(finalPage);
				
				
				// Cache page
				//lastPage = new String(finalPage);
				
			}
			catch(IOException ioe)
			{
				writer.println("<P><B>Exception while reading perl output" + ioe.getMessage() + "</B></P>");
			}
			catch(Exception e)
			{
				writer.println("<P><B>Exception while reading perl output" + e + "</B></P>");
			}
		}
		else
		{
			writer.println("<P><B>Error. Failed to run perl script [" + perlScript + "]</B></P>");
		}
	} 
    
    /*
     * Generate a rewrite controller using the basic rules file
     */
    private RewriterController getController(String contextPath) throws Exception
    {
        Class[] rewriterClasses = new Class[]
        { PerlContentRewriter.class, PerlContentRewriter.class};
        
        Class[] adaptorClasses = new Class[]
        { SwingParserAdaptor.class, SaxParserAdaptor.class};
        RewriterController rwc = new JetspeedRewriterController(contextPath + "conf/rewriter-rules-mapping.xml", Arrays
                .asList(rewriterClasses), Arrays.asList(adaptorClasses));

        FileReader reader = new FileReader(contextPath + "conf/default-rewriter-rules.xml");

        Ruleset ruleset = rwc.loadRuleset(reader);
        reader.close();
        rewriter = rwc.createRewriter(ruleset);
        return rwc;
    }
    
    protected byte[] doWebContent(StringBuffer perlRenderedPage, PortletURL actionURL, String actionParameterName)
    throws PortletException
	{
		// Initialization
		Writer htmlWriter = null;
		
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		
		try
		{
		    htmlWriter = new OutputStreamWriter(byteOutputStream, this.defaultEncoding);
		
		    // Set the action URL in the rewriter
		   
		   ((PerlContentRewriter) rewriter).setActionURL(actionURL);
		   ((PerlContentRewriter) rewriter).setActionParameterName(actionParameterName);
		   
		    StringReader perlReader = new StringReader(perlRenderedPage.toString());
		    rewriter.rewrite(rewriteController.createParserAdaptor("text/html"), perlReader, htmlWriter);
		    htmlWriter.flush();
		}
		catch (UnsupportedEncodingException ueex)
		{
		    throw new PortletException("Encoding " + defaultEncoding + " not supported. Error: " + ueex.getMessage());
		}
		catch (RewriterException rwe)
		{
		    throw new PortletException("Failed to rewrite Perl ouput. Error: " + rwe.getMessage());
		}
		catch (Exception e)
		{
		    throw new PortletException("Exception while rewritting Perl output. Error: " + e.getMessage());
		}
		
		return byteOutputStream.toByteArray();
	}
    
    // Helper
    private String urlEncoding(String url, String source, String replace)
    {
    	String value = url;
    	int ix = value.indexOf(source);
	    if (ix != -1)
	    {
	        String replacement = "";
		    
	        // Replace all & in the value
		    while (ix != -1)
		    {
		        replacement += value.substring(0, ix);
		        replacement += replace;
		        // Check if the & was the last char
		        if (value.length() > ix)
		            value = value.substring(ix+1);
		        else
		            value = "";	// End of string
		        
		        // Check again
		        ix = value.indexOf(source);
		    }
		    
	        // Reminder
	        replacement += value;
	        // Assignement
	        value = replacement;
	    }
	    return value;
    }
}
	
