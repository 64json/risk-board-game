import React, {Component} from 'react';

import server from '../../common/server';
import './stylesheet.css';

class Territory extends Component {
  constructor(props) {
    super(props);

    this.state = {
      armies: '',
    };
  }

  handleChangeArmies = e => {
    const armies = e.target.value;
    this.setState({armies});
  };

  handleAssignArmies = e => {
    const {territory} = this.props;
    const {armies} = this.state;
    server.assignArmies(territory.id, Number(armies) | 0);
    this.setState({armies: ''});
  };


  render() {
    const {territory} = this.props;
    const {armies} = this.state;
    const {game} = server;
    console.log(server);

    return (
      <div>
        <input type="number" placeholder="# of Armies" value={armies}
               onChange={this.handleChangeArmies}/>
        <button onClick={this.handleAssignArmies}>
          Assign
        </button>
        {territory.name} ({territory.owner ? `${game.players.find(player => player.id === territory.owner).name}: ${territory.armies} Armies` : `Not Claimed`})
      </div>
    );
  }
}

export default Territory;
