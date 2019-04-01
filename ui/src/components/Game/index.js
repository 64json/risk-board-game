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
    server.proceedWithTurn();
  };

  render() {
    const {game, player} = server;
    console.log(server);

    let playerOnMove = game.playing && game.players[game.turnIndex];

    return (
      <div>
        {
          game.playing &&
          <h1>{playerOnMove.id === player ? 'Your turn.' : `${playerOnMove.name}'s turn.`}</h1>
        }
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
          {
            game.playing &&
            <button onClick={this.handleProceedWithTurn}
                    disabled={playerOnMove.id !== player || playerOnMove.assignedArmies > 0}>
              Proceed With Turn
            </button>
          }
          <button onClick={this.handleLeaveGame}>
            Leave
          </button>
        </div>
        <hr/>
        {
          game.playing &&
          game.continents.map(continent => (
            <Continent key={continent.id} continent={continent}/>
          ))
        }
        <hr/>
        <Map/>
      </div>
    );
  }
}

export default Game;

