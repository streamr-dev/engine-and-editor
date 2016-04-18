<html>
<head>
	<meta name="layout" content="main" />
	<title>User guide</title>

	<r:require module="user-guide"/>
	<r:require module="codemirror"/>

	<r:script>
		// Draws sidebar with scrollspy. If h1 -> first level title. If h2 -> second level title.
		// Scrollspy uses only titles to track scrolling. div.help-text elements are not significant for the scrollspy.
		new UserGuide("#module-help-tree", "#sidebar")
	</r:script>

	<r:script>
		$(document).ready(function() {
			var textAreaElements = document.querySelectorAll("textarea");
			for (var i=0; i < textAreaElements.length; ++i) {
				CodeMirror.fromTextArea(textAreaElements[i]);
			}
		});
	</r:script>

</head>
<body class="user-guide">

<ui:flashMessage/>

<ui:breadcrumb>
	<g:render template="/help/breadcrumb"/>
</ui:breadcrumb>

<div class="row">
	<div class="col-sm-12">
		<div class="scrollspy-wrapper col-md-9" id="module-help-tree">

			<markdown:renderHtml template="userGuide/introduction" />
			<hr style="width: 70%; border-top: #E9570F solid 2px;  margin-top: 30px; margin-bottom: 30px">

			<!-- <markdown:renderHtml template="userGuide/real_life_use_cases" /> -->

			<markdown:renderHtml template="userGuide/getting_started" />
			<hr style="width: 70%; border-top: #E9570F solid 2px;  margin-top: 30px; margin-bottom: 30px">

			<markdown:renderHtml template="userGuide/streams" />
			<hr style="width: 70%; border-top: #E9570F solid 2px;  margin-top: 30px; margin-bottom: 30px">

			<markdown:renderHtml template="userGuide/modules" />
			<hr style="width: 70%; border-top: #E9570F solid 2px;  margin-top: 30px; margin-bottom: 30px">

			<markdown:renderHtml template="userGuide/services" />
			<hr style="width: 70%; border-top: #E9570F solid 2px;  margin-top: 30px; margin-bottom: 30px">


			<markdown:renderHtml template="userGuide/dashboards" />
			<hr style="width: 70%; border-top: #E9570F solid 2px;  margin-top: 30px; margin-bottom: 30px">

			<!-- <markdown:renderHtml template="userGuide/embedded_widgets" /> -->

			<markdown:renderHtml template="userGuide/extensions" />
			<hr style="width: 70%; border-top: #E9570F solid 2px;  margin-top: 30px; margin-bottom: 30px">

			<markdown:renderHtml template="userGuide/life_outside_streamr" />
			<hr style="width: 70%; border-top: #E9570F solid 2px;  margin-top: 30px; margin-bottom: 30px">

			<!-- <markdown:renderHtml template="userGuide/sharing_and_collaboration" /> -->
			<!-- <markdown:renderHtml template="userGuide/streaming_data_cookbook" /> -->
			<!-- <markdown:renderHtml template="userGuide/glossary" /> -->

		</div>


		<!-- Don't remove this div -->
		<div class="col-xs-0 col-sm-0 col-md-3" id="sidebar"></div>
	</div>
</div>
</body>
</html>
