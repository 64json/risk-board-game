import React, {Component} from 'react';

import server from '../../common/server';
import './stylesheet.css';

class Lobby extends Component {
  constructor(props) {
    super(props);

    this.state = {
      gameName: '',
    };
  }

  handleChangeGameName = e => {
    const gameName = e.target.value;
    this.setState({gameName});
  };

  handleCreateGame = e => {
    const {gameName} = this.state;
    const playerName = window.prompt('Please enter the player name.');
    server.createGame(gameName, playerName);
  };

  handleJoinGame = gameId => {
    const playerName = window.prompt('Please enter the player name.');
    server.joinGame(gameId, playerName);
  };

  render() {
    const {gameName} = this.state;
    const {games} = server;

    return (
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
    );
  }
}

export default Lobby;
