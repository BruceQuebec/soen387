<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
int uid_session = (int) request.getSession().getAttribute("uid");
%>
<html>

<head>
    <meta http-equiv="Content-Language" content="zh-cn">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>加贴</title>
    <style>
        <jsp:include page="/views/css/bbs03.css" />
    </style>
</head>
<body>
<!--首次进入本页-->
<div align="center">
    <center>
        <table border="0" width="100%">
            <tr>
                <td width="100%">
                    <p align="center"></p>
                </td>
            </tr>
        </table>
    </center>
</div>
<div align="center">
    <center>
        <table border="1" width="500" bordercolorlight="#000000" cellspacing="0" cellpadding="0" bordercolordark="#FFFFFF">
            <tr>
                <td width="100%" bgcolor="white">
                    <p align="left"><b><a href="/messageboard?page=1">go to list</a></b></p>
                </td>
            </tr>
            <tr>
                <td width="100%" bgcolor="#FFFFFF" valign="top">
                    <div align="center">
                        <center>
                            <table border="0" width="440">
                                <tr>
                                    <td>
                                        <p style="margin-top: 0; margin-bottom: 0"><b><font color="#008000">Section:</font></b>&nbsp;<b><font color="red">Development in process</font></b></p><br/>
                                        <p align="left" style="margin-top: 0; margin-bottom: 0"><b><font color="#008000">Subject:</font></b>&nbsp;${post.key.getTitle()} </p><br/>
                                        <p align="left" style="margin-top: 0; margin-bottom: 0"><b><font color="#008000">Content:</font></b>&nbsp;${post.key.getContent()}</p><br/>
                                        <p align="left" style="margin-top: 0; margin-bottom: 0"><b><font color="#008000">Created in:</font></b>&nbsp;${post.key.getCreatedTime()} &nbsp;Modified in:${post.key.getLastModifiedTime()}</p><br/>
                                        <p align="left" style="margin-top: 0; margin-bottom: 0">
                                            <b><font color="#008000">Hash Tags:</font></b><br/>
                                            <c:forEach items="${post.key.getHashTags()}" var="tag">
                                                <a href="messageboard?search_type=tag&keyword=${tag}"># ${tag}</a>&nbsp;
                                            </c:forEach>
                                        </p><br/>
                                        <p align="left" style="margin-top: 0; margin-bottom: 0">
                                            <b><font color="#008000">Attachments:</font></b><br/>
                                            <c:forEach items="${post.value}" var="atts">
                                                <a href="attachment?attopen=1&aid=${atts.key}">${atts.value}</a><br/>
                                            </c:forEach>
                                        </p><br/>
                                        <form action="messageboardcontent" method="get">
                                        <p align="center" style="margin-top: 0; margin-bottom: 0">
                                            <input class="buttonface" type="submit" value="reply" name="reply">
                                            <c:set var="uid_session" value="<%=uid_session %>" scope="page" />
                                            <c:if test="${uid_session == post.key.getUserId()}">
                                                <input class="buttonface" type="submit" value="modify" name="modify">
                                                <input class="buttonface" type="submit" value="delete" name="postdel">
                                            </c:if>
                                            <input type="hidden" name="pid" value="${post.key.getPid()}">
                                        </p>
                                        </form>
                                    </td>
                                </tr>
                            </table>
                        </center>
                    </div>
                </td>
            </tr>
        </table>
    </center>
</div>
<script language="JavaScript">
    window.location="bbsadd.jsp?boardid="+boardid+"&tt="+tt;
</script>
<script language="JavaScript">
    window.location="bbsadd.jsp?boardid="+boardid+"&tt="+tt;
</script>
</body>
</html>