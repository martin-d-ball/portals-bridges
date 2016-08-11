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
package com.ibatis.struts;

import com.ibatis.common.exception.NestedRuntimeException;

/**
 * This exception is thrown internally by BeanAction and
 * can also be used by bean action methods as a general
 * or base exception.
 * <p/>
 * Date: Mar 13, 2004 8:17:00 PM
 *
 * @author Clinton Begin
 */
public class BeanActionException extends NestedRuntimeException {

  public BeanActionException() {
    super();
  }

  public BeanActionException(String s) {
    super(s);
  }

  public BeanActionException(Throwable throwable) {
    super(throwable);
  }

  public BeanActionException(String s, Throwable throwable) {
    super(s, throwable);
  }

}
