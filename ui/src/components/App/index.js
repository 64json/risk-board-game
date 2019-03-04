import React, {Component} from 'react';
import {BrowserRouter as Router} from 'react-router-dom';

import server from '../../common/server';
import {Game, Lobby} from '../';
import './stylesheet.css';

class App extends Component {
  componentDidMount() {
    server.open(() => this.forceUpdate());
  }

  componentWillUnmount() {
    server.close();
  }

  render() {
    const {connected, game, player} = server;

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
                  <Game/> :
                  <Lobby/>
              }
            </div>
          }
        </div>
      </Router>
    );
  }
}

export default App;
