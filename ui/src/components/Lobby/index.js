import React, {Component} from 'react';
import {connect} from 'react-redux';
import socket from '../../common/socket';
import {actions} from '../../reducers';

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
    e.preventDefault();

    const {gameName} = this.state;
    this.props.prompt('Please enter the player name.', playerName => {
      socket.createGame(gameName, playerName);
    });
  };

  handleJoinGame = gameId => {
    this.props.prompt('Please enter the player name.', playerName => {
      socket.joinGame(gameId, playerName);
    });
  };

  render() {
    const {games} = this.props.server;
    const {gameName} = this.state;

    return (
      <div className="Lobby">
        <form onSubmit={this.handleCreateGame}>
          <input type="text" value={gameName}
                 placeholder="Game Name"
                 onChange={this.handleChangeGameName}/>
          <button>
            Create Game
          </button>
        </form>
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

export default connect(({server}) => ({server}), actions)(Lobby);
