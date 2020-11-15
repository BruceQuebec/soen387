<%--<%@ page session="true" %>--%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
int iPageSize = 10;
int iPageCount = (int) request.getAttribute("iPageCount");
int iPageCurrent = (int) request.getAttribute("iPageCurrent");
int iPageLast = (int) request.getAttribute("iPageLast");
int iPageNext = (int) request.getAttribute("iPageNext");
int uid_session = (int) request.getSession().getAttribute("uid");
String user_session = (String) request.getSession().getAttribute("username");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>New Page 3</title>
<base target="rtop">
<style>
    <jsp:include page="/views/css/bbs03.css" />
</style>
</head>
<body link="#000080" vlink="#000080" alink="#000080">
<div align="center"> 
  <center> 
<table border="0" width="600"> 
  <tr>
    <c:set var="user_session" value="<%=user_session %>" scope="page" />
      <td width="100%">welcome back:&nbsp;<b>${user_session}</b> &nbsp; <a href="/messageboard?query=1">&nbsp;search</a> &nbsp; <a href="/messageboard?logout=1">logout</a></td>
  </tr>
</table>          
  </center>          
</div>
<!--表头部分 -->
<div align="center">
    <center>
        <table border="1" width="600" bordercolorlight="#000000" bordercolordark="#FFFFFF" bgcolor="#FAD185" cellspacing="0" cellpadding="3">
            <tr>
                <td width="100" bgcolor="#000080">
                    <p align="center"><a href="messageboardcontent?newpost=1" target="_self"><font color="#FFFFFF">new post</font></a></p>
                </td>
                <td width="50" bgcolor="#000080">
                    <p align="center"><a href="messageboard?page=1"><font color="#FFFFFF">front page</font></a></p>
                </td>
                <td width="200" bgcolor="#000080">
                    <p align="center">
                        <%if (iPageCount>1) {%><A HREF="messageboard?page=1&${boardQuery}"><font color="#FFFFFF">first page</font></A><%}%>
                        <%if (iPageLast!=0) {%><A HREF="messageboard?page=<%=iPageLast%>&${boardQuery}"><font color="#FFFFFF">previous page</font></A><%}%>
                        <%if (iPageNext!=0) {%><A HREF="messageboard?page=<%=iPageNext%>&${boardQuery}"><font color="#FFFFFF">next page</font></A><%}%>
                        <%if (iPageCount>1) {%><A HREF="messageboard?page=<%=iPageCount%>&${boardQuery}"><font color="#FFFFFF">last page</font></A><%}%><font color="#FFFFFF"><%=iPageCurrent%>/<%=iPageCount%></font></p>
                    </p>
                </td>
                <td width="250" valign="middle" bgcolor="#000080">
                    <form method="GET" action="messageboard" style="margin-top: 0; margin-bottom: 0">
                        <p align="center" style="margin-top: 0; margin-bottom: 0"><font color="#FFFFFF">Number of post each page:&nbsp;<input type="text" name="numPostPage" size="2" value="10"><input type="hidden" name="boardQuery" value="${boardQuery}"><input type="submit" value="GO" name="GO"></font></p>
                    </form>
                </td>
            </tr>
        </table>
    </center>
</div>
<P></P>
<!--主贴开始----------------------->
<div align="center">
  <table border="0" width="600">          
    <tr>          
      <td width="100%"><c:set var="uid_session" value="<%=uid_session %>" scope="page" />
	  <ul>
            <c:forEach items="${postList}" var="post">
          <li>
              <ul align="left">
                  <li>
                    <img border="0" src="../views/images/001.gif">&nbsp;<a href="messageboardcontent?pid=${post.key.getPid()}" >${post.key.getTitle()}</a>
                    【<a href="messageboard?search_type=userid&keyword=${post.key.getUserId()}"><b>${post.key.getUsername()}</b></a>】<i>${post.key.timeFormat(post.key.getCreatedTime())}</i>
                    <c:if test="${post.key.getCreatedTime()!=post.key.getLastModifiedTime()}">
                        <font color="#FF0000">★</font>
                    </c:if>
                    <c:if test="${uid_session == post.key.getUserId()}">
                      <a href="/messageboardcontent?pid=${post.key.getPid()}&postdel=delete">delete</a>&nbsp;&nbsp;
                    </c:if>
                    <c:forEach items="${post.key.getHashTags()}" var="tag">
                    <a href="messageboard?search_type=tag&keyword=${tag}"># ${tag}</a>&nbsp;
                    </c:forEach>
                  </li>
              </ul>
<!--跟贴开始----------------------->
<%--              <ul>--%>
<%--                <li type="circle">--%>
<%--                    <p align="left">--%>
<%--                    <img border="0" src="../views/images/010.gif"><a href="bbsaddre.jsp?boardid=&bbsid=" >reply</a>--%>
<%--                    【<a href="mailto:user email"><b>user name</b></a>】<i>created time</i> [hit rate]--%>
<%--                    <a href="delete.jsp?bbsid=">delete</a>--%>
<%--                  </p>--%>
<%--              </ul>--%>
              </c:forEach>
      </ul>
<!--跟贴结束------------------------->
      <hr size="0" color="#808080">
      </td>                    
    </tr>                  
  </table>                
</div>
<!--表头部分 -->
<div align="center">
    <center>
        <table border="1" width="600" bordercolorlight="#000000" bordercolordark="#FFFFFF" bgcolor="#FAD185" cellspacing="0" cellpadding="3">
            <tr>
                <td width="100" bgcolor="#000080">
                    <p align="center"><a href="messageboardcontent?newpost=1" target="_self"><font color="#FFFFFF">new post</font></a></p>
                </td>
                <td width="50" bgcolor="#000080">
                    <p align="center"><a href="messageboard?page=1"><font color="#FFFFFF">front page</font></a></p>
                </td>
                <td width="200" bgcolor="#000080">
                    <p align="center">
                        <%if (iPageCount>1) {%><A HREF="messageboard?page=1&${boardQuery}"><font color="#FFFFFF">first page</font></A><%}%>
                        <%if (iPageLast!=0) {%><A HREF="messageboard?page=<%=iPageLast%>&${boardQuery}"><font color="#FFFFFF">previous page</font></A><%}%>
                        <%if (iPageNext!=0) {%><A HREF="messageboard?page=<%=iPageNext%>&${boardQuery}"><font color="#FFFFFF">next page</font></A><%}%>
                        <%if (iPageCount>1) {%><A HREF="messageboard?page=<%=iPageCount%>&${boardQuery}"><font color="#FFFFFF">last page</font></A><%}%><font color="#FFFFFF"><%=iPageCurrent%>/<%=iPageCount%></font></p>
                    </p>
                </td>
                <td width="250" valign="middle" bgcolor="#000080">
                    <form method="GET" action="messageboard" style="margin-top: 0; margin-bottom: 0">
                        <p align="center" style="margin-top: 0; margin-bottom: 0"><font color="#FFFFFF">Number of post each page:&nbsp;<input type="text" name="numPostPage" size="2" value="10"><input type="hidden" name="boardQuery" value="${boardQuery}"><input type="submit" value="GO" name="GO"></font></p>
                    </form>
                </td>
            </tr>
        </table>
    </center>
</div>
</body>              
</html>