<%--
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ page session="false" %>
<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<portlet:actionURL portletMode="edit" var="myAction">
	<portlet:param name="save" value="save"/>
    <portlet:param name="action" value="editDomTree"/>
</portlet:actionURL>

<portlet:renderURL portletMode="view" var="viewRender">
    <portlet:param name="action" value="list"/>
</portlet:renderURL>

<jsp:useBean id="addUrl" scope="request" 
 class="java.lang.String" />
<jsp:useBean id="cancelUrl" scope="request" 
 class="java.lang.String" />
<portlet:defineObjects/>
<%
ResourceBundle myText = portletConfig.getResourceBundle(request.getLocale());
%>
<B><%=myText.getString("domtree_list")%></B><br>

<form name="editDomTreeForm" method="post" action="<%=myAction%>" method="post">
<TABLE CELLPADDING=0 CELLSPACING=4>
  <TR>  
    <TD>
      <B><%=myText.getString("name")%></B>
    </TD>
    <TD>
      <B><%=myText.getString("path")%></B>
    </TD>
    <TD>
    </TD>
  </TR>
  <TR>
    <TD>
      <html:input path="command.name"/>           <!-- <INPUT NAME="name" TYPE="text"> -->
    </TD>
    <TD>
      <html:input path="command.path" size="20"/> <!-- <INPUT NAME="value" size='20' TYPE="text"> -->
    </TD>
    <TD>
      <button dojoType="Button" onclick="document.forms[ 'editDomTreeForm' ].submit()">
	     <img src="<html:imagesPath/>note.gif" border=0/> <%=myText.getString("save")%>
	  </button>  <!-- <INPUT NAME="save" TYPE="submit" value="<%=myText.getString("save")%>"> -->
    </TD>
  </TR>
</TABLE>
</FORM>
<button dojoType="Button" onclick="window.location.href='<%=viewRender%>'">
<%=myText.getString("cancel")%>
</button>            
