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
package org.apache.portals.network.monitor.options;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionMapping;

/**
 * @see org.apache.portals.network.monitor.options.OptionsController
 * @version $Id: OptionsControllerImpl.java 516448 2007-03-09 16:25:47Z ate $
 */
public class OptionsControllerImpl extends OptionsController {
	private static final Log log = LogFactory
			.getLog(OptionsControllerImpl.class);

	/**
	 * @see org.apache.portals.network.monitor.options.OptionsController#preProcess(org.apache.struts.action.ActionMapping,
	 *      org.apache.portals.network.monitor.options.PreProcessForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public final void preProcess(ActionMapping mapping,
			org.apache.portals.network.monitor.options.PreProcessForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.info("Let's call a service here");
	}

}