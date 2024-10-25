/**
 * 
 */

// no user logged in, show "log out" button
function userLoggedIn() {
	return sessionStorage.user != undefined && sessionStorage.user != '';
}

if (userLoggedIn()) {
	
	document.querySelector('#login-register').innerHTML = `
			<a href="farm_login.html" id="logout">Logout</a>`;
			
	document.getElementById('logout').onclick = function () {
		sessionStorage.user = '';
		window.location.href = 'farm_login.html';
	}
	
	const profileLink = document.querySelector('#nav li:nth-child(2) a');

	//  account type is buyer
	if (JSON.parse(sessionStorage.user).account_type == 0) {		
		profileLink.href = 'farm_profile_buyer.html';
		profileLink.innerHTML = 'Buyer Profile';
	}
	else {
		profileLink.innerHTML = 'Vendor Profile';
	}
}
else {
	document.querySelector('#nav li:nth-child(2)').remove();
}	 