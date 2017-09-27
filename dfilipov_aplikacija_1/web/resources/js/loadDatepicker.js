$(document).ready(function() {
	
	$(".datepicker").each(function () {
		$(this).datepicker({
			dateFormat: "dd.mm.yy.",
			changeMonth: true,
			changeYear: true,
			minDate: "-100Y",
			maxDate: "+1M"
		});
	});
	
});