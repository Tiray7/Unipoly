
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
var $yesnopopup;
var $selectpopup;
var $gameoverpopup;
var $numbernpccon;
var $numbernpc = 0;
var $diceinput;
var $checkendturn;
var $aftershowtext;
var $sellingprice;
var $forselling;
var $sell_btn;

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

	// Popups
	$diceinput = $('#chooseDice_popup');
	$alertpopup = $('#alert_popup');
	$yesnopopup = $('#yesno_popup');
	$selectpopup = $('#select_popup');
	$sell_btn = $('#sell_btn');
	$gameoverpopup = $('#gameover_popup');

	$('.space').prepend('<table class="tablecon"><tr><td></td><td></td></tr><tr><td></td><td></td></tr></table>');
	$('.tablecon').find('td').addClass('leer');
	$lastphase = 'NONE';
	$checkendturn = false;
	$sellingprice = 0;
	$forselling = JSON.parse('{}');
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

function resetHTML(list) {
	// List all added Players
	var prevind, type;
	for (let i = 0; i < list.length; i++) {
		type = list[i].token.type.toLowerCase();
		prevind = $(`#pos${list[i].token.currFieldIndex}`).find("td." + type).first();
		prevind.toggleClass('leer');
		prevind.toggleClass(`used ${type}`);
	}
	$gameoverpopup.hide();
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

// Move the PlayerToken
function moveToken($scope) {
	const type = $scope.state.currentPlayer.token.type.toLowerCase();
	if (!$(`#pos${$scope.state.currentPlayer.token.currFieldIndex}`).find("td").hasClass(type)) {
		console.log('moveToken');
		const prevind = $(`#pos${$scope.state.currentPlayer.token.prevFieldIndex}`).find("td." + type).first();
		const currind = $(`#pos${$scope.state.currentPlayer.token.currFieldIndex}`).find("td.leer").first();
		prevind.toggleClass('leer');
		prevind.toggleClass(`used ${type}`);
		currind.toggleClass('leer');
		currind.toggleClass(`used ${type}`);
	}
}

function showalert(text, bool, txt = '') {
	$checkendturn = bool;
	$aftershowtext = txt;
	$alertpopup.find('.popup-p').html(text);
	$alertpopup.show();
}

function showGameOver(text) {
	$gameoverpopup.find('.popup-end').html(text);
	$gameoverpopup.show();
}

function showyesOrno(text) {
	$yesnopopup.find('.popup-p').text(text);
	$yesnopopup.show();
}

function showSelection($scope) {
	$sellingprice = $scope.state.currentPlayer.debt;
	txt = `Wähle die Module die du verkaufen möchtest um deine Schulden abzahlen zu können!`;
	$selectpopup.find('.popup-p').text(txt);
	txt = `Du schuldest dd noch ${$sellingprice}CHF.`;
	$selectpopup.find('.popup-div').text(txt);
	if (Object.entries($scope.state.currentPlayer.ownedModuls).length === 0) {
		console.log('Game Over');
	} else {
		Object.entries($scope.state.currentPlayer.ownedModuls).forEach(function (modul) {
			console.log(modul[0]);
			console.log(modul[1]);
			field = $(`#pos${modul[0]}`).find('.container');
			field.toggleClass('mine');
		});
	}
	$selectpopup.show();
}

function handleDetention($scope, bool = false, areadyasked = false) {
	txt = '';
	if (!areadyasked) {
		var leftTimeInDetention = $scope.state.currentPlayer.leftTimeInDetention;
		txt = 'Du bist noch bei der Schuldirektorin. Sie möchte dich von der Uni verweisen...';
		if (leftTimeInDetention > 0) {
			if ($scope.state.currentPlayer.money >= 100) {
				txt += `\nWillst du versuchen über deinen Schulverweis zu verhandeln, anstatt Sie zu bestechen (100 CHF)?\nDu hast noch ${leftTimeInDetention} Verhandlungsversuche.`;
				showyesOrno(txt);
				return;
			} else {
				txt = '<br>Du hast nicht genug Geld um Sie zu bestechen, dir bleibt nichts übrig als zu verhandeln.';
				bool = true;
			}
		} else {
			bool = false;
			txt += '<br>Du hast schon 3mal versucht Sie zu überzeugen; Ohne Erfolg!';
			if ($scope.state.currentPlayer.money >= 100) {
				txt += '<br>Dir bleibt nichts anders übrig als sie zu bestechen.';
			}
		}
	}

	if (bool) {
		txt += 'Würfle ein Pash um Sie zu überzeugen.';
		showalert(txt, false);
	} else {
		txt += '<br>Du bestichst sie also.';
		showalert(txt, false, 'payDetentionRansom');
	}
}

// Update UI when phase changes
async function phaseChange($scope, bool = false, areadyasked = false) {
	const newphase = $scope.state.phase;
	var txt;

	switch (newphase) {
		case 'SHOWANDSWITCH':
			console.log('New Phase SHOWANDSWITCH');
			moveToken($scope);
			showalert($scope.state.displayMessage, true);
			break;

		case 'SHOWCARD':
			console.log('New Phase SHOWCARD');
			showalert($scope.state.displayMessage, false);
			break;

		case 'WAITING':
			console.log('New Phase WAITING');
			if ($lastphase == 'NONE') break;
			txt = `<b>${$scope.state.currentPlayer.name}</b> ist am Zug.`;
			showalert(txt, false);
			break;

		case 'ROLLING':
			if ($lastphase == 'DETENTION') {
				txt = `<b>${$scope.state.currentPlayer.name} würfelt:</b><br>${$scope.state.firstDice} und ${$scope.state.secondDice}`;
				$dicesgif.show();
				$dicesgif.delay(1100).fadeOut(200);
				$rolledvaluetext.html(txt);
				await Sleep(1300);
				if ($scope.state.rolledPash) {
					txt = `Du hast es geschafft die Schuldirektorin zu überzeugen mit ${$scope.state.firstDice} und ${$scope.state.secondDice}`;
					txt += '<br>Du darfst nun ganz normal deine Runde fortführen.';
					showalert(txt, false, 'leaveDetention');
				} else {
					txt = 'Du hast es nicht geschafft die Schuldirektorin zu überzeugen.';
					showalert(txt, true);
				}
			} else {
				const total = $scope.state.firstDice + $scope.state.secondDice;
				txt = `<b>${$scope.state.currentPlayer.name} würfelt:</b><br>${$scope.state.firstDice} + ${$scope.state.secondDice}<br>Total: ${total}`;
				$dicesgif.show();
				$dicesgif.delay(1100).fadeOut(200);
				$rolledvaluetext.html(txt);
				await Sleep(1350);
				moveToken($scope);
				$scope.checkField();
			}
			break;

		case 'BUY_PROPERTY':
			console.log('New Phase BUY_PROPERTY');
			if (areadyasked) {
				if (bool) {
					$scope.buyProperty();
				} else {
					$scope.endTurn();
				}
			} else {
				txt = `Willst du das Modul "${$scope.state.currentField.name}" besuchen?\nKostet nur ${$scope.state.currentField.propertyCost}CHF!`;
				showyesOrno(txt);
				return;
			}
			break;

		case 'JUMP':
			console.log('New Phase JUMP');
			if (areadyasked) {
				if (bool) {
					txt = 'Klick auf das Feld auf das du springen willst!';
					showalert(txt, false);
				} else {
					txt = 'Dann endet deine Runde hier';
					showalert(txt, true);
				}
			} else {
				txt = "Willst du zu einem anderem Feld springen?\nKostet nur 100 CHF!";
				showyesOrno(txt);
				return;
			}
			break;

		case 'DETENTION':
			console.log('New Phase DETENTION');
			handleDetention($scope, bool, areadyasked);
			break;

		case 'INDEBT':
			console.log('New Phase INDEBT');
			txt = $scope.state.displayMessage;
			txt += `<br>Du musst nun ${$scope.state.currentPlayer.debtor.name} zuerst deine Schulden zurück zahlen.<br>Der Schuldbetrag ist ${$scope.state.currentPlayer.debt}CHF.`;
			showalert(txt, false, 'showselection');
			break;

		case 'QUIZTIME':
			console.log('New Phase QUIZTIME');
			txt = `Beantworte folgende Frage:`;
			showalert(txt, true);
			break;

		case 'GAMEOVER':
			console.log('New Phase GAMEOVER');
			showalert($scope.state.displayMessage, false, 'gameoverMessage');
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
	if ($lastphase !== $scope.state.phase || $lastphase == 'SHOWANDSWITCH') {
		phaseChange($scope);
		$lastphase = $scope.state.phase;
	}

	$scope.$apply();
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
		$lastphase = 'NONE';
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
						txt = `<b>${$scope.state.currentPlayer.name}</b> ist am Zug.`;
						showalert(txt, false);
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
			showalert(txt, false);
		} else if ($scope.state.phase == 'JUMP') {
			txt = 'Klick auf das Feld, auf das du springen willst!';
			showalert(txt, false);
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

	// Ask Player wants to buy Property
	$scope.buyProperty = function () {
		$scope.getOp('userwantstobuy',
			function (success) {
				// Check if  success
				if (success) {
					console.log('success: userwantstobuy');
				} else {
					console.error('error: userwantstobuy');
				}
			});
	}

	// Ask Player wants to buy Property
	$scope.sellProperty = function () {
		var array = []
		Object.entries($forselling).forEach(function (element) {
			array.push(element[0]);
		});
		$scope.getOp(`payoffdebt?FieldIndexes=${array}`,
			function (success) {
				// Check if  success
				if (success) {
					$(`.mine`).forEach(function (element) {
						element.removeClass('mine');
					});
					$(`.selling`).forEach(function (element) {
						element.removeClass('selling');
					});
					console.log('success: sellproperty');
				} else {
					console.error('error: sellproperty');
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
		$checkendturn = false;
		if ($scope.state.phase == 'SHOWCARD') {
			$scope.getOp('readcard',
				function (success) {
					// Check if  success
					if (success) {
						console.log('success: readcard');
					} else {
						console.error('error: readcard');
					}
				});
		} else {
			txt = 'Du musst auf einem Chance Feld landen um eine Chance Karte ziehen zu dürfen.';
			showalert(txt, false);
		}
	}

	// Player klicked on a Field
	$scope.fieldaction = function (FieldIndex) {
		var fieldInfo = $scope.state.board.fields[FieldIndex];
		var currentfield = $(`#pos${FieldIndex}`).find('.container');
		if ($scope.state.phase == 'JUMP') {
			$scope.jump(FieldIndex);
		} else if ($scope.state.phase == 'INDEBT' && (currentfield.hasClass('mine') || currentfield.hasClass('selling'))) {
			if (currentfield.hasClass('mine')) {
				$sellingprice -= fieldInfo.propertyCost;
				$forselling[FieldIndex] = fieldInfo;
			} else {
				$sellingprice += fieldInfo.propertyCost;
				delete $forselling[FieldIndex];
			}
			currentfield.toggleClass('mine');
			currentfield.toggleClass('selling');
			txt = `<p>Du schuldest ${$scope.state.currentPlayer.debtor.name} noch ${$sellingprice}CHF.</p>`;
			txt += `<ul>`;
			console.log($forselling);
			Object.entries($forselling).forEach(function (element) {
				txt += `<li>${element[1].name}: Verkaufspreis ${element[1].propertyCost}CHF</li>`;
			});
			txt += `</ul>`;
			$selectpopup.find('.popup-div').html(txt);
			if ($sellingprice <= 0) {
				$sell_btn.show();
			} else {
				$sell_btn.hide();
			}
		} else {
			if (fieldInfo.label == 'PROPERTY') {
				txt = `<h2>${fieldInfo.name}</h2>`;
				if (fieldInfo.ownerIndex == -1) {
					txt += `Gehört: Niemandem<br>`;
				} else {
					txt += `Gehört: ${$scope.state.players[fieldInfo.ownerIndex].name}<br>`;
					txt += `Miete: ${fieldInfo.currentRent}CHF<br>`;
				}
				txt += `Kaufpreis: ${fieldInfo.propertyCost}CHF<br>`;
				txt += `Miete LV1: ${fieldInfo.rentLV1}CHF<br>`;
				txt += `Miete LV2: ${fieldInfo.rentLV2}CHF<br>`;
				txt += `Miete LV3: ${fieldInfo.rentLV3}CHF<br>`;
				txt += `Miete LV4: ${fieldInfo.rentLV4}CHF<br>`;
				txt += `Miete LV5: ${fieldInfo.rentLV5}CHF<br>`;
			} else {
				txt = `<h2>${fieldInfo.label.charAt(0) + fieldInfo.label.slice(1).toLowerCase()} Feld</h2>`;
				txt += `${fieldInfo.explanation}`;
			}
			showalert(txt, false);
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
		txt += `ModulGruppen: ${player.roadOwned}`;
		showalert(txt, false);
	}

	// Display the Yes or No Popup
	$scope.yesOrno = function (bool) {
		$yesnopopup.hide();
		phaseChange($scope, bool, true);
	}

	// Player pressed closepopup
	$scope.closealert = function () {
		$alertpopup.hide();
		if ($checkendturn) {
			$scope.endTurn();
		} else if ($aftershowtext != '') {
			if ($aftershowtext == 'payDetentionRansom') {
				$scope.payDetentionRansom();
			} else if ($aftershowtext == 'leaveDetention') {
				$scope.leaveDetention();
			} else if ($aftershowtext == 'showselection') {
				showSelection($scope);
			} else if ($aftershowtext == 'gameoverMessage') {
				showGameOver($scope.state.gameoverString);
			}
		}
	}
});