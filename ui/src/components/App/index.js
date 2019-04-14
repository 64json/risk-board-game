import React, {Component} from 'react';
import {connect} from 'react-redux';
import socket from '../../common/socket';
import {actions} from '../../reducers';
import {Game, Lobby} from '../';
import './stylesheet.scss';

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      answer: '',
    };
  }

  componentDidMount() {
    socket.open(this.props.updateData);
  }

  componentWillUnmount() {
    socket.close();
  }

  handleChangeAnswer = e => {
    const answer = e.target.value;
    this.setState({answer});
  };

  handleSubmitAnswer = e => {
    e.preventDefault();

    const {onAnswer} = this.props.dialog;
    const {answer} = this.state;
    onAnswer(answer);
    this.handleCloseDialog();
  };

  handleCloseDialog = e => {
    this.setState({answer: ''});
    this.props.prompt(null, null);
  };

  render() {
    const {question} = this.props.dialog;
    const {connected, game, player} = this.props.server;
    const {answer} = this.state;

    return (
      <div className="App">
        {
          connected && (
            game && player ?
              <Game/> :
              <Lobby/>
          )
        }
        {
          question &&
          <form className="dialog" onSubmit={this.handleSubmitAnswer}>
            <div className="question">
              {question}
            </div>
            <input className="answer" type="text" autoFocus value={answer}
                   onChange={this.handleChangeAnswer}/>
            <div className="buttons">
              <button>Submit</button>
              <button type="button" onClick={this.handleCloseDialog}>Cancel
              </button>
            </div>
          </form>
        }
      </div>
    );
  }
}

export default connect(({dialog, server}) => ({dialog, server}), actions)(App);
