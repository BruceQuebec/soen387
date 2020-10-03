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
  <style><%@ include file="/WEB-INF/views/css/black.css"%></style>
  <meta http-equiv="Content-type" content="text/html; charset=utf-8">
  <title>chat room</title>
</head>
<body>
<div class="txt">Welcome to chat room</div>
<div id="app1">
  <div class="txt">Message List:</div>
  <div id="msgDiv" class="small_txt">
    <c:forEach items="${messageMap}" var="entry">
      <p>
        <div>
          <span>No. ${entry.key}</span>
          <span>User: ${entry.value.key}</span>
        </div>
        <div>
          Message: ${entry.value.value}
        </div>
      </p>
    </c:forEach>
  </div>

  <div class="txt" style="margin-top:30px">Send Message:</div>
  <div id="sendDiv" class="small_txt">
    <form action="chatroom" method="post">
      <p><span>Netname: <input type="text" id="user" name="user" size="30"></span></p>
      <p>
        <span>Message: <input type="text" id="msg" name="message" size="30"></span>
        <input type="hidden" name="send" value=1>
      </p>
      <p><span><input type="submit" value="send message" onclick="checkEnter()"></span></p>
    </form>
  </div>

  <div class="txt" style="margin-top:30px">Download Message:</div>
  <div id="downlistFromToDiv" class="small_txt">
    <form action="chatroom" method="get">
      <p><span>Select index range to list:</span></p>
      <p>
        <span>From <input type="text" id="from" name="from" size="20"></span>
        <span>To <input type="text" id="to" name="to" size="20"></span>
      </p>
      <p>
        <span>Download in the form of plainText<input type="radio" name="format" value="plainText" checked> or XML<input type="radio" name="format" value="xml"></span>
        <input type="hidden" name="download" value=1>
      </p>
      <p>
        <span><input type="submit" value="download message" ></span>
      </p>
    </form>
  </div>

  <div class="txt" style="margin-top:30px">Delete Message:</div>
  <div id="clearListDiv" class="small_txt">
    <form action="chatroom" method="get">
      <p><span>Select index range to delete:</span></p>
      <p>
        <span>From <input type="text" name="from" size="20" onkeypress="checkEnter(event)"></span>
        <span>To <input type="text" name="to" size="20" onkeypress="checkEnter(event)"></span>
        <input type="hidden" name="delete" value=1>
      </p>
      <p>
        <span><input type="submit" value="delete message" ></span>
      </p>
    </form>
  </div>
</div>
</body>
</html>
