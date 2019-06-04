var absolutepath = getAbsolutePath();
// Obtener el Dominio
var URLdomain = window.location.host;
var propertiesGlobal;
function getAbsolutePath() {
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}


//Validar session
validarSession();
userProp();

function validarSession() {
    document.getElementById('loader').classList.add('is-active');
    document.getElementById("loader").setAttribute("data-text", "Loading Page");
    var data = new FormData();
    data.append("user", "session");

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            var response = JSON.parse(this.responseText);
            if (response.status === "false") {
                window.location.replace("login.html");
            } else {
                document.getElementById("loader").classList.remove("is-active");
                document.getElementById("loader").setAttribute("data-text", "");
            }
        }
    });

    xhr.open("GET", absolutepath + "LogIn");
    xhr.send(data);
}

function userProp() {
    var data = new FormData();
    data.append("action", "userProp");

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {

            var properties = JSON.parse(this.responseText);
            console.log(properties);
            propertiesGlobal = properties;
            if(properties.admin === "admin"){
                document.getElementById('verbioDisplay').style.display = "block";
                document.getElementById('ibmDisplay').style.display = "block";
            }            
            if (properties.real_name !== "") {
                document.getElementById('welcome').innerHTML = "Welcome " + properties.real_name;
                document.getElementById('realName').innerHTML = properties.real_name;

            } else {
                document.getElementById('welcome').innerHTML = "Welcome";
                document.getElementById('realName').innerHTML = "No Name Set";
            }

            if (properties.date !== "") {
                document.getElementById('fecha').innerHTML = properties.date;
            } else {
                document.getElementById('fecha').innerHTML = "No Date Set";
            }
            if(properties.country !== ""){
                document.getElementById('countryProfile').innerHTML = properties.country;
            }else{
                document.getElementById('countryProfile').innerHTML = "No Country Set";
            }
            if (properties.verbio_user !== "") {
                document.getElementById('verbioUser').innerHTML = properties.verbio_user;
                document.getElementById('createVerbioBtn').style.display = "none";
                $("#tbody").empty();
                getAudios();
            } else {
                document.getElementById('verbioUser').innerHTML = "No Verbio User";
                document.getElementById('createVerbioBtn').style.display = "block";
                document.getElementById('recordButton').disabled = true;
            }
            if (properties.phone_active !== "") {
                document.getElementById('verbioPhone').innerHTML = properties.phone_active;
                document.getElementById('phoneProfile').innerHTML = properties.phone_active;
            } else {
                document.getElementById('verbioPhone').innerHTML = "No Phone Set";
                document.getElementById('phoneProfile').innerHTML = "No Phone Set";
            }

            verbioUserInfo();

            document.getElementById('createVerbioBtn').addEventListener('click', function (e) {
                e.preventDefault();

                var user = properties.user;
                var split = user.split("@");

                document.getElementById('verbioUser').innerHTML = split[0] + "_user";
                var verbioPhone = document.getElementById('verbioPhone');
                verbioPhone.innerHTML = "";

                if (propertiesGlobal.phone_active === "") {
                    var inputText = document.createElement("input");
                    inputText.setAttribute('type', 'text');
                    inputText.setAttribute('id', 'inputPhone');
                    inputText.setAttribute('placeholder', '+5215555555555');
                    verbioPhone.appendChild(inputText);
                } else {
                    verbioPhone.innerHTML = propertiesGlobal.phone_active;
                    verbioPhone.setAttribute("id", "verbioPhone");
                }



                document.getElementById('verbioGroupBtn').style.display = "block";

            });



            document.getElementById('cancelVerbioBtn').addEventListener('click', function (e) {
                if (properties.verbio_user !== "") {
                    document.getElementById('verbioUser').innerHTML = properties.verbio_user;
                    document.getElementById('createVerbioBtn').style.display = "none";
                } else {
                    document.getElementById('verbioUser').innerHTML = "No Verbio User";
                    document.getElementById('createVerbioBtn').style.display = "block";
                    document.getElementById('recordButton').disabled = true;
                }
                if (properties.phone_active !== "") {
                    document.getElementById('verbioPhone').innerHTML = properties.phone_active;
                } else {
                    document.getElementById('verbioPhone').innerHTML = "No Phone Set";
                }
                document.getElementById('verbioGroupBtn').style.display = "none";
            });

        }
    });

    xhr.open("POST", absolutepath + "UserProperties");
    xhr.send(data);
}

document.getElementById('helpBtn').addEventListener('click', function (e) {
    e.preventDefault();
    var display = document.getElementById('guide').style.display;
    if (display === "none") {
        document.getElementById('guide').style.display = "block";
    } else {
        document.getElementById('guide').style.display = "none";
    }
});

document.getElementById('homeA').addEventListener('click', function (e) {
    e.preventDefault();
    document.getElementById('bioMetricsA').classList.remove('active');
    document.getElementById('settingsA').classList.remove('active');
    document.getElementById('bioMetrics').classList.remove('active');
    document.getElementById('settings').classList.remove('active');


    document.getElementById('homeA').classList.add("active");
    document.getElementById('home').classList.add("active");


    document.getElementById('frameGuide').src = "Frames/Home.html";



});

document.getElementById('bioMetricsA').addEventListener('click', function (e) {
    document.getElementById('homeA').classList.remove('active');
    document.getElementById('home').classList.remove('active');
    document.getElementById('settingsA').classList.remove('active');
    document.getElementById('settings').classList.remove('active');


    document.getElementById('bioMetricsA').classList.add("active");
    document.getElementById('bioMetrics').classList.add("active");
    document.getElementById('frameGuide').src = "Frames/BioMetrics.html";


});

document.getElementById('settingsA').addEventListener('click', function (e) {
    document.getElementById('homeA').classList.remove('active');
    document.getElementById('home').classList.remove('active');
    document.getElementById('bioMetricsA').classList.remove('active');
    document.getElementById('bioMetrics').classList.remove('active');


    document.getElementById('settingsA').classList.add("active");
    document.getElementById('settings').classList.add("active");
    document.getElementById('frameGuide').src = "Frames/Settings.html";
});


document.getElementById('btnTrain').addEventListener('click', function (e) {
    e.preventDefault();
    document.getElementById('loader').classList.add('is-active');
    document.getElementById("loader").setAttribute("data-text", "Training");
    var data = new FormData();
    data.append("request", "TRAIN");

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            document.getElementById("loader").classList.remove("is-active");
            document.getElementById("loader").setAttribute("data-text", "");
            var response = JSON.parse(this.responseText);
            if (response.response.status === "SUCCESS") {
            	document.getElementById('verbioTrain').innerHTML = "Yes";
                Swal({
                    position: 'center',
                    type: 'success',
                    title: 'Trained',
                    showConfirmButton: false,
                    timer: 3000
                });
            } else {
                Swal({
                    type: 'error',
                    title: 'Error' + response.response.error_message,
                    text: 'Error'
                });
            }
        }
    });

    xhr.open("POST", absolutepath + "VerbioClient");
    xhr.send(data);
});


document.getElementById('closeSession').addEventListener('click', function (e) {
    e.preventDefault();
    document.getElementById('loader').classList.add('is-active');
    document.getElementById("loader").setAttribute("data-text", "Loading Page");
    var data = new FormData();
    data.append("action", "closeSession");

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            document.getElementById("loader").classList.remove("is-active");
            document.getElementById("loader").setAttribute("data-text", "");
            var close = JSON.parse(this.responseText);
            if (close.status === "ok") {
                window.location.replace("login.html");
            }
        }
    });

    xhr.open("POST", absolutepath + "UserProperties");
    xhr.send(data);
});


document.getElementById('saveVerbioBtn').addEventListener('click', function (e) {
    var verbioUserNew = document.getElementById('verbioUser').childNodes[0].nodeValue;
    if (propertiesGlobal.phone_active !== "") {
        var verbioPhoneNew = document.getElementById('verbioPhone').childNodes[0].nodeValue;
    } else {
        var verbioPhoneNew = document.getElementById('inputPhone').value;
    }

    console.log(verbioPhoneNew);
    console.log(document.getElementById('verbioPhone'));
    if (verbioPhoneNew === "") {
        Swal({
            type: 'error',
            title: 'Error',
            text: 'Please enter the phone number'
        });
    } else {
        var data = new FormData();
        data.append("action", "createVerbio");
        data.append("userVerbio", verbioUserNew);
        data.append("phoneActive", verbioPhoneNew);

        var xhr = new XMLHttpRequest();
        xhr.withCredentials = true;
        document.getElementById('loader').classList.add('is-active');
        document.getElementById("loader").setAttribute("data-text", "Saving");
        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                document.getElementById("loader").classList.remove("is-active");
                document.getElementById("loader").setAttribute("data-text", "");
                var response = JSON.parse(this.responseText);
                if (response.status === "updated") {
                    document.getElementById('verbioGroupBtn').style.display = "none";
                    userProp();
                    Swal({
                        position: 'center',
                        type: 'success',
                        title: 'Success',
                        showConfirmButton: false,
                        timer: 3000
                    });

                } else {
                    Swal({
                        type: 'error',
                        title: 'Error',
                        text: 'Error'
                    });
                }
            }
        });

        xhr.open("POST", absolutepath + "UserProperties");
        xhr.send(data);

    }

});


document.getElementById('saveSettings').addEventListener('click', function (e) {

    var first_name = document.getElementById('first_name').value;
    var phone = document.getElementById('phone').value;
    var country = document.getElementById('country').value;
    var encryptedAES = CryptoJS.AES.encrypt(document.getElementById('password').value, "secret");

    var data = new FormData();
    data.append("action", "saveSettings");
    data.append("name", first_name);
    data.append("phone", phone);
    data.append("country", country);
    data.append("password", encryptedAES);

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;
    document.getElementById('loader').classList.add('is-active');
    document.getElementById("loader").setAttribute("data-text", "Saving");

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {

            var result = JSON.parse(this.responseText);
            if (result.status === "updated") {
                document.getElementById("loader").classList.remove("is-active");
                document.getElementById("loader").setAttribute("data-text", "");
                userProp();
                Swal({
                    position: 'center',
                    type: 'success',
                    title: 'Success',
                    showConfirmButton: false,
                    timer: 3000
                });
            } else {
                Swal({
                    type: 'error',
                    title: 'Error',
                    text: 'Error'
                });
            }
        }
    });

    xhr.open("POST", absolutepath + "UserProperties");
    xhr.send(data);

});

document.getElementById('reset').addEventListener('click', function (e) {

    document.getElementById('first_name').value = "";
    document.getElementById('phone').value = "";
    document.getElementById('country').value = "";
    document.getElementById('password').value = "";
});


document.getElementById('btnControlPad').addEventListener('click', function (e) {
    var sitio = "https://" + URLdomain + "/services/AAADEVCONTROLPAD/";
    window.open(sitio);
});

document.getElementById('btnLogger').addEventListener('click', function (e) {
    var sitio = "https://" + URLdomain + "/services/AAADEVLOGGER/";
    window.open(sitio);
});

document.getElementById('btnVantage').addEventListener('click', function (e) {
    var sitio = "https://devavaya.ddns.net/websockets";
    window.open(sitio);
});

document.getElementById('btnVerbiosOficial').addEventListener('click', function (e) {
    var sitio = "https://avaya:DRNUDUsWh5o3uRdQcZ@cloud2.verbio.com/asv/users.php";
    window.open(sitio);
});

document.getElementById('btnIBMOfficial').addEventListener('click', function (e) {
    var sitio = "https://cloud.ibm.com/services/conversation/5be4eadc-9423-4d7b-a429-aaf5b06cd924?env_id=us-south";
    window.open(sitio);
});

function getAudios() {
    var data = null;

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {

            var resultAudioRecordings = JSON.parse(this.responseText);
            if (resultAudioRecordings.status === "empty") {

            } else {

                var tbody = document.getElementById('tbody');

                for (var i = 0; i < resultAudioRecordings.Results.length; i++) {
                    var tr = document.createElement('TR');
                    for (var j = 1; j <= 4; j++) {
                        var td = document.createElement('TD');
                        let nuevoaudio = document.createElement("AUDIO");
                        if (j === 3) {

                            var dateTextNode = document.createTextNode(resultAudioRecordings.Results[i].Date);
                            td.appendChild(dateTextNode);


                        }
                        if (j === 2) {
                            var hourTextNode = document.createTextNode(resultAudioRecordings.Results[i].Hour);
                            td.appendChild(hourTextNode);
                        }
                        if (j === 1) {
                            var tdfielNameTextNode = document.createTextNode(resultAudioRecordings.Results[i].File.toString());
                            td.appendChild(tdfielNameTextNode);
                        }

                        if (j === 4) {

                            var info2 = document.createElement("i");
                            info2.setAttribute("type", "button");
                            info2.setAttribute("id", resultAudioRecordings.Results[i].File);
                            info2.setAttribute("onclick", "copyPath()");
                            info2.setAttribute("title", "Copiar al ClipBoard url audio");
                            info2.setAttribute("class", "fas fa-file-audio");
                            info2.setAttribute("style", "cursor: pointer; cursor: hand;");
                            td.appendChild(info2);
                        }



                        tr.appendChild(td);

                    }
                    sleep(500);
                    tbody.appendChild(tr);
                }

            }



        }
    });

    xhr.open("POST", absolutepath + "Audios");
    xhr.send(data);
}

function sleep(miliseconds) {
    var currentTime = new Date().getTime();

    while (currentTime + miliseconds >= new Date().getTime()) {
    }
}

function copyPath() {
    let tds = event.path[0].id;

    var el = document.createElement('textarea');
    el.value = "http://" + URLdomain + "/services/AAADEVLOGGER/FileSaveServlet/web/VerbioAudios/" + tds;
    document.body.appendChild(el);
    el.select();
    document.execCommand('copy');
    document.body.removeChild(el);
}

function verbioUserInfo() {
    var data = new FormData();
    data.append("request", "USER_INFO");
    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            console.log(this.responseText);
            var response = JSON.parse(this.responseText);

            if (response.response.result.verbio_result.score === "" && response.response.status === "ERROR") {
                document.getElementById('verbioTrain').innerHTML = "No";
                document.getElementById('totalAudioRecordings').innerHTML = "0";
            }
            if (response.response.result.verbio_result.score !== "0" && response.response.status === "SUCCESS") {
                document.getElementById('verbioTrain').innerHTML = "Yes";
                document.getElementById('totalAudioRecordings').innerHTML = response.response.result.verbio_result.result;
                document.getElementById('divbtnTrain').style.display = "block";
            }
        }
    });

    xhr.open("POST", absolutepath + "VerbioClient");
    xhr.send(data);
}