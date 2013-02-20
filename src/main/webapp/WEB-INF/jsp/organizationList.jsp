<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Organization List</title>
</head>
<body>
<ul class="organizations">
    <c:forEach items="${it.organizations}" var="organization">
        <li><a href="${organization}">${organization}</a></li>
    </c:forEach>
</ul>
</body>
</html>