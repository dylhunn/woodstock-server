var marginOffset = 10; // extra space around staves
var staffSystemHeight = 240;
var spaceBetweenChords = 500;

var chords = [[]]; // cached chord data, organized by chord
var staveNotes = [[]]; // cached VexFlow staveNote data, organized by melody
var httpRequest;

function showMusicArea() {
  $('#tutdiv').hide(); // Hide the tutorial on first execution only
  $('#musicdiv').show();
  $('#musiccontroldiv').show();
}

function hideMusicArea() {
  $('#musicdiv').hide();
  $('#musiccontroldiv').hide();
}

// Parameter is optional; do not include "Error: "
function showError(msg) {
  $('#errormsg').show()
  msg = msg || "";
  $("#errormsg").html("<strong>Error</strong>: " + msg);
  //hideMusicArea();
}

function hideError() {
  $('#errormsg').hide()
}

function requestAndDraw() {
  hideError();
  var text = document.getElementById("inputlg").value;
  if (!text) {
    showError("you must specify a progression.");
    return;
  }
  initiateHarmonyRequest(text);
  // The callback will automatically redraw the music.

  // Accepts a space-separated progression string as a parameter
  function initiateHarmonyRequest(progression) {
    var requestData = encodeURIComponent(progression.trim());

    httpRequest = new XMLHttpRequest();

    if (!httpRequest) {
      console.log('Cannot create an XMLHTTP instance');
      return false;
    }
    httpRequest.onreadystatechange = completeHarmonyRequest;
    var url = "/harmonize/" + requestData;
    httpRequest.open('GET', url);
    httpRequest.send();
  }
}

function completeHarmonyRequest() {
  //try {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
      if (httpRequest.status === 200) {
        var parsedChords = JSON.parse(httpRequest.response);
        chords = parsedChords; // Cache the data
        var melodies = transformFromChordsToMelodies(parsedChords)
        staveNotes = generateStaveNotes(melodies[0], melodies[1], melodies[2], melodies[3]);
        showMusicArea();
        drawMusic();
      } else {
        var undefinedWarning = "";
        if (!httpRequest.response) {
          undefinedWarning = "the server returned no response."
        }
        // TODO
        console.log(httpRequest.response.toString());
        showError(decodeURI(httpRequest.response.toString()) + undefinedWarning);
      }
    }
  //}
  //catch(e) {
  //  showError(e.description);
  //}
}

// accepts json formatted chords
function drawChords(parsedChords) {
  chords = parsedChords; // Cache the data
  var melodies = transformFromChordsToMelodies(parsedChords)
  staveNotes = generateStaveNotes(melodies[0], melodies[1], melodies[2], melodies[3]);
  showMusicArea();
  drawMusic();
}

// Accept an array of arrays, each of which lists chord tones. Outer array is ordered. Inner array is SATB.
// Return an array of arrays, each of which lists voice tones, in order. Outer array is SATB.
function transformFromChordsToMelodies(progression) {
  var newArray = progression[0].map(function(col, i) {
    return progression.map(function(row) {
      return row[i]
    })
  });

  return(newArray);
}

function drawMusic() {
  // All four parts must have the same number of notes!
  var chordsPerLine = numberOfChordsPerLine();
  //console.log(chordsPerLine + " chords per line");
  //console.log(staveNotes[0].length + " total chords");

  var numSystems = Math.ceil(staveNotes[0].length / chordsPerLine);
  //console.log("Expanding div to fit " + numSystems + " systems");

  // Clear and correctly size renderer
  var canvas = $("div.container div.jumbotron canvas")[0];
  var renderer = new Vex.Flow.Renderer(canvas, Vex.Flow.Renderer.Backends.CANVAS);
  var parentDivWidth = $("#musicdiv").width();
  spaceBetweenChords = parentDivWidth - 20;
  renderer.resize(parentDivWidth - 2*marginOffset, staffSystemHeight*numSystems); // Resize and clear canvas

  var startingChordIdx = 0;
  var staffIndex = 0; // keep track of the current system index

  while (startingChordIdx < staveNotes[0].length) {
    endingChordIdx = Math.min(startingChordIdx + chordsPerLine - 1, staveNotes[0].length-1); // Don't pass the last chord
    currentSystemSoprano = staveNotes[0].slice(startingChordIdx, endingChordIdx+1);
    currentSystemAlto = staveNotes[1].slice(startingChordIdx, endingChordIdx+1);
    currentSystemTenor = staveNotes[2].slice(startingChordIdx, endingChordIdx+1);
    currentSystemBass = staveNotes[3].slice(startingChordIdx, endingChordIdx+1);
    //console.log("Rendering chords between inclusive indices " + startingChordIdx + ", " + endingChordIdx);
    renderVoices(currentSystemSoprano, currentSystemAlto, currentSystemTenor, currentSystemBass, staffIndex*staffSystemHeight);

    startingChordIdx = endingChordIdx + 1;
    staffIndex++;
  }
  $("musicdiv").height((staffIndex+1)*staffSystemHeight);

  // Helpers

  // Compute the number or chords per line
  function numberOfChordsPerLine() {
    var parentDivWidth = $("#musicdiv").width();
    //console.log("parent div is " + parentDivWidth)
    //var space = parentDivWidth - 2*marginOffset;
    //var staffPadding = 20;
    //var chords = (space-staffPadding)/spaceBetweenChords; // constant subtracted to give space for cleff
    //console.log("we get " + chords + " chords")
    //return Math.max(Math.floor(chords/4), 1);
    return Math.max(1, Math.floor((parentDivWidth-10)/70)); // TODO: fix
  }
}

// Accepts four arrays of strings representing pitch names,
// returns an array of four arrays of VexFlow StaveNotes.
// The input pitch names must be upper case.
function generateStaveNotes(sopranoStrs, altoStrs, tenorStrs, bassStrs) {

  var snotes = sopranoStrs.map(function(val) {
    if (parseAccidental(val).length > 0) return new Vex.Flow.StaveNote({ keys: [buildVexPitchString(val)], duration: "q", stem_direction: 1 }).
    addAccidental(0, new Vex.Flow.Accidental(parseAccidental(val)));
    else return new Vex.Flow.StaveNote({ keys: [buildVexPitchString(val)], duration: "q", stem_direction: 1 });
  });

  var anotes = altoStrs.map(function(val) {
    if (parseAccidental(val).length > 0) return new Vex.Flow.StaveNote({ keys: [buildVexPitchString(val)], duration: "q", stem_direction: -1 }).
    addAccidental(0, new Vex.Flow.Accidental(parseAccidental(val)));
    else return new Vex.Flow.StaveNote({ keys: [buildVexPitchString(val)], duration: "q", stem_direction: -1 });
  });

  var tnotes = tenorStrs.map(function(val) {
    if (parseAccidental(val).length > 0) return new Vex.Flow.StaveNote({ clef: "bass", keys: [buildVexPitchString(val)], duration: "q", stem_direction: 1 }).
    addAccidental(0, new Vex.Flow.Accidental(parseAccidental(val)));
    else return new Vex.Flow.StaveNote({ clef: "bass", keys: [buildVexPitchString(val)], duration: "q", stem_direction: 1 });
  });

  var bnotes = bassStrs.map(function(val) {
    if (parseAccidental(val).length > 0) return new Vex.Flow.StaveNote({ clef: "bass", keys: [buildVexPitchString(val)], duration: "q", stem_direction: -1 }).
    addAccidental(0, new Vex.Flow.Accidental(parseAccidental(val)));
    else return new Vex.Flow.StaveNote({ clef: "bass", keys: [buildVexPitchString(val)], duration: "q", stem_direction: -1 });
  });

  return [snotes, anotes, tnotes, bnotes];

  // helpers
  function buildVexPitchString(pitchString) {
    return parseRawPitch(pitchString).concat("/").concat(parsePitchClass(pitchString));
  }

  function parsePitchClass(noteStr) {
    noteStr = noteStr.replace(/\D/g,'');
    return noteStr;
  }

  function parseRawPitch(noteStr) {
    noteStr = noteStr.replace(/[^A-G]/g,'').toLowerCase();
    return noteStr;
  }

  // Also replaces X with ## for VexFlow
  function parseAccidental(noteStr) {
    noteStr = noteStr.replace(/[^b#X]/g,'');
    noteStr = noteStr.replace(/[X]/g,'##');
    return noteStr;
  }
}

// Renders the music in four parts.
// Accepts four arrays of VexFlow StaveNotes, then renders them 
// as separate voices.
// The starting y-position should be specified, for the purposes of drawing
// several systems. This is usually 0.
function renderVoices(sopranoSN, altoSN, tenorSN, bassSN, yPos) {
  var canvas = $("div.container div.jumbotron canvas")[0];
  var renderer = new Vex.Flow.Renderer(canvas,
      Vex.Flow.Renderer.Backends.CANVAS);

  var parentDivWidth = $("#musicdiv").width();

  var ctx = renderer.getContext();
  var tstave = new Vex.Flow.Stave(marginOffset, yPos, parentDivWidth - 2*marginOffset);
  tstave.addClef("treble").setContext(ctx).draw()

  var bstave = new Vex.Flow.Stave(marginOffset, yPos + staffSystemHeight/2, parentDivWidth - 2*marginOffset);
  bstave.addClef("bass").setContext(ctx).draw()

  function create_voice(nb) {
    return new Vex.Flow.Voice({
      num_beats: nb,
      beat_value: 4,
      resolution: Vex.Flow.RESOLUTION
    });
  }
  // Create voices and add notes to each of them.
  var sopranoV = create_voice(sopranoSN.length).addTickables(sopranoSN);
  var altoV = create_voice(altoSN.length).addTickables(altoSN);
  var tenorV = create_voice(tenorSN.length).addTickables(tenorSN);
  var bassV = create_voice(bassSN.length).addTickables(bassSN);

  // Format and justify the notes
  var formatter = new Vex.Flow.Formatter().format([sopranoV, altoV, tenorV, bassV], spaceBetweenChords);

  // Render voices
  sopranoV.draw(ctx, tstave);
  altoV.draw(ctx, tstave);
  tenorV.draw(ctx, bstave);
  bassV.draw(ctx, bstave);
}