import React, {Component} from 'react';

import {Territory} from '../';
import './stylesheet.css';

class Continent extends Component {
  render() {
    const {continent} = this.props;

    return (
      <div>
        {continent.name}
        {
          continent.territories.map(territory => (
            <Territory key={territory.id} territory={territory}/>
          ))
        }
        <br/>
      </div>
    );
  }
}

export default Continent;
