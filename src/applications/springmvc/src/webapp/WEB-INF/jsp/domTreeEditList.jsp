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

<portlet:defineObjects/>
<%
ResourceBundle myText = portletConfig.getResourceBundle(request.getLocale());
%>

<B><%=myText.getString("domtree_list")%></B><br>

<table border="0" cellpadding="4">
    <col width="15%"></col>
    <col width="45%"></col>
    <col width="20%"></col>
    <col width="20%"></col>
    <thead>
	<tr><th><%=myText.getString("name")%></th><th><%=myText.getString("path")%></th><th></th><th></th></tr>
	</thead>
	<c:forEach items="${model.list}" var="domTree">
		<tr>
			<td>${domTree.name}</td>
			<td>${domTree.path}</td>
			<portlet:renderURL var="editRender">
				<portlet:param name="action" value="editDomTree"/>
				<portlet:param name="domTree" value="${domTree.name}"/>
			</portlet:renderURL>
			<portlet:actionURL var="deleteRender">
				<portlet:param name="action" value="deleteDomTree"/>
				<portlet:param name="domTree" value="${domTree.name}"/>
			</portlet:actionURL>
			<td>			
				<button dojoType="Button" onclick="window.location.href='<%=editRender%>'">
					<img src="<html:imagesPath/>note.gif" border=0/> <%=myText.getString("edit")%>
				</button>
			</td>
			<td>
			    <button dojoType="Button" onclick="window.location.href='<%=deleteRender%>'">
					<img src="<html:imagesPath/>removesmall.gif" border=0/> <%=myText.getString("delete")%>
				</button>
			</td>
		</tr>
	</c:forEach>
		<tr>
			<td colspan="4">
				<portlet:renderURL var="addRender">
					<portlet:param name="action" value="newDomTree"/>
				</portlet:renderURL>
				<button dojoType="Button" onclick="window.location.href='<%=addRender%>'">
					<img title="New" src="<html:imagesPath/>plus.gif" border=0/> <%=myText.getString("add")%>
				</button>
			</td>
		</tr>
</table>
