<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <style>
        <jsp:include page="/views/css/black.css" />
    </style>
<%--    <script src="http://code.jquery.com/jquery-latest.min.js"></script>--%>
<%--    <script>--%>
<%--        function onChangeSubmitCallWebServiceAJAX()--%>
<%--        {--%>
<%--            var e = document.getElementById("styleSelect");--%>
<%--            var style = e.options[e.selectedIndex].text;--%>
<%--            var url = '/index?style=' + style--%>
<%--            window.location.href = url;--%>
<%--        }--%>
<%--    </script>--%>
</head>
<body>
<div id="app">
    <div class="small_txt1">
            <span>
            select page style:
            <select id="styleSelect" onchange="onChangeSubmitCallWebServiceAJAX()">
                <option>select style</option>
                <option value="1">black</option>
                <option value="2">white</option>
            </select>
            </span>
    </div>
    <div class="txt">
        <form action="/messageboard" method="post" name="login_form">
            <p>User name: <input type="text" name="username"></p>
            <p>Password: <input type="password" name="password"></p>
            <p><input type="submit" name="login_sub" value="login"></p>
        </form>
    </div>
    <div class="txt">
        Your request is illegal! <span class="blink">_</span>
    </div>
</div>
</body>
</html>