<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>IR Project</title>


<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<link
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
	rel="stylesheet" />
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
					<label class="sr-only">Search:</label> <input
						placeholder="enter soccer query" style="width: 500px"
						class="form-control" type="text" name="query" value="${query}">
				</div>
				<input class="btn btn-primary" type="submit" name="search"
					value="Search">
			</form>
			<br />
		</div>
		<div class="lSearch">
			<ul class="nav nav-tabs">
				<li class="active"><a data-toggle="tab" href="#hitSearch">Hits
						Search</a></li>
				<li><a data-toggle="tab" href="#googleSearch">Google Search</a></li>
				<<<<<<< HEAD
				<li><a data-toggle="tab" href="#flatCluster">Flat Cluster</a></li>
				<li><a data-toggle="tab" href="#singleLinkageCluster">Single
						Linkage Clustering</a></li>
				<li><a data-toggle="tab" href="#avgLinkageCluster">Average
						Linkage Clustering</a></li>
				<li><a data-toggle="tab" href="#weightedLinkageCluster">Weighted
						Linkage Clustering</a></li>
				<li><a data-toggle="tab" href="#completeLinkageCluster">Complete
						Linkage Clustering</a></li>
				<li><a data-toggle="tab" href="#completeLinkageCluster">Query
						Expansion</a></li> =======
				<li><a data-toggle="tab" href="#bingSearch">Bing Search</a></li>
				<li><a data-toggle="tab" href="#flatCluster">K-Means</a></li>
				<li><a data-toggle="tab" href="#singleCluster">Agglomerative</a></li>
				<li><a data-toggle="tab" href="#avgCluster">Query Expansion</a></li>
				>>>>>>> 816ec875b7d106a77c1a2d188fbbf5da4c3d80af

			</ul>
		</div>
		<div class="tab-content searchRes"
			style="position: fixed; width: 90%; height: 90%; margin-top: 40px; overflow-y: scroll; text-align: left;">
			<div id="hitSearch" class="tab-pane fade in active">

				<c:choose>
					<c:when test="${DocEntities.size()!=0}">
						<c:forEach items="${DocEntities}" var="doc"
							varStatus="recordIndex">
							<span class="titleres"> <strong><a
									href='<c:out value="${doc.url}"/>'> </a></strong>
							</span>
							<br />
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
			<div id="googleSearch" class="tab-pane fade in active">

				<c:choose>
					<c:when test="${googleDE.size()!=0}">
						<c:forEach items="${googleDE}" var="doc" varStatus="recordIndex">
							<span class="titleres"> <strong><a
									href='<c:out value="${doc.url}"/>'> </a></strong>
							</span>
							<br />
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
			<div id="flatCluster" class="tab-pane fade in active">
				<c:choose>
					<c:when test="${DocEntities.size()!=0}">
						<c:forEach items="${DocEntities}" var="doc"
							varStatus="recordIndex">
							<span class="titleres"> <strong><a
									href='<c:out value="${doc.url}"/>'> </a></strong>
							</span>
							<br />
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

			<div id="bingSearch" class="tab-pane fade in active">

				<c:choose>
					<c:when test="${bingDE.size()!=0}">
						<c:forEach items="${bingDE}" var="doc" varStatus="recordIndex">
							<span class="titleres"> <strong><a
									href='<c:out value="${doc.url}"/>'> </a></strong>
							</span>
							<br />
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

<spring:url value="/resources/core/css/bootstrap.min.js"
	var="bootstrapJs" />

<script src="${coreJs}"></script>
<script src="${bootstrapJs}"></script>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>

</body>
</html>