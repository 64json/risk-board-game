class Socket {
  open(onChange) {
    this.connected = false;
    this.games = [];
    this.game = null;
    this.player = null;

    this.close();
    this.ws = new WebSocket(`ws://${document.domain}:9000/api/ws`);
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

      const isId = obj => typeof obj === 'string';
      if (this.games) {
        this.games.forEach(game => {
          if (isId(game.owner)) {
            game.owner = this.findPlayer(game.owner, game);
          }
        });
      }
      if (this.game) {
        if (isId(this.player)) {
          this.player = this.findPlayer(this.player);
        }
        if (isId(this.game.owner)) {
          this.game.owner = this.findPlayer(this.game.owner);
        }
        if (this.game.continents) {
          this.game.continents.forEach(continent => {
            continent.territories.forEach(territory => {
              if (isId(territory.adjacencyTerritories[0])) {
                territory.adjacencyTerritories = territory.adjacencyTerritories.map(adjacencyTerritory => this.findTerritory(adjacencyTerritory));
              }
              if (isId(territory.owner)) {
                territory.owner = this.findPlayer(territory.owner);
              }
            });
          });
        }
        if (this.game.attack) {
          if (isId(this.game.attack.fromTerritory)) {
            this.game.attack.fromTerritory = this.findTerritory(this.game.attack.fromTerritory);
          }
          if (isId(this.game.attack.toTerritory)) {
            this.game.attack.toTerritory = this.findTerritory(this.game.attack.toTerritory);
          }
        }
        if (isId(this.game.winner)) {
          this.game.winner = this.findPlayer(this.game.winner);
        }
      }
    }
    if (onChange) onChange(this);
  }

  findPlayer(id, game = this.game) {
    return game.players.find(player => player.id === id);
  }

  findTerritory(id) {
    return this.game.continents.flatMap(continent => continent.territories).find(territory => territory.id === id);
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

  allotArmy(territoryId) {
    this.send('allotArmy', [territoryId]);
  }

  assignArmies(territoryId, armies) {
    this.send('assignArmies', [territoryId, armies]);
  }

  createAttack(fromTerritoryId, toTerritoryId, attackingDiceCount) {
    this.send('createAttack', [fromTerritoryId, toTerritoryId, attackingDiceCount]);
  }

  defend(defendingDiceCount) {
    this.send('defend', [defendingDiceCount]);
  }

  endAttack() {
    this.send('endAttack', []);
  }

  fortify(fromTerritoryId, toTerritoryId, armies) {
    this.send('fortify', [fromTerritoryId, toTerritoryId, armies]);
  }

  endFortify() {
    this.send('endFortify', []);
  }

  keepAlive() {
    this.send('keepAlive', []);
  }
}

const server = new Socket();
export default server;
