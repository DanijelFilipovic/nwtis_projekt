<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>NWTiS Projekt - Aplikacija 1</title>
		<link rel="stylesheet" type="text/css" href="resources/css/index.css"/>
    </head>
    <body>
        <div class="redirect-menu">
			<div class="redirect-menu-title">
				<div class="redirect-menu-title-group">Administrator</div>
				<div class="redirect-menu-title-group">Korisnik</div>
			</div>
			<div class="redirect-menu-group">
				<a class="redirect-button" href="admin/showUsers.jsp?page=1">Korisnici</a>
				<a class="redirect-button" href="admin/showLog.jsp?page=1">Dnevnik</a>
				<a class="redirect-button" href="admin/showRequests.jsp?page=1">Zahtjevi</a>
			</div>
			<div class="redirect-menu-group">
				<a class="redirect-button" href="user/showRequests.jsp">Zahtjevi</a>
			</div>
		</div>
    </body>
</html>
