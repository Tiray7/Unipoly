
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

// Update UI when phase changes
async function phaseChange($scope) {
	const newphase = $scope.state.phase;

	if (newphase == 'ROLLING') {
		$scope.diceVal1 = $scope.state.firstDice;
		$scope.diceVal2 = $scope.state.secondDice;
		const total = $scope.diceVal1 + $scope.diceVal2;
		var text = '<b>' + $scope.state.currentPlayer.name + ' rolled:</b><br>' + $scope.diceVal1 +
			' + ' + $scope.diceVal2 + '<br>Total: ' + total;
		$dicesgif.show();
		$dicesgif.delay(1100).fadeOut(200);
		$rolledvaluetext.html(text)
		await Sleep(1350);
		$scope.moveToken();
	} else if (newphase == 'BUY_PROPERTY') {
		console.log('New Phase BUY_PROPERTY');
		$scope.buyproperty();
	} else if (newphase == 'JUMP') {
		console.log('New Phase Jump');
		$scope.jump();
	} else if (newphase == 'GO') {
		console.log('New Phase GO');
	} else if (newphase == 'SHOWCARD') {
		console.log('New Phase Showcard');
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

var app = angular.module('monopolyApp', []);
app.controller('Controller', function ($scope) {

	$scope.diceVal1;
	$scope.diceVal2;

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
		if (confirm("Are you sure you want to Reset the Game?")) {
			$scope.getOp('resetgame',
				function (success) {
					// Check if Game Reset was a succsess
					if (success) {
						console.log('success: resetGame');
						$gameboard.hide();
						$menu.show();
						$joinToken.hide();
						$joinName.hide();
						$joininglist.hide();
						$joinstart.hide();
						$joinwaiting.hide();
						$gamemodetext.text('Choose a Gamemode!');
						$gamemodeop.show();
					} else {
						console.error('error: resetGame');
						alert('Error: Please try again!');
					}
				});
		}
	}

	// Move the PlayerToken and checkfieldoptions 
	$scope.moveToken = function () {
		const type = $scope.state.currentPlayer.token.type.toLowerCase();
		const prevind = $('#pos' + $scope.state.currentPlayer.token.prevFieldIndex).find("td." + type).first();
		const currind = $('#pos' + $scope.state.currentPlayer.token.currFieldIndex).find("td.leer").first();
		prevind.toggleClass('leer');
		prevind.toggleClass('used ' + type);
		currind.toggleClass('leer');
		currind.toggleClass('used ' + type);

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

	// Roll the dice(s)
	$scope.rollDice = function () {

		var diceVal1 = prompt('Gib den gewünschten Wert des Ersten Würfels ein (1-6):', '');

		// Check if Player pressed abort
		if (diceVal1 != null) {

			// Check if Player submited a acceptable value
			if (diceVal1 == '' || diceVal1 < 1 || diceVal1 > 6) {
				console.warn('error: The entered value is out of Range (1-6)!');
				alert('Error: Value needs to be between 1-6!');
			} else {
				console.log('success: assign value of first Dice');

				// Send Value of first Dice to java
				$scope.getOp('rolldice?firstDice=' + diceVal1,
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
		}
	}

	// Ask Player if he wants to Teleport
	$scope.jump = function () {
		var next = confirm("Willst du zu einem anderem Feld springen?\nKostet nur 100 CHF!");
		if (next) {
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
			$scope.getOp('moveplayer?moveby=' + moveby,
				function (success) {
					// Check if  success
					if (success) {
						console.log('success: MovePlayer');
						$scope.moveToken();
					} else {
						console.error('error: MovePlayer');
					}
				});
		} else {
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
	}

	// Ask Player if he wants to buy Property
	$scope.buyproperty = function () {
		const currplayer = $scope.state.currentPlayer;
		var next = confirm("Willst du dieses Modul besuchen?\nKostet nur ${PropertyValue}CHF!");
		$scope.getOp('userwantstobuy?buy=' + next + '&currentFieldIndex=' + currplayer.token.currFieldIndex,
			function (success) {
				// Check if  success
				if (success) {
					console.log('success: MovePlayer');
				} else {
					console.error('error: MovePlayer');
				}
			});
	}
});