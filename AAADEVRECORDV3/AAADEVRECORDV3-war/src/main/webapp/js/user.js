	/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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

            if (properties.admin === "admin") {
                document.getElementById('verbioDisplay').style.display = "block";
                document.getElementById('ibmDisplay').style.display = "block";
            }

            if (properties.real_name !== "") {
                document.getElementById('welcome').innerHTML = "Welcome " + properties.real_name;
                document.getElementById('realName').innerHTML = properties.real_name;

            } else {
                var user = properties.user;
                var split = user.split("@");
                document.getElementById('welcome').innerHTML = "Welcome " + split[0];
                document.getElementById('realName').innerHTML = "No Name Set";
            }

            if (properties.date !== "") {
                document.getElementById('fecha').innerHTML = properties.date;
            } else {
                document.getElementById('fecha').innerHTML = "No Date Set";
            }

            if (properties.country !== "") {
                document.getElementById('countryProfile').innerHTML = properties.country;
            } else {
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

            //MODIFICACIÓN 10 DE JULIO
            console.log(properties.cuentaCajaSocial);
            if (properties.cuentaCajaSocial !== undefined) {
                console.log("Existe la cuenta");
                var div = document.getElementById("cajaNacionalDiv");
                div.innerHTML = "";
                //CARD ACCOUNT
                var divCard = document.createElement('DIV');
                divCard.setAttribute('class', 'card border-dark mb-3');
                divCard.setAttribute('style', 'max-width: 50rem;');
                var divHeader = document.createElement('DIV');
                divHeader.setAttribute('class', 'card-header');
                var iconAccount = document.createElement('I');
                iconAccount.setAttribute('class', 'glyphicon glyphicon-user');
                iconAccount.setAttribute('style', 'font-size: 20px;');

                var Text = document.createElement('H3');
                Text.setAttribute('style', 'display:inline;');
                var TextNode = document.createTextNode(' Numero de cuenta único');
                Text.appendChild(TextNode);

                var divBody = document.createElement('DIV');
                divBody.setAttribute('class', 'card-body text-dark');

                var numeroDeCuenta = document.createElement('H3');
                var numeroDeCuentaTextNode = document.createTextNode(properties.cuentaCajaSocial);
                numeroDeCuenta.appendChild(numeroDeCuentaTextNode);

                var editIcon = document.createElement('I');
                editIcon.setAttribute('class', 'glyphicon glyphicon-refresh');
                editIcon.setAttribute('style', 'padding-left: 183px; cursor : pointer;');
                editIcon.setAttribute('onclick', 'editCuenta()');
                numeroDeCuenta.appendChild(editIcon);
                divBody.appendChild(numeroDeCuenta);


                divHeader.appendChild(iconAccount);
                divHeader.appendChild(Text);


                divCard.appendChild(divHeader);
                divCard.appendChild(divBody);
                /////////////////////////////////////////
                var divCardSaldo = document.createElement('DIV');
                divCardSaldo.setAttribute('class', 'card border-dark mb-3');
                divCardSaldo.setAttribute('style', 'max-width: 50rem;');
                var divHeader = document.createElement('DIV');
                divHeader.setAttribute('class', 'card-header');
                var iconAccount = document.createElement('I');
                iconAccount.setAttribute('class', 'glyphicon glyphicon-usd');
                iconAccount.setAttribute('style', 'font-size: 20px;');

                var Text = document.createElement('H3');
                Text.setAttribute('style', 'display:inline;');
                var TextNode = document.createTextNode(' Saldo Actual');
                Text.appendChild(TextNode);

                var divBody = document.createElement('DIV');
                divBody.setAttribute('class', 'card-body text-dark');

                var numeroDeCuenta = document.createElement('H3');
                var numeroDeCuentaTextNode = document.createTextNode("$ " + properties.saldoCajaSocial);
                numeroDeCuenta.appendChild(numeroDeCuentaTextNode);

                var editIcon = document.createElement('I');
                editIcon.setAttribute('class', 'glyphicon glyphicon-edit');
                editIcon.setAttribute('style', 'padding-left: 183px; cursor : pointer;');
                editIcon.setAttribute('onclick', 'editSaldo(this)');
                numeroDeCuenta.appendChild(editIcon);
                divBody.appendChild(numeroDeCuenta);


                divHeader.appendChild(iconAccount);
                divHeader.appendChild(Text);


                divCardSaldo.appendChild(divHeader);
                divCardSaldo.appendChild(divBody);

///////////////////////////////////////////////
                var divHistorico = document.createElement('DIV');
                divHistorico.setAttribute('class', 'card border-dark mb-3');
                divHistorico.setAttribute('style', 'max-width: 64rem;');
                var divHeader = document.createElement('DIV');
                divHeader.setAttribute('class', 'card-header');
                var iconAccount = document.createElement('I');
                iconAccount.setAttribute('class', 'glyphicon glyphicon-usd');
                iconAccount.setAttribute('style', 'font-size: 20px;');

                var Text = document.createElement('H3');
                Text.setAttribute('style', 'display:inline;');
                var TextNode = document.createTextNode(' Movements');
                Text.appendChild(TextNode);

                var iconPlus = document.createElement('I');
                iconPlus.setAttribute('class', 'glyphicon glyphicon-plus-sign');
                iconPlus.setAttribute('style', 'font-size: 20px; display: inline-block; padding-left: 255px;');
                iconPlus.setAttribute('onclick', 'addMovement(this)');
                var iconDecrees = document.createElement('I');
                iconDecrees.setAttribute('class', 'glyphicon glyphicon-minus-sign');
                iconDecrees.setAttribute('style', 'font-size: 20px; display: inline-block;padding-left: 20px;');
                iconDecrees.setAttribute('onclick', 'lessMovement(this)');
                var divBody = document.createElement('DIV');
                divBody.setAttribute('class', 'card-body text-dark');
                console.log(properties.historicoMovimientos.length);
                for (var i = 0; i <= properties.historicoMovimientos.length - 1; i++) {
                    console.log(i);
                    console.log(properties.historicoMovimientos[i]);
                    var numeroDeCuenta = document.createElement('H3');
                    var numeroDeCuentaTextNode = document.createTextNode(properties.historicoMovimientos[i]);
                    numeroDeCuenta.appendChild(numeroDeCuentaTextNode);
                    divBody.appendChild(numeroDeCuenta);
                }







                divHeader.appendChild(iconAccount);
                divHeader.appendChild(Text);
                divHeader.appendChild(iconPlus);
                divHeader.appendChild(iconDecrees);


                divHistorico.appendChild(divHeader);
                divHistorico.appendChild(divBody);

///////////////////////////////////////////////
                div.appendChild(divCard);
                div.appendChild(divCardSaldo);
                div.appendChild(divHistorico);

            } else {
                console.log("NO Existe cuenta social");
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
    document.getElementById('cajaNacionalA').classList.remove("active");
    document.getElementById('cajaNacional').classList.remove("active");

    document.getElementById('homeA').classList.add("active");
    document.getElementById('home').classList.add("active");


    document.getElementById('frameGuide').src = "Frames/Home.html";



});

document.getElementById('bioMetricsA').addEventListener('click', function (e) {
    document.getElementById('homeA').classList.remove('active');
    document.getElementById('home').classList.remove('active');
    document.getElementById('settingsA').classList.remove('active');
    document.getElementById('settings').classList.remove('active');
    document.getElementById('cajaNacionalA').classList.remove("active");
    document.getElementById('cajaNacional').classList.remove("active");

    document.getElementById('bioMetricsA').classList.add("active");
    document.getElementById('bioMetrics').classList.add("active");
    document.getElementById('frameGuide').src = "Frames/BioMetrics.html";


});

document.getElementById('settingsA').addEventListener('click', function (e) {
    document.getElementById('homeA').classList.remove('active');
    document.getElementById('home').classList.remove('active');
    document.getElementById('bioMetricsA').classList.remove('active');
    document.getElementById('bioMetrics').classList.remove('active');
    document.getElementById('cajaNacionalA').classList.remove("active");
    document.getElementById('cajaNacional').classList.remove("active");

    document.getElementById('settingsA').classList.add("active");
    document.getElementById('settings').classList.add("active");
    document.getElementById('frameGuide').src = "Frames/Settings.html";
});

document.getElementById('cajaNacionalA').addEventListener('click', function (e) {
    document.getElementById('homeA').classList.remove('active');
    document.getElementById('home').classList.remove('active');
    document.getElementById('bioMetricsA').classList.remove('active');
    document.getElementById('bioMetrics').classList.remove('active');
    document.getElementById('settingsA').classList.remove("active");
    document.getElementById('settings').classList.remove("active");

    document.getElementById('cajaNacionalA').classList.add("active");
    document.getElementById('cajaNacional').classList.add("active");

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
                console.log(resultAudioRecordings);
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
                //<button type="button" class="btn btn-info">Info</button>
                document.getElementById('divbtnTrain').style.display = "block";
            }
        }
    });

    xhr.open("POST", absolutepath + "VerbioClient");
    xhr.send(data);
}




/*
 *  MODIFICACIÓN 10 DE JULIO 2019
 */

document.getElementById('createCajaNacional').addEventListener('click', function (e) {
    e.preventDefault();
    console.log("Create Caja Nacional");
    var data = new FormData();
    data.append("action", "createCajaNacional");

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            console.log(this.responseText);
            var jsonObj = JSON.parse(this.responseText);
            if (jsonObj.status === "ok") {
                userProp();
                Swal({
                    position: 'center',
                    type: 'success',
                    title: 'Account created',
                    showConfirmButton: false,
                    timer: 3000
                });
            }
            if (jsonObj.error === "error") {
                Swal({
                    type: 'error',
                    title: 'Error creating the account.',
                    text: 'Error'
                });
            }
        }
    });

    xhr.open("POST", absolutepath + "UserProperties");
    xhr.send(data);


});

function editCuenta() {
    var data = new FormData();
    data.append("action", "refreshAccount");

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            var jsonObj = JSON.parse(this.responseText);
            console.log(this.responseText);
            if (jsonObj.status === "ok") {
                userProp();
                Swal({
                    position: 'center',
                    type: 'success',
                    title: 'Updated account.',
                    showConfirmButton: false,
                    timer: 3000
                });
            }
            if (jsonObj.error === "error") {
                Swal({
                    type: 'error',
                    title: 'Error wanting to update the account.',
                    text: 'Error'
                });
            }
        }
    });

    xhr.open("POST", absolutepath + "UserProperties");
    xhr.send(data);
}


function editSaldo(_this) {
    console.log("Edit cuenta");
    console.log(_this.parentElement);
    var H3Elemnt = _this.parentElement;
    H3Elemnt.innerHTML = "";
    var divElement = document.createElement('DIV');
    divElement.setAttribute('class', 'input-group col-sm-12');
    var inputElement = document.createElement('INPUT');
    inputElement.setAttribute('type', 'text');
    inputElement.setAttribute('class', 'form-control');
    inputElement.setAttribute('placeholder', 'New unsigned dollar balance');
    var divButton = document.createElement('DIV');
    divButton.setAttribute('class', 'input-group-btn');

    var button = document.createElement('BUTTON');
    button.setAttribute('class', 'btn btn-success');
    button.setAttribute('onClick', 'nuevoPrecio(this)');
    var icon = document.createElement('I');
    icon.setAttribute('class', 'glyphicon glyphicon-ok-circle');
    button.appendChild(icon);
    divButton.appendChild(button);


    divElement.appendChild(inputElement);
    divElement.appendChild(divButton);

    H3Elemnt.appendChild(divElement);

}

function nuevoPrecio(_this) {
    console.log(_this.parentElement.parentElement.children[0].value);
    var newBalance = _this.parentElement.parentElement.children[0].value;

    if (newBalance === "") {
        userProp();
    } else {
        var data = new FormData();
        data.append("action", "newBalance");
        data.append("balance", newBalance);

        var xhr = new XMLHttpRequest();
        xhr.withCredentials = true;

        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                var jsonObj = JSON.parse(this.responseText);
                console.log(this.responseText);
                if (jsonObj.status === "ok") {
                    userProp();
                    Swal({
                        position: 'center',
                        type: 'success',
                        title: 'Updated New Balance.',
                        showConfirmButton: false,
                        timer: 3000
                    });
                }
                if (jsonObj.error === "error") {
                    Swal({
                        type: 'error',
                        title: 'Error Updating New Balance.',
                        text: 'Error'
                    });
                }
            }
        });

        xhr.open("POST", absolutepath + "UserProperties");
        xhr.send(data);
    }

}

function addMovement(_this) {
    console.log('Add Movement');
    console.log(_this.parentElement.parentElement.children[1]);
    var cardDivBody = _this.parentElement.parentElement.children[1];

    var divElement = document.createElement('DIV');
    divElement.setAttribute('class', 'input-group col-sm-12');
    var inputElement = document.createElement('INPUT');
    inputElement.setAttribute('type', 'text');
    inputElement.setAttribute('class', 'form-control');
    inputElement.setAttribute('placeholder', 'NEW MOVEMENT, UNDATED AND IN ENGLISH.');
    var divButton = document.createElement('DIV');
    divButton.setAttribute('class', 'input-group-btn');

    var button = document.createElement('BUTTON');
    button.setAttribute('class', 'btn btn-success');
    button.setAttribute('onClick', 'nuevoMovimiento(this)');
    var icon = document.createElement('I');
    icon.setAttribute('class', 'glyphicon glyphicon-upload');
    button.appendChild(icon);
    divButton.appendChild(button);


    divElement.appendChild(inputElement);
    divElement.appendChild(divButton);

    cardDivBody.appendChild(divElement);

}

function nuevoMovimiento(_this) {
    console.log(_this.parentElement.parentElement.children[0].value);
    var newMovement = _this.parentElement.parentElement.children[0].value;
    if (newMovement === "") {
        userProp();
    } else {
        var data = new FormData();
        data.append("action", "addMovement");
        data.append("movement", newMovement);

        var xhr = new XMLHttpRequest();
        xhr.withCredentials = true;

        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                var jsonObj = JSON.parse(this.responseText);
                console.log(this.responseText);
                if (jsonObj.status === "ok") {
                    userProp();
                    Swal({
                        position: 'center',
                        type: 'success',
                        title: 'Updated New Movement.',
                        showConfirmButton: false,
                        timer: 3000
                    });
                }
                if (jsonObj.error === "error") {
                    Swal({
                        type: 'error',
                        title: 'Error Updating New Movement.',
                        text: 'Error'
                    });
                }
            }
        });

        xhr.open("POST", absolutepath + "UserProperties");
        xhr.send(data);
    }
}

function lessMovement(_this) {
    console.log(_this.parentElement.parentElement.children[1].children);
    var newBalance = _this.parentElement.parentElement.children[1].children;

    for (var i = 0; i <= newBalance.length - 1; i++) {
        var iconDelete = document.createElement('I');
        iconDelete.setAttribute('class', 'glyphicon glyphicon-remove');
        iconDelete.setAttribute('style', 'color : red;');
        iconDelete.setAttribute('onclick', 'deleteMovement(this)');
        newBalance[i].appendChild(iconDelete);
    }

}

function deleteMovement(_this) {
    console.log("DElete Movement");
    console.log(_this.parentElement.innerText);
    var movement = _this.parentElement.innerText;
    var data = new FormData();
    data.append("action", "deleteMovement");
    data.append("movement", movement);

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = false;

    xhr.addEventListener("readystatechange", function () {
        if (this.readyState === 4) {
            var jsonObj = JSON.parse(this.responseText);
            console.log(this.responseText);
            if (jsonObj.status === "ok") {
                userProp();
                Swal({
                    position: 'center',
                    type: 'success',
                    title: 'Deleted Movement.',
                    showConfirmButton: false,
                    timer: 3000
                });
            }
            if (jsonObj.error === "error") {
                Swal({
                    type: 'error',
                    title: 'Error Deleted Movement.',
                    text: 'Error'
                });
            }
        }
    });

    xhr.open("POST", absolutepath + "UserProperties");
    xhr.send(data);
}