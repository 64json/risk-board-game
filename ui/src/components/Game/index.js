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
      fromTerritoryId: null,
      toTerritoryId: null,
      showingAttackId: null,
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
    if (fromTerritoryId === toTerritoryId) {
      this.setState({fromTerritoryId: null});
    } else {
      this.setState({toTerritoryId});
      this.props.prompt('Enter the number of attacking dice to roll: ', attackingDiceCount => {
        socket.createAttack(fromTerritoryId, toTerritoryId, Number(attackingDiceCount) | 0);
      }, null, () => {
        this.setState({fromTerritoryId: null, toTerritoryId: null});
      });
    }
  };

  handleFortify = territory => {
    const {fromTerritoryId} = this.state;
    const toTerritoryId = territory.id;
    if (fromTerritoryId === toTerritoryId) {
      this.setState({fromTerritoryId: null});
    } else {
      this.setState({toTerritoryId});
      this.props.prompt('Enter the number of armies to move: ', armies => {
        socket.fortify(fromTerritoryId, toTerritoryId, Number(armies) | 0);
      }, null, () => {
        this.setState({fromTerritoryId: null, toTerritoryId: null});
      });
    }
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
          isEnabled: territory => !territory.owner,
          onClick: this.handleAllotArmy,
        };
      } else if (me.assigning) {
        return {
          text: 'Choose your territory to assign your armies to.',
          isEnabled: territory => territory.owner === me.id,
          onClick: this.handleAssignArmies,
        };
      } else if (me.attacking) {
        if (game.attack && !game.attack.done) {
          return {
            text: 'Waiting for response from the defender ...',
          };
        } else {
          const {fromTerritoryId, toTerritoryId} = this.state;
          if (!fromTerritoryId) {
            return {
              text: 'Choose your territory to attack from.',
              isEnabled: territory => territory.owner === me.id,
              onClick: this.handleClickFromTerritory,
            };
          } else if (!toTerritoryId) {
            const territories = game.continents.flatMap(continent => continent.territories);
            return {
              text: 'Choose the territory to attack.',
              isEnabled: territory => territory.owner !== me.id && territories.find(territory => territory.id === fromTerritoryId).adjacencyTerritories.includes(territory.id),
              onClick: this.handleAttack,
            };
          } else {
            return {
              text: 'You are attacking.',
            };
          }
        }
      } else if (me.fortifying) {
        const {fromTerritoryId, toTerritoryId} = this.state;
        if (!fromTerritoryId) {
          return {
            text: 'Choose your territory to move armies from.',
            isEnabled: territory => territory.owner === me.id,
            onClick: this.handleClickFromTerritory,
          };
        } else if(!toTerritoryId) {
          const territories = game.continents.flatMap(continent => continent.territories);
          return {
            text: 'Choose your territory to move armies to.',
            isEnabled: territory => territory.owner === me.id && territories.find(territory => territory.id === fromTerritoryId).adjacencyTerritories.includes(territory.id),
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

  componentWillReceiveProps(nextProps) {
    const {game} = nextProps.server;
    const attackId = game.attack && game.attack.id;
    if (this.state.showingAttackId !== attackId) {
      this.setState({showingAttackId: attackId});
    }
  }

  renderAttack() {
    const {game} = this.props.server;
    const territories = game.continents.flatMap(continent => continent.territories);
    const fromTerritory = territories.find(territory => territory.id === game.attack.fromTerritory);
    const toTerritory = territories.find(territory => territory.id === game.attack.toTerritory);
    const attacker = game.players.find(player => player.id === fromTerritory.owner);
    const defender = game.players.find(player => player.id === toTerritory.owner);
    const attackerIndex = game.players.findIndex(p => p.id === attacker.id);
    const defenderIndex = game.players.findIndex(p => p.id === defender.id);
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
          className={classes('player', 'attacker', `player-${attackerIndex + 1}`)}>
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
          className={classes('player', 'defender', `player-${defenderIndex + 1}`)}>
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
    const {fromTerritoryId, toTerritoryId, showingAttackId} = this.state;
    const instruction = this.getInstruction();

    let currentPlayer = game.players[game.turnIndex];

    let territories = null;
    const links = [];
    if (game.playing) {
      territories = game.continents.flatMap(continent => continent.territories);
      territories.forEach(territory => {
        territory.adjacencyTerritories.forEach(adjacencyTerritory => {
          const link = {
            from: territory,
            to: territories.find(territory => territory.id === adjacencyTerritory),
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
              currentPlayer && currentPlayer.id === player && currentPlayer.attacking && !game.attack &&
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
                      return (
                        <line className="link" key={from.id + '-' + to.id}
                              x1={from.x * 80} y1={from.y * 50}
                              x2={to.x * 80} y2={to.y * 50}/>
                      );
                    })
                  }
                </svg>
                {
                  territories.map(territory => (
                    <Territory
                      key={territory.id} territory={territory}
                      from={territory.id === fromTerritoryId || game.attack && game.attack.fromTerritory === territory.id}
                      to={territory.id === toTerritoryId || game.attack && game.attack.toTerritory === territory.id}
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
              game.attack && game.attack.done && game.attack.id === showingAttackId &&
              this.renderAttack()
            }
          </div>
        </div>
      </div>
    );
  }
}

export default connect(({server}) => ({server}), actions)(Game);

