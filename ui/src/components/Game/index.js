import React, {Component} from 'react';

import server from '../../common/server';
import {Continent, Map} from '../';
import './stylesheet.css';

class Game extends Component {
  constructor(props) {
    super(props);

    this.state = {
      attackingTerritory: '',
      enemyTerritory: ''
    };
  }

  handleStartGame = () => {
    server.startGame();
  };

  handleLeaveGame = () => {
    server.leaveGame();
  };

  handleProceedWithTurn = () => {
    server.proceedWithTurn();
  };

  handleAttack = () => {
    server.attack(this.attackingTerritory, this.enemyTerritory)
  };

  handleAttackDeclaration = (attackingTerritory) => {
    server.attackDeclaration(attackingTerritory)
  };

  handleEnemyDeclaration = (enemyTerritory) => {
    server.enemyDeclaration(enemyTerritory)
  };

  render() {
    const {game, player} = server;
    console.log(server);

    let playerOnMove = game.playing && game.turnIndex != null && game.players[game.turnIndex];

    return (
      <div>
        {
          game.playing &&
          <h1>{
            playerOnMove ?
              playerOnMove.id === player ? 'Your turn.' : `${playerOnMove.name}'s turn.` :
              'Waiting on other players to assign their armies.'
          }</h1>
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
            Pass Turn to Next Player
            </button>
            }
          {
            game.playing &&
            <input type="attackingTerritory" placeholder={"Your Attacking Territory"}
                   value={this.attackingTerritory}
                   onChange={this.handleAttackDeclaration(this.attackingTerritory)}/>
          }
          {
            game.playing &&
            <input type="opponentTerritory" placeholder={"Enemy's Defending Territory"}
                   value={this.enemyTerritory}
                   onChange={this.handleEnemyDeclaration(this.enemyTerritory)}/>
          }
          {
            game.playing &&
            <button onClick={this.handleAttack()}
            disabled={playerOnMove.id !== player || playerOnMove.assignedArmies > 0}>
              Attack
            </button>
          }
        </div>
        <button onClick={this.handleLeaveGame}>
          Leave
        </button>
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

