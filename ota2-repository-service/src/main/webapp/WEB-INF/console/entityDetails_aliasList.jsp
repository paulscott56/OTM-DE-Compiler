<%--

    Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	
<c:if test="${!aliasList.isEmpty()}">
	<tr class="${rowStyle}">
		<td>Aliases</td>
		<td>
		<c:set var="firstRow" value="true" />
		<c:forEach var="alias" items="${aliasList}">
			<c:if test="${!firstRow}"><br/></c:if>
			<img src="${pageContext.request.contextPath}/images/alias.gif" />&nbsp;${alias.name}
			<c:set var="firstRow" value="false" />
		</c:forEach>
		</td>
	</tr>
	<c:set var="rowStyle" value="${pageUtils.swapValue( rowStyle, 'd0', 'd1')}" />
</c:if>
