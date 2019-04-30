import React, {Component} from 'react';
import {classes} from '../../common/utils';
import './stylesheet.scss';

class Territory extends Component {
  handleClick = e => {
    const {territory, onClick} = this.props;
    if (onClick) onClick(territory);
  };

  render() {
    const {territory, style, selected, from, to, enabled, className} = this.props;

    return (
      <div className={classes(
        'Territory',
        territory.owner && `player-${territory.owner.color} active`,
        selected && 'selected',
        from && 'from',
        to && 'to',
        enabled && 'enabled',
        className,
      )} style={style}
           onClick={enabled ? this.handleClick : null}>
        <img alt={territory.flag} src={`/flags/${territory.flag}`}
             className="flag"/>
        <span className="name">
          {territory.name}
        </span>
        {
          territory.owner &&
          <span className="armies">
            {territory.armies}
          </span>
        }
      </div>
    );
  }
}

export default Territory;
