<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Issue</title></head>
<body>
<h1>Issue</h1>

<h3>${it.issue} of '${it.publication}' Publication</h3>
<ul class="issue">
    <li><a href="${it.issue}.epub">Epub</a></li>
    <li><a href="${it.issue}/META-INF/container.xml">Container</a></li>
</ul>
</body>
</html>