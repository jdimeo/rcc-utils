$(function() {
	$('.toggle-type').each(function() {
		$(this).click(function(e) {
			$('.' + $(this).attr('name')).toggle();
		});
	});
	
	$('.toggle-gift').each(function() {
		$(this).click(function(e) {
			$('.' + $(this).attr('name')).toggle();
		});
	});
	
	$('.toggle-role').each(function() {
		$(this).click(function(e) {
			$('.' + $(this).attr('name')).toggle();
		});
	});
});