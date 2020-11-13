<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
<script Language="JavaScript">
    <!--
    function isspacestring(mystring)
//是否为空格字符串;true为空，FALSE为非空
    { var istring=mystring;
        var temp,i,strlen;
        temp=true;
        strlen=istring.length;
        for (i=0;i<strlen;i++)
        {
            if ((istring.substring(i,i+1)!=" ")&(temp))
            { temp=false;  }
        }
        return temp;
    }

    function firstisspace(mystring)
//检查首字母是否是空格，TRUE首字母为空格；FALSE首字母不为空格
    { var istring=mystring;
        var temp,i;
        temp=true;
        if (istring.substring(0,1)!=" ")
        { temp=false;  }
        return temp;
    }

    function isemail(mystring)
    {
        var istring=mystring;
        var atpos=mystring.indexOf("@");
        var temp=true;
        if (atpos==-1) //email中没有@符号；不正确的EMAIL
        {
            temp=false;
        }
        return temp;
    }


    function check_input(theForm)
    {

        if ((theForm.username.value == "")|(isspacestring(theForm.username.value)))
        {
            alert("请输入正确的用户名.");
            theForm.username.focus();
            return (false);
        }

        if ((theForm.userpassword.value == "")|(isspacestring(theForm.userpassword.value)))
        {
            alert("请输入密码.");
            theForm.userpassword.focus();
            return (false);
        }

        if ((theForm.bbstopic.value == "")|(isspacestring(theForm.bbstopic.value)))
        {
            alert("您没写主题.");
            theForm.bbstopic.focus();
            return (false);
        }


        return (true);
    }
    //-->
</script>
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
                <td width="100%" bgcolor="#000080">
                    <p align="center"><font color="red"><b>Create New Post</b></font></td>
            </tr>
            <tr>
                <td width="100%" bgcolor="#FFFFFF" valign="top">
                    <div align="center">
                        <center>
                            <table border="0" width="440">
                                <tr>
                                    <td width="100%" valign="top">
                                        <form method="POST" action="messageboardcontent" onsubmit="return check_input(this)" name="addNewPostForm" enctype='multipart/form-data'>
                                            <p style="margin-top: 0; margin-bottom: 0"><b><font color="#008000">Section:</font></b>&nbsp;<b><font color="red">Development in process</font></b></p><br/>
                                            <p align="left" style="margin-top: 0; margin-bottom: 0"><b><font color="#008000">Subject:</font></b>&nbsp;<input type="text" name="title" size="50" value="${post.key.getTitle()}"></p><br/>
                                            <p align="left" style="margin-top: 0; margin-bottom: 0"><font color="#008000"><b>Emoji:</b></font><br/>
                                                <input type="radio" value="001" name="expression" checked><img border="0" src="../views/images/001.gif">
                                                <input type="radio" value="002" name="expression"><img border="0" src="../views/images/002.gif">
                                                <input type="radio" value="003" name="expression"><img border="0" src="../views/images/003.gif" width="20" height="20">
                                                <input type="radio" value="004" name="expression"><img border="0" src="../views/images/004.gif" >
                                                <input type="radio" value="005" name="expression"><img border="0" src="../views/images/005.gif" >
                                                <input type="radio" value="006" name="expression"><img border="0" src="../views/images/006.gif" >
                                                <input type="radio" value="007" name="expression"><img border="0" src="../views/images/007.gif" >
                                                <input type="radio" value="008" name="expression"><img border="0" src="../views/images/008.gif" >
                                                <input type="radio" value="009" name="expression"><img border="0" src="../views/images/009.gif" ></p>
                                            <p align="left" style="margin-top: 0; margin-bottom: 0">
                                                <input type="radio" value="010" name="expression"><img border="0" src="../views/images/010.gif" >
                                                <input type="radio" value="011" name="expression"><img border="0" src="../views/images/011.gif" >
                                                <input type="radio" value="012" name="expression"><img border="0" src="../views/images/012.gif" >
                                                <input type="radio" value="013" name="expression"><img border="0" src="../views/images/013.gif" >
                                                <input type="radio" value="014" name="expression"><img border="0" src="../views/images/014.gif" ></p><br/>
                                            <p align="center" style="margin-top: 0; margin-bottom: 0">
                                                <textarea rows="4" name="content" cols="50">${post.key.getContent()}</textarea>
                                            </p><br/>
                                            <p align="left" style="margin-top: 0; margin-bottom: 0">
                                                <b><font color="#008000">Hash Tags:</font></b><br/>
                                                <c:forEach items="${post.key.getHashTags()}" var="tag">
                                                    <a href="messageboard?tagsearch=1&tag=${tag}"># ${tag}</a>
                                                </c:forEach>
                                            </p><br/>
                                            <p align="left" style="margin-top: 0; margin-bottom: 0">
                                                <b><font color="#008000">Attachments:</font></b><br/>
                                                <c:forEach items="${post.value}" var="atts">
                                                    ${atts.value}&nbsp;<a href="attachment?attdel=1&aid=${atts.key}">delete</a><br/>
                                                </c:forEach>
                                            </p><br/>
                                            <p align="left"><b><font color="#008000">Add Attachment: </font></b><input type="file" name="file" size="50" /></p><br/>
                                            <p align="center" style="margin-top: 0; margin-bottom: 0">
                                                <input class="buttonface" type="submit" value="edit post" name="editpost">
                                                <input class="buttonface" type="reset" value="reset" name="reset">
                                                <input type="hidden" name="pid" value="${post.key.getPid()}">
                                                <input type="hidden" name="uid" value="${post.key.getUserId()}">
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