/**
 * Check if page is the index page and redirect if necessary.
 */
$(document).ready(function(){

		var p = window.location.pathname;

 		if (p.length === 0 || p === "/" || p.match(/^\/?index/)) {
 			window.location.href="summary.html";
    	}
});