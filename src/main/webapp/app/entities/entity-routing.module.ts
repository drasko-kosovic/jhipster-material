import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'ponudjaci',
        data: { pageTitle: 'Ponudjacis' },
        loadChildren: () => import('./ponudjaci/ponudjaci.module').then(m => m.PonudjaciModule),
      },
      {
        path: 'ponude',
        data: { pageTitle: 'Ponudes' },
        loadChildren: () => import('./ponude/ponude.module').then(m => m.PonudeModule),
      },

    ]),
  ],
})
export class EntityRoutingModule {}
