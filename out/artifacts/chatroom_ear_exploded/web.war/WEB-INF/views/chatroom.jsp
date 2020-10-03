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
  <style><%@include file="/WEB-INF/views/css/black.css"%></style>
  <meta http-equiv="Content-type" content="text/html; charset=utf-8">
  <title>chat room</title>
<%--  <style type="text/css">--%>
<%--    /* 页面字体样式 */--%>
<%--    body, td, input {--%>
<%--      font-family:Arial;--%>
<%--      font-size:12px;--%>
<%--    }--%>

<%--    /* 显示发言信息 */--%>
<%--    #msgDiv {--%>
<%--      height:300px;--%>
<%--      border:1px solid black;--%>
<%--      overflow:scroll;--%>
<%--    }--%>
<%--  </style>--%>

<%--  <script type="text/javascript">--%>
<%--    var xmlHttp;--%>
<%--    var userName;                       //用于保存当前登录用户名--%>
<%--    var lastId = 0;                     //用于保存最后读取的聊天记录编号--%>
<%--    var newMsgTimer;                    //用于保存刷新消息计时器--%>
<%--    //用于创建XMLHttpRequest对象--%>
<%--    function createXmlHttp() {--%>
<%--      if (window.XMLHttpRequest) {--%>
<%--        xmlHttp = new XMLHttpRequest();--%>
<%--      } else {--%>
<%--        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");--%>
<%--      }--%>
<%--    }--%>
<%--    //聊天用户登录--%>
<%--    function login() {--%>
<%--      userName = document.getElementById("userName").value;--%>
<%--      if (userName=="" && !document.getElementById("anonymous_login").checked) {--%>
<%--        alert("请输入您的昵称。");--%>
<%--      } else {--%>
<%--        document.getElementById("loginDiv").style.display = "none"; //隐藏登录div--%>
<%--        document.getElementById("chatDiv").style.display = "";      //显示聊天div--%>
<%--        getNewMessage();                                            //获取新信息--%>
<%--      }--%>
<%--    }--%>

<%--    //用户发言--%>
<%--    function sendMessage() {--%>
<%--      var msg = document.getElementById("msg").value;                 //获取用户发言内容--%>

<%--      //当内容为空时，提示用户--%>
<%--      if (msg == "") {--%>
<%--        alert("发言不可为空。");--%>
<%--      } else {--%>
<%--        document.getElementById("msg").value = "";                  //清除用户发言--%>
<%--        clearInterval(newMsgTimer);                                 //清除获取新消息计时器--%>
<%--        createXmlHttp();--%>
<%--        xmlHttp.onreadystatechange = writeNewMessage;--%>
<%--        xmlHttp.open("POST", "chatroom.jsp", true);                 //发送POST请求--%>
<%--        xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");--%>
<%--        xmlHttp.send("action=send" +--%>
<%--                "&userName=" + encodeURIComponent(userName) +--%>
<%--                "&msg=" + encodeURIComponent(msg) +--%>
<%--                "&lastId=" + lastId);                          //参数包含用户信息和发言内容--%>
<%--      }--%>
<%--    }--%>

<%--    //获取最新发言--%>
<%--    function getNewMessage() {--%>
<%--      createXmlHttp();--%>
<%--      xmlHttp.onreadystatechange = writeNewMessage;--%>
<%--      xmlHttp.open("POST", "chatroom.jsp", true);--%>
<%--      xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");--%>
<%--      xmlHttp.send("action=get&lastId=" + lastId);--%>
<%--    }--%>

<%--    //将服务器返回的最新发言写入页面--%>
<%--    function writeNewMessage(newMsg) {--%>
<%--      if (xmlHttp.readyState == 4) {--%>
<%--        var msgDiv = document.getElementById("msgDiv");--%>
<%--        var newMsgObj = eval("("+xmlHttp.responseText+")");--%>

<%--        //当最后发言编号大于当前lastId时，在页面写入新内容--%>
<%--        if (newMsgObj.lastId > lastId) {--%>
<%--          lastId = newMsgObj.lastId;                              //更新lastId--%>
<%--          msgDiv.innerHTML += newMsgObj.msg;                      //追加新内容--%>
<%--          msgDiv.scrollTop = msgDiv.scrollHeight;                 //滚动div内容到底部--%>
<%--        }--%>
<%--        newMsgTimer = setTimeout("getNewMessage();", 1000);         //1秒后获取新发言--%>
<%--      }--%>
<%--    }--%>

<%--    //判断用户输入--%>
<%--    function checkEnter(evt) {--%>
<%--      var code = evt.keyCode||evt.which;--%>

<%--      //如果用户输入回车，调用sendMessage函数--%>
<%--      if (code == 13) {--%>
<%--        sendMessage();--%>
<%--      }--%>
<%--    }--%>
<%--  </script>--%>
</head>

<body>
<div class="txt">Welcome to chat room</div>

<%--<div id="loginDiv">--%>
<%--  昵称：<input type="text" id="userName">--%>
<%--  <input type="button" value="登录聊天室" onclick="login()">--%>
<%--  <input type="radio" id="anonymous_login" unchecked> anonymous_login--%>
<%--</div>--%>

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
