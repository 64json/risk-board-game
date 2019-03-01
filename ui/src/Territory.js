import React, {Component} from 'react';

import './Territory.css';

class Territory extends Component {
  constructor(props) {
    super(props);

    this.state = {
      army: '',
    };
  }

  handleChangeArmy = e => {
    const army = e.target.value;
    this.setState({army});
  };

  handleAssignArmies = e => {
    const {territory, onAssignArmies} = this.props;
    const {army} = this.state;
    onAssignArmies(territory.id, Number(army) | 0);
    this.setState({army: ''});
  };

  render() {
    const {territory, game} = this.props;
    const {army} = this.state;

    return (
      <div>
        <input type="number" id="army"
               placeholder="# of Armies"
               value={army}
               onChange={this.handleChangeArmy}/>
        <button onClick={this.handleAssignArmies}>
          Assign
        </button>
        {territory.name} ({territory.owner ? `${game.players.find(player => player.id === territory.owner).name}: ${territory.armies} Armies` : `Not Claimed`})
      </div>
    );
  }
}

export default Territory;
