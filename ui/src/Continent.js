import React, {Component} from 'react';

import './Continent.css';
import Territory from './Territory';

class Continent extends Component {
  render() {
    const {continent, game, onAssignArmies} = this.props;

    return (
      <div key={continent.id}>
        {continent.name}
        {
          continent.territories.map(territory => (
            <Territory key={territory.id} game={game}
                       territory={territory}
                       onAssignArmies={onAssignArmies}/>
          ))
        }
        <br/>
      </div>
    );
  }
}

export default Continent;
