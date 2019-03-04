import React, {Component} from 'react';

import {SvgLoader, SvgProxy} from 'react-svgmt';
import svgUrl from './map.svg';
import './stylesheet.css';

class Map extends Component {
  constructor(props) {
    super(props);

    this.state = {
      hoveredCountryId: null,
    };
  }

  handleMouseOverCountry = (e) => {
    const hoveredCountryId = e.target.id;
    this.setState({hoveredCountryId});
  };

  handleMouseOutCountry = (e) => {
    const hoveredCountryId = null;
    this.setState({hoveredCountryId});
  };

  render() {
    const {hoveredCountryId} = this.state;

    return (
      <SvgLoader path={svgUrl}>
        <SvgProxy selector=".country" fill=""
                  onmouseout={this.handleMouseOutCountry}
                  onmouseover={this.handleMouseOverCountry}/>
        <SvgProxy selector={`#${hoveredCountryId}`} fill="#ff0000"/>
      </SvgLoader>
    );
  }
}

export default Map;
