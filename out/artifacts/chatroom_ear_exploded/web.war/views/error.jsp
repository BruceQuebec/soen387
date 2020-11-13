<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <style>
        <jsp:include page="/WEB-INF/views/css/${style}.css" />
    </style>
    <script src="http://code.jquery.com/jquery-latest.min.js"></script>
    <script>
        function onChangeSubmitCallWebServiceAJAX()
        {
            var e = document.getElementById("styleSelect");
            var style = e.options[e.selectedIndex].text;
            var url = '/index?style=' + style
            window.location.href = url;
        }
    </script>
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
        Exception: ${err} <span class="blink">_</span>
    </div>
</div>
</body>
</html>