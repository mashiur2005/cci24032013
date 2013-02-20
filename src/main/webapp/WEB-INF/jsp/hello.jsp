<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Hello ${it.user}</title>
</head>
<body>
Hello ${it.user}..............<br/>
Service value is ${it.message}.......
<br/>
<c:out value="Done....."/>

</body>
</html>