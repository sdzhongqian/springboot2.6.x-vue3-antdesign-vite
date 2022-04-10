/**
 * Configure and register global directives
 */
import type { App } from 'vue';
import { setupActionDirective } from './action';
import { setupActionDAuthDirective } from './action-dauth';

export function setupGlobDirectives(app: App) {
  setupActionDirective(app);
  setupActionDAuthDirective(app);
}
