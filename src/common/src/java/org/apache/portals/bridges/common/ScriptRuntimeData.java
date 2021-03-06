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

package org.apache.portals.bridges.common;

/**
 * ScriptRuntimeData
 * 
 * Base class  that provides meta data to no Java portlets such as:
 * PHP, Perl
 * 
 * @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
 * @version $Id: ScriptRuntimeData.java 517068 2007-03-12 01:44:37Z ate $
 */
public class ScriptRuntimeData {

	// Class members
	private String	scriptName = "";
	private String	queryString = "";
	private String	displayMessage = "";
	private String	sessionParameterName = "";
	private String	applicationName = "";
	
	/**
	 * addQueryString()
	 * Adds one argument at the time to the query strings. Takes care of the separators
	 * 
	  */
	public void addQueryArgument(String queryArgument)
	{
		if (queryString == null)
		{
			queryString = queryArgument;
		}
		else
		{
			queryString += '&';
			queryString += queryArgument;
		}	
	}
	
	/**
	 * @return Returns the displayMessage.
	 */
	public String getDisplayMessage() {
		return displayMessage;
	}
	/**
	 * @param displayMessage The displayMessage to set.
	 */
	public void setDisplayMessage(String displayMessage) {
		this.displayMessage = displayMessage;
	}
	/**
	 * @return Returns the queryString.
	 */
	public String getQueryString() {
		return queryString;
	}
	/**
	 * @param queryString The queryString to set.
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	/**
	 * @return Returns the scriptName.
	 */
	public String getScriptName() {
		return scriptName;
	}
	/**
	 * @param scriptName The scriptName to set.
	 */
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}
	/**
	 * @return Returns the sessionParameterName.
	 */
	public String getSessionParameterName() {
		return sessionParameterName;
	}
	/**
	 * @param sessionParameterName The sessionParameterName to set.
	 */
	public void setSessionParameterName(String sessionParameterName) {
		this.sessionParameterName = sessionParameterName;
	}
	/**
	 * @return Returns the applicationName.
	 */
	public String getApplicationName() {
		return this.applicationName;
	}
	/**
	 * @param applicationName The applicationName to set.
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
}
