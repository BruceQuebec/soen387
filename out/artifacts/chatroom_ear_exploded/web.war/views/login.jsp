<%@ page contentType="text/html;charset=utf-8" %>
<html>

<head>
<title>用户注册</title>
  <style>
    <jsp:include page="/views/css/bbs03.css" />
  </style>
</head>
<body>
<div align="center">
<table border="1" width="600" bordercolorlight="#000000" cellspacing="0" cellpadding="0" bordercolordark="#FFFFFF">
  <tr>
    <td width="600" align="center">
        <p align="center" style="margin-top: 0; margin-bottom: 0">
          <b><font size="4"><br>SOEN 387 Message Board User Login&nbsp;&nbsp; </font></b><br/>
        </p>
        <div align="center">
        <table border="0" width="95%" height="85">   
          <tr>
            <form action="/messageboard" method="post" name="login_form">
            <td width="540" height="41"> 
              <p align="center" style="margin-top: 0; margin-bottom: 0">
                User Name:&nbsp;<input type="text" name="username" size="20">&nbsp;<font color="#FF0000">*</font>&nbsp;&nbsp;
                Password:&nbsp;<input type="password" name="password" size="20"><font color="#FF0000">*</font>&nbsp;
              </p><br/>

              <p align="center">
                <input class="buttonface" type="submit" name="login_sub" value="login">
                <input class="buttonface" type="reset" value="reset" name="reset">&nbsp;
              </p>
            </td>
            </form>
          </tr>                                                                                         
        </table>               
          </center>                                                                                   
        </div>
      </center>
    </td>          
  </tr>            
</table>                                                                                        
</div>
</body>  
</html>