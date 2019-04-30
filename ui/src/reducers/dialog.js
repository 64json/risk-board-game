import {combineActions, createAction, handleActions} from 'redux-actions';

const prefix = 'DIALOG';

const prompt = createAction(`${prefix}/PROMPT`, (question, onAnswer, onCancel, onClose, cancellable = true) => ({
  question,
  onAnswer,
  onCancel,
  onClose,
  cancellable,
}));

const defaultState = {
  question: null,
  onAnswer: null,
  onCancel: null,
  onClose: null,
  cancellable: null,
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
