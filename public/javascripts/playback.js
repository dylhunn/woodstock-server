var synth;
var default_voices = 6;
var notePlaybackLength = 1000;
var soundingPitches;
var musicPlaying = false;
var sounding = false;

// Precondition! music.js must be loaded to provide var chords.
function playMusic() {
    synth = makeSynth(default_voices);
    //hideError();
    if (!chords || chords == [[]] || chords.length == 0) {
        showError("We can't find any chords to play. Try re-harmonizing.");
        return;
    }
    musicPlaying = true;
    $("#playbtn").prop("disabled", true);
    $("#stopbtn").prop("disabled", false);
    $("#harmonizeBtn").prop("disabled", true);
    $("#harmonizeBtn").prop("disabled", true);

    var chordsClone = chords.slice(0);
    playSound(chordsClone[0]);
    setTimeout(function(){playMusicHelper(chordsClone.slice(1))}, notePlaybackLength);

    function playMusicHelper(remainingChords) {
        stopPreviousSound();
        if (remainingChords.length == 0) {
            stopMusic();
            return;
        }
        if (!musicPlaying) {
            stopMusic();
            return;
        }
        playSound(remainingChords[0]);
        setTimeout(function(){playMusicHelper(remainingChords.slice(1))}, notePlaybackLength);
    }
}

function stopMusic() {
    musicPlaying = false;
    $("#stopbtn").prop("disabled", true);
    $("#playbtn").prop("disabled", false);
    $("#inputlg").prop("disabled", false);
    $("#harmonizeBtn").prop("disabled", false);

}

// Accepts an array of strings indicating which pitches to play.
function playSound(pitches) {
    console.log(pitches);
    if (sounding == true) throw "Synth is already sounding.";
    if (pitches.length > default_voices) synth = makeSynth(99);
    synth.triggerAttack(pitches);
    soundingPitches = pitches;
    sounding = true;
}

function stopPreviousSound() {
    synth.triggerRelease(soundingPitches);
    soundingPitches = [];
    sounding = false;
}

function makeSynth(voices) {
    //a polysynth composed of 6 Voices of MonoSynth
    var lsynth = new Tone.PolySynth(voices, Tone.SimpleFM).toMaster();
    //set the attributes using the set interface
    lsynth.set("detune", -1200);
    return lsynth;
}