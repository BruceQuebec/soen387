<%--
  Created by IntelliJ IDEA.
  User: oscar
  Date: 2020-09-23
  Time: 3:35 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <%--    <meta http-equiv="refresh" content="10; URL=http://localhost:8080/chatroom">--%>
    <style>
        <jsp:include page="/views/css/black.css" />
    </style>
    <script src="http://code.jquery.com/jquery-latest.min.js"></script>
    <script>
        function onChangeSubmitCallWebServiceAJAX()
        {
            var e = document.getElementById("styleSelect");
            var style = e.options[e.selectedIndex].text;
            var url = '/chatroom?style=' + style
            window.location.href = url;
        }
    </script>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8">
    <title>message board</title>
</head>
<body>
<div class="small_txt1">
    select page style:
    <select id="styleSelect" onchange="onChangeSubmitCallWebServiceAJAX()">
        <option>select style</option>
        <option value="1">black</option>
        <option value="2">white</option>
    </select>
</div>
<div class="txt">
    Welcome to Chat Room
</div>
<div id="app1">
    <div class="txt">Message List:</div>
    <div class="small_txt">
        <p>title: ${post.key.getTitle()}</p>
        <p>user: ${post.key.getUsername()}</p>
        <p>created time: ${post.key.getCreatedTime()}</p>
    </div>
    <div class="small_txt">
        <p>content: ${post.key.getContent()}</p>
    </div>
    <div class="small_txt">
        <c:forEach items="${post.key.getHashTags()}" var="hashTag">
            <span><a href="/messageboard?tagsearch=1&tag=${hashTag}" target="_self">#${hashTag}</a> </span>
        </c:forEach>
    </div>
    <div class="small_txt">
        <c:forEach items="${post.value}" var="attachment">
            <span><a href=attachment?attopen=1&aid=${attachment.key}>${attachment.value}</a></span>
        </c:forEach>
    </div>
    <div>
        <a href="/messageboardcontent?pid=${post.key.getPid()}&modify=1">modify</a>
    </div>
</div>
</body>
</html>