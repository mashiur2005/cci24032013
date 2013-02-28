<%@ page contentType="application/xhtml+xml;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head><title>Organization</title></head>
<body>
<h1>Organization</h1>
<h3>${it.organization.name}</h3>
<ul class="organization">
    <c:forEach items="${it.organization.publications}" var="publication">
        <li><a href="${it.organization.id}/${publication.id}">${publication.name}</a></li>
    </c:forEach>
</ul>
</body>
</html>