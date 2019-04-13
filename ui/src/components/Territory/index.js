import React, {Component} from 'react';
import {connect} from 'react-redux';
import {actions} from '../../reducers';
import './stylesheet.scss';

class Territory extends Component {
  handleClick = e => {
    const {territory, onClick} = this.props;
    if (onClick) onClick(territory);
  };

  render() {
    const {territory, style} = this.props;
    const {game} = this.props.server;

    return (
      <div className="Territory" style={style} onClick={this.handleClick}>
        <span className="name">
          {territory.name}
        </span>
        <span className="owner">
          ({territory.owner ? `${game.players.find(player => player.id === territory.owner).name}: ${territory.armies} Armies` : `Not Claimed`})
        </span>
      </div>
    );
  }
}

export default connect(({server}) => ({server}), actions)(Territory);
