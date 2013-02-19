<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="/c.tld" %>
<%@ taglib prefix="fmt" uri="/fmt.tld" %>

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