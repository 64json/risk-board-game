import React, {Component} from 'react';
import {connect} from 'react-redux';
import socket from '../../common/socket';
import {classes} from '../../common/utils';
import {Territory} from '../../components';
import {actions} from '../../reducers';
import coords from './coords';
import './stylesheet.scss';

class Game extends Component {
  constructor(props) {
    super(props);

    this.state = {
      fromTerritoryId: null,
    };
  }

  handleStartGame = () => {
    socket.startGame();
  };

  handleLeaveGame = () => {
    socket.leaveGame();
  };

  handleEndAttack = () => {
    socket.endAttack();
  };

  handleEndFortify = () => {
    socket.endFortify();
  };

  handleAllotArmy = territory => {
    socket.allotArmy(territory.id);
  };

  handleAssignArmies = territory => {
    this.props.prompt('Enter the number of armies to assign: ', armies => {
      socket.assignArmies(territory.id, Number(armies) | 0);
    });
  };

  handleClickFromTerritory = territory => {
    const fromTerritoryId = territory.id;
    this.setState({fromTerritoryId});
  };

  handleAttack = territory => {
    const {fromTerritoryId} = this.state;
    const toTerritoryId = territory.id;
    this.props.prompt('Enter the number of attacking dice to roll: ', attackingDiceCount => {
      socket.createAttack(fromTerritoryId, toTerritoryId, Number(attackingDiceCount) | 0);
      this.setState({fromTerritoryId: null});
    });
  };

  handleFortify = territory => {
    const {fromTerritoryId} = this.state;
    const toTerritoryId = territory.id;
    this.props.prompt('Enter the number of armies to move: ', armies => {
      socket.fortify(fromTerritoryId, toTerritoryId, Number(armies) | 0);
      this.setState({fromTerritoryId: null});
    });
  };

  getInstruction = () => {
    const {game, player} = this.props.server;
    if (!game.playing) {
      return {
        text: 'Waiting ...',
      };
    } else {
      const me = game.players.find(p => p.id === player);
      if (me.allotting) {
        return {
          text: 'Choose an unoccupied territory to allot an army to',
          onClick: this.handleAllotArmy,
        };
      } else if (me.assigning) {
        return {
          text: 'Choose your territory to assign your armies to.',
          onClick: this.handleAssignArmies,
        };
      } else if (me.attacking) {
        const {fromTerritoryId} = this.state;
        if (!fromTerritoryId) {
          return {
            text: 'Choose your territory to attack from.',
            onClick: this.handleClickFromTerritory,
          };
        } else {
          return {
            text: 'Choose the territory to attack.',
            onClick: this.handleAttack,
          };
        }
      } else if (me.fortifying) {
        const {fromTerritoryId} = this.state;
        if (!fromTerritoryId) {
          return {
            text: 'Choose your territory to move armies from.',
            onClick: this.handleClickFromTerritory,
          };
        } else {
          return {
            text: 'Choose your territory to move armies to.',
            onClick: this.handleFortify,
          };
        }
      } else if (game.turnIndex === null) {
        return {
          text: 'Waiting on others to assign their armies.',
        };
      } else {
        const currentPlayer = game.players[game.turnIndex];
        if (game.attack && !game.attack.done) {
          const territories = game.continents.flatMap(continent => continent.territories);
          const attackingTerritory = territories.find(territory => territory.id === game.attack.fromTerritory);
          const defendingTerritory = territories.find(territory => territory.id === game.attack.toTerritory);
          const attackingPlayer = game.players.find(player => player.id === attackingTerritory.owner);
          if (defendingTerritory.owner === player) {
            this.props.prompt(`${attackingPlayer.name} is attacking your ${defendingTerritory.name}. Enter the number of defending dice to roll: `, defendingDiceCount => {
              socket.defend(Number(defendingDiceCount) | 0);
            });
          }
        }
        return {
          text: `${currentPlayer.name} is ${currentPlayer.allotting ? 'allott' : currentPlayer.assigning ? 'assign' : currentPlayer.attacking ? 'attack' : currentPlayer.fortifying ? 'fortify' : 'doing someth'}ing.`,
        };
      }
    }
  };

  render() {
    const {game, player} = this.props.server;
    const instruction = this.getInstruction();

    let currentPlayer = game.players[game.turnIndex];

    let territories = null;
    const links = [];
    if (game.playing) {
      territories = game.continents.flatMap(continent => continent.territories);
      territories.forEach(territory => {
        territory.adjacencyTerritories.forEach(adjacencyTerritory => {
          const link = {
            from: territories.indexOf(territory),
            to: territories.findIndex(t => t.id === adjacencyTerritory),
          };
          if (!links.find(l => l.from === link.to && l.to === link.from)) {
            links.push(link);
          }
        });
      });
    }

    return (
      <div className="Game">
        <div className="sidebar">
          <div className="title">
            {game.name}
          </div>
          <div className="players">
            {
              game.players.map((player, i) => {
                return (
                  <div key={player.id}
                       className={classes('player', game.playing && `player-${i + 1}`, currentPlayer && currentPlayer.id === player.id && 'current')}>
                    {
                      game.playing &&
                      <span className="turn">
                        {i + 1}.&nbsp;
                      </span>
                    }
                    <span
                      className={classes('name', socket.player === player.id && 'you')}>
                      {player.name}
                    </span>
                    <span className="status">
                      {
                        game.playing ?
                          `${player.assignedArmies} armies` :
                          player.id === game.owner && 'Host'
                      }
                    </span>
                  </div>
                );
              })
            }
          </div>
          <div className="instruction">
            {instruction.text}
          </div>
          <div className="actions">
            {
              player === game.owner && !game.playing &&
              <button onClick={this.handleStartGame}>
                Start
              </button>
            }
            {
              currentPlayer && currentPlayer.id === player && currentPlayer.attacking &&
              <button onClick={this.handleEndAttack}>
                End Attack
              </button>
            }
            {
              currentPlayer && currentPlayer.id === player && currentPlayer.fortifying &&
              <button onClick={this.handleEndFortify}>
                Skip Fortifying
              </button>
            }
            <button onClick={this.handleLeaveGame}>
              Leave
            </button>
          </div>
        </div>
        <div className="board">
          {
            game.playing && (
              <div className="map">
                <svg viewBox="0 0 80 50" preserveAspectRatio="none"
                     className="svg">
                  {
                    links.map(({from, to}) => {
                      const {x: fromX, y: fromY} = coords[from];
                      const {x: toX, y: toY} = coords[to];
                      return (
                        <line className="link" key={from + ' ' + to}
                              x1={fromX * 80} y1={fromY * 50}
                              x2={toX * 80} y2={toY * 50}/>
                      );
                    })
                  }
                </svg>
                {
                  territories.map((territory, i) => {
                    const {x, y} = coords[i];
                    const playerIndex = game.players.findIndex(p => p.id === territory.owner);
                    return (
                      <Territory
                        className={territory.owner && `player-${playerIndex + 1}`}
                        key={territory.id} territory={territory}
                        onClick={instruction.onClick}
                        style={{
                          top: `${(y * 100).toFixed(2)}%`,
                          left: `${(x * 100).toFixed(2)}%`,
                        }}/>
                    );
                  })
                }
              </div>
            )
          }
        </div>
      </div>
    );
  }
}

export default connect(({server}) => ({server}), actions)(Game);

