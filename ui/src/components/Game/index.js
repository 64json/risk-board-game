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

  render() {
    const {game, player} = server;

    return (
      <div>
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
