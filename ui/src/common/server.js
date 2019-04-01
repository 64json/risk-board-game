class Server {
  open(onChange) {
    this.connected = false;
    this.games = [];
    this.game = null;
    this.player = null;

    this.close();
    this.ws = new WebSocket('ws://localhost:9000/api/ws');
    this.ws.onmessage = message => this.receive(message, onChange);
  }

  close() {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }

  receive(message, onChange) {
    const data = JSON.parse(message.data);
    if ('error' in data) {
      alert(data.error);
      console.error(data.error);
    } else {
      const isObject = obj => obj && obj.constructor === {}.constructor;
      const isArray = obj => obj && obj.constructor === [].constructor;

      // merge updates into the risk object in this.state
      const deepMerge = (dist, src) => {
        Object.keys(src).forEach(key => {
          const value = src[key];
          if (isArray(dist[key]) && isArray(value)) {
            if (dist[key].length) {
              if (isObject(dist[key][0])) {
                dist[key] = value.map(e => {
                  if (isObject(e)) {
                    const object = dist[key].find(object => object.id === e.id) || {};
                    deepMerge(object, e);
                    return object;
                  } else {
                    return dist[key].find(object => object.id === e);
                  }
                });
              } else {
                dist[key] = value;
              }
            } else {
              dist[key] = value;
            }
          } else if (isObject(dist[key]) && isObject(value)) {
            if (dist[key].id === value.id) {
              deepMerge(dist[key], value);
            } else {
              dist[key] = value;
            }
          } else {
            dist[key] = value;
          }
        });
      };
      deepMerge(this, data);

      if (onChange) onChange(data);
    }
  }

  send(method, args) {
    this.ws.send(JSON.stringify({method, args}));
  }

  createGame(gameName, playerName) {
    this.send('createGame', [gameName, playerName]);
  }

  joinGame(gameId, playerName) {
    this.send('joinGame', [gameId, playerName]);
  }

  startGame() {
    this.send('startGame', []);
  }

  leaveGame() {
    this.send('leaveGame', []);
  }

  assignArmies(territoryId, armies) {
    this.send('assignArmies', [territoryId, armies]);
  }

  proceedWithTurn() {
    this.send('proceedWithTurn', []);
  }
}

const server = new Server();
export default server;
