import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { UgovorComponent } from './list/ugovor.component';
import { UgovorDetailComponent } from './detail/ugovor-detail.component';
import { UgovorUpdateComponent } from './update/ugovor-update.component';
import { UgovorDeleteDialogComponent } from './delete/ugovor-delete-dialog.component';
import { UgovorRoutingModule } from './route/ugovor-routing.module';

@NgModule({
  imports: [SharedModule, UgovorRoutingModule],
  declarations: [UgovorComponent, UgovorDetailComponent, UgovorUpdateComponent, UgovorDeleteDialogComponent],
  entryComponents: [UgovorDeleteDialogComponent],
})
export class UgovorModule {}
