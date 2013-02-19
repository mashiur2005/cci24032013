<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
    <title>Organization List</title>
</head>
<body>
<ul class="organizations">
    <c:forEach items="${it.organizations}" var="organization">
        <li><a href="${fn:toLowerCase(organization)}">${organization}</a></li>
    </c:forEach>
</ul>
</body>
</html>