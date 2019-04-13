import {combineActions, createAction, handleActions} from 'redux-actions';

const prefix = 'SERVER';

const updateData = createAction(`${prefix}/UPDATE_DATA`, ({connected, games, game, player}) => ({
  connected,
  games,
  game,
  player,
}));

const defaultState = {
  connected: false,
  games: [],
  game: null,
  player: null,
};

export default handleActions({
  [combineActions(
    updateData,
  )]: (state, {payload}) => ({
    ...state,
    ...payload,
  }),
}, defaultState);

export const actions = {
  updateData,
};
