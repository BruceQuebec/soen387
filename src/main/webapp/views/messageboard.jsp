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
    <div id="ownBubble" class="small_txt">
        <c:forEach items="${postList}" var="post">
            <p>
            <div>
            <span><a href="/messageboardcontent?pid=${post.key.getPid()}">${post.key.getTitle()}</a></span>
                <span>${post.key.getUsername()}</span>
                <span>${post.key.getCreatedTime()}</span>
            <span><a href="/messageboardcontent?pid=${post.key.getPid()}&postdel=1">delete</a></span>
            </div>
            </p>
        </c:forEach>
    </div>

    <div class="txt" style="margin-top:30px">Post Message:</div>
    <div id="" class="small_txt">
        <form action="messageboard" method="post" enctype="multipart/form-data">
            <p>title: <input type="text" id="title" name="title" size="30"></p>
            <p>content: <textarea id="content" name="content" size="300" rows="5", cols="20"></textarea></p>
            <p>Select attachment: <input type="file" name="file" size="50" /></p>
            <p><input type="submit" value="send message" name="addNewPost"></p>
        </form>
    </div>

<%--    <div class="txt">Download Message:</div>--%>
<%--    <div id="otherBubble" class="small_txt">--%>
<%--        <form action="chatroom" method="get">--%>
<%--            <p><span>Select index range to list:</span></p>--%>
<%--            <p>--%>
<%--                <span>From <input type="text" id="from" name="from" size="20"></span>--%>
<%--                <span>To <input type="text" id="to" name="to" size="20"></span>--%>
<%--            </p>--%>
<%--            <p>--%>
<%--                <span>Download in the form of plainText<input type="radio" name="format" value="plainText" checked> or XML<input type="radio" name="format" value="xml"></span>--%>
<%--                <input type="hidden" name="download" value=1>--%>
<%--            </p>--%>
<%--            <p>--%>
<%--                <span><input type="submit" value="download message" ></span>--%>
<%--            </p>--%>
<%--        </form>--%>
<%--    </div>--%>

<%--    <div class="txt" style="margin-top:30px">Delete Message:</div>--%>
<%--    <div id="clearListDiv" class="small_txt">--%>
<%--        <form action="chatroom" method="get">--%>
<%--            <p><span>Select index range to delete:</span></p>--%>
<%--            <p>--%>
<%--                <span>From <input type="text" name="from" size="20" onkeypress="checkEnter(event)"></span>--%>
<%--                <span>To <input type="text" name="to" size="20" onkeypress="checkEnter(event)"></span>--%>
<%--                <input type="hidden" name="delete" value=1>--%>
<%--            </p>--%>
<%--            <p>--%>
<%--                <span><input type="submit" value="delete message" ></span>--%>
<%--            </p>--%>
<%--        </form>--%>
<%--    </div>--%>
</div>
</body>
</html>