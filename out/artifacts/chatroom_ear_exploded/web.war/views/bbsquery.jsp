<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
<meta http-equiv="Content-Language" content="zh-cn">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>New Page 1</title>
    <style>
        <jsp:include page="/views/css/bbs03.css" />
    </style>
</head>
<body>

<div align="center">
  <center>
  <table border="0" width="500">
    <tr>
      <td width="100%" align="center"><b>Search the post</b></td>
    </tr>
      <tr>
          <td width="100%" align="left"><a href="/messageboard?page=1">go back to list</a></td>
      </tr>
    <tr>
      <td width="100%" align="left">
        <form method="GET" action="messageboard" target="_self">
            <p>
                <input type="text" name="keyword" size="60">
                <input class="buttonface" type="submit" value="search" name="B1">
            </p>
            <p>
                <input type="radio" name="search_type" value="tag">&nbsp;Tag&nbsp;
                <input type="radio" name="search_type" value="username">&nbsp;Username
                <input type="radio" name="search_type" value="userid">&nbsp;Userid&nbsp;
                <input type="radio" name="search_type" value="title">&nbsp;Title
                <input type="radio" name="search_type" value="datetime">&nbsp;Date&nbsp;& TIME
            </p>
        </form>
      </td>
    </tr>
  </table>         
  </center>         
</div>         
</body>         
</html>         
