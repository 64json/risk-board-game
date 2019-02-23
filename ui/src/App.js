import React, {Component} from 'react';
import {BrowserRouter as Router} from 'react-router-dom';

import './App.css';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      risk: {
        connected: false,
        user: null,
        games: [],
        game: null,
      },
      playerName: '',
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
        console.log({risk, data});

        const isObject = obj => obj && obj.constructor === {}.constructor;

        const deepMerge = (dist, src) => {
          Object.keys(src).forEach(key => {
            const value = src[key];
            if (isObject(dist[key]) && isObject(value)) {
              deepMerge(dist[key], value);
            } else {
              dist[key] = value;
            }
          });
        };
        deepMerge(risk, data);
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

  handleChangePlayerName = e => {
    const playerName = e.target.value;
    this.setState({playerName});
  };

  handleChangeGameName = e => {
    const gameName = e.target.value;
    this.setState({gameName});
  };

  handleRegister = e => {
    const {playerName} = this.state;
    this.send('register', [playerName]);
  };

  handleCreateGame = e => {
    const {gameName} = this.state;
    this.send('createGame', [gameName]);
  };

  handleJoinGame = gameId => {
    this.send('joinGame', [gameId]);
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
    const {playerName, gameName} = this.state;
    const {connected, user, player, game, games} = this.state.risk;

    return (
      <Router>
        <div className="App">
          <div>{connected ? 'Connected' : 'Connecting ...'}</div>
          <hr/>
          {
            connected &&
            <div>
              {
                user == null ?
                  <div>
                    <input type="text" value={playerName}
                           placeholder="Player Name"
                           onChange={this.handleChangePlayerName}/>
                    <button onClick={this.handleRegister}>Register</button>
                  </div> :
                  <div>
                    <div>
                      User: {user.name}
                    </div>
                    <hr/>
                    {
                      game ?
                        <div>
                          <div>
                            Game: {game.name}
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
                                Players: {game.players.map(player => `${player.name}${player.id === game.owner ? ' (owner) ' : ''}`).join(', ')}
                              </div>
                          }
                          <div>
                            <div>
                              {
                                game.playing ? 'Playing ...' : 'Waiting ...'
                              }
                            </div>
                            {
                              user.player === game.owner && !game.playing &&
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
          }
        </div>
      </Router>
    );
  }
}

export default App;
