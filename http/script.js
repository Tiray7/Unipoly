
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

$(document).ready(function () {

	// menu
	$menu = $('#menu');

	// join
	$gamemodeop = $('#gamemode');
	$gamemodetext = $('#gamemode-text')
	$joinName = $('#join-name');
	$joinToken = $('#join-token');
	$joinwaiting = $('#join-waiting');
	$joininglist = $('#joininglist');
	$joinstart = $('#join-start');

	// Board
	$gameboard = $('#gameboard');
	$playerlist = $('#playerlist');
	$dicesgif = $('#rollingdices');
	$rolledvaluetext = $('#rolledvalue');

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
	const prevind = $('#pos' + $scope.state.currentPlayer.token.prevFieldIndex).find("td." + type).first();
	const currind = $('#pos' + $scope.state.currentPlayer.token.currFieldIndex).find("td.leer").first();
	prevind.toggleClass('leer');
	prevind.toggleClass('used ' + type);
	currind.toggleClass('leer');
	currind.toggleClass('used ' + type);
}

async function showalert(text, time = 2000) {
	$alertpopup.find('.popup-con').text(text);
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
			txt = '<b>' + $scope.state.currentPlayer.name + ' rolled:</b><br>' + diceVal1 +
				' + ' + diceVal2 + '<br>Total: ' + total;
			$dicesgif.show();
			$dicesgif.delay(1100).fadeOut(200);
			$rolledvaluetext.html(txt);
			await Sleep(1350);
			moveToken($scope);
			$scope.checkField();
			break;

		case 'ROLLINGTWO':
			txt = '<b>' + $scope.state.currentPlayer.name + ' rolled:</b><br>' + $scope.state.firstDice +
				' and ' + $scope.state.secondDice;
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
			txt = 'Znüni Zeit. Ruh dich etwas aus:\nTrink einen Kaffee und iss ein Sandwich!';
			showalert(txt);
			$scope.endTurn();
			break;

		case 'NOT_ENOUGH_MONEY':
			console.log('New Phase NOT_ENOUGH_MONEY');
			txt = 'Du hast leider nicht genug Geld für eine Aktion\nDie Runde geht automatisch an den nächsten Spieler.';
			showalert(txt);
			$scope.endTurn();
			break;

		case 'BUY_PROPERTY':
			console.log('New Phase BUY_PROPERTY');
			var name = $scope.state.currentFieldProperty.name;
			var cost = $scope.state.currentFieldProperty.propertyCost;
			if (confirm('Willst du das Modul "' + name + '" besuchen?\nKostet nur ' + cost + ' CHF!')) {
				$scope.buyProperty();
			}
			$scope.endTurn();
			break;

		case 'JUMP':
			console.log('New Phase JUMP');
			if (confirm("Willst du zu einem anderem Feld springen?\nKostet nur 100 CHF!")) {
				$scope.jump();
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
			txt = 'Du musst jetzt eine Chance Karte ziehen!';
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
			txt = 'Du wurdest beim plagieren erwischt und musst deshalb zur Schuldirektorin!\nDu warnst sie das wenn sie dich von der Schule schmeist, du ihr Geheimnis rumerzählst.\nSie lässt dich sofort gehen.';
			showalert(txt);
			$scope.endTurn();
			break;

		case 'DETENTION':
			console.log('New Phase DETENTION');
			var verhandeln = false;
			var leftTimeInDetention = $scope.state.currentPlayer.leftTimeInDetention;
			txt = 'Du bist noch bei der Schuldirektorin. Sie möchte dich von der Uni verweisen...\n';
			if (leftTimeInDetention > 0) {
				if ($scope.state.currentPlayer.money >= 100) {
					txt += 'Willst du versuchen über deinen Schulverweis zu verhandeln, anstatt Sie zu bestechen (100 CHF)?\nDu hast noch ' + leftTimeInDetention + 'Varhandlungsversuche.';
					verhandeln = confirm(txt);
					txt = '';
				} else if ($scope.state.currentPlayer.money < 100) {
					txt = 'Du hast nicht genug Geld um Sie zu bestechen, dir bleibt nichts übrig als zu verhandeln.\n';
					verhandeln = true;
				}
			} else {
				verhandeln = false;
				txt += 'Du hast scho 3mal versucht Sie zu überzeugen; Ohne Erfolg!\n';
				if ($scope.state.currentPlayer.money >= 100) {
					txt += 'Dir bleibt nichts anders übrig als sie zu bestechen.\n';
				} else if ($scope.state.currentPlayer.money < 100) {
					txt += 'Ausserdem hast du nicht genug Geld um Sie zu bestechen.\n';
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
	var list = '';
	var playerlist = $scope.state.players;

	if ($scope.state.currentPlayer !== null) {
		var currplayer = $scope.state.currentPlayer;

		list += '<table>';
		for (let i = 0; i < playerlist.length; i++) {
			list += '<tr><td class="listtoken ' + playerlist[i].token.type.toLowerCase() + '"></td>';
			if (playerlist[i].name == currplayer.name) {
				list += '<td><b>' + playerlist[i].name + ': ' + playerlist[i].money + '</b></td></tr>';
			} else {
				list += '<td>' + playerlist[i].name + ': ' + playerlist[i].money + '</td></tr>';
			}
		}
		list += '</table>';

		$playerlist.html(list);
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
		prevind = $('#pos' + list[i].token.currFieldIndex).find("td." + type).first();
		prevind.toggleClass('leer');
		prevind.toggleClass('used ' + type);
	}
	$gameboard.hide();
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
		console.log('getJSON: ' + path);
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
		const mode = gamemode;
		$gamemode = mode;
		$gamemodeop.hide();
		if (mode == 'SINGLE') {
			text = 'Playing against AI';
		} else if (mode == 'MULTI') {
			text = 'Playing in Multiplayer Modus';
		}
		$gamemodetext.text(text);
		$joinwaiting.show();
		$joinName.show();
	}

	$scope.join = function (token) {
		var playerlist = '';
		var list;
		const mode = $gamemode;
		$scope.getOp('join?name=' + $scope.username + "&token=" + token,
			function (success) {
				// Check if adding new Payer was successfull
				if (success) {
					console.log('success: added Player: ' + $scope.username);
				}
				else {
					console.error('error: adding Player: ' + $scope.username);
					alert('Error: adding Player: ' + $scope.username);
				}

				// List all added Players
				list = $scope.state.players;
				for (let i = 0; i < list.length; i++) {
					type = list[i].token.type.toLowerCase();
					playerlist += list[i].name + ': ' + type.charAt(0).toUpperCase() + type.slice(1) + '<br>';
				}
				if ((mode == 'SINGLE' && list.length == 1) || (mode == 'MULTI' && list.length >= 2)) {
					$joinstart.show();
				}
				if ((mode == 'SINGLE' && list.length < 1) || (mode == 'MULTI' && list.length < 4)) {
					$joinName.show();
				}
				$joinToken.hide();
				$joininglist.html(playerlist);
			});
	}

	// Begin Game
	$scope.start = function () {
		$scope.getOp('start?gamemode=' + $gamemode,
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
						index = '#pos' + list[i].token.currFieldIndex;
						td = $(index).find("td.leer").first();
						td.toggleClass('leer');
						td.toggleClass('used ' + type);
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

	// Check how many Dices should be rolled
	$scope.checkDice = function () {
		var path;
		if ($scope.state.phase == 'DETENTION') {
			path = 'rolltwodice';
			$scope.rollDice(path);
		} else {
			path = 'rolldice?firstDice=';
			input = prompt('Gib den gewünschten Wert des Ersten Würfels ein (1-6):', '');
			// Check if Player pressed abort
			if (input != null) {
				// Check if Player submited a acceptable value
				if (input == '' || input < 1 || input > 6) {
					console.warn('error: The entered value is out of Range (1-6)!');
					alert('Error: Value needs to be between 1-6!');
				} else {
					console.log('success: assign value of first Dice');
					path += input;
					$scope.rollDice(path);
				}
			}
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


	// Ask Player if he wants to Teleport
	$scope.jump = function () {

		var moveby = prompt('Gib die gewünschte Entfernung ein (1-35):', '');
		// Check if Player submited a acceptable value
		while (moveby == null || moveby == '' || moveby < 1 || moveby > 35) {
			if (moveby == null || moveby == '') {
				console.warn('error: assign value of jump Distance.');
				alert('Error: Please try again!');
			} else if (moveby < 1 || moveby > 35) {
				console.warn('error: Value needs to be between 1-35');
				alert('Error: Please try again!');
			}
			var moveby = prompt('Gib die gewünschte Entfernung ein (1-35):', '');
		}

		// TODO: Player has to pay 100$ 
		$scope.getOp('jumpplayer?moveby=' + moveby,
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
		$scope.getOp('userwantstobuy?currentFieldIndex=' + currplayer.token.currFieldIndex,
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
	$scope.showCard = async function () {
		const txt = 'Du musst auf einem Chance Feld landen um eine Chance Karte ziehen zu dürfen.';
		showalert(txt);
	}

	// Player pressed closepopup
	$scope.closealert = function () {
		$alertpopup.hide();
	}
});