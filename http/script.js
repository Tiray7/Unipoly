
var $menu;
var $menuToggle;

var $gamemode;
var $gamemodeop;
var $gamemodetext;
var $join;
var $joinBackground;
var $joinName;
var $joinToken;
var $joininglist;
var $joinstart;

var $gameboard;
var $playerlist;
var $dicesgif;
var $lastphase;
var $alertpopup;
var $numbernpccon;
var $numbernpc = 0;
var $diceinput;

$(document).ready(function () {

	// menu
	$menu = $('#menu');

	// join
	$gamemodeop = $('#gamemode');
	$gamemodetext = $('#gamemode-text');
	$numbernpccon = $('#number-npc-con');
	$joinName = $('#join-name');
	$joinToken = $('#join-token');
	$joinwaiting = $('#join-waiting');
	$joininglist = $('#joininglist');
	$joinstart = $('#join-start');

	// Board
	$gameboard = $('#gameboard');
	$playerlist = $('#playerlist tr');
	$dicesgif = $('#rollingdices');
	$rolledvaluetext = $('#rolledvalue');
	$diceinput = $('#chooseDice_popup');

	// Popups
	$alertpopup = $('#alert_popup');

	$('.space').prepend('<table class="tablecon"><tr><td></td><td></td></tr><tr><td></td><td></td></tr></table>');
	$('.tablecon').find('td').addClass('leer');
	$lastphase = 'WAITING';
});

function Sleep(milliseconds) {
	return new Promise(resolve => setTimeout(resolve, milliseconds));
}

function poll($scope) {
	$.getJSON('state', function (json) {
		update($scope, json);

		/*
		setTimeout(function () {
			poll($scope)
		}, 1000);
		*/
	});
}

// Move the PlayerToken
function moveToken($scope) {
	const type = $scope.state.currentPlayer.token.type.toLowerCase();
	const prevind = $(`#pos${$scope.state.currentPlayer.token.prevFieldIndex}`).find("td." + type).first();
	const currind = $(`#pos${$scope.state.currentPlayer.token.currFieldIndex}`).find("td.leer").first();
	prevind.toggleClass('leer');
	prevind.toggleClass(`used ${type}`);
	currind.toggleClass('leer');
	currind.toggleClass(`used ${type}`);
}

async function showalert(text, time = 10000) {
	$alertpopup.find('.popup-p').html(text);
	$alertpopup.show();
	await Sleep(time)
	$alertpopup.hide();
}

// Update UI when phase changes
async function phaseChange($scope) {
	const newphase = $scope.state.phase;
	var txt;

	switch (newphase) {
		case 'ROLLINGONE':
			var diceVal1 = $scope.state.firstDice;
			var diceVal2 = $scope.state.secondDice;
			const total = diceVal1 + diceVal2;
			txt = `<b>${$scope.state.currentPlayer.name} rolled:</b><br>${diceVal1} + ${diceVal2}<br>Total: ${total}`;
			$dicesgif.show();
			$dicesgif.delay(1100).fadeOut(200);
			$rolledvaluetext.html(txt);
			await Sleep(1350);
			moveToken($scope);
			$scope.checkField();
			break;

		case 'ROLLINGTWO':
			txt = `<b>${$scope.state.currentPlayer.name} rolled:</b><br>${$scope.state.firstDice} and ${$scope.state.secondDice}`;
			$rolledvaluetext.html(txt);
			$dicesgif.show();
			$dicesgif.delay(1100).fadeOut(200);
			await Sleep(1350);

			if ($scope.state.rolledPash) {
				txt = 'Du hast es geschafft die Schuldirektorin zu überzeugen.'
				showalert(txt);
				$scope.leaveDetention();
			} else {
				txt = 'Du hast es nicht geschafft die Schuldirektorin zu überzeugen.'
				showalert(txt);
				$scope.endTurn();
			}
			break;

		case 'RECESS':
			console.log('New Phase RECESS');
			txt = 'Znüni Zeit. Ruh dich etwas aus:<br>Trink einen Kaffee und iss ein Sandwich!';
			showalert(txt);
			$scope.endTurn();
			break;

		case 'NOT_ENOUGH_MONEY':
			console.log('New Phase NOT_ENOUGH_MONEY');
			txt = 'Du hast leider nicht genug Geld für eine Aktion!<br>Die Runde geht automatisch an den nächsten Spieler.';
			showalert(txt);
			$scope.endTurn();
			break;

		case 'BUY_PROPERTY':
			console.log('New Phase BUY_PROPERTY');
			var name = $scope.state.currentFieldProperty.name;
			var cost = $scope.state.currentFieldProperty.propertyCost;
			if (confirm(`Willst du das Modul "${name}" besuchen?<br>Kostet nur ${cost}CHF!`)) {
				$scope.buyProperty();
			}
			$scope.endTurn();
			break;

		case 'JUMP':
			console.log('New Phase JUMP');
			if (confirm("Willst du zu einem anderem Feld springen?<br>Kostet nur 100 CHF!")) {
				txt = 'Klick auf das Feld auf das du springen willst!';
				showalert(txt);
			} else {
				$scope.endTurn();
			}
			break;

		case 'GO':
			console.log('New Phase GO');
			txt = 'Weil du auf Start gelandet bist, bekommst du das doppelte Honorar!';
			showalert(txt);
			$scope.endTurn();
			break;

		case 'SHOWCARD':
			console.log('New Phase Showcard');
			txt = 'Du bist auf einem Chance Feld gelandet! Du musst eine Chance Karte ziehen!';
			showalert(txt);
			break;

		case 'GO_DETENTION':
			console.log('New Phase GODETENTION');
			txt = 'Du wurdest beim plagieren erwischt und musst deshalb zur Schuldirektorin!';
			moveToken($scope);
			showalert(txt);
			$scope.endTurn();
			break;

		case 'VISIT':
			console.log('New Phase VISIT');
			txt = 'Einen Abstecher ins Rektorat.';
			showalert(txt);
			$scope.endTurn();
			break;

		case 'FREECARD':
			console.log('New Phase FREECARD');
			txt = 'Du wurdest beim plagieren erwischt und musst deshalb zur Schuldirektorin!<br>Du warnst sie das wenn sie dich von der Schule schmeist, du ihr Geheimnis rumerzählst.<br>Sie lässt dich sofort gehen.';
			showalert(txt);
			$scope.endTurn();
			break;

		case 'DETENTION':
			console.log('New Phase DETENTION');
			var verhandeln = false;
			var leftTimeInDetention = $scope.state.currentPlayer.leftTimeInDetention;
			txt = 'Du bist noch bei der Schuldirektorin. Sie möchte dich von der Uni verweisen...<br>';
			if (leftTimeInDetention > 0) {
				if ($scope.state.currentPlayer.money >= 100) {
					txt += `Willst du versuchen über deinen Schulverweis zu verhandeln, anstatt Sie zu bestechen (100 CHF)?<br>Du hast noch ${leftTimeInDetention} Varhandlungsversuche.`;
					verhandeln = confirm(txt);
					txt = '';
				} else if ($scope.state.currentPlayer.money < 100) {
					txt = 'Du hast nicht genug Geld um Sie zu bestechen, dir bleibt nichts übrig als zu verhandeln.<br>';
					verhandeln = true;
				}
			} else {
				verhandeln = false;
				txt += 'Du hast scho 3mal versucht Sie zu überzeugen; Ohne Erfolg!<br>';
				if ($scope.state.currentPlayer.money >= 100) {
					txt += 'Dir bleibt nichts anders übrig als sie zu bestechen.<br>';
				} else if ($scope.state.currentPlayer.money < 100) {
					txt += 'Ausserdem hast du nicht genug Geld um Sie zu bestechen.<br>';
				}
			}

			if (verhandeln) {
				txt += 'Würfle ein Pash um Sie zu überzeugen.';
				showalert(txt);
			} else {
				// Todo: bestechen
				showalert(txt);
				$scope.payDetentionRansom();
			}
			break;
	}
}

function update($scope, json) {
	$scope.state = json;
	var list = $scope.state.players;

	if ($scope.state.currentPlayer !== null) {
		var currplayer = $scope.state.currentPlayer;
		var tr = '';
		for (let i = 0; i < list.length; i++) {
			tr = `<td class="listtoken ${list[i].token.type.toLowerCase()}"></td>`;
			if (list[i].name == currplayer.name) {
				tr += `<td><b>${list[i].name}: ${list[i].money}</b></td>`;
			} else {
				tr += `<td>${list[i].name}: ${list[i].money}</td>`;
			}
			$playerlist.eq(i).html(tr);
		}
	}

	// Check if phase changed, update accordingly
	if ($lastphase !== $scope.state.phase) {
		phaseChange($scope);
		$lastphase = $scope.state.phase;
	}

	$scope.$apply();
}

function resetHTML(list) {
	// List all added Players
	var prevind, type;
	for (let i = 0; i < list.length; i++) {
		type = list[i].token.type.toLowerCase();
		prevind = $(`#pos${list[i].token.currFieldIndex}`).find("td." + type).first();
		prevind.toggleClass('leer');
		prevind.toggleClass(`used ${type}`);
	}
	$gameboard.hide();
	$numbernpc = 0;
	$rolledvaluetext.html('');
	$menu.show();
	$joinToken.hide();
	$joinName.hide();
	$joinstart.hide();
	$joininglist.html('');
	$joinwaiting.hide();
	$gamemodetext.text('Choose a Gamemode!');
	$gamemodeop.show();
}

var app = angular.module('monopolyApp', []);
app.controller('Controller', function ($scope) {

	poll($scope);

	$scope.getOp = function (path, callback) {
		console.log(`getJSON: ${path}`);
		//Load JSON-encoded data from the server using a GET HTTP request.
		$.getJSON(path, function (json) {
			update($scope, json);
			console.log($scope);
			if (callback != undefined) {
				callback(true);
			}
		}).fail(function (json) {
			if (callback != undefined) {
				callback(false);
			}
		});
	}

	$scope.postOp = function (path, data, callback) {
		$.post(path, data, function (json) {
			update($scope, json)

			if (callback != undefined) {
				callback(true);
			}
		}, 'json')
			.fail(function () {
				if (callback != undefined) {
					callback(false);
				}
			});
	}

	// Player signed a Name now let him choose a Token
	$scope.chooseToken = function () {
		$joinName.hide();
		$joinToken.show();
	}

	// Read User Input on Gamemode
	$scope.gamemode = function (gamemode) {
		var text;
		$gamemode = gamemode;
		$gamemodeop.hide();
		if (gamemode == 'SINGLE') {
			text = 'Playing against AI';
			$numbernpccon.show();
		} else if (gamemode == 'MULTI') {
			text = 'Playing in Multiplayer Modus';
			$joinwaiting.show();
			$joinName.show();
		}
		$gamemodetext.text(text);
	}

	// SinglePlayer is able to choose Number of NPCs
	$scope.numbernpc = function (num) {
		console.log(`${num} NPCs choosen`);
		$numbernpc = num;
		$numbernpccon.hide();
		$joinwaiting.show();
		$joinName.show();
	}

	$scope.join = function (token) {
		var playerlist = '';
		var list;
		// Call function join() in class UnipolyApp
		$scope.getOp(`join?name=${$scope.username}&token=${token}`,
			function (success) {
				// Check if adding new Payer was successfull
				if (success) {
					console.log(`success: added Player: ${$scope.username}`);
				}
				else {
					console.error(`error: adding Player: ${$scope.username}`);
					alert(`Error: adding Player: ${$scope.username}`);
				}
				// List all added Players
				list = $scope.state.players;
				// Iterate trough all added Players and create a HTML text to display on right in the Menu
				for (let i = 0; i < list.length; i++) {
					type = list[i].token.type.toLowerCase();
					playerlist += `${list[i].name} : ${type.charAt(0).toUpperCase() + type.slice(1)}<br>`;
				}
				// In Singleplayer only one Player should be able to join
				// In Multiplayer up to 4 Player shoud be able to join and at least 2 have to
				if (($gamemode == 'SINGLE' && list.length == 1) || ($gamemode == 'MULTI' && list.length >= 2)) {
					// If the minimum of needed Player has joined, the option to start the game should be displayed
					$joinstart.show();
				}
				// If there is still room for more Players, The join Option should be displayed
				if (($gamemode == 'SINGLE' && list.length < 1) || ($gamemode == 'MULTI' && list.length < 4)) {
					$joinName.show();
				}
				$joinToken.hide();
				// Display the current List of added Players
				$joininglist.html(playerlist);
			});
	}

	// Begin Game
	$scope.start = function () {
		$scope.getOp(`start?gamemode=${$gamemode}&npcnum=${$numbernpc}`,
			function (success) {
				// Check if Gamemode choice got accepted
				if (success) {
					console.log('success: Start Game');
					$menu.hide();
					$gameboard.show();

					var type, index, td;
					// Iterate through all Players
					const list = $scope.state.players;
					for (let i = 0; i < list.length; i++) {
						type = list[i].token.type.toLowerCase();
						index = `#pos${list[i].token.currFieldIndex}`;
						td = $(index).find("td.leer").first();
						td.toggleClass('leer');
						td.toggleClass(`used ${type}`);
					}
				} else {
					console.error('error: Start Game');
					alert('Error: Please try again!');
				}
			});
	}

	$scope.resetGame = function () {
		const list = $scope.state.players;
		if (confirm("Are you sure you want to Reset the Game?")) {
			$scope.getOp('resetgame',
				function (success) {
					// Check if Game Reset was a succsess
					if (success) {
						console.log('success: resetGame');
						resetHTML(list);
					} else {
						console.error('error: resetGame');
						alert('Error: Please try again!');
					}
				});
		}
	}

	// Check what to do on this Field
	$scope.checkField = function () {
		$scope.getOp('checkfieldoptions',
			function (success) {
				// Check if  success
				if (success) {
					console.log('success: checkfieldoptions');
				} else {
					console.error('error: checkfieldoptions');
				}
			});
	}

	$scope.ToggleDiceInput = function () {
		// If Player is in Detention he has to roll both Dices
		if ($scope.state.phase == 'DETENTION') {
			console.log('Player is in Detention, therefor rolls both dices.')
			$scope.rollDice('rolltwodice');
			// If Player landed on ChanceCards he cant roll dices
		} else if ($scope.state.phase == 'SHOWCARD') {
			txt = 'Du bist auf einem Chance Feld gelandet! Du musst eine Chance Karte ziehen!';
			showalert(txt);
		} else if ($scope.state.phase == 'JUMP') {
			txt = 'Du bist auf Springer Feld gelandet!\nKlick auf das Feld auf das du springen willst!';
			showalert(txt);
		} else {
			console.log('Player has to Input the first Dice.')
			$diceinput.toggle();
		}
	}

	// Roll the dice(s)
	$scope.rollDice = function (path) {
		$scope.getOp(path,
			function (success) {
				// Check if starting turn worked
				if (success) {
					console.log('success: rollDice');
				} else {
					console.error('error: rollDice');
					alert('Error: Please try again!');
				}
			});
	}

	// Player wants to Teleport
	$scope.jump = function (FieldIndex) {
		$scope.getOp(`jumpplayer?FieldIndex=${FieldIndex}`,
			function (success) {
				// Check if  success
				if (success) {
					console.log('success: jumpPlayer');
					moveToken($scope);
					$scope.checkField();
				} else {
					console.error('error: jumpPlayer');
				}
			});
	}

	// End Turn / switchPlayer
	$scope.endTurn = function () {
		//End Turn
		$scope.getOp('endturn',
			function (success) {
				// Check if  success
				if (success) {
					console.log('success: endTurn');
				} else {
					console.error('error: endTurn');
				}
			});
	}

	// Ask Player if he wants to buy Property
	$scope.buyProperty = function () {
		const currplayer = $scope.state.currentPlayer;
		$scope.getOp(`userwantstobuy?currentFieldIndex=${currplayer.token.currFieldIndex}`,
			function (success) {
				// Check if  success
				if (success) {
					console.log('success: userwantstobuy');
				} else {
					console.error('error: userwantstobuy');
				}
			});
	}

	// Besteche die Direktorin
	$scope.payDetentionRansom = function () {
		$scope.getOp('paydetentionransom',
			function (success) {
				// Check if  success
				if (success) {
					console.log('success: paydetentionransom');
				} else {
					console.error('error: paydetentionransom');
				}
			});
	}

	$scope.leaveDetention = function () {
		$scope.getOp('leavedetention',
			function (success) {
				// Check if  success
				if (success) {
					console.log('success: leaveDetention');
				} else {
					console.error('error: leaveDetention');
				}
			});
	}

	// Player pressed on Card Deck
	$scope.showCard = function () {
		var txt;
		if ($scope.state.phase == 'SHOWCARD') {
			txt = $scope.state.currentCardText;
			showalert(txt);
			$scope.endTurn();
		} else {
			txt = 'Du musst auf einem Chance Feld landen um eine Chance Karte ziehen zu dürfen.';
			showalert(txt);
		}
	}

	// Player klicked on a Field
	$scope.fieldaction = function (FieldIndex) {
		if ($scope.state.phase == 'JUMP') {
			$scope.jump(FieldIndex);
		} else {
			var txt = 'keine Info zu diesem Feld! Comming Soon!';
			showalert(txt);
		}
	}

	// Player pressed on Card Deck
	$scope.showPlayerInfo = function (PlayerIndex) {
		var player = $scope.state.players[PlayerIndex];
		var txt = `<h2>${player.name}</h2>`;
		txt += `Geld: ${player.money}<br>`;
		txt += `Frei Karte: ${player.freeCard}<br>`;
		txt += `Muss Nachsitzen: ${player.leftTimeInDetention}<br>`;
		txt += `Module: ${player.propertyOwned}<br>`;
		txt += `ModulGruppen: ${player.roadOwned}<br>`;
		showalert(txt);
	}

	// Player pressed closepopup
	$scope.closealert = function () {
		$alertpopup.hide();
	}
});