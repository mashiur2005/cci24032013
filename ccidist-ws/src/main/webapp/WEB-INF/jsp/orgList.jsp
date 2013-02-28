<%@ page contentType="application/xhtml+xml;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Organization List</title>
</head>
<body>
<h1>Organization List</h1>
<ul class="organizations">
    <c:forEach items="${it.organizations}" var="organization">
        <li><a href="${organization.id}">${organization.name}</a></li>
    </c:forEach>
</ul>
</body>
</html>