<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>IR Project</title>

<spring:url value="/resources/core/css/hello.css" var="coreCss" />
<spring:url value="/resources/core/css/bootstrap.min.css" var="bootstrapCss" />
<link href="${bootstrapCss}" rel="stylesheet" />
<link href="${coreCss}" rel="stylesheet" />
</head>

<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="#">Soccer Search Engine</a>
		</div>
	</div>
</nav>

<div class="jumbotron">
	<div class="container">

		<div align="left" class="main">
		<br>
			<form class="form-inline" action="search" method="post">
				<div class="form-group">
					<label class="sr-only">Search:</label> <input placeholder="enter soccer query" style="width: 500px"
						class="form-control" type="text" name="searchText"
						value="${searchText}" >
				</div>
				<input class="btn btn-primary" type="submit" name="search"
					value="Search">
			</form>
			<br/>
		</div>
				<div class="lSearch">
			<ul class="nav nav-tabs">
				<li class="active"><a data-toggle="tab" href="#hitSearch">Hits
						Search</a></li>
				<li><a data-toggle="tab" href="#pageRank">VSM + Page Rank</a></li>
				<li><a data-toggle="tab" href="#flatCluster">K-Means</a></li>
				<li><a data-toggle="tab" href="#singleCluster">Agglomerative</a></li>
				<li><a data-toggle="tab" href="#avgCluster">Query Expansion</a></li>

			</ul>
		</div>
		<div class="tab-content searchRes" style="position: fixed; width: 90%; height: 90%; margin-top: 40px; overflow-y: scroll; text-align: left;">
	<div id="hitSearch" class="tab-pane fade in active">
	
				<c:choose>
					<c:when test="${records.size()!=0}">
						<c:forEach items="${records}" var="doc" varStatus="recordIndex">
							<span class="titleres"> <strong><a
									href='<c:out value="${doc.url}"/>'>
									</a></strong>
							</span><br />
							<a href='<c:out value="${doc.url}"/>'><c:out
									value="${doc.url}" /></a>
							<br>
										${doc.contents}
										<br />
							<br />
						</c:forEach>
					</c:when>
					<c:otherwise>
						<p>No Result</p>
					</c:otherwise>
				</c:choose>
				<br>
			</div>
		</div>
	</div>
	
</div>

<spring:url value="/resources/core/css/bootstrap.min.js" var="bootstrapJs" />

<script src="${coreJs}"></script>
<script src="${bootstrapJs}"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

</body>
</html>