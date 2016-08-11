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
import java.util.Collection;
import java.util.ArrayList;

import javax.portlet.PortletPreferences;
import javax.portlet.PreferencesValidator;
import javax.portlet.ValidatorException;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * <p>
 * GroovyPreferencesValidator parses and invokes a groovy-scripted validator. A groovy-scripted
 * PreferencesValidator just need to be implemented like any other Java-based preferences validator. 
 * 
 * @author <a href="mailto:woon_san@yahoo.com">Woonsan Ko</a>
 * @Id@
 */
public class GroovyPreferencesValidator implements PreferencesValidator
{
    public static final String SCRIPT_SOURCE_PREF_KEY = "validator-script-source";

    public static final String SCRIPT_SOURCE_URL_ENCODING_PREF_KEY = "validator-script-source-uri-encoding";

    public static final String AUTO_REFRESH_PREF_KEY = "validator-auto-refresh";

    protected String scriptSourceUri;

    protected String scriptSourceUriEncoding = "UTF-8";

    protected boolean autoRefresh;

    protected long parsedFileLastModified;

    protected GroovyCodeSource groovyCodeSource;

    protected PreferencesValidator scriptPreferencesValidatorInstance;

    protected GroovyClassLoader groovyClassLoader;

    public GroovyPreferencesValidator()
    {
    }
    
    public void validate(PortletPreferences preferences) throws ValidatorException
    {
        if (this.groovyCodeSource == null)
        {
            initialize(preferences);
        }
        
        refreshPreferencesValidatorInstance();
        
        this.scriptPreferencesValidatorInstance.validate(preferences);
    }

    public void initialize(PortletPreferences preferences) throws ValidatorException
    {
        this.groovyClassLoader = new GroovyClassLoader();

        this.autoRefresh = "true".equals(preferences.getValue(AUTO_REFRESH_PREF_KEY, null));

        String param = preferences.getValue(SCRIPT_SOURCE_URL_ENCODING_PREF_KEY, null);

        if (param != null)
        {
            this.scriptSourceUriEncoding = param;
        }

        this.scriptSourceUri = preferences.getValue(SCRIPT_SOURCE_PREF_KEY, null);

        if (this.scriptSourceUri == null)
        {
            Collection failedKeys = new ArrayList();
            failedKeys.add(SCRIPT_SOURCE_PREF_KEY);
            throw new ValidatorException("Configuration failed: " + SCRIPT_SOURCE_PREF_KEY + " should be set properly!", failedKeys);
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
                        Collection failedKeys = new ArrayList();
                        failedKeys.add(SCRIPT_SOURCE_URL_ENCODING_PREF_KEY);
                        throw new ValidatorException("Unsupported encoding: " + this.scriptSourceUriEncoding, failedKeys);
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
                            Collection failedKeys = new ArrayList();
                            failedKeys.add(SCRIPT_SOURCE_URL_ENCODING_PREF_KEY);
                            throw new ValidatorException("Unsupported encoding: " + this.scriptSourceUriEncoding, failedKeys);
                        }

                        this.groovyCodeSource = new GroovyCodeSource(new File(decodedScriptSourceUri.substring(5)));
                    }
                    else
                    {
                        Collection failedKeys = new ArrayList();
                        failedKeys.add(SCRIPT_SOURCE_PREF_KEY);
                        throw new ValidatorException(SCRIPT_SOURCE_PREF_KEY
                                + " with 'classpath:' prefix should indicate to a local resource", failedKeys);
                    }
                }
                else
                {
                    Collection failedKeys = new ArrayList();
                    failedKeys.add(SCRIPT_SOURCE_PREF_KEY);
                    throw new ValidatorException("Configuration failed: " + SCRIPT_SOURCE_PREF_KEY + " should be prefixed by 'file:' or 'classpath'.", failedKeys);
                }
            }
            catch (FileNotFoundException e)
            {
                Collection failedKeys = new ArrayList();
                failedKeys.add(SCRIPT_SOURCE_PREF_KEY);
                throw new ValidatorException("File not found: " + this.scriptSourceUri, failedKeys);
            }

            this.groovyCodeSource.setCachable(!this.autoRefresh);
        }
    }

    protected void refreshPreferencesValidatorInstance() throws ValidatorException
    {
        if (this.scriptPreferencesValidatorInstance == null)
        {
            try
            {
                createScriptPreferencesValidatorInstance();
            }
            catch (Exception ex)
            {
                Collection failedKeys = new ArrayList();
                failedKeys.add(SCRIPT_SOURCE_PREF_KEY);
                throw new ValidatorException("Could not compile script: " + this.scriptSourceUri, failedKeys);
            }
        }
        else if (this.autoRefresh && isScriptFileModified())
        {
            synchronized (this.scriptPreferencesValidatorInstance)
            {
                try
                {
                    createScriptPreferencesValidatorInstance();
                }
                catch (Exception ex)
                {
                    Collection failedKeys = new ArrayList();
                    failedKeys.add(SCRIPT_SOURCE_PREF_KEY);
                    throw new ValidatorException("Could not compile script: " + this.scriptSourceUri, failedKeys);
                }
            }
        }
    }

    protected boolean isScriptFileModified()
    {
        return (this.groovyCodeSource.getFile().lastModified() > this.parsedFileLastModified);
    }

    protected void createScriptPreferencesValidatorInstance() throws CompilationFailedException, InstantiationException,
            IOException, IllegalAccessException, ValidatorException
    {
        Class scriptPreferencesValidatorClass = this.groovyClassLoader.parseClass(this.groovyCodeSource);
        this.scriptPreferencesValidatorInstance = (PreferencesValidator) scriptPreferencesValidatorClass.newInstance();
        this.parsedFileLastModified = this.groovyCodeSource.getFile().lastModified();
    }
}
