import React, {Component} from 'react';

import server from '../../common/server';
import {Continent, Map} from '../';
import './stylesheet.css';

class Game extends Component {
  handleStartGame = () => {
    server.startGame();
  };

  handleLeaveGame = () => {
    server.leaveGame();
  };

  handleProceedWithTurn = () => {
    const {player} = server;
    server.proceedWithTurn(player);
  }

  render() {
    const {game, player} = server;
    console.log(server);

    let canProceed;
    if(game.playing) {
      const playerObject = game.players[game.turnIndex];
      canProceed = playerObject.id === player && playerObject.assignedArmies === 0;
    }

    const turn = game.players.map((player, i) => ({id: player.id, name:player.name, turn: i + 1}))
    const currentTurn = turn.filter(x => x.id === player)[0].turn

    return (
      <div>
        <div>
          {
            ////turn is notified from here
            game.playing? <div><h1>Your turn is {currentTurn}</h1></div> : ''
          }
        </div>
        <div>
          Game: {game.name}
        </div>
        <div>
          Player: {game.players.find(p => p.id === player).name}
        </div>
        {
          game.playing ?
            <div>
              Players: {
              game.players
                .map((player, i) => `${player.name} (${player.id === game.owner ? 'owner / ' : ''}armies: ${player.assignedArmies} / turn: ${i + 1})`).join(', ')
            }
            {
              //Greyed out or not present unless it is your turn and you have assigned all of your armies
              <button onClick={this.handleProceedWithTurn} disabled={!canProceed}>
                Proceed With Turn
              </button>
            }
            </div> :
            <div>
              Players: {game.players.map(player => `${player.name}${player.id === game.owner ? ' (owner)' : ''}`).join(', ')}
            </div>
        }
        <div>
          <div>
            {
              game.playing ? 'Playing ...' : 'Waiting ...'
            }
          </div>
          {
            player === game.owner && !game.playing &&
            <button onClick={this.handleStartGame}>
              Start
            </button>
          }
          <button onClick={this.handleLeaveGame}>
            Leave
          </button>
        </div>
        <hr/>
        <Map/>
        <hr/>
        {
          game.playing &&
          game.continents.map(continent => (
            <Continent key={continent.id} continent={continent}/>
          ))
        }
      </div>
    );
  }
}

export default Game;
