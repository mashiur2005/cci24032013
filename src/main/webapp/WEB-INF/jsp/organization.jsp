<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head><title>Organization</title></head>
<body>
<ul class="organization">
    <c:forEach items="${it.publications}" var="publication">
        <li><a href="${it.organization}/${publication}">${publication}</a></li>
    </c:forEach>
</ul>
</body>
</html>