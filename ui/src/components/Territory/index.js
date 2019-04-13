import React, {Component} from 'react';
import {connect} from 'react-redux';
import {actions} from '../../reducers';
import {classes} from '../../common/utils';
import './stylesheet.scss';

class Territory extends Component {
  handleClick = e => {
    const {territory, onClick} = this.props;
    if (onClick) onClick(territory);
  };

  render() {
    const {territory, style, className} = this.props;
    const {game} = this.props.server;

    return (
      <div className={classes('Territory', className)} style={style}
           onClick={this.handleClick}>
        <span className="name">
          {territory.name}
        </span>
        {
          territory.owner &&
          <span className="armies">
            {territory.armies} armies
          </span>
        }
      </div>
    );
  }
}

export default connect(({server}) => ({server}), actions)(Territory);
