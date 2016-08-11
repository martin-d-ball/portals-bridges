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
<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:loadBundle basename="org.apache.portals.applications.desktop.resources.Calendar" var="MESSAGE" />

<f:view>

<h:form id="calendarForm">
<t:inputCalendar 
    value="#{calendar.date}"
    monthYearRowClass="portlet-section-header" 
    weekRowClass="portlet-section-subheader"
    dayCellClass="portlet-menu-item"
    currentDayCellClass="portlet-menu-item-selected" 
     />     
<h:outputText value="#{calendar.date}" />
<h:commandButton id="edit" value="Edit" action="#{calendar.selectDate}"/>
<h:outputText value="#{MESSAGE['calendar.notes']}" />
</h:form>     
     
</f:view>
