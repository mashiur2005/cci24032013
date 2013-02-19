<%--
  Created by IntelliJ IDEA.
  User: mashiur
  Date: 2/19/13
  Time: 12:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
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