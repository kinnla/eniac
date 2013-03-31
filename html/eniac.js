/* JavaScript code for the ENIAC simulation website */

function openWindow(url) {
	window.open(url, '_blank');
}

function startApplet() {
	window.open('v1.0/applet.html', 'applet', 
				'screenX=50, screenY=50, width=200, height=200, toolbar=no, location=no, directories=no, status=yes, menubar=no, scrollbars=yes, resizable=yes, copyhistory=no');
}

function lastModified() {
	document.write("last modified at: " + document.lastModified);
}