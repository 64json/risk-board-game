import {actions as dialogActions} from './dialog';
import {actions as serverActions} from './server';

export {default as dialog} from './dialog';
export {default as server} from './server';

export const actions = {
  ...dialogActions,
  ...serverActions,
};
