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
/**
 * User: Clinton Begin
 * Date: Jul 13, 2003
 * Time: 8:17:52 PM
 */
package com.ibatis.jpetstore.persistence.iface;

import com.ibatis.jpetstore.domain.Account;

import java.util.List;

public interface AccountDao {

  public Account getAccount(String username);

  public List getUsernameList();

  public Account getAccount(String username, String password);

  public void insertAccount(Account account);

  public void updateAccount(Account account);

}
