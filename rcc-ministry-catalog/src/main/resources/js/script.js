$(function() {
	$('.toggle-type').each(function() {
		$(this).click(function(e) {
			$('.' + $(this).attr('name')).toggle();
		});
	});
});