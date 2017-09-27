<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>NWTiS Projekt - Admin - Prikaz korisnika</title>
		<% request.setCharacterEncoding("UTF-8"); %>
		<link rel="stylesheet" type="text/css" href="../resources/css/table.css"/>
    </head>
    <body>
		<jsp:useBean id="showUsers" class="org.foi.nwtis.dfilipov.web.beans.ShowUsersBean" scope="session"/>
		<c:if test="${param.page != null}">
			${showUsers.setCurrentPage(param.page)}
		</c:if>
		<c:if test="${param.page < 1}">
			<c:redirect url="showUsers.jsp?page=1"/>
		</c:if>
		<c:if test="${param.page > showUsers.maxPage}">
			<c:redirect url="showUsers.jsp?page=${showUsers.maxPage}"/>
		</c:if>
		<c:if test="${param.filterSubmit != null}">
			${showUsers.setFilterRole(param.filterRole)}
			${showUsers.loadUsers()}
		</c:if>
		<form class="filter" action="showUsers.jsp?page=${showUsers.currentPage}" method="POST">
			<label for="filterRole">Uloga</label>
			<select name="filterRole">
				<option value=" " <c:if test="${param.filterRole != null && param.filterRole == ' '}">selected</c:if>> </option>
				<option value="USER" <c:if test="${param.filterRole != null && param.filterRole == 'USER'}">selected</c:if>>Korisnik</option>
				<option value="ADMIN" <c:if test="${param.filterRole != null && param.filterRole == 'ADMIN'}">selected</c:if>>Administrator</option>
			</select>
			<input type="submit" name="filterSubmit" value="Filtriraj"/>
		</form>
		<table>
			<thead>
				<tr>
					<th>ID</th>
					<th>Korisničko ime</th>
					<th>Lozinka</th>
					<th>Uloga</th>
					<th>Kategorija</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${showUsers.users}" var="user">
					<tr>
						<td>${user.id}</td>
						<td>${user.username}</td>
						<td>${user.password}</td>
						<td>${user.role}</td>
						<td>${user.category}</td>
					</tr>
				</c:forEach> 
			</tbody>
		</table>
		<div class="pagination">
			<button 
				class="pagination-button"
				${showUsers.currentPage == 1 ? "disabled" : ""}
				onclick="location.href='showUsers.jsp?page=${showUsers.currentPage - 1}'">
				Prethodna
			</button>
			<span class="pagination-number">${showUsers.currentPage}</span>
			<button 
				class="pagination-button"
				${showUsers.currentPage == showUsers.maxPage ? "disabled" : ""}
				onclick="location.href='showUsers.jsp?page=${showUsers.currentPage + 1}'">
				Sljedeća
			</button>
		</div>
    </body>
</html>
