import React, {Component} from 'react';
import {connect} from 'react-redux';
import socket from '../../common/socket';
import {actions} from '../../reducers';
import {classes} from '../../common/utils';
import './stylesheet.scss';

class Lobby extends Component {
  handleCreateGame = e => {
    e.preventDefault();

    this.props.prompt('Please enter the game name.', gameName => {
      this.props.prompt('Please enter the player name.', playerName => {
        socket.createGame(gameName, playerName);
      });
    });
  };

  handleJoinGame = gameId => {
    this.props.prompt('Please enter the player name.', playerName => {
      socket.joinGame(gameId, playerName);
    });
  };

  render() {
    const {connected, games} = this.props.server;

    return (
      <div className="Lobby">
        <div className="title">
          <span className="icon">🗺</span>Risk: Disney Edition
        </div>
        {
          connected ?
            <div className="games">
              {
                games.map(game => (
                  <div key={game.id}
                       className={classes('game', game.playing && 'disabled')}
                       onClick={() => this.handleJoinGame(game.id)}>
                    <span className="name">{game.name}</span>
                    <div className="row">
                      <span className="owner">{game.owner.name}</span>
                      {
                        game.playing ?
                          <span className="playing">Playing ...</span> :
                          <span className="players">{game.players.length}</span>
                      }
                    </div>
                  </div>
                ))
              }
              <div className="game create" onClick={this.handleCreateGame}>
                Create a Game
              </div>
            </div> :
            <span className="connecting">
              Connecting to server ...
            </span>
        }
      </div>
    );
  }
}

export default connect(({server}) => ({server}), actions)(Lobby);
