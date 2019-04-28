import React, {Component} from 'react';
import {classes} from '../../common/utils';
import './stylesheet.scss';

class Territory extends Component {
  handleClick = e => {
    const {territory, onClick} = this.props;
    if (onClick) onClick(territory);
  };

  render() {
    const {territory, style, className} = this.props;

    return (
      <div className={classes('Territory', className)} style={style}
           onClick={this.handleClick}>
        <img src={`/flags/${territory.flag}`} className="flag"/>
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

export default Territory;
