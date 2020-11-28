<%@page pageEncoding="UTF-8" %>
<%@include file="/jsp/init.jsp" %>

<%@include file="/react/login/index.html" %>


<c:if test="${not empty param.login_error}">
<c:choose>
    <c:when test="${param.login_error == 3}">
        <div class="alert alert-warning"><spring:message code="USER_INACTIVE"/></div>
    </c:when>
    <c:otherwise>
        <div class="alert alert-warning"><spring:message code="LOGIN_ERROR"/></div>
    </c:otherwise>
</c:choose>
</c:if>

