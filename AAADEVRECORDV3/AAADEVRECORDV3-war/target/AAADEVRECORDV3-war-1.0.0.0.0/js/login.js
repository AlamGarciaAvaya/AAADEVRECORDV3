/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

console.log("Login.js");

var absolutepath = getAbsolutePath();
function getAbsolutePath() {
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}



document.getElementById('submitbtn').addEventListener('click', function (e) {
    console.log("Submit");
    var email = validateEmail(document.getElementById('email').value);

    if (email !== true) {
        Swal({
            type: 'error',
            title: 'Error',
            text: 'Please enter the email correctly'
        });
    }
    if (document.getElementById('pass').value === "" && email !== true) {
        Swal({
            type: 'error',
            title: 'Error',
            text: 'Please enter the password'
        });
    } else {
        var encryptedAES = CryptoJS.AES.encrypt(document.getElementById('pass').value, "secret");
        var data = new FormData();
        data.append("Email", document.getElementById('email').value);
        data.append("Pass", encryptedAES);

        var xhr = new XMLHttpRequest();
        xhr.withCredentials = true;

        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                var result = JSON.parse(this.responseText);
                if (result.status === "ok") {
                    makePost(document.getElementById('email').value);
                    window.location.replace(absolutepath + "User.html");
                } else {
                    Swal({
                        type: 'error',
                        title: 'Error',
                        text: 'Please retry logon'
                    });
                }
            }
        });

        xhr.open("POST", absolutepath + "LogIn");

        xhr.send(data);
    }

});

document.getElementById('forgotten').addEventListener('click', function (e) {
    console.log("Forgotten");
});


function validateEmail(email) {
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

function makePost(usuario) {
    var data = null;
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {

        }
    });

    xhr.open("POST", "https://breeze2-132.collaboratory.avaya.com/services/AAADEVLOGGER/UserAdminAccess?usuario=" + usuario);

    xhr.send(data);
}
