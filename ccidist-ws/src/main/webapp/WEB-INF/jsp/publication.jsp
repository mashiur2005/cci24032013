<%@ page contentType="application/xhtml+xml;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>Publication</title>
</head>
<body>
    <h1>Publication</h1>
    <h3>
        <c:out escapeXml="true" value="${it.publication.name}" />
    </h3>
    <div>
        <span>Issue Search:</span>
        <c:set var="formSubmitUri"><c:out value="${it.issueSearchURI}"/></c:set>
        <form action="${formSubmitUri}">
            <dl>
                <dt>
                    <label for="limit">Limit</label>
                </dt>
                <dd>
                    <input type="text" name="limit" id="limit" />
                </dd>
            </dl>
            <dl>
                <dt>
                    <label for="start">Start</label>
                </dt>
                <dd>
                    <input type="text" name="start" id="start" />
                </dd>
            </dl>
            <dl>
                <dt>
                    <label for="deviceType">Device Type</label>
                </dt>
                <dd>
                    <input type="text" name="device-type" id="deviceType" />
                </dd>
            </dl>
            <dl>
                <dt>
                    <label for="date">From date</label>
                </dt>
                <dd>
                    <input type="text" name="from" id="date" />yyyy-MM-dd
                </dd>
            </dl>
            <dl>
                <dt>
                    <label for="sortOrder">Sort order</label>
                </dt>
                <dd>
                    <select id="sortOrder" name="sortOrder">
                           <option value="desc">Desc</option>
                           <option value="asc">Asc</option>
                    </select>
                </dd>
            </dl>
            <dl>
                <dd>
                    <input type="submit" name="submit" />
                </dd>
            </dl>
        </form>
    </div>
</body>
</html>
