<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>NWTiS Projekt - Admin - Prikaz dnevnika</title>
		<% request.setCharacterEncoding("UTF-8"); %>
		<link rel="stylesheet" type="text/css" href="../resources/css/table.css"/>
		<link rel="stylesheet" type="text/css" href="../resources/css/datepicker.css"/>
		<script src="../resources/js/jquery.js"></script>
		<script src="../resources/js/jquery-ui.js"/></script>
		<script src="../resources/js/loadDatepicker.js"/></script>
    </head>
    <body>
        <jsp:useBean id="showLog" class="org.foi.nwtis.dfilipov.web.beans.ShowLogBean" scope="session"/>
		<c:if test="${param.page != null}">
			${showLog.setCurrentPage(param.page)}
		</c:if>
		<c:if test="${param.filterSubmit != null}">
			${showLog.setFilterType(param.filterType)}
			${showLog.setFilterMethod(param.filterMethod)}
			${showLog.setFilterUsername(param.filterUsername)}
			${showLog.setFilterDateFrom(param.filterDateFrom)}
			${showLog.setFilterDateTo(param.filterDateTo)}
			${showLog.loadLogs()}
		</c:if>
		<form class="filter" action="showLog.jsp?page=${showLog.currentPage}" method="POST">
			<label for="filterType">Tip servisa</label>
			<select id="filterType" name="filterType">
				<option value=" " <c:if test="${param.filterType != null && param.filterType == ' '}">selected</c:if>> </option>
				<option value="SOAP" <c:if test="${param.filterType != null && param.filterType == 'SOAP'}">selected</c:if>>SOAP</option>
				<option value="REST" <c:if test="${param.filterType != null && param.filterType == 'REST'}">selected</c:if>>REST</option>
			</select>
			<label for="filterUsername">Korisničko ime</label>
			<input id="filterUsername" name="filterUsername" type="text" value="${param.filterUsername}"/>
			<label for="filterMethod">Metoda</label>
			<input id="filterMethod" name="filterMethod" type="text" value="${param.filterMethod}"/>
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
					<th class="wide">Metoda</th>
					<th class="wide">Parametri</th>
					<th>Korisničko ime</th>
					<th>Tip <i>Web</i> servisa</th>
					<th>Datum i vrijeme</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${showLog.logs}" var="log">
					<tr>
						<td>${log.id}</td>
						<td class="wide">${log.method}</td>
						<td class="wide">${log.parameters}</td>
						<td>${log.username}</td>
						<td>${log.webServiceType}</td>
						<td>${showLog.formatDate(log.datetime)}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="pagination">
			<button 
				class="pagination-button"
				${showLog.currentPage == 1 ? "disabled" : ""}
				onclick="location.href='showLog.jsp?page=${showLog.currentPage - 1}'">
				Prethodna
			</button>
			<span class="pagination-number">${showLog.currentPage}</span>
			<button 
				class="pagination-button"
				${showLog.currentPage == showLog.maxPage ? "disabled" : ""}
				onclick="location.href='showLog.jsp?page=${showLog.currentPage + 1}'">
				Sljedeća
			</button>
		</div>
    </body>
</html>
