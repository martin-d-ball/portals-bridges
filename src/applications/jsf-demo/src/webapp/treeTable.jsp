<!--
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
-->
<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<link href='css/tree.css' type='text/css'/>

<f:view>

	<f:verbatim><h2>Here is a tree table example.</h2></f:verbatim>

    <h:form id='tree2form'>
        <t:tree id="tree" value="#{treeTable.treeModel}"
        		var="treeItem"
        		styleClass="tree"
        	    nodeClass="tree-node"
        	    headerClass="portlet-table-header"
        	    footerClass="portlet-table-footer"
    	        rowClasses="portlet-table-body, portlet-table-alternate"
    	        columnClasses="portlet-table-col1, portlet-table-col2"
	            selectedNodeClass="tree-node-selected"
	            expandRoot="true">
	        <h:column>
	        	<f:facet name="header">
                	<h:outputText value="Header 1" />
                </f:facet>
               	<h:outputText value="#{treeItem.isoCode}" />
            </h:column>
	        <x:treeColumn>
	        	<f:facet name="header">
                	<h:outputText value="Header 2" />
                </f:facet>
	        	<h:outputText value="#{treeItem.name}" />
	        </x:treeColumn>
	        <h:column>
	        	<f:facet name="header">
                	<h:outputText value="Header 3" />
                </f:facet>
        		<h:outputText value="#{treeItem.description}" />
            </h:column>
            <f:facet name="footer">
            	<h:outputText value="Footer" />
            </f:facet>
     	</t:tree>
		<f:verbatim><br></f:verbatim>
   
   <h:commandLink id="goHome" action="guessGameStartFromTreeTable">
        <h:outputText value="Return to Guess Game"/>
    </h:commandLink>
   </h:form>
   
</f:view>