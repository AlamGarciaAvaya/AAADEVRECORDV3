

var absolutepath = getAbsolutePath();
function getAbsolutePath() {
    var loc = window.location;
    var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') + 1);
    return loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}


// webkitURL is deprecated but nevertheless
URL = window.URL || window.webkitURL;

var gumStream; 						// stream from getUserMedia()
var rec; 							// Recorder.js object
var input; 							// MediaStreamAudioSourceNode we'll be
									// recording

// shim for AudioContext when it's not avb.
var AudioContext = window.AudioContext || window.webkitAudioContext;
var audioContext // audio context to help us record

var recordButton = document.getElementById("recordButton");
var stopButton = document.getElementById("stopButton");
var pauseButton = document.getElementById("pauseButton");

// add events to those 2 buttons
recordButton.addEventListener("click", startRecording);
stopButton.addEventListener("click", stopRecording);
pauseButton.addEventListener("click", pauseRecording);

function startRecording() {
    console.log("recordButton clicked");

    /*
	 * Simple constraints object, for more advanced audio features see
	 * https://addpipe.com/blog/audio-constraints-getusermedia/
	 */

    var constraints = {audio: true, video: false}

    /*
	 * Disable the record button until we get a success or fail from
	 * getUserMedia()
	 */

    recordButton.disabled = true;
    stopButton.disabled = false;
    pauseButton.disabled = false

    /*
	 * We're using the standard promise based getUserMedia()
	 * https://developer.mozilla.org/en-US/docs/Web/API/MediaDevices/getUserMedia
	 */

    navigator.mediaDevices.getUserMedia(constraints).then(function (stream) {
        console.log("getUserMedia() success, stream created, initializing Recorder.js ...");

        /*
		 * create an audio context after getUserMedia is called sampleRate might
		 * change after getUserMedia is called, like it does on macOS when
		 * recording through AirPods the sampleRate defaults to the one set in
		 * your OS for your playback device
		 */
        audioContext = new AudioContext({
            sampleRate: 8000
        });

        // update the format
        document.getElementById("formats").innerHTML = "Format: 1 channel pcm @ " + audioContext.sampleRate / 1000 + "kHz"

        /* assign to gumStream for later use */
        gumStream = stream;

        /* use the stream */
        input = audioContext.createMediaStreamSource(stream);

        /*
		 * Create the Recorder object and configure to record mono sound (1
		 * channel) Recording 2 channels will double the file size
		 */
        rec = new Recorder(input, {numChannels: 1})

        // start the recording process
        rec.record()

        console.log("Recording started");

    }).catch(function (err) {
        // enable the record button if getUserMedia() fails
        recordButton.disabled = false;
        stopButton.disabled = true;
        pauseButton.disabled = true
    });
}

function pauseRecording() {
    console.log("pauseButton clicked rec.recording=", rec.recording);
    if (rec.recording) {
        // pause
        rec.stop();
        pauseButton.innerHTML = "Resume";
    } else {
        // resume
        rec.record()
        pauseButton.innerHTML = "Pause";

    }
}

function stopRecording() {
    console.log("stopButton clicked");

    // disable the stop button, enable the record too allow for new recordings
    stopButton.disabled = true;
    recordButton.disabled = false;
    pauseButton.disabled = true;

    // reset button just in case the recording is stopped while paused
    pauseButton.innerHTML = "Pause";

    // tell the recorder to stop the recording
    rec.stop();

    // stop microphone access
    gumStream.getAudioTracks()[0].stop();

    // create the wav blob and pass it on to createDownloadLink
    rec.exportWAV(createDownloadLink);
}

function createDownloadLink(blob) {

    var url = URL.createObjectURL(blob);
    var au = document.createElement('audio');
    var li = document.createElement('li');
    var link = document.createElement('a');

    // name of .wav file to use during upload and download (without extendion)
    var filename = new Date();
    var fileNameParse = Date.parse(filename);

    // add controls to the <audio> element
    au.controls = true;
    au.src = url;

    // save to disk link
    link.href = url;
    link.download = propertiesGlobal.verbio_user + "_" + fileNameParse + ".wav"; // download
																					// forces
																					// the
																					// browser
																					// to
																					// donwload
																					// the
																					// file
																					// using
																					// the
																					// filename
    link.innerHTML = "Download";
    link.setAttribute("class", "badge badge-pill badge-primary");
    // add the filename to the li
    li.appendChild(document.createTextNode(propertiesGlobal.verbio_user + "_" + fileNameParse + ".wav "))
    // add the new audio element to li
    li.appendChild(au);
    // add the save to disk link to li
    li.appendChild(link);

    var analize = document.createElement('a');
    analize.innerHTML = "Analize with Verbio BioMetrics";
    analize.setAttribute("class", "badge badge-pill badge-success");
    analize.setAttribute("style", "cursor: pointer; cursor: hand; color : #ffffff;");
    var p = document.createElement('P');
    analize.addEventListener('click', function (e) {
        e.preventDefault();
        var reader = new FileReader();
        reader.readAsDataURL(blob);
        reader.onloadend = function () {
            base64data = reader.result;
            var base64Substring = base64data.substring(22);

            var data = new FormData();
            data.append("request", "VERIFY");
            data.append("base64Audio", base64Substring);

            var xhr = new XMLHttpRequest();
            xhr.withCredentials = true;
            document.getElementById('loader').classList.add('is-active');
            document.getElementById("loader").setAttribute("data-text", "Analyzing");
            xhr.addEventListener("readystatechange", function () {
                if (this.readyState === 4) {
                    console.log(this.responseText);
                    document.getElementById("loader").classList.remove("is-active");
                    document.getElementById("loader").setAttribute("data-text", "");
                    var responseVerbio = JSON.parse(this.responseText);
                    if (responseVerbio.response.status === "SUCCESS") {
                        var scoreVerbio = responseVerbio.response.result.verbio_result.score;

                        var pTextNode = document.createTextNode("Score Voice Recognition = " + scoreVerbio);
                        var espacio = document.createElement('BR');
                        p.appendChild(espacio);
                        p.appendChild(pTextNode);
                    }

                }
            });

            xhr.open("POST", absolutepath + "VerbioClient");
            xhr.send(data);

        };
    });

    li.appendChild(document.createTextNode(" "));// add a space in between
    li.appendChild(analize);// add the upload link to li


    // upload link
    var upload = document.createElement('a');
    upload.innerHTML = "Add File To Verbio";
    upload.setAttribute("class", "badge badge-pill badge-success");
    upload.setAttribute("style", "cursor: pointer; cursor: hand; color : #ffffff;");
    upload.addEventListener("click", function (e) {
        e.preventDefault();
        // convert blob to Base64

        document.getElementById('loader').classList.add('is-active');
        document.getElementById("loader").setAttribute("data-text", "Uploading");
        var reader = new FileReader();
        reader.readAsDataURL(blob);
        reader.onloadend = function () {
            base64data = reader.result;
            var base64Substring = base64data.substring(22);
            var data = new FormData();
            data.append("base64wav", base64Substring);
            data.append("audioName", propertiesGlobal.verbio_user + "_" + fileNameParse + ".wav ");
            console.log("ADD_VERBIO");

            e = e || window.event;
            var target = e.target || e.srcElement,
                    text = target.textContent || target.innerText;


            var xhr = new XMLHttpRequest();
            xhr.withCredentials = false;

            xhr.addEventListener("readystatechange", function () {
                if (this.readyState === 4) {
                    var response = JSON.parse(this.responseText);
                    console.log(response);
                    document.getElementById("loader").classList.remove("is-active");
                    document.getElementById("loader").setAttribute("data-text", "");
                    if (response.status === "ok") {
                        var targetParent = target.parentElement;
                        var icon = document.createElement('I');

                        icon.setAttribute("class", "far fa-check-circle");
                        icon.setAttribute("style", "color: green; font-size: 30px;");
                        var iconTextNode = document.createTextNode('Uploaded');
                        icon.appendChild(iconTextNode);
                        targetParent.removeChild(target);
                        targetParent.appendChild(icon);

                        userProp();
                        Swal({
                            position: 'center',
                            type: 'success',
                            title: 'Audio File Save!',
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
            xhr.open("POST", absolutepath + "SaveAudio");
            xhr.send(data);
        };


    });
    li.appendChild(document.createTextNode(" "));// add a space in between
    li.appendChild(upload);// add the upload link to li
    
    
    // Remove Link
    var remove = document.createElement('I');
    remove.setAttribute('class', 'fas fa-minus-circle');
    remove.setAttribute("style", "color: red; font-size: 20px; cursor: pointer; cursor: hand;");

    remove.addEventListener("click", function (e) {
    e.preventDefault();
    e = e || window.event;
    var target = e.target || e.srcElement;
    target.parentElement.innerHTML = "";
    });

    li.appendChild(document.createTextNode(" "));// add a space in between
    li.appendChild(remove);// add the upload link to li

    // add the li element to the ol
    recordingsList.appendChild(li);
    recordingsList.appendChild(p);
}