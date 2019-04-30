import React, {Component} from 'react';
import {connect} from 'react-redux';
import socket from '../../common/socket';
import {classes} from '../../common/utils';
import {Territory} from '../../components';
import {actions} from '../../reducers';
import './stylesheet.scss';

class Game extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedTerritory: null,
      fromTerritory: null,
      toTerritory: null,
    };
  }

  handleStartGame = () => {
    socket.startGame();
  };

  handleLeaveGame = () => {
    socket.leaveGame();
  };

  handleEndAttack = () => {
    this.setState({fromTerritory: null});
    socket.endAttack();
  };

  handleEndFortify = () => {
    this.setState({fromTerritory: null});
    socket.endFortify();
  };

  handleAllotArmy = territory => {
    socket.allotArmy(territory.id);
  };

  handleAssignArmies = territory => {
    this.setState({selectedTerritory: territory});
    this.props.prompt('Enter the number of armies to assign: ', armies => {
      socket.assignArmies(territory.id, Number(armies) | 0);
    }, null, () => {
      this.setState({selectedTerritory: null});
    });
  };

  handleClickFromTerritory = fromTerritory => {
    this.setState({fromTerritory});
  };

  handleAttack = toTerritory => {
    const {fromTerritory} = this.state;
    if (fromTerritory === toTerritory) {
      this.setState({fromTerritory: null});
    } else {
      this.setState({toTerritory});
      this.props.prompt('Enter the number of attacking dice to roll: ', attackingDiceCount => {
        socket.createAttack(fromTerritory.id, toTerritory.id, Number(attackingDiceCount) | 0);
      }, null, () => {
        this.setState({fromTerritory: null, toTerritory: null});
      });
    }
  };

  handleFortify = toTerritory => {
    const {fromTerritory} = this.state;
    if (fromTerritory === toTerritory) {
      this.setState({fromTerritory: null});
    } else {
      this.setState({toTerritory});
      this.props.prompt('Enter the number of armies to move: ', armies => {
        socket.fortify(fromTerritory.id, toTerritory.id, Number(armies) | 0);
      }, null, () => {
        this.setState({fromTerritory: null, toTerritory: null});
      });
    }
  };

  getInstruction = () => {
    const {game, player} = this.props.server;
    if (!game.playing) {
      return {
        text: 'Waiting for players to join ...',
      };
    } else {
      if (game.winner) {
        if (player === game.winner) {
          return {
            text: 'You won the game!',
          };
        } else {
          return {
            text: `<b>${game.winner.name}</b> won the game!`,
          };
        }
      } else if (player.allotting) {
        return {
          text: 'Choose an unoccupied territory to allot an army to.',
          isEnabled: territory => !territory.owner,
          onClick: this.handleAllotArmy,
        };
      } else if (player.assigning) {
        return {
          text: 'Choose your territory to assign your armies to.',
          isEnabled: territory => territory.owner === player,
          onClick: this.handleAssignArmies,
        };
      } else if (player.attacking) {
        if (game.attack && !game.attack.done) {
          return {
            text: 'Waiting for response from the defender ...',
          };
        } else {
          const {fromTerritory, toTerritory} = this.state;
          if (!fromTerritory) {
            return {
              text: 'Choose your territory to attack from.',
              isEnabled: territory => territory.owner === player,
              onClick: this.handleClickFromTerritory,
            };
          } else if (!toTerritory) {
            return {
              text: 'Choose the territory to attack.',
              isEnabled: territory => territory === fromTerritory || (territory.owner !== player && fromTerritory.adjacencyTerritories.includes(territory)),
              onClick: this.handleAttack,
            };
          } else {
            return {
              text: 'You are attacking.',
            };
          }
        }
      } else if (player.fortifying) {
        const {fromTerritory, toTerritory} = this.state;
        if (!fromTerritory) {
          return {
            text: 'Choose your territory to move armies from.',
            isEnabled: territory => territory.owner === player,
            onClick: this.handleClickFromTerritory,
          };
        } else if (!toTerritory) {
          return {
            text: 'Choose your territory to move armies to.',
            isEnabled: territory => territory === fromTerritory || (territory.owner === player && fromTerritory.adjacencyTerritories.includes(territory)),
            onClick: this.handleFortify,
          };
        } else {
          return {
            text: 'You are fortifying.',
          };
        }
      } else if (game.turnIndex === null) {
        return {
          text: 'Waiting on others to assign their armies.',
        };
      } else {
        const currentPlayer = game.players[game.turnIndex];
        if (game.attack && !game.attack.done) {
          const attackingTerritory = game.attack.fromTerritory;
          const defendingTerritory = game.attack.toTerritory;
          const attackingPlayer = attackingTerritory.owner;
          if (defendingTerritory.owner === player) {
            this.props.prompt(`<b>${attackingPlayer.name}</b> is attacking your <b>${defendingTerritory.name}</b>.<br>Enter the number of defending dice to roll: `, defendingDiceCount => {
              socket.defend(Number(defendingDiceCount) | 0);
            });
          }
        }
        return {
          text: `<b>${currentPlayer.name}</b> is ${currentPlayer.allotting ? 'allott' : currentPlayer.assigning ? 'assign' : currentPlayer.attacking ? 'attack' : currentPlayer.fortifying ? 'fortify' : 'doing someth'}ing.`,
        };
      }
    }
  };

  renderAttack() {
    const {game} = this.props.server;
    const {fromTerritory, toTerritory} = game.attack;
    const attacker = fromTerritory.owner;
    const defender = toTerritory.owner;
    const attackingDice = game.attack.attackingDice.split('').map(die => ({
      value: Number(die),
      win: false,
      lose: false,
    }));
    const defendingDice = game.attack.defendingDice.split('').map(die => ({
      value: Number(die),
      win: false,
      lose: false,
    }));
    const sortedAttackingDice = [...attackingDice].sort((a, b) => b.value - a.value);
    const sortedDefendingDice = [...defendingDice].sort((a, b) => b.value - a.value);
    const minDiceCount = Math.min(attackingDice.length, defendingDice.length);
    let rolledDiceCount = 0;
    while (rolledDiceCount < minDiceCount && toTerritory.armies > 0) {
      const attackingDie = sortedAttackingDice[rolledDiceCount];
      const defendingDie = sortedDefendingDice[rolledDiceCount];
      if (attackingDie.value > defendingDie.value) {
        attackingDie.win = true;
        attackingDie.order = rolledDiceCount;
        defendingDie.lose = true;
        defendingDie.order = rolledDiceCount;
      } else {
        attackingDie.lose = true;
        attackingDie.order = rolledDiceCount;
        defendingDie.win = true;
        defendingDie.order = rolledDiceCount;
      }
      rolledDiceCount += 1;
    }
    return (
      <div className="attack">
        <div
          className={classes('player', 'attacker', `player-${attacker.color}`)}>
          <span className="name">{attacker.name}</span>
          <div className="dice">
            {
              attackingDice.map((die, i) => (
                <div key={i}
                     className={classes('die', die.win && 'win', die.lose && 'lose', `order-${die.order + 1}`)}>
                  {die.value}
                </div>
              ))
            }
          </div>
        </div>
        <div
          className={classes('player', 'defender', `player-${defender.color}`)}>
          <span className="name">{defender.name}</span>
          <div className="dice">
            {
              defendingDice.map((die, i) => (
                <div key={i}
                     className={classes('die', die.win && 'win', die.lose && 'lose', `order-${die.order + 1}`)}>
                  {die.value}
                </div>
              ))
            }
          </div>
        </div>
      </div>
    );
  }

  render() {
    const {game, player} = this.props.server;
    const {selectedTerritory, fromTerritory, toTerritory} = this.state;
    const instruction = this.getInstruction();

    const currentPlayer = game.players[game.turnIndex];
    const territories = game.continents && game.continents.flatMap(continent => continent.territories);

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
                       className={classes('player', game.playing && `player-${player.color}`, currentPlayer && currentPlayer === player && 'active')}>
                    {
                      game.playing &&
                      <span className="turn">
                        {i + 1}.&nbsp;
                      </span>
                    }
                    <span
                      className={classes('name', this.props.server.player === player && 'you')}>
                      {player.name}
                    </span>
                    {
                      game.playing ?
                        player.assignedArmies > 0 &&
                        <span className="status">
                          + {player.assignedArmies} armies
                        </span> :
                        player === game.owner &&
                        <span className="status">
                          Host
                        </span>
                    }
                  </div>
                );
              })
            }
          </div>
          <div className="instruction"
               dangerouslySetInnerHTML={{__html: instruction.text}}/>
          <div className={classes('actions', `player-${player.color}`)}>
            {
              player === game.owner && !game.playing &&
              <button className="action" onClick={this.handleStartGame}>
                Start
              </button>
            }
            {
              currentPlayer && currentPlayer === player && currentPlayer.attacking && !game.attack &&
              <button className="action" onClick={this.handleEndAttack}>
                End Attack
              </button>
            }
            {
              currentPlayer && currentPlayer === player && currentPlayer.fortifying &&
              <button className="action" onClick={this.handleEndFortify}>
                Skip Fortifying
              </button>
            }
          </div>
          <div className="empty"/>
          <button className="leave" onClick={this.handleLeaveGame}>
            Leave
          </button>
        </div>
        <div className="board">
          {
            game.playing && (
              <div className="map">
                <svg viewBox="0 0 80 50" preserveAspectRatio="none"
                     className="svg">
                  {
                    territories.flatMap(fromTerritory => fromTerritory.adjacencyTerritories.map(toTerritory => {
                      return (
                        <line className="link"
                              key={fromTerritory.id + '-' + toTerritory.id}
                              x1={fromTerritory.x * 80}
                              y1={fromTerritory.y * 50}
                              x2={toTerritory.x * 80} y2={toTerritory.y * 50}/>
                      );
                    }))
                  }
                </svg>
                {
                  territories.map(territory => (
                    <Territory
                      key={territory.id} territory={territory}
                      selected={territory === selectedTerritory}
                      from={territory === fromTerritory || (game.attack && game.attack.fromTerritory === territory)}
                      to={territory === toTerritory || (game.attack && game.attack.toTerritory === territory)}
                      onClick={instruction.onClick}
                      enabled={instruction.isEnabled && instruction.isEnabled(territory)}
                      style={{
                        top: `${(territory.y * 100).toFixed(2)}%`,
                        left: `${(territory.x * 100).toFixed(2)}%`,
                      }}/>
                  ))
                }
              </div>
            )
          }
          <div className="attackContainer">
            {
              game.attack && game.attack.done &&
              this.renderAttack()
            }
          </div>
          <div className="winnerContainer">
            {
              game.winner &&
              <span>🏆 {game.owner.name} 🏆</span>
            }
          </div>
        </div>
      </div>
    );
  }
}

export default connect(({server}) => ({server}), actions)(Game);

