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

    const {onAnswer, onClose} = this.props.dialog;
    const {answer} = this.state;
    this.setState({answer: ''});
    this.props.prompt(null, null);
    if (onAnswer) onAnswer(answer);
    if (onClose) onClose();
  };

  handleCancelDialog = e => {
    const {onCancel, onClose} = this.props.dialog;
    this.setState({answer: ''});
    this.props.prompt(null, null);
    if (onCancel) onCancel();
    if (onClose) onClose();
  };

  render() {
    const {question} = this.props.dialog;
    const {connected, game, player} = this.props.server;
    const {answer} = this.state;

    return (
      <div className="App">
        <div className="main">
          {
            connected && (
              game && player ?
                <Game/> :
                <Lobby/>
            )
          }
        </div>
        <div className="dialogContainer">
          {
            question &&
            <form className="dialog" onSubmit={this.handleSubmitAnswer}>
              <div className="question" dangerouslySetInnerHTML={{__html:question}}/>
              <input className="answer" type="text" autoFocus value={answer}
                     onChange={this.handleChangeAnswer}/>
              <div className="empty"/>
              <div className="buttons">
                <button className="button">Submit</button>
                <button className="button" type="button"
                        onClick={this.handleCancelDialog}>
                  Cancel
                </button>
              </div>
            </form>
          }
        </div>
      </div>
    );
  }
}

export default connect(({dialog, server}) => ({dialog, server}), actions)(App);
