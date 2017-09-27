<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>NWTiS Projekt - Admin - Prikaz zahtjeva</title>
		<link rel="stylesheet" type="text/css" href="../resources/css/table.css"/>
		<link rel="stylesheet" type="text/css" href="../resources/css/datepicker.css"/>
		<script src="../resources/js/jquery.js"></script>
		<script src="../resources/js/jquery-ui.js"/></script>
		<script src="../resources/js/loadDatepicker.js"/></script>
		<% request.setCharacterEncoding("UTF-8"); %>
    </head>
    <body>
        <jsp:useBean id="showRequests" class="org.foi.nwtis.dfilipov.web.beans.ShowRequestsBean" scope="session"/>
		<c:if test="${param.page != null}">
			${showRequests.setCurrentPage(param.page)}
		</c:if>
		<c:if test="${param.filterSubmit != null}">
			${showRequests.setFilterAddress(param.filterAddress)}
			${showRequests.setFilterDateFrom(param.filterDateFrom)}
			${showRequests.setFilterDateTo(param.filterDateTo)}
			${showRequests.loadLogs()}
		</c:if>
		<form class="filter" action="showRequests.jsp?page=${showRequests.currentPage}" method="POST">
			<label for="filterAddress">Adresa</label>
			<input id="filterAddress" name="filterAddress" type="text" value="${param.filterAddress}"/>
			<label for="filterDateFrom">Datum od</label>
			<input class="datepicker" id="filterDateFrom" name="filterDateFrom" type="text" readonly="true" value="${param.filterDateFrom}"/>
			<label for="filterDateTo">Datum do</label>
			<input class="datepicker" id="filterDateTo" name="filterDateTo" type="text" readonly="true" value="${param.filterDateTo}"/>
			<input type="submit" name="filterSubmit" value="Filtriraj"/>
		</form>
		<table class="lots-of-text">
			<thead>
				<tr>
					<th>ID</th>
					<th class="wide">Naredba</th>
					<th>Korisničko ime</th>
					<th>Status</th>
					<th>Tip zahtjeva</th>
					<th>Datum i vrijeme</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${showRequests.requests}" var="request">
					<tr>
						<td>${request.id}</td>
						<td class="wide">${request.method}</td>
						<td>${request.username}</td>
						<td>${request.status}</td>
						<td>${request.webServiceType}</td>
						<td>${showRequests.formatDate(request.datetime)}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="pagination">
			<button 
				class="pagination-button"
				${showRequests.currentPage == 1 ? "disabled" : ""}
				onclick="location.href='showRequests.jsp?page=${showRequests.currentPage - 1}'">
				Prethodna
			</button>
			<span class="pagination-number">${showRequests.currentPage}</span>
			<button 
				class="pagination-button"
				${showRequests.currentPage == showRequests.maxPage ? "disabled" : ""}
				onclick="location.href='showRequests.jsp?page=${showRequests.currentPage + 1}'">
				Sljedeća
			</button>
		</div>
    </body>
</html>
