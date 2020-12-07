
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
var $quizpopup;
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
	$quizpopup = $('#quiz_popup');

	$('.space').prepend('<table class="tablecon"><tr><td></td><td></td></tr><tr><td></td><td></td></tr></table>');
	$('.tablecon td').addClass('leer');
	$('.color-bar').prepend('<table><tr><td class="field_owner"></td><td class="field_lvl"></td></tr></table>');
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

function resetHTML() {
	console.log("resetHTML()");
	$('td.used').attr('class', 'leer');
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
	if (!$(`#pos${$scope.state.currentPlayer.token.currFieldIndex} .tablecon td`).hasClass(type)) {
		console.log(`moveToken: ${$scope.state.currentPlayer.name} / ${type};`);
		$(`.tablecon .${type}`).attr('class', 'leer');
		const currind = $(`#pos${$scope.state.currentPlayer.token.currFieldIndex} .tablecon .leer`).first();
		currind.attr('class', `used ${type}`);
	}
}

function update_fieldowner($scope) {
	const unowned = Object.entries($scope.state.board.fields).filter(x => x[1].label == 'PROPERTY' & x[1].ownerBank);
	const owned = Object.entries($scope.state.board.fields).filter(x => x[1].label == 'PROPERTY' & !x[1].ownerBank);
	unowned.forEach(function (x) {
		$(`#pos${x[0]} .field_owner`).attr('class', 'field_owner');
		$(`#pos${x[0]} .field_lvl`).text('');
	});
	owned.forEach(function (x) {
		$(`#pos${x[0]} .field_owner`).attr('class', `field_owner listtoken ${$scope.state.players[x[1].ownerIndex].token.type.toLowerCase()}`);
		$(`#pos${x[0]} .field_lvl`).text(`${x[1].currentRent} CHF`);
	});
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
	$yesnopopup.find('.popup-p').html(text);
	$yesnopopup.show();
}

function showquiz($scope) {
	var question = $scope.state.questions[$scope.state.currentPlayer.token.currFieldIndex];
	console.log(question);
	txt = `Beantworte folgende Frage:<br>${question.question}`;
	$quizpopup.find('.popup-p').html(txt);
	$quizpopup.find('#quizanswer_1').html(`<h4>${question.option1}</h4>`);
	$quizpopup.find('#quizanswer_2').html(`<h4>${question.option2}</h4>`);
	$quizpopup.find('#quizanswer_3').html(`<h4>${question.option3}</h4>`);
	$quizpopup.show();
}

function showSelection($scope) {
	$sellingprice = $scope.state.currentPlayer.debt;
	txt = `Wähle die Module die du verkaufen möchtest um deine Schulden abzahlen zu können!`;
	$selectpopup.find('.popup-p').text(txt);
	txt = `Du schuldest dd noch ${$sellingprice}CHF.`;
	$selectpopup.find('.popup-div').text(txt);
	Object.entries($scope.state.currentPlayer.ownedModuls).forEach(function (modul) {
		field = $(`#pos${modul[0]} .container`);
		field.toggleClass('mine');
	});
	$selectpopup.show();
}

function handleDetention($scope, bool = false, areadyasked = false) {
	txt = '';
	if (!areadyasked) {
		var leftTimeInDetention = $scope.state.currentPlayer.leftTimeInDetention;
		txt = 'Du bist noch bei der Schuldirektorin. Sie möchte dich von der Uni verweisen...';
		if (leftTimeInDetention > 0) {
			if ($scope.state.currentPlayer.money >= 100) {
				txt += `<br>Willst du versuchen über deinen Schulverweis zu verhandeln, anstatt Sie zu bestechen (100 CHF)?<br>Du hast noch ${leftTimeInDetention} Verhandlungsversuche.`;
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
		txt += 'Du bestichst sie also.';
		showalert(txt, false, 'payDetentionRansom');
	}
}

// Update UI when phase changes
async function phaseChange($scope, bool = false, areadyasked = false) {
	const newphase = $scope.state.phase;
	var txt;

	switch (newphase) {
		case 'SHOWANDSWITCH':
			console.log('Phase: SHOWANDSWITCH');
			moveToken($scope);
			showalert($scope.state.displayMessage, true);
			break;

		case 'SHOWCARD':
			console.log('Phase: SHOWCARD');
			showalert($scope.state.displayMessage, false);
			break;

		case 'WAITING':
			console.log('Phase: WAITING');
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
			console.log('Phase: BUY_PROPERTY');
			if (areadyasked) {
				if (bool) {
					$scope.buyProperty();
				} else {
					$scope.endTurn();
				}
			} else {
				txt = `Willst du das Modul "${$scope.state.currentField.name}" besuchen?<br>Die Einschreibegebühr beträgt nur ${$scope.state.currentField.propertyCost}CHF!`;
				showyesOrno(txt);
				return;
			}
			break;

		case 'JUMP':
			console.log('Phase: JUMP');
			if (areadyasked) {
				if (bool) {
					txt = 'Klick auf das Feld auf das du springen willst!';
					showalert(txt, false);
				} else {
					txt = 'Dann endet deine Runde hier';
					showalert(txt, true);
				}
			} else {
				txt = "Willst du zu einem anderem Feld springen?<br>Kostet nur 100 CHF!";
				showyesOrno(txt);
				return;
			}
			break;

		case 'DETENTION':
			console.log('Phase: DETENTION');
			handleDetention($scope, bool, areadyasked);
			break;

		case 'INDEBT':
			console.log('Phase: INDEBT');
			$forselling = JSON.parse('{}');
			txt = $scope.state.displayMessage;
			txt += `<br>Du musst nun ${$scope.state.currentPlayer.debtor.name} zuerst deine Schulden zurück zahlen.<br>Der Schuldbetrag ist ${$scope.state.currentPlayer.debt}CHF.`;
			showalert(txt, false, 'showselection');
			break;

		case 'QUIZTIME':
			console.log('Phase: QUIZTIME');
			showquiz($scope);
			break;

		case 'GAMEOVER':
			console.log('Phase: GAMEOVER');
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
				tr += `<td><b>${list[i].name}: ${list[i].money}CHF, ${list[i].ects}ECTS</b></td>`;
			} else {
				tr += `<td>${list[i].name}: ${list[i].money}CHF, ${list[i].ects}ECTS</td>`;
			}
			$playerlist.eq(i).html(tr);
		}
	}

	// Check if phase changed, update accordingly
	if ($lastphase !== $scope.state.phase || $lastphase == 'SHOWANDSWITCH') {
		phaseChange($scope);
		$lastphase = $scope.state.phase;
	}
	update_fieldowner($scope);

	$scope.$apply();
}

var app = angular.module('monopolyApp', []);
app.controller('Controller', function ($scope) {

	poll($scope);

	$scope.getOp = function (path, callback) {
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
					console.log(`success: Start Game, ${$gamemode} & ${$numbernpc} NPCs`);
					$menu.hide();
					$gameboard.show();

					var type, index, td;
					// Iterate through all Players
					const list = $scope.state.players;
					for (let i = 0; i < list.length; i++) {
						type = list[i].token.type.toLowerCase();
						td = $(`#pos${list[i].token.currFieldIndex} .tablecon .leer`).first();
						td.attr('class', `used ${type}`);
						txt = `<b>${$scope.state.currentPlayer.name}</b> ist am Zug.`;
						showalert(txt, false);
					}
				} else {
					console.error('error: Start Game');
					alert('Error: Please try again!');
				}
			});
	}

	$scope.resetGame = function (bool = false) {
		if (!bool) {
			bool = confirm("Are you sure you want to Reset the Game?");
		}
		if (bool) {
			$scope.getOp('resetgame',
				function (success) {
					// Check if Game Reset was a succsess
					if (success) {
						console.log('success: resetGame');
						resetHTML();
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
			$scope.rollDice('rolltwodice');
			// If Player landed on ChanceCards he cant roll dices
		} else if ($scope.state.phase == 'SHOWCARD') {
			txt = 'Du bist auf einem Chance Feld gelandet! Du musst eine Chance Karte ziehen!';
			showalert(txt, false);
		} else if ($scope.state.phase == 'JUMP') {
			txt = 'Klick auf das Feld, auf das du springen willst!';
			showalert(txt, false);
		} else {
			$diceinput.toggle();
		}
	}

	// Roll the dice(s)
	$scope.rollDice = function (path) {
		$scope.getOp(path,
			function (success) {
				// Check if starting turn worked
				if (success) {
					console.log(`success: rollDice -> ${path}`);
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
		$scope.getOp('endturn',
			function (success) {
				if (success) {
					console.log(`success: endTurn -> now ${$scope.state.currentPlayer.name}`);
				} else {
					console.error('error: endTurn');
				}
			});
	}

	// Ask Player wants to buy Property
	$scope.buyProperty = function () {
		$scope.getOp('userwantstobuy',
			function (success) {
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
		$scope.getOp(`payoffdebt?indexes=${array}`,
			function (success) {
				if (success) {
					console.log(`success: sellproperties -> [${array}]`);
					$(`.mine`).removeClass('mine');
					$(`.selling`).removeClass('selling');
					$selectpopup.hide();
					$scope.endTurn();
				} else {
					console.error('error: sellproperty');
				}
			});
	}

	// Besteche die Direktorin
	$scope.payDetentionRansom = function () {
		$scope.getOp('paydetentionransom',
			function (success) {
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
		var currentfield = $(`#pos${FieldIndex} .container`);
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
					txt += `Miete: ${fieldInfo.currentRent} CHF<br>`;
				}
				txt += `Kaufpreis: ${fieldInfo.propertyCost} CHF<br>`;
				txt += `Credits: ${fieldInfo.currentECTSLevel} ECTS<br>`;
				txt += `Miete LV1: ${fieldInfo.rentLV1} CHF<br>`;
				txt += `Miete LV2: ${fieldInfo.rentLV2} CHF<br>`;
				txt += `Miete LV3: ${fieldInfo.rentLV3} CHF<br>`;
				txt += `Miete LV4: ${fieldInfo.rentLV4} CHF<br>`;
				txt += `Miete LV5: ${fieldInfo.rentLV5} CHF<br>`;
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
		txt += `Geld: ${player.money} CHF<br>`;
		txt += `Credits: ${player.ects} ECTS<br>`;
		txt += `Frei Karte: ${player.freeCard}<br>`;
		txt += `Muss Nachsitzen: ${player.leftTimeInDetention}<br>`;
		txt += `Module: ${player.propertyOwned}`;
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

	$scope.quizanswer = function (x) {
		$quizpopup.hide();
		const correctans = (x == $scope.state.questions[$scope.state.currentPlayer.token.currFieldIndex].solution);
		$scope.getOp(`quizanswer?x=${correctans}`,
			function (success) {
				if (success) {
					console.log('success: quizanswer');
				} else {
					console.error('error: quizanswer');
				}
			});
		if (correctans) {
			showalert(`Das war die richtige Antwort!<br>Du erhältst ${$scope.state.currentField.currentECTSLevel} ECTS`, true);
		} else {
			showalert('Das war die falsche Antwort!<br>Du erhältst keine ECTS', true);
		}
	}
});