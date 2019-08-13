/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
console.log("Control.html");

/*
 * Variables
 */
var global = [];
var absolutepath = getAbsolutePath();
/*
 * Event Listeners
 */

document.getElementById('createDirectory').addEventListener('click', function (e) {
    console.log("Create New Folder");
    Swal.fire({
        title: '<strong>Please Put de Directory Name</strong>',
        type: 'info',
        html: '<strong>Directory Name</strong>' +
                '<hr>' +
                '<input type="text" id="inputTextDirectoryName" name="newFile">' +
                '<hr>',

        showCloseButton: true,
        showCancelButton: true,
        focusConfirm: false,
        confirmButtonText:
                '<i class="glyphicon glyphicon-thumbs-up" id="upload"></i> OK!',
        cancelButtonText:
                '<i class="glyphicon glyphicon-thumbs-down id="cancel"></i>'
    }).then((result) => {
        if (result.value) {
            creteNewFile();
        }
    });

    function creteNewFile() {
        console.log("Create File");
        console.log();
        var data = new FormData();
        data.append("action", "createFile");
        data.append("directoryName", document.getElementById('inputTextDirectoryName').value);

        var xhr = new XMLHttpRequest();
        xhr.withCredentials = false;

        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                console.log(this.responseText);
                var jsonObjectCreateDirectory = JSON.parse(this.responseText);
                if (jsonObjectCreateDirectory.status === "ok") {
                    Swal.fire(
                            'Good job!',
                            'The folder was created successfully!',
                            'success'
                            );
                    location.reload();
                }
                if (jsonObjectCreateDirectory.status === "the file already exists") {
                    Swal.fire(
                            'Opps!',
                            'The file already exists',
                            'error'
                            );

                }
                if (jsonObjectCreateDirectory.status === "error") {
                    Swal.fire(
                            'Opps!',
                            'Error creating directory',
                            'error'
                            );

                }
            }
        });

        xhr.open("POST", absolutepath + "Audios");
        xhr.send(data);
    }

});

/*
 * Funciones
 */

function uploadAudio(_this) {


    console.log(_this.parentElement.children[0].id);
    var folderName = _this.parentElement.children[0].id;
    Swal.fire({
        title: '<strong>Select WAV File</strong>',
        type: 'info',
        html: '<strong>Upload In Folder ' + folderName + '</strong>' +
                '<hr>' +
                '<input type="file" id="inputNewFile" name="newFile" accept="audio/wav">' +
                '<hr>',

        showCloseButton: true,
        showCancelButton: true,
        focusConfirm: false,
        confirmButtonText:
                '<i class="glyphicon glyphicon-thumbs-up" id="upload"></i> OK!',
        cancelButtonText:
                '<i class="glyphicon glyphicon-thumbs-down id="cancel"></i>'
    }).then((result) => {
        if (result.value) {
            uploadConfirm();
        }
    });

    function uploadConfirm() {
        console.log("Confirm");

        var valorFileUploaded = document.getElementById('inputNewFile');
        console.log(valorFileUploaded.files[0]);

        if (validate_fileupload(valorFileUploaded.value)) {
            var reader = new FileReader();
            reader.addEventListener("load", function () {
                console.log("Reader");
                var fileName = valorFileUploaded.files[0].name;
                var data = new FormData();
                data.append("action", "uploadFile");
                data.append("File_bin", reader.result);
                data.append("File_Name", fileName);
                data.append("File_Folder_Name", folderName);
                var xhr = new XMLHttpRequest();
                xhr.withCredentials = false;
                xhr.addEventListener("readystatechange", function () {
                    if (this.readyState === 4) {
                        var jsonObject = JSON.parse(this.responseText);
                        console.log(jsonObject);
                        if (jsonObject.status === "ok") {
                            function sleep(ms) {
                                return new Promise(resolve => setTimeout(resolve, ms));
                            }
                            async function demo() {
                                console.log('Taking a break...');
                                await sleep(2000);
                                console.log('Two seconds later, showing sleep in a loop...');
                                var newAudio = document.getElementById(folderName);
                                var panelChildrenGroup = newAudio.parentNode.parentNode.parentNode.children;
                                for (var i = 0; i < panelChildrenGroup.length; i++) {
                                    if (panelChildrenGroup[i].id === jsonObject.language + "," + jsonObject.folderName) {
                                        var folderInsertAudio = document.getElementById(panelChildrenGroup[i].id);
                                        if (folderInsertAudio.children.length === 0) {
                                            console.log("No tiene elementos");
                                            //CREACIÖN DEL PANEL BODY DIV
                                            var divPanelBody = document.createElement('DIV');
                                            divPanelBody.setAttribute("class", "panel-body");
                                            divPanelBody.setAttribute("style", "padding: 0px; border: 1px solid gray;");
                                            //CREACION DEL ROW 
                                            var rowElement = document.createElement('DIV');
                                            rowElement.setAttribute("class", "row");
                                            //CREACION DE DIV NOMBRE WAV
                                            var divFileName = document.createElement('DIV');
                                            divFileName.setAttribute("class", "col-md-5 text-center");
                                            var h3FileName = document.createElement('H3');
                                            h3FileName.setAttribute("style", "font-size: 15px;");
                                            var FileNameTextNode = document.createTextNode(jsonObject.fileName);
                                            h3FileName.appendChild(FileNameTextNode);
                                            divFileName.appendChild(h3FileName);
                                            //CREACION DIV AUDIO
                                            var divAudio = document.createElement('DIV');
                                            divAudio.setAttribute("class", "col-md-6");
                                            var audioElement = document.createElement('AUDIO');
                                            audioElement.setAttribute("controls", "true");
                                            audioElement.setAttribute("style", "width:100%");
                                            var sourceElement = document.createElement('SOURCE');
                                            sourceElement.setAttribute("src", "../Audios/" + jsonObject.folderName.toString() + "/" + jsonObject.language.toString() + "/" + jsonObject.fileName.toString());
                                            sourceElement.setAttribute("type", "audio/wav");
                                            audioElement.appendChild(sourceElement);
                                            divAudio.appendChild(audioElement);
                                            //CREACION DIV CHECKBOX
                                            var divCheckBox = document.createElement('DIV');
                                            divCheckBox.setAttribute("class", "col-md-1 text-center");
                                            divCheckBox.setAttribute("style", "padding-top: 17px;");
                                            var inputCheckBox = document.createElement('INPUT');
                                            inputCheckBox.setAttribute("type", "checkbox");
                                            inputCheckBox.setAttribute("onclick", "checkBox(this)");
                                            divCheckBox.appendChild(inputCheckBox);
                                            //APPEND DIV IN ROW
                                            rowElement.appendChild(divFileName);
                                            rowElement.appendChild(divAudio);
                                            rowElement.appendChild(divCheckBox);
                                            //APPEND ROW IN PANEL BODY
                                            divPanelBody.appendChild(rowElement);
                                            //APPEND PANEL BODY IN FOLDER
                                            folderInsertAudio.appendChild(divPanelBody);
                                        } else {
                                            console.log("Si tiene elementos");
//                                        var divPanelBody = folderInsertAudio.children[0];
                                            console.log(folderInsertAudio);
                                            for (var j = 0; j < folderInsertAudio.children.length; j++) {
                                                if (folderInsertAudio.children[j].children[0].children[0].innerText === jsonObject.fileName) {
                                                    console.log("Equals");
                                                    console.log(folderInsertAudio.children);
                                                    var element = folderInsertAudio.children[j];
                                                    element.parentNode.removeChild(element);
                                                }
                                            }
                                            //CREACIÖN DEL PANEL BODY DIV
                                            var divPanelBody = document.createElement('DIV');
                                            divPanelBody.setAttribute("class", "panel-body");
                                            divPanelBody.setAttribute("style", "padding: 0px; border: 1px solid gray;");
                                            //CREACION DEL ROW 
                                            var rowElement = document.createElement('DIV');
                                            rowElement.setAttribute("class", "row");
                                            //CREACION DE DIV NOMBRE WAV
                                            var divFileName = document.createElement('DIV');
                                            divFileName.setAttribute("class", "col-md-5 text-center");
                                            var h3FileName = document.createElement('H3');
                                            h3FileName.setAttribute("style", "font-size: 15px;");
                                            var FileNameTextNode = document.createTextNode(jsonObject.fileName);
                                            h3FileName.appendChild(FileNameTextNode);
                                            divFileName.appendChild(h3FileName);
                                            //CREACION DIV AUDIO
                                            var divAudio = document.createElement('DIV');
                                            divAudio.setAttribute("class", "col-md-6");
                                            var audioElement = document.createElement('AUDIO');
                                            audioElement.setAttribute("controls", "true");
                                            audioElement.setAttribute("style", "width:100%");
                                            var sourceElement = document.createElement('SOURCE');
                                            console.log("../Audios/" + jsonObject.folderName.toString() + "/" + jsonObject.language.toString() + "/" + jsonObject.fileName.toString());
                                            sourceElement.setAttribute("src", "../Audios/" + jsonObject.folderName.toString() + "/" + jsonObject.language.toString() + "/" + jsonObject.fileName.toString());
                                            sourceElement.setAttribute("type", "audio/wav");
                                            audioElement.appendChild(sourceElement);
                                            divAudio.appendChild(audioElement);
                                            //CREACION DIV CHECKBOX
                                            var divCheckBox = document.createElement('DIV');
                                            divCheckBox.setAttribute("class", "col-md-1 text-center");
                                            divCheckBox.setAttribute("style", "padding-top: 17px;");
                                            var inputCheckBox = document.createElement('INPUT');
                                            inputCheckBox.setAttribute("type", "checkbox");
                                            inputCheckBox.setAttribute("onclick", "checkBox(this)");
                                            divCheckBox.appendChild(inputCheckBox);
                                            //APPEND DIV IN ROW
                                            rowElement.appendChild(divFileName);
                                            rowElement.appendChild(divAudio);
                                            rowElement.appendChild(divCheckBox);
                                            //APPEND ROW IN PANEL BODY
                                            divPanelBody.appendChild(rowElement);
                                            //APPEND PANEL BODY IN FOLDER
                                            folderInsertAudio.appendChild(divPanelBody);
                                        }

                                    }
                                }

                            }

                            demo();

                            Swal.fire(
                                    'Good job!',
                                    'The audio has been inserted correctly!',
                                    'success'
                                    );
                        }
                        if (jsonObject.status === "error") {
                            Swal.fire(
                                    'Opps!',
                                    'Error inserting audio',
                                    'error'
                                    );
                        }
                    }
                });

                xhr.open("POST", absolutepath + "Audios");
                xhr.send(data);
            }, false);


            if (valorFileUploaded.files[0]) {
                reader.readAsDataURL(valorFileUploaded.files[0]);
            }


        } else {
            alert("El archivo no es .WAV");
        }

    }



    function validate_fileupload(fileName)
    {
        var allowed_extensions = new Array("wav");
        var file_extension = fileName.split('.').pop().toLowerCase(); // split function will split the filename by dot(.), and pop function will pop the last element from the array which will give you the extension as well. If there will be no extension then it will return the filename.

        for (var i = 0; i <= allowed_extensions.length; i++)
        {
            if (allowed_extensions[i] === file_extension)
            {
                return true; // valid file extension
            }
        }

        return false;
    }

}


function deleteAudios() {

    if (global.length === 0) {
        Swal.fire({
            type: 'error',
            title: 'No audios selected to Delete'
        });
    } else {
        var stringBuilder = [];
        for (var i = 0; i < global.length; i++) {
            var jsonGlobal = global[i];
            for (var key in jsonGlobal) {
                stringBuilder.push(jsonGlobal[key]);
                stringBuilder.push(" ");
            }
        }

        Swal.fire({
            title: 'Surely you want to erase the following audios?',
            text: "" + stringBuilder + "",
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it!'
        }).then((result) => {
            if (result.value) {
                makePostDelete();
            }
        });
    }

    function makePostDelete() {
        var data = new FormData();
        data.append("action", "deleteFile");
        data.append("files_Array", JSON.stringify(global));
        var xhr = new XMLHttpRequest();
        xhr.withCredentials = false;
        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                var jsonObject = JSON.parse(this.responseText);
                if (jsonObject.status === "ok") {
                    for (var i = global.length - 1; i > 0 - 1; i--) {
                        var jsonGlobal = global[i];
                        for (var key in jsonGlobal) {
                            var findElementToDelete = document.getElementById(key).children;
                            if (findElementToDelete.length !== 0) {
                                for (var j = 0; j < findElementToDelete.length; j++) {
                                    if (findElementToDelete[j].children[0].children[0].children[0].innerText === jsonGlobal[key]) {
                                        var elementToDelete = findElementToDelete[j];
                                        elementToDelete.parentNode.removeChild(elementToDelete);
                                        console.log("Delete " + jsonGlobal[key]);
                                        global.splice(i, 1);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    Swal.fire(
                            'Good job!',
                            'The audio has been deleted correctly!',
                            'success'
                            );
                    console.log(global);

                }
                if (jsonObject.status === "error") {
                    Swal.fire(
                            'Opps!',
                            'Error deleting audio',
                            'error'
                            );
                }

            }
        });

        xhr.open("POST", absolutepath + "Audios");
        xhr.send(data);



        Swal.fire(
                'Deleted!',
                'Your file has been deleted.',
                'success'
                );


    }
}



function checkBox(_this) {
    var validateCheckBox = _this.checked;
    var folderNameAndLanguage = _this.parentElement.parentElement.parentElement.parentElement.id;
    if (validateCheckBox === true) {
        var obj = {};
        obj[folderNameAndLanguage] = _this.parentElement.parentElement.children[0].children[0].innerText;
        global.push(obj);

    } else {
        var obj = {};
        obj[folderNameAndLanguage] = _this.parentElement.parentElement.children[0].children[0].innerText;
        for (var i = 0; i < global.length; i++) {
            var jsonGlobal = global[i];
            for (var key in jsonGlobal) {
                if (folderNameAndLanguage === key && _this.parentElement.parentElement.children[0].children[0].innerText === jsonGlobal[key]) {
                    global.splice(i, 1);
                }

            }
        }

    }
    console.log(global);
}

function activeDirectorieButtonEN(_this) {
    var spanDirectoryIcon = _this.parentElement.parentElement.children[0].children[0].children[0];
    if (spanDirectoryIcon.classList.contains('glyphicon-folder-close')) {
        spanDirectoryIcon.classList.remove('glyphicon-folder-close');
        spanDirectoryIcon.classList.add('glyphicon-folder-open');
    } else {
        spanDirectoryIcon.classList.add('glyphicon-folder-close');
        spanDirectoryIcon.classList.remove('glyphicon-folder-open');
    }

    // Toggle the content
    toggle(_this.parentElement.parentElement.parentElement.children[2]);

}

function activeDirectorieButtonES(_this) {
    var spanDirectoryIcon = _this.parentElement.parentElement.children[0].children[0].children[0];
    if (spanDirectoryIcon.classList.contains('glyphicon-folder-close')) {
        spanDirectoryIcon.classList.remove('glyphicon-folder-close');
        spanDirectoryIcon.classList.add('glyphicon-folder-open');
    } else {
        spanDirectoryIcon.classList.add('glyphicon-folder-close');
        spanDirectoryIcon.classList.remove('glyphicon-folder-open');
    }
    // Toggle the content
    toggle(_this.parentElement.parentElement.parentElement.children[4]);

}

function activeDirectorieButtonPT(_this) {
    var spanDirectoryIcon = _this.parentElement.parentElement.children[0].children[0].children[0];
    if (spanDirectoryIcon.classList.contains('glyphicon-folder-close')) {
        spanDirectoryIcon.classList.remove('glyphicon-folder-close');
        spanDirectoryIcon.classList.add('glyphicon-folder-open');
    } else {
        spanDirectoryIcon.classList.add('glyphicon-folder-close');
        spanDirectoryIcon.classList.remove('glyphicon-folder-open');
    }
    // Toggle the content
    toggle(_this.parentElement.parentElement.parentElement.children[6]);

}


// Show an element
var show = function (elem) {
    // Get the natural height of the element
    var getHeight = function () {
        elem.style.display = 'block'; // Make it visible
        var height = elem.scrollHeight + 'px'; // Get it's height
        elem.style.display = ''; //  Hide it again
        return height;
    };

    var height = getHeight(); // Get the natural height
    elem.classList.add('is-visible'); // Make the element visible
    elem.style.height = height; // Update the max-height

    // Once the transition is complete, remove the inline max-height so the content can scale responsively
    window.setTimeout(function () {
        elem.style.height = '';
    }, 350);

};

// Hide an element
var hide = function (elem) {

    // Give the element a height to change from
    elem.style.height = elem.scrollHeight + 'px';

    // Set the height back to 0
    window.setTimeout(function () {
        elem.style.height = '0';
    }, 1);

    // When the transition is complete, hide it
    window.setTimeout(function () {
        elem.classList.remove('is-visible');
    }, 350);

};

// Toggle element visibility
var toggle = function (elem, timing) {

    // If the element is visible, hide it
    if (elem.classList.contains('is-visible')) {
        hide(elem);
        return;
    }

    // Otherwise, show it
    show(elem);

};


function getAbsolutePath() {
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}


