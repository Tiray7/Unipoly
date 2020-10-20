
var $menu;
var $menuToggle;

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
});

function poll($scope) {
	$.getJSON('state', function (json) {
		update($scope, json);

		setTimeout(function () {
			poll($scope)
		}, 1000);
	});
}

// Initialize UI when phase changes
function phaseChange($scope) {
	// Cover tiles during management phase
	if ($scope.state.phase == 'TURN') {
		$scope.state.board.tiles.forEach(function (tile, i, arr) {
			var $cell = $('#' + i);
			if (tile.ownerIndex != $scope.state.currentPlayerIndex) {
				$cell.addClass('covered');
			}
		});
	}
	else {
		// Clear tile selection
		$('.covered').removeClass('covered');
	}
	// Clear the selection
	$scope.currentTile = null;
	$('.selected').removeClass('selected');
	// Disable UI for inactive player
	if (($scope.state.phase == 'TURN' ||
		$scope.state.phase == 'BUY_PROPERTY' ||
		$scope.state.phase == 'JAILED' ||
		$scope.state.phase == 'SHOWCARD' ||
		$scope.state.phase == 'CHEAT_ROLL' ||
		$scope.state.phase == 'SHOWRENT')
		&&
		$scope.currentPlayer.name !== $scope.username) {

		$('#phase-ui').css('pointer-events', 'none');
		$('#phase-ui').css('opacity', '0.5');
	}
	else {
		$('#phase-ui').css('pointer-events', 'all');
		$('#phase-ui').css('opacity', '1.0');
	}
	if ($scope.state.phase == "AUCTION") {
		$scope.bid = $scope.state.highestBid;
	}
}

var lastPhase = null;
function update($scope, json) {
	$scope.state = json;

	var list = '';
	var playerlist = $scope.state.players;
	var currindex = $scope.state.currentPlayerIndex;

	// Get bindable objects for angular based on current indices
	$scope.currentPlayer = playerlist[currindex];

	/*
	if ($scope.currentPlayer != null && $scope.state.phase != "TURN") {
		$scope.currentTile = $scope.state.board.tiles[$scope.currentPlayer.token.tileIndex];
	}
	*/

	for (let i = 0; i < playerlist.length; i++) {
		if (i == currindex) {
			list += '<b>' + playerlist[i].name + ': ' + playerlist[i].money + '</b><br>';
		} else {
			list += playerlist[i].name + ': ' + playerlist[i].money + '<br>';
		}
	}

	$playerlist.html(list);

	/*
	// If phase changed, update accordingly
	if (lastPhase !== $scope.state.phase) {
		phaseChange($scope);
		lastPhase = $scope.state.phase;
	}
	else if ($scope.state.phase == "TURN") {
		// Check for sold tiles
		$scope.state.board.tiles.forEach(function(tile, i, arr) {
			var $cell = $('#' + i);
			if (!$cell.is('.covered') && tile.ownerIndex != $scope.state.currentPlayerIndex) {
				$cell.removeClass('selected');
				$cell.addClass('covered');
			}
		});
	}
	*/
	$scope.$apply();
}

var app = angular.module('monopolyApp', []);
app.controller('Controller', function ($scope) {

	$scope.gamemode;
	$scope.diceVal1;
	$scope.diceVal2;

	poll($scope);

	$scope.getOp = function (path, callback) {

		console.log(path);

		//Load JSON-encoded data from the server using a GET HTTP request.
		$.getJSON(path, function (json) {
			update($scope, json);
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

	$scope.chooseToken = function () {
		$joinName.hide();
		$joinToken.show();
	}

	$scope.gamemode = function (gamemode) {
		var text;
		const mode = gamemode;
		$scope.gamemode = mode;
		$gamemodeop.hide();
		if (mode == 'SINGLE') {
			text = 'Playing against AI';
		} else if (mode == 'MULTI') {
			text = 'Playing in Multiplayer Modus';
		}
		$gamemodetext.text(text);
		$joinwaiting.show();
		$joinName.show();
		console.log($scope);
	}

	$scope.join = function (token) {
		var playerlist = '';
		var list;
		const mode = $scope.gamemode;
		$scope.getOp('join?name=' + $scope.username + "&token=" + token,
			function (success) {
				// Check if adding new Payer was successfull
				if (success) {
					console.log('successfully added Player: ' + $scope.username);
				}
				else {
					console.error('failed adding Player: ' + $scope.username);
					alert('failed adding Player: ' + $scope.username);
				}

				// List all added Players
				list = $scope.state.players;
				for (let i = 0; i < list.length; i++) {
					playerlist += list[i].name + ': ' + list[i].token.type + '<br>';

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
		console.log($scope);
	}

	// Begin Game
	$scope.start = function () {
		$scope.getOp('start?gamemode=' + $scope.gamemode,
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
					console.error('failed: Start Game');
					alert('Error: Please try again!');
				}
			});
		console.log($scope);
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
						console.error('failed: resetGame');
						alert('Error: Please try again!');
					}
				});
		}
		console.log($scope);
	}

	// Startturn
	$scope.rollDice = function () {

		var diceVal1;
		// if Player is jailed both dices get rolled
		if ($scope.state.phase == 'WAITING') {
			diceVal1 = prompt('Enter value of your first Dice (1-6):', '');
		} else if ($scope.state.phase == 'JAILED') {
			diceVal1 = -1;
		}

		// Check if Player submited a acceptable value
		if (diceVal1 == null || diceVal1 == "") {
			console.error('failed: assign value of first Dice');
			alert('Error: Please try again!');
		} else {
			if ((diceVal1 >= 1 && diceVal1 <= 6) || $scope.state.phase == 'JAILED') {
				console.log('success: assign value of first Dice');

				// Send Value of first Dice to java
				$scope.getOp('rolldice?diceval1=' + diceVal1,
					function (success) {
						// Check if starting turn worked
						if (success) {
							console.log('success: rollDice');
							$scope.diceVal1 = $scope.state.rolledValue1;
							$scope.diceVal2 = $scope.state.rolledValue2;
							const total = $scope.diceVal1 + $scope.diceVal2;
							var text = '<b>' + $scope.currentPlayer.name + ' rolled:</b><br>' + $scope.diceVal1 +
								' + ' + $scope.diceVal2 + '<br>Total: ' + total;
							$dicesgif.show();
							$dicesgif.delay(1100).fadeOut(200);
							$rolledvaluetext.html(text)
							$scope.moveToken().delay(1000);
						} else {
							console.error('failed: rollDice');
							alert('Error: Please try again!');
						}
					});
			} else {
				console.error('Error: The entered value is out of Range (1-6)!');
				alert('Error: The entered value is out of Range (1-6)!\nPlease try again!');
			}
		}
		console.log($scope);
	}

	$scope.moveToken = function () {
		const type = $scope.currentPlayer.token.type.toLowerCase();
		const prevind = $('#pos' + $scope.currentPlayer.token.prevFieldIndex).find("td." + type).first();
		const currind = $('#pos' + $scope.currentPlayer.token.currFieldIndex).find("td.leer").first();
		prevind.toggleClass('leer');
		prevind.toggleClass('used ' + type);
		currind.toggleClass('leer');
		currind.toggleClass('used ' + type);
	}
});