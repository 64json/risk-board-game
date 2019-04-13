import {combineActions, createAction, handleActions} from 'redux-actions';

const prefix = 'DIALOG';

const prompt = createAction(`${prefix}/PROMPT`, (question, onAnswer) => ({
  question,
  onAnswer,
}));

const defaultState = {
  question: null,
  onAnswer: null,
};

export default handleActions({
  [combineActions(
    prompt,
  )]: (state, {payload}) => ({
    ...state,
    ...payload,
  }),
}, defaultState);

export const actions = {
  prompt,
};
