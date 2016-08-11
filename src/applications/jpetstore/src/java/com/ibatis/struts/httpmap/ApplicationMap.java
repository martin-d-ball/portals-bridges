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
package com.ibatis.struts.httpmap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Map to wrap application scope attributes.
 * <p/>
 * Date: Mar 11, 2004 11:21:25 PM
 *
 * @author Clinton Begin
 */
public class ApplicationMap extends BaseHttpMap {

  private ServletContext context;

  public ApplicationMap(HttpServletRequest request) {
    context = request.getSession().getServletContext();
  }

  protected Enumeration getNames() {
    return context.getAttributeNames();
  }

  protected Object getValue(Object key) {
    return context.getAttribute(String.valueOf(key));
  }

  protected void putValue(Object key, Object value) {
    context.setAttribute(String.valueOf(key), value);
  }

  protected void removeValue(Object key) {
    context.removeAttribute(String.valueOf(key));
  }

}
