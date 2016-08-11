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
package org.apache.portals.network.monitor.status;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.network.monitor.status.Status;
import org.apache.struts.action.ActionMapping;

/**
 * @see org.apache.portals.network.monitor.status.StatusController
 * @version $Id: StatusControllerImpl.java 516448 2007-03-09 16:25:47Z ate $
 */
public class StatusControllerImpl extends StatusController {
	private static final Log log = LogFactory
			.getLog(StatusControllerImpl.class);

	/**
	 * @see org.apache.portals.network.monitor.status.StatusController#preProcess(org.apache.struts.action.ActionMapping,
	 *      org.apache.portals.network.monitor.status.PreProcessForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public final void preProcess(ActionMapping mapping,
			org.apache.portals.network.monitor.status.PreProcessForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO define this method as a spring service and call as show
		// form.setStatusList(this.getStatusService().getStatusByAvailability(
		// form.getChoice()));
		form.setStatusList(buildStatusList(form));
	}

	private List buildStatusList(
			org.apache.portals.network.monitor.status.PreProcessForm form) {
		List statusList = new ArrayList();

		if (form.getChoice() == null || form.getChoice().equals("1")
				|| form.getChoice().equals("0")) {
			StatusValueObject a = new StatusValueObject("apache.org",
					"140.211.11.130", "Available 113ms");
			statusList.add(a);

			StatusValueObject bs = new StatusValueObject("bluesunrise.com",
					"207.44.240.46", "Available 81ms");
			statusList.add(bs);

			StatusValueObject mi = new StatusValueObject("mapimage.com",
					"82.29.65.234", "Available 111ms");
			statusList.add(mi);

			StatusValueObject l = new StatusValueObject("linux.org",
					"198.182.196.48", "Available 59ms");
			statusList.add(l);

			StatusValueObject bsd = new StatusValueObject("freebsd.org",
					"69.147.83.40", "Available 122ms");
			statusList.add(bsd);

			StatusValueObject andromda = new StatusValueObject("andromda.org",
					"130.237.165.134", "Available 139ms");
			statusList.add(andromda);

			StatusValueObject dt = new StatusValueObject("displaytag.org",
					"64.202.189.170", "Available 121ms");
			statusList.add(dt);

			StatusValueObject ch = new StatusValueObject("codehaus.org",
					"63.246.7.187", "Available 64ms");
			statusList.add(ch);

			StatusValueObject nbsd = new StatusValueObject("netbsd.org",
					"204.152.190.12", "Available 121ms");
			statusList.add(nbsd);

			StatusValueObject m = new StatusValueObject("mozilla.org",
					"63.245.209.11", "Available 105ms");
			statusList.add(m);

			StatusValueObject oo = new StatusValueObject("openoffice.org",
					"204.16.104.2", "Available 111ms");
			statusList.add(oo);

			StatusValueObject e = new StatusValueObject("eclipse.org",
					"206.191.52.50", "Available 102ms");
			statusList.add(e);

			StatusValueObject j = new StatusValueObject("java.org",
					"146.145.196.172", "Available 111ms");
			statusList.add(j);

			StatusValueObject isc = new StatusValueObject("isc.org",
					"204.152.184.88", "Available 120ms");
			statusList.add(isc);

			StatusValueObject s = new StatusValueObject("samba.org",
					"66.70.73.150", "Available 42ms");
			statusList.add(s);

			StatusValueObject osgeo = new StatusValueObject("osgeo.org",
					"66.223.95.242", "Available 55ms");
			statusList.add(osgeo);

			StatusValueObject tigris = new StatusValueObject("tigris.org",
					"204.16.104.146", "Available 110ms");
			statusList.add(tigris);
		}

		if (form.getChoice() == null || form.getChoice().equals("2")
				|| form.getChoice().equals("0")) {
			StatusValueObject ms = new StatusValueObject("microsoft.com",
					"207.46.232.182", "Unavailable timeout");
			statusList.add(ms);
		}

		return statusList;
	}
}