//import ClassicBoard from "/html/classicboard.html"
import Gamemenu from "gamemenu/gamemenu.html"

document.getElementById("reset-btn").addEventListener("click", newgame);

function newgame() {
    document.querySelector("board").innerHTML = Gamemenu;
    document.getElementById("start-btn").addEventListener("click", startgame);
}

function startgame() {
    const theme = document.getElementById("themefilter").value;
    const gamemode = document.getElementById("modefilter").value;
    document.querySelector("board").innerHTML = theme;
    if (theme == 1) {
        document.querySelector("board").innerHTML = ClassicBoard;
        document.getElementById("rolldice").addEventListener("click", rolldice);
    } else if (theme == 2) {
        $.ajax({
            url: "HelloWorld.java", // your java file here
            context: document.body,
            dataType: "json", // make sure it's you're using JSON
        }).done(function () {
            $(this).addClass("done");
        });
    } else {
        document.querySelector("board").innerHTML = Gameboard;
    }
}

function rolldice() {
    document.getElementById("rollingdices").style.display = "block";
    setTimeout(stopdice, 1500);
}

function stopdice() {
    var w1 = Math.round(Math.random() * 5) + 1;
    var w2 = Math.round(Math.random() * 5) + 1;
    var total = w1 + w2;
    document.getElementById("rollingdices").style.display = "none";
    document.getElementById("rolledvalue").innerHTML = "You rolled:<br>" + w1 + "  +  " + w2;
    while (w1 == w2) {
        w1 = Math.round(Math.random() * 5) + 1;
        w2 = Math.round(Math.random() * 5) + 1;
        total += w1 + w2;
        document.getElementById("rolledvalue").innerHTML += "  +  " + w1 + "  +  " + w2;
    }
    document.getElementById("rolledvalue").innerHTML += "  =  " + total;
}
