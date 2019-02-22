import React, {Component} from 'react';
import {BrowserRouter as Router} from 'react-router-dom';

import './App.css';

const Tech = ({match}) => {
  return <div>Current Route: {match.params.tech}</div>;
};


class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      risk: {
        connected: false,
        player: null,
        game: null,
        players: [],
        games: [],
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
        console.log(data);
        const risk = {...this.state.risk, ...data};
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
    const {connected, player, game, games} = this.state.risk;

    return (
      <Router>
        <div className="App">
          <div>{connected ? 'Connected' : 'Connecting ...'}</div>
          <hr/>
          {
            connected &&
            <div>
              {
                player == null ?
                  <div>
                    <input type="text" value={playerName}
                           placeholder="Player Name"
                           onChange={this.handleChangePlayerName}/>
                    <button onClick={this.handleRegister}>Register</button>
                  </div> :
                  <div>
                    <div>
                      Player: {player.name}
                    </div>
                    <hr/>
                    {
                      game ?
                        <div>
                          <div>
                            Game: {game.name}
                          </div>
                          <div>
                            Players: {game.players.map(player => player.name).join(', ')}
                          </div>
                          <div>
                            <div>
                              {
                                game.playing ? 'Playing ...' : 'Waiting ...'
                              }
                            </div>
                            {
                              player.id === game.owner && !game.playing &&
                              <button onClick={this.handleStartGame}>
                                Start
                              </button>
                            }
                            <button onClick={this.handleLeaveGame}>
                              Leave
                            </button>
                          </div>
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
