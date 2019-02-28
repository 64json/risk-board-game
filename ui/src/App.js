import React, {Component} from 'react';
import {BrowserRouter as Router} from 'react-router-dom';

import './App.css';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      risk: {
        connected: false,
        games: [],
        game: null,
        player: null,
      },
      gameName: '',
    };
  }

  componentDidMount() {
    this.ws = new WebSocket('ws://localhost:9000/api/ws');
    this.ws.onmessage = message => {
      const data = JSON.parse(message.data);
      if ('error' in data) {
        alert(data.error);
        console.error(data.error);
      } else {
        const risk = JSON.parse(JSON.stringify(this.state.risk));

        const isObject = obj => obj && obj.constructor === {}.constructor;
        const isArray = obj => obj && obj.constructor === [].constructor;

        // merge updates into the risk object in this.state
        const deepMerge = (dist, src) => {
          Object.keys(src).forEach(key => {
            const value = src[key];
            if (isArray(dist[key]) && isArray(value)) {
              if (dist[key].length) {
                if (isObject(dist[key][0])) {
                  dist[key] = value.map(e => {
                    if (isObject(e)) {
                      const object = dist[key].find(object => object.id === e.id) || {};
                      deepMerge(object, e);
                      return object;
                    } else {
                      return dist[key].find(object => object.id === e);
                    }
                  });
                } else {
                  dist[key] = value;
                }
              } else {
                dist[key] = value;
              }
            } else if (isObject(dist[key]) && isObject(value)) {
              if (dist[key].id === value.id) {
                deepMerge(dist[key], value);
              } else {
                dist[key] = value;
              }
            } else {
              dist[key] = value;
            }
          });
        };
        deepMerge(risk, data);

        console.log(risk);

        this.setState({risk});
      }
    };
  }

  componentWillUnmount() {
    this.ws.close();
  }

  send(method, args) {
    this.ws.send(JSON.stringify({method, args}));
  }

  handleChangeGameName = e => {
    const gameName = e.target.value;
    this.setState({gameName});
  };

  handleCreateGame = e => {
    const {gameName} = this.state;
    const playerName = window.prompt('Please enter the player name.');
    this.send('createGame', [gameName, playerName]);
  };

  handleJoinGame = gameId => {
    const playerName = window.prompt('Please enter the player name.');
    this.send('joinGame', [gameId, playerName]);
  };

  handleStartGame = () => {
    const {game} = this.state.risk;
    this.send('startGame', [game.id]);
  };

  handleLeaveGame = () => {
    const {game} = this.state.risk;
    this.send('leaveGame', [game.id]);
  };

  render() {
    const {gameName} = this.state;
    const {connected, games, game, player} = this.state.risk;

    return (
      <Router>
        <div className="App">
          <div>{connected ? 'Connected' : 'Connecting ...'}</div>
          <hr/>
          {
            connected &&
            <div>
              {
                game && player ?
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
                    {
                      game.playing &&
                      game.continents.map(continent => (
                        <div key={continent.id}>
                          {continent.name}
                          {
                            continent.territories.map(territory => (
                              <div key={territory.id}>
                                -- {territory.name}
                              </div>
                            ))
                          }
                          <br/>
                        </div>
                      ))
                    }
                  </div> :
                  <div>
                    <div>
                      <input type="text" value={gameName}
                             placeholder="Game Name"
                             onChange={this.handleChangeGameName}/>
                      <button onClick={this.handleCreateGame}>
                        Create Game
                      </button>
                    </div>
                    {
                      games.map(game => (
                        <div key={game.id}>
                          {game.name} ({game.players.length}/6)
                          <button
                            onClick={() => this.handleJoinGame(game.id)}>
                            Join
                          </button>
                        </div>
                      ))
                    }
                  </div>
              }
            </div>
          }
        </div>
      </Router>
    );
  }
}

export default App;
